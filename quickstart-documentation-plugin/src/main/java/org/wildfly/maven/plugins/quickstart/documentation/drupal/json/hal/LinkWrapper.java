package org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
public class LinkWrapper {
    private final String uri;
    private final String title;
    private final List<String> options;

    public LinkWrapper(String url, String text) {
        this(url, text, Collections.emptyList());
    }

    public LinkWrapper(String url, String text, List<String> options) {
        this.uri = url;
        this.title = text;
        this.options = options;
    }

    public String getUri() {
        return uri;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getOptions() {
        return new ArrayList<>(options);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkWrapper that = (LinkWrapper) o;
        return Objects.equals(getUri(), that.getUri()) &&
                Objects.equals(getTitle(), that.getTitle()) &&
                Objects.equals(getOptions(), that.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUri(), getTitle(), getOptions());
    }
}
