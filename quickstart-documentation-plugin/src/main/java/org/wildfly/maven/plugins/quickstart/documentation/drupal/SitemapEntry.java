package org.wildfly.maven.plugins.quickstart.documentation.drupal;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
public final class SitemapEntry {
    private String loc;
    private Instant lastmod;

    public SitemapEntry(String loc, Instant lastmod) {
        this.loc = loc;
        this.lastmod = lastmod;
    }

    public SitemapEntry(String loc, String lastmod) {
        this(loc, Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(lastmod)));
    }

    public SitemapEntry(String loc) {
        this(loc, Instant.EPOCH);
    }

    public String getLoc() {
        return loc;
    }

    public Instant getLastmod() {
        return lastmod;
    }

    @Override
    public String toString() {
        return "SitemapEntry{" +
                "loc='" + loc + '\'' +
                ", lastmod=" + lastmod +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SitemapEntry that = (SitemapEntry) o;
        return Objects.equals(getLoc(), that.getLoc());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLoc());
    }
}
