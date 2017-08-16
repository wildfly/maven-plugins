package org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathWrapper that = (PathWrapper) o;
        return Objects.equals(getAlias(), that.getAlias());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAlias());
    }

    @Override
    public String toString() {
        return this.alias;
    }
}
