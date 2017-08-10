package org.wildfly.maven.plugins.quickstart.documentation.drupal.hal;

/**
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
public class PathWrapper {
    private final String alias;

    public PathWrapper(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }
}
