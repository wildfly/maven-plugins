package org.wildfly.maven.plugins.quickstart.documentation;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
public class MetaDataParserTest {
    @Test
    public void assertFullMetaDataParse() throws Exception {
        Path readmeLocation = Paths.get(this.getClass().getClassLoader().getResource("cdi-veto").getPath());
        final MetaData metaData = MetaData.parseReadme(readmeLocation);

        assertTrue(metaData.getName().equals("cdi-veto"));
        assertTrue(metaData.getAuthor().equals("Jason Porter"));
        assertTrue(metaData.getLevel().equals("Intermediate"));
        assertTrue(metaData.getTargetProduct().equals("JBoss EAP"));
        assertTrue(metaData.getTechnologiesAsString().equals("CDI"));
        assertTrue(metaData.getSummary().equals("The `cdi-veto` quickstart is a simple CDI Portable Extension that uses SPI classes to show how to remove beans and inject JPA entities into an application."));
        assertTrue(metaData.getPrerequisites() == null);
        assertTrue(metaData.getSource().equals("https://github.com/jbossas/eap-quickstarts/"));
    }
}
