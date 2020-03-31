package org.wildfly.maven.plugins.licenses;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Singleton
public class DependenciesResolver extends AbstractLogEnabled {

  /**
   * Message used when an invalid expression pattern is found.
   */
  public static final String INVALID_PATTERN_MESSAGE =
          "The pattern specified by expression <%s> seems to be invalid.";

  /**
   * Project builder.
   */
  @javax.inject.Inject
  private MavenProjectBuilder mavenProjectBuilder;

  public <R> SortedMap<String, R> loadDependenciesAndConvertThem(MavenProject project,
                                                                 MavenProjectDependenciesConfiguration configuration,
                                                                 ArtifactRepository localRepository,
                                                                 List<ArtifactRepository> remoteRepositories,
                                                                 SortedMap<String, MavenProject> cache,
                                                                 Function<MavenProject, R> convertFunction) {


    Pattern includedGroupPattern = (isEmpty(configuration.includedGroups) ? null : Pattern.compile(configuration.includedGroups));
    Pattern includedArtifactPattern = (isEmpty(configuration.includedArtifacts) ? null : Pattern.compile(configuration.includedArtifacts));

    Pattern excludedGroupPattern = (isEmpty(configuration.excludedGroups) ? null : Pattern.compile(configuration.excludedGroups));
    Pattern excludedArtifactPattern = (isEmpty(configuration.excludedArtifacts) ? null : Pattern.compile(configuration.excludedArtifacts));

    boolean matchInclusions = includedGroupPattern != null || includedArtifactPattern != null;
    boolean matchExclusions = excludedGroupPattern != null || excludedArtifactPattern != null;

    Set<Artifact> depArtifacts;

    if (configuration.includeTransitiveDependencies) {
      // All project dependencies
      depArtifacts = project.getArtifacts();
    } else {
      // Only direct project dependencies
      depArtifacts = project.getDependencyArtifacts();
    }

    if (configuration.includeSelfArtifact) {
      depArtifacts.add(project.getArtifact());
    }

    List<String> includedScopes = configuration.includedScopes;
    List<String> excludeScopes = configuration.excludedScopes;

    boolean verbose = configuration.verbose;

    SortedMap<String, R> result = new TreeMap<String, R>();
    Logger log = getLogger();

    for (Object o : depArtifacts) {
      Artifact artifact = (Artifact) o;
      String id = getArtifactId(artifact);

      if (verbose) {
        log.info("detected artifact " + id);
      }

      if (!configuration.includeOptionalDependencies && artifact.isOptional()) {
        if (verbose) {
          log.info("skip optional artifact " + id);
        }
        continue;
      }

      String scope = artifact.getScope();
      if (isNotEmptyCollection(includedScopes) && !includedScopes.contains(scope)) {
        if (verbose) {
          log.info("skip artifact " + id + " - not in included scopes");
        }
        continue;
      }

      if (excludeScopes.contains(scope)) {
        if (verbose) {
          log.info("skip artifact " + id + " - in excluded scope " + scope);
        }
        continue;
      }


      // Check if the project should be included
      boolean isToInclude = matchesIncluded(artifact, includedGroupPattern, includedArtifactPattern);
      if (!isToInclude) {
        if (verbose) {
          log.info("skip artifact " + id + " - not in included artifactId / groupId patterns");
        }
        continue;
      }
      // Check if the project should be excluded
      boolean isToExclude = matchesExcluded(artifact, excludedGroupPattern, excludedArtifactPattern);

      if (isToExclude) {
        if (verbose) {
          log.info("skip artifact " + id + " - in excluded artifactId / groupId patterns");
        }
        continue;
      }

      MavenProject depMavenProject = getDependencyMavenProject(localRepository,
              remoteRepositories, cache, verbose, artifact, log, id);
      if (depMavenProject != null) {
        // keep the project
        result.put(id, convertFunction.apply(depMavenProject));
      }
    }
    return result;
  }

  private MavenProject getDependencyMavenProject(ArtifactRepository localRepository,
                                                 List<ArtifactRepository> remoteRepositories, SortedMap<String, MavenProject> cache,
                                                 boolean verbose, Artifact artifact, Logger log, String id) {
    MavenProject depMavenProject = null;

    if (cache != null) {
      // try to get project from cache
      depMavenProject = cache.get(id);
      if (depMavenProject != null) {
        if (verbose) {
          log.info("add dependency [" + id + "] (from cache)");
        }
        return depMavenProject;
      }
    }

    try {
      depMavenProject = mavenProjectBuilder.buildFromRepository(artifact, remoteRepositories, localRepository, true);
      depMavenProject.getArtifact().setScope(artifact.getScope());
    } catch (ProjectBuildingException e) {
      log.warn("Unable to obtain POM for artifact : " + artifact, e);
      return null;
    }

    if (verbose) {
      log.info("add dependency [" + id + "]");
    }

    if (cache != null) {
      cache.put(id, depMavenProject);
    }
    return depMavenProject;
  }


  /**
   * Tests if the given project can be included against a groupdId pattern and a artifact pattern.
   *
   * @param project                 the project to test
   * @param includedGroupPattern    the include group pattern
   * @param includedArtifactPattern the include artifact pattenr
   * @return {@code true} if the project is includavble, {@code false} otherwise
   */
  private boolean matchesIncluded(Artifact project, Pattern includedGroupPattern, Pattern includedArtifactPattern) {

    Logger log = getLogger();
    if (includedArtifactPattern == null && includedGroupPattern == null) {// If there is no specified artifacts and group to include, include all
      return true;
    }

    // check if the groupId of the project should be included
    if (includedGroupPattern != null) {
      // we have some defined license filters
      try {
        Matcher matchGroupId = includedGroupPattern.matcher(project.getGroupId());
        if (matchGroupId.find()) {
          if (log.isDebugEnabled()) {
            log.debug("Include " + project.getGroupId());
          }
          return true;
        }
      } catch (PatternSyntaxException e) {
        log.warn(String.format(INVALID_PATTERN_MESSAGE, includedGroupPattern.pattern()));
      }
    }

    // check if the artifactId of the project should be included
    if (includedArtifactPattern != null) {
      // we have some defined license filters
      try {
        Matcher matchGroupId = includedArtifactPattern.matcher(project.getArtifactId());
        if (matchGroupId.find()) {
          if (log.isDebugEnabled()) {
            log.debug("Include " + project.getArtifactId());
          }
          return true;
        }
      } catch (PatternSyntaxException e) {
        log.warn(String.format(INVALID_PATTERN_MESSAGE, includedArtifactPattern.pattern()));
      }
    }
    return false;
  }

  /**
   * Tests if the given project can be excluded against a groupdId pattern and a artifact pattern.
   *
   * @param project                 the project to test
   * @param excludedGroupPattern    the exlcude group pattern
   * @param excludedArtifactPattern the exclude artifact pattenr
   * @return {@code true} if the project is excludable, {@code false} otherwise
   */
  protected boolean matchesExcluded(Artifact project, Pattern excludedGroupPattern, Pattern excludedArtifactPattern) {

    Logger log = getLogger();
    if (excludedGroupPattern == null && excludedArtifactPattern == null) {
      return false;
    }

    // check if the groupId of the project should be included
    if (excludedGroupPattern != null) {
      // we have some defined license filters
      try {
        Matcher matchGroupId = excludedGroupPattern.matcher(project.getGroupId());
        if (matchGroupId.find()) {
          if (log.isDebugEnabled()) {
            log.debug("Exclude " + project.getGroupId());
          }
          return true;
        }
      } catch (PatternSyntaxException e) {
        log.warn(String.format(INVALID_PATTERN_MESSAGE, excludedGroupPattern.pattern()));
      }
    }

    // check if the artifactId of the project should be included
    if (excludedArtifactPattern != null) {
      // we have some defined license filters
      try {
        Matcher matchGroupId = excludedArtifactPattern.matcher(project.getArtifactId());
        if (matchGroupId.find()) {
          if (log.isDebugEnabled()) {
            log.debug("Exclude " + project.getArtifactId());
          }
          return true;
        }
      } catch (PatternSyntaxException e) {
        log.warn(String.format(INVALID_PATTERN_MESSAGE, excludedArtifactPattern.pattern()));
      }
    }
    return false;
  }

  boolean isNotEmptyCollection(Collection coll) {
    return coll != null && !coll.isEmpty();
  }

  String getArtifactId(Artifact artifact) {
    StringBuilder sb = new StringBuilder();
    sb.append(artifact.getGroupId());
    sb.append("--");
    sb.append(artifact.getArtifactId());
    sb.append("--");
    sb.append(artifact.getVersion());
    return sb.toString();
  }

  boolean isEmpty(String str) {
    return str == null || str.length() == 0;
  }

}
