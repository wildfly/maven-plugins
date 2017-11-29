package org.wildfly.maven.plugins.quickstart.documentation;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
public class MetaDataParserTest {
    @Test
    public void assertFullMetaDataParse() throws Exception {
        Path readmeLocation = Paths.get(this.getClass().getClassLoader().getResource("helloworld").toURI());
        final MetaData metaData = MetaData.parseReadme(readmeLocation);

        assertThat(metaData.getName().equals("helloworld")).isTrue();
        Assert.assertEquals("Pete Muir", metaData.getAuthor());
        Assert.assertEquals("Beginner", metaData.getLevel());
        Assert.assertEquals("JBoss EAP", metaData.getTargetProduct());
        Assert.assertEquals("CDI, Servlet", metaData.getTechnologiesAsString());
        Assert.assertEquals("The `helloworld` quickstart demonstrates the use of *CDI* and *Servlet 3* and is a good starting point to verify {productName} is configured correctly.", metaData.getSummary());
        Assert.assertEquals("https://github.com/jbossas/eap-quickstarts/", metaData.getSource());
        assertThat(metaData.getPrerequisites()).isNullOrEmpty();
    }
}
