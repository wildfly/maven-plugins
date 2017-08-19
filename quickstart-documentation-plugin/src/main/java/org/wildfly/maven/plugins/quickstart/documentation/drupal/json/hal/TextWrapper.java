package org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal;

import java.util.Objects;

/**
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
public class TextWrapper {
    private final String value;
    private final String format;

    public TextWrapper(String body) {
        this.value = body;
        this.format = "rhd_html";
    }

    public TextWrapper(String value, String format) {
        this.value = value;
        this.format = format;
    }

    public String getValue() {
        return value;
    }

    public String getFormat() {
        return format;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextWrapper that = (TextWrapper) o;
        return Objects.equals(getValue(), that.getValue()) &&
                Objects.equals(getFormat(), that.getFormat());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue(), getFormat());
    }
}
