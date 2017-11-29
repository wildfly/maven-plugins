package org.wildfly.maven.plugins.quickstart.documentation.drupal;

import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
public class HtmlParsingTest {
    @Test
    public void assertPullingTheRightAmountFromHtml() throws Exception {
        InputStream file = this.getClass().getClassLoader().getResourceAsStream("helloworld/README.html");
        Document doc = Jsoup.parse(file, "UTF-8", "");
        Elements docContent = doc.select("h1 ~ *:not(p:first-of-type)");

        assertThat(docContent.html().isEmpty()).isFalse();
        assertThat(docContent.select("pre").select("code")).isNotNull();
    }
}
