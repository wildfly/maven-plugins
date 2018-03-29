package org.wildfly.maven.plugins.quickstart.documentation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.wildfly.maven.plugins.quickstart.documentation.drupal.CodingResourceGenerator;
import org.wildfly.maven.plugins.quickstart.documentation.drupal.DrupalCommunication;
import org.wildfly.maven.plugins.quickstart.documentation.drupal.SitemapEntry;
import org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal.CodingResource;

/**
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
@Mojo(name = "drupal-push", threadSafe = true, defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class DrupalPushMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${project.basedir}", required = true, property = "rootDirectory")
    private File rootDirectory;

    @Parameter(defaultValue = "${settings}", readonly = true)
    private Settings settings;

    @Parameter(defaultValue = "", readonly = true, required = true)
    private String drupalUrl;

    @Parameter(defaultValue = "quickstart", readonly = true)
    private String resourceType;

    @Parameter(required = true, readonly = true)
    private String serverName;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (Objects.isNull(project) || Objects.isNull(project.getVersion()) || Objects.isNull(rootDirectory)
                || Objects.isNull(settings) || Objects.isNull(serverName) || Objects.isNull(settings.getServer(serverName))
                || Objects.isNull(resourceType) || Objects.isNull(drupalUrl)) {
            throw new MojoExecutionException("Missing required arguments, please consult the README");
        }
        final Server drupal = this.settings.getServer(serverName);
        final DrupalCommunication drupalCommunication = new DrupalCommunication(drupal.getUsername(),
                drupal.getPassword(), this.drupalUrl, this.getLog());
        final List<String> ignoredDirs = Arrays.asList("target", "dist", "template", "guide");

        try {
            Files.find(rootDirectory.toPath(), 2,
                    (path, attributes) -> path.getFileName().endsWith("README.adoc")
                            && !path.getParent().equals(rootDirectory.toPath())
                            && !ignoredDirs.contains(path.getParent().getFileName().toString()))
                    .parallel()
                    .forEach(path -> {
                        getLog().debug("Processing files at " + path.getParent());
                        final CodingResource resource = new CodingResourceGenerator(drupalCommunication, this.getLog())
                                .createResource(path.getParent(), resourceType);

                        resource.addVersion(this.project.getVersion());

                        List<SitemapEntry> entries = drupalCommunication.getEntriesOfType(resourceType);

                        final SitemapEntry testEntry = new SitemapEntry(resource.getPathValue());
                        if (entries.contains(testEntry)) {
                            final SitemapEntry entry = entries.get(entries.indexOf(testEntry));
                            //getLog().info("path.toFile().lastModifed() "+Instant.ofEpochMilli(path.toFile().lastModified()));
                            //getLog().info("entry.getLastmod()          "+entry.getLastmod());
                            if (entry.getLastmod().isBefore(Instant.ofEpochMilli(path.toFile().lastModified()))) {
                                final boolean success = drupalCommunication.updateCodingResource(resource);
                                if (success) {
                                    getLog().info("Successful update to " + resource.getPathValue());
                                } else {
                                    getLog().warn("Was not able to update " + resource.getPathValue() + ". Consult log.");
                                }
                            }else{
                                getLog().debug("not modified");
                            }
                        } else {
                            final boolean success = drupalCommunication.postNewCodingResource(resource);
                            if (success) {
                                getLog().info("Successful creation of " + resource.getPathValue());
                            } else {
                                getLog().warn("Was not able to create " + resource.getPathValue() + ". Consult log.");
                            }
                        }
                    });
        } catch (IOException e) {
            throw new MojoExecutionException("Could not send coding resources to Drupal.", e);
        }
    }
}
