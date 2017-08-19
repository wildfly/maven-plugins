package org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal;

import java.util.Objects;

/**
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
public class TextWithSummaryWrapper extends TextWrapper {
    private final String summary;

    public TextWithSummaryWrapper(String value, String format, String summary) {
        super(value, format);
        this.summary = summary;
    }

    public TextWithSummaryWrapper(String body, String summary) {
        super(body);
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextWithSummaryWrapper that = (TextWithSummaryWrapper) o;
        return Objects.equals(getSummary(), that.getSummary());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSummary());
    }
}
