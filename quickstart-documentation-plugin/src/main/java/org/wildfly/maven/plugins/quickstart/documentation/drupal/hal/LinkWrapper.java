package org.wildfly.maven.plugins.quickstart.documentation.drupal.hal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
public class LinkWrapper {
    private final String url;
    private final String title;
    private final List<String> options;

    public LinkWrapper(String url, String text) {
        this(url, text, Collections.emptyList());
    }

    public LinkWrapper(String url, String text, List<String> options) {
        this.url = url;
        this.title = text;
        this.options = options;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getOptions() {
        return new ArrayList<>(options);
    }
}
