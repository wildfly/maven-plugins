package org.wildfly.maven.plugins.licenses;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.License;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Proxy;
import org.codehaus.plexus.util.Base64;
import org.wildfly.maven.plugins.licenses.model.KnownLicenseInfo;
import org.wildfly.maven.plugins.licenses.model.ProjectInfo;
import org.wildfly.maven.plugins.licenses.model.ProjectLicenseInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Insert versions into generated licenses.xml
 */
@Mojo(name = "insert-versions", requiresDependencyResolution = ResolutionScope.TEST,
        defaultPhase = LifecyclePhase.PACKAGE)
public class UpdateLicensesMojo
        extends AbstractMojo {
  private final LicensesFileWriter licensesFileWriter;
  private final LicensesFileReader licensesFileReader;

  // ----------------------------------------------------------------------
  // Mojo Parameters
  // ----------------------------------------------------------------------

  /**
   * A flag to skip the goal.
   */
  @Parameter(property = "license.skipDownloadLicenses", defaultValue = "false")
  private boolean skipDownloadLicenses;

  /**
   * Location of the local repository.
   */
  @Parameter(defaultValue = "${localRepository}", readonly = true)
  private ArtifactRepository localRepository;

  /**
   * List of Remote Repositories used by the resolver
   */
  @Parameter(defaultValue = "${project.remoteArtifactRepositories}", readonly = true)
  private List<ArtifactRepository> remoteRepositories;

  /**
   * Input file containing a mapping between each dependency and it's license information.
   */
  @Parameter(property = "licensesConfigFile", defaultValue = "${project.basedir}/src/license/licenses.xml")
  private File licensesConfigFile;

  /**
   * List of input files containing a mapping between each dependency and it's license information.
   */
  @Parameter(property = "licensesConfigFiles")
  private List<File> licensesConfigFiles;

  /**
   * The output file containing a mapping between each dependency and it's license information.
   */
  @Parameter(property = "licensesOutputFile",
          defaultValue = "${project.build.directory}/generated-resources/licenses.xml")
  private File licensesOutputFile;

  /**
   * A filter to exclude some scopes.
   */
  @Parameter(property = "license.excludedScopes", defaultValue = "system")
  private String excludedScopes;

  /**
   * A filter to include only some scopes, if let empty then all scopes will be used (no filter).
   */
  @Parameter(property = "license.includedScopes", defaultValue = "")
  private String includedScopes;

  /**
   * Settings offline flag (will not download anything if setted to true).
   */
  @Parameter(defaultValue = "${settings.offline}")
  private boolean offline;

  /**
   * Don't show warnings about bad or missing license files.
   */
  @Parameter(defaultValue = "false")
  private boolean quiet;

  /**
   * Include transitive dependencies when downloading license files.
   */
  @Parameter(defaultValue = "true")
  private boolean includeTransitiveDependencies;

  /**
   * Include optional dependencies when downloading license files.
   */
  @Parameter(property = "license.includeOptionalDependencies", defaultValue = "true")
  private boolean includeOptionalDependencies;

  /**
   * Include the artifact of this project.
   */
  @Parameter(property = "license.includeSelfArtifact", defaultValue = "false")
  private boolean includeSelfArtifact;

  /**
   * Get declared proxies from the {@code settings.xml} file.
   */
  @Parameter(defaultValue = "${settings.proxies}", readonly = true)
  private List<Proxy> proxies;

  @Parameter(property = "license.sortByGroupIdAndArtifactId", defaultValue = "false")
  private boolean sortByGroupIdAndArtifactId;

  /**
   * A filter to exclude some GroupIds
   * This is a regular expression that is applied to groupIds (not an ant pattern).
   */
  @Parameter(property = "license.excludedGroups", defaultValue = "")
  private String excludedGroups;

  /**
   * A filter to include only some GroupIds
   * This is a regular expression applied to artifactIds.
   */
  @Parameter(property = "license.includedGroups", defaultValue = "")
  private String includedGroups;

  /**
   * A filter to exclude some ArtifactsIds
   * This is a regular expression applied to artifactIds.
   */
  @Parameter(property = "license.excludedArtifacts", defaultValue = "")
  private String excludedArtifacts;

  /**
   * A filter to include only some ArtifactsIds
   * This is a regular expression applied to artifactIds.
   */
  @Parameter(property = "license.includedArtifacts", defaultValue = "")
  private String includedArtifacts;

  /**
   * Instead of adding the artifact version, add the property ${version.groupId.artifactId}.
   * This property is expected to be replaced during Galleon provisioning.
   */
  @Parameter(defaultValue = "false", property = "license.generateVersionProperty")
  private boolean generateVersionProperty;
  /**
   * The Maven Project Object
   */
  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject project;

  /**
   * Dependencies tool.
   */
  @javax.inject.Inject
  private DependenciesResolver dependenciesResolver;

  /**
   * Keeps a collection of the URLs of the licenses that have been downlaoded. This helps the plugin to avoid
   * downloading the same license multiple times.
   */
  private Set<String> downloadedLicenseURLs = new HashSet<String>();

  /**
   * Proxy Login/Password encoded(only if usgin a proxy with authentication).
   */
  private String proxyLoginPasswordEncoded;

  private java.util.Properties systemProperties;

  private void backupSystemProperties() {
    systemProperties = (java.util.Properties) System.getProperties().clone();
  }

  private void restoreSystemProperties() {
    if (systemProperties != null)
      System.setProperties(systemProperties);
  }

  protected UpdateLicensesMojo() {
    this.licensesFileWriter = new LicensesFileWriter();
    this.licensesFileReader = new LicensesFileReader();
  }

  // ----------------------------------------------------------------------
  // AbstractDownloadLicensesMojo Implementation
  // ----------------------------------------------------------------------

  public void execute()
          throws MojoExecutionException {

    if (skipDownloadLicenses) {
      getLog().info("skip flag is on, will skip goal.");
      return;
    }

    backupSystemProperties();
    try {
      initProxy();

      initDirectories();

      Map<String, ProjectLicenseInfo> configuredDepLicensesMap = new HashMap<String, ProjectLicenseInfo>();
      Map<String, License> licenseAliasesMap = new HashMap<>();

      // License info from previous build
      if (licensesOutputFile.exists()) {
        loadLicenseInfo(configuredDepLicensesMap, licenseAliasesMap, licensesOutputFile, true);
      }

      // Manually configured license info, loaded second to override previously loaded info
      if (licensesConfigFile.exists()) {
        loadLicenseInfo(configuredDepLicensesMap, licenseAliasesMap, licensesConfigFile, false);
      }

      if (licensesConfigFiles != null) {
        for (File licCfgFile : licensesConfigFiles) {
          if (licCfgFile.exists()) {
            loadLicenseInfo(configuredDepLicensesMap, licenseAliasesMap, licCfgFile, false);
          }
        }
      }

      Collection<ProjectLicenseInfo> dependenciesLicenseInfos = getDependenciesLicenseInfos();

      // The resulting list of licenses after dependency resolution
      List<ProjectLicenseInfo> depProjectLicenses = new ArrayList<ProjectLicenseInfo>();

      for (ProjectLicenseInfo dependencyLicenseInfo : dependenciesLicenseInfos) {
        getLog().debug("Checking licenses for project " + dependencyLicenseInfo.toString());
        String artifactProjectId = dependencyLicenseInfo.getId();
        ProjectLicenseInfo licenseInfo;
        if (configuredDepLicensesMap.containsKey(artifactProjectId)) {
          licenseInfo = configuredDepLicensesMap.get(artifactProjectId);
          licenseInfo.setVersion(dependencyLicenseInfo.getVersion());

          if (!dependencyLicenseInfo.getLicenses().isEmpty()) {
            String declaredLicenseNames = dependencyLicenseInfo.getLicenses().stream().map(License::getName)
                    .collect(Collectors.joining(",", "'", "'"));
            String localLicenseNames = licenseInfo.getLicenses().stream().map(License::getName)
                    .collect(Collectors.joining(",", "'", "'"));

            getLog().warn("Possible license mismatch for " + dependencyLicenseInfo + ", check that the configuration files are up to date\n" +
                    "Declared licenses: " + declaredLicenseNames + "\n" +
                    "Local licenses: " + localLicenseNames);
          }
        } else {
          licenseInfo = dependencyLicenseInfo;
        }
        if (generateVersionProperty) {
            licenseInfo.setVersion("${version."+dependencyLicenseInfo.getGroupId()+"."+dependencyLicenseInfo.getArtifactId()+"}");
        }

        // adjust to canonical license info
        List<License> declaredLicenses = licenseInfo.getLicenses();
        Map<String, License> knownLicenses = KnownLicenses.get();
        if (!declaredLicenses.isEmpty()) {
          for (int i = 0; i < declaredLicenses.size(); i++) {
            License license = declaredLicenses.get(i);
            String licenseName = license.getName().toLowerCase();
            License canonicalLicense = knownLicenses.containsKey(licenseName) ? knownLicenses.get(licenseName)
                    : licenseAliasesMap.get(licenseName);
            if (canonicalLicense != null) {
              canonicalLicense.setDistribution(license.getDistribution());
              canonicalLicense.setComments(license.getComments());
              declaredLicenses.set(i, canonicalLicense);
            } else {
              throw new MojoExecutionException("Unknown license '" + licenseName + "' for " + dependencyLicenseInfo
                      + ", update the configuration files");
            }
          }
        } else {
          throw new MojoExecutionException("No licenses found for " + dependencyLicenseInfo + ", update the configuration files");
        }
        depProjectLicenses.add(licenseInfo);
      }

      try {
        getLog().info("Sort licenses " + sortByGroupIdAndArtifactId);
        if (sortByGroupIdAndArtifactId) {
          depProjectLicenses = sortByGroupIdAndArtifactId(depProjectLicenses);
        }
        licensesFileWriter.writeLicenseSummary(depProjectLicenses, licensesOutputFile);
      } catch (Exception e) {
        throw new MojoExecutionException("Unable to write license summary file: " + licensesOutputFile, e);
      }
    } finally {
      restoreSystemProperties();
    }
  }

  private Collection<ProjectLicenseInfo> getDependenciesLicenseInfos() {

    MavenProjectDependenciesConfiguration configuration = new MavenProjectDependenciesConfiguration(

            includeTransitiveDependencies,
            includeOptionalDependencies,
            includeSelfArtifact,
            convertStringToList(includedScopes), convertStringToList(excludedScopes),
            includedArtifacts,
            includedGroups,
            excludedGroups,
            excludedArtifacts, isVerbose()
    );
    SortedMap<String, ProjectLicenseInfo> set = dependenciesResolver.loadDependenciesAndConvertThem(project, configuration, localRepository, remoteRepositories, null, new Function<MavenProject, ProjectLicenseInfo>() {
      public ProjectLicenseInfo apply(MavenProject project) {
        return createDependencyProject(project);
      }
    });
    return set.values();
  }

  private List<ProjectLicenseInfo> sortByGroupIdAndArtifactId(List<ProjectLicenseInfo> depProjectLicenses) {
    List<ProjectLicenseInfo> sorted = new ArrayList<ProjectLicenseInfo>(depProjectLicenses);
    Comparator<? super ProjectLicenseInfo> comparator = new Comparator<ProjectLicenseInfo>() {
      public int compare(ProjectLicenseInfo info1, ProjectLicenseInfo info2) {
        //ProjectLicenseInfo::getId() can not be used because . is before : thus a:b.c would be after a.b:c
        return (info1.getGroupId() + "+" + info1.getArtifactId()).compareTo(info2.getGroupId() + "+" + info2.getArtifactId());
      }
    };
    Collections.sort(sorted, comparator);
    return sorted;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isIncludeTransitiveDependencies() {
    return includeTransitiveDependencies;
  }

  public boolean isIncludeOptionalDependencies() {
    return includeOptionalDependencies;
  }

  public boolean isIncludeSelfArtifact() {
    return includeSelfArtifact;
  }

  private boolean isVerbose() {
    return getLog().isDebugEnabled();
  }

  private void initProxy()
          throws MojoExecutionException {
    Proxy proxyToUse = null;
    for (Proxy proxy : proxies) {
      if (proxy.isActive() && "http".equals(proxy.getProtocol())) {

        // found our proxy
        proxyToUse = proxy;
        break;
      }
    }
    if (proxyToUse != null) {

      System.getProperties().put("proxySet", "true");
      System.setProperty("proxyHost", proxyToUse.getHost());
      System.setProperty("proxyPort", String.valueOf(proxyToUse.getPort()));
      if (proxyToUse.getNonProxyHosts() != null) {
        System.setProperty("nonProxyHosts", proxyToUse.getNonProxyHosts());
      }
      if (proxyToUse.getUsername() != null) {
        String loginPassword = proxyToUse.getUsername() + ":" + proxyToUse.getPassword();
        proxyLoginPasswordEncoded = new String(Base64.encodeBase64(loginPassword.getBytes()));
      }
    }
  }

  private void initDirectories()
          throws MojoExecutionException {
    try {
      createDirectoryIfNecessary(licensesOutputFile.getParentFile());
    } catch (IOException e) {
      throw new MojoExecutionException("Unable to create a directory...", e);
    }
  }

  /**
   * Creates the directory (and his parents) if necessary.
   *
   * @param dir the directory to create if not exisiting
   * @return {@code true} if directory was created, {@code false} if was no
   * need to create it
   * @throws IOException if could not create directory
   */
  private boolean createDirectoryIfNecessary(File dir)
          throws IOException {
    if (!dir.exists()) {
      boolean b = dir.mkdirs();
      if (!b) {
        throw new IOException("Could not create directory " + dir);
      }
      return true;
    }
    return false;
  }

  /**
   * Load the license information contained in a file if it exists. Will overwrite existing license information in the
   * map for dependencies with the same id. If the config file does not exist, the method does nothing.
   *
   * @param configuredDepLicensesMap A map between the dependencyId and the license info
   * @param licenseAliasesMap        A map between the license names and licenses
   * @param licenseConfigFile        The license configuration file to load
   * @param previouslyDownloaded     Whether these licenses were already downloaded
   * @throws MojoExecutionException if could not load license infos
   */
  private void loadLicenseInfo(Map<String, ProjectLicenseInfo> configuredDepLicensesMap, Map<String, License> licenseAliasesMap,
                               File licenseConfigFile, boolean previouslyDownloaded)
          throws MojoExecutionException {
    try (FileInputStream fis = new FileInputStream(licenseConfigFile)) {
      ProjectInfo projectInfo = licensesFileReader.parseLicenseSummary(fis);
      for (ProjectLicenseInfo dep : projectInfo.getDependenciesList()) {
        configuredDepLicensesMap.put(dep.getId(), dep);
        if (previouslyDownloaded) {
          for (License license : dep.getLicenses()) {
            // Save the URL so we don't download it again
            downloadedLicenseURLs.add(license.getUrl());
          }
        }
      }
      for (KnownLicenseInfo knownLicenseInfo : projectInfo.getKnownLicensesList()) {
        licenseAliasesMap.put(knownLicenseInfo.getLicense().getName().toLowerCase(), knownLicenseInfo.getLicense());
        for (String alias : knownLicenseInfo.getAliases()) {
          licenseAliasesMap.put(alias.toLowerCase(), knownLicenseInfo.getLicense());
        }
      }
    } catch (Exception e) {
      throw new MojoExecutionException("Unable to parse license summary output file: " + licenseConfigFile, e);
    }
  }

  /**
   * Create a simple DependencyProject object containing the GAV and license info from the Maven Artifact
   *
   * @param depMavenProject the dependency maven project
   * @return DependencyProject with artifact and license info
   */
  private ProjectLicenseInfo createDependencyProject(MavenProject depMavenProject) {
    ProjectLicenseInfo dependencyProject =
            new ProjectLicenseInfo(depMavenProject.getGroupId(), depMavenProject.getArtifactId(),
                    depMavenProject.getVersion());
    List<?> licenses = depMavenProject.getLicenses();
    for (Object license : licenses) {
      dependencyProject.addLicense((License) license);
    }
    return dependencyProject;
  }

  static List<String> convertStringToList(String params) {
    String[] split = params == null ? new String[0] : params.split(",");
    return Arrays.asList(split);
  }


}
