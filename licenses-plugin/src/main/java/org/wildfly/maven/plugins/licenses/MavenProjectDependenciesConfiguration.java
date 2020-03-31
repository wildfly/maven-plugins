package org.wildfly.maven.plugins.licenses;

import java.util.List;

public class MavenProjectDependenciesConfiguration {

  /**
   * true if should include transitive dependencies, false to include only direct dependencies
   */
  public final boolean includeTransitiveDependencies;

  /**
   * true if should include optional dependencies, false to include only non-optional dependencies
   */
  public final boolean includeOptionalDependencies;

  /**
   * true if the the artifact of this project should be included
   */
  public final boolean includeSelfArtifact;


  /**
   * list of scopes to include while loading dependencies, if null, then include all scopes
   */
  public final List<String> includedScopes;

  /**
   * list of scopes to exclude while loading dependencies, if null, then include all scopes
   */
  public final List<String> excludedScopes;

  /**
   * pattern to include dependencies by their artificatId, if null then include all artifacts
   */
  public final String includedArtifacts;

  /**
   * pattern to include dependencies by their groupId, if null then include all dependencies
   */
  public final String includedGroups;

  /**
   * pattern to exclude dependencies by their groupId, if null then the include all dependencies
   */
  public final String excludedGroups;

  /**
   * pattern to exclude dependencies by their groupId, if null then include all dependencies
   */
  public final String excludedArtifacts;

  /**
   * if true verbose mode is on, if false off
   */
  public final boolean verbose;

  public MavenProjectDependenciesConfiguration(boolean includeTransitiveDependencies,
                                               boolean includeOptionalDependencies,
                                               boolean includeSelfArtifact,
                                               List<String> includedScopes,
                                               List<String> excludedScopes,
                                               String includedArtifacts,
                                               String includedGroups,
                                               String excludedGroups,
                                               String excludedArtifacts,
                                               boolean verbose) {
    this.includeTransitiveDependencies = includeTransitiveDependencies;
    this.includeOptionalDependencies = includeOptionalDependencies;
    this.includeSelfArtifact = includeSelfArtifact;
    this.includedScopes = includedScopes;
    this.excludedScopes = excludedScopes;
    this.includedArtifacts = includedArtifacts;
    this.includedGroups = includedGroups;
    this.excludedGroups = excludedGroups;
    this.excludedArtifacts = excludedArtifacts;
    this.verbose = verbose;
  }
}
