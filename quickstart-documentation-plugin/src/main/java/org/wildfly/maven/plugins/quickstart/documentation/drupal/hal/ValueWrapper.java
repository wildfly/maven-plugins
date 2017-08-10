package org.wildfly.maven.plugins.quickstart.documentation.drupal.hal;

/**
 * Worthless class that just holds a value. Needed to get the json structure correct.
 *
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
public class ValueWrapper {
    private final Object value;

    public ValueWrapper(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
