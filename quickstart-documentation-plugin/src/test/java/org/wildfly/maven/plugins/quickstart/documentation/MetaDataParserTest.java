package org.wildfly.maven.plugins.quickstart.documentation;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
public class MetaDataParserTest {
    @Test
    public void assertFullMetaDataParse() throws Exception {
        Path readmeLocation = Paths.get(this.getClass().getClassLoader().getResource("cdi-veto").toURI());
        final MetaData metaData = MetaData.parseReadme(readmeLocation);

        assertThat(metaData.getName().equals("cdi-veto")).isTrue();
        assertThat(metaData.getAuthor().equals("Jason Porter")).isTrue();
        assertThat(metaData.getLevel().equals("Intermediate")).isTrue();
        assertThat(metaData.getTargetProduct().equals("JBoss EAP")).isTrue();
        assertThat(metaData.getTechnologiesAsString().equals("CDI")).isTrue();
        assertThat(metaData.getSummary().equals("The `cdi-veto` quickstart is a simple CDI Portable Extension that uses SPI classes to show how to remove beans and inject JPA entities into an application.")).isTrue();
        assertThat(metaData.getPrerequisites()).isNullOrEmpty();
        assertThat(metaData.getSource().equals("https://github.com/jbossas/eap-quickstarts/")).isTrue();
    }
}
