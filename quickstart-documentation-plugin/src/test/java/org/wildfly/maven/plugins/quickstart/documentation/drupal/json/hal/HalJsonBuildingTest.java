package org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal;

import java.io.IOException;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.nykredit.jackson.dataformat.hal.HALMapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertTrue;

/**
 * Basic test to assert I am creating the json properly to send to Drupal.
 *
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2075 Red Hat, Inc. and/or its affiliates.
 */
public class HalJsonBuildingTest {
    @Test
    public void assertHalJsonStructure() {
        CodingResource cr = new CodingResource("/quickstarts/test-creating-with-rest", "Testing take 5", "Hello World");
        cr.addRelatedProduct("33900", "48a3f108-d582-4507-b168-89619ac708f7");
        cr.addTag("7635", "846c0bc0-7df1-4b9f-b9a2-ab7a2b9e0939");
        cr.addTag("1155", "59664075-5b7f-4efa-b511-4e69f5b18218");
        cr.addDescription("Some description");
        cr.addAuthor("Jason Porter");
        cr.addContibutor("Pete Muir");
        cr.addContibutor("Sande Gilda");
        cr.addLevel("Intermediate");
        cr.addPublishDate("2017-08-10");
        cr.addResourceType("quickstart");
        cr.addTechnologies("JPA, CDI, Arquillian");
        cr.addVersion("7.0.0.");
        cr.addSourceLink("https://github.com/jboss-developer/jboss-eap-quickstarts", "");

        ObjectMapper halMapper = new HALMapper();
        try {
            String json = halMapper.writerWithDefaultPrettyPrinter().writeValueAsString(cr);
            json = json.replaceAll("%drupalLocation%", "http://127.0.0.1:8888"); // Set the drupal location
            JsonNode rootNode = halMapper.readTree(json);

            // Check the main structure for completeness
            assertThat(rootNode.path("_links").isMissingNode()).as("property '_links' is missing").isFalse();
            assertThat(rootNode.path("_links").size() == 3).isTrue();
            assertThat(rootNode.path("_links").hasNonNull("type")).isTrue();

            assertThat(rootNode.path("_embedded").isMissingNode()).as("property '_embedded' is missing").isFalse();
            assertThat(rootNode.path("_embedded").size() == 2).as("'_embedded size is not 2").isTrue();
            assertTrue("'_embedded[].http://127.0.0.1:8888/rest/relation/node/coding_resource/field_tags size is not 2",
                    rootNode.path("_embedded").findPath("http://127.0.0.1:8888/rest/relation/node/coding_resource/field_tags").size() == 2);

            assertThat(rootNode.path("path").isMissingNode()).as("property 'path' is missing").isFalse();
            assertThat(rootNode.path("path").isArray()).as("property 'path' is not an array").isTrue();
            assertThat(rootNode.path("path").findPath("alias").isMissingNode()).as("property 'path[].alias' is missing").isFalse();

            assertThat(rootNode.path("title").isMissingNode()).as("property 'title' is missing").isFalse();
            assertThat(rootNode.path("title").isArray()).as("property 'title' is not an array").isTrue();
            assertThat(rootNode.path("title").findPath("value").isMissingNode()).as("property 'title[].value' is missing").isFalse();

            assertThat(rootNode.path("body").isMissingNode()).as("property 'body' is missing").isFalse();
            assertThat(rootNode.path("body").isArray()).as("property 'body' is not an array").isTrue();
            assertThat(rootNode.path("body").findPath("value").isMissingNode()).as("property 'body[].value' is missing").isFalse();
            assertThat(rootNode.path("body").findPath("format").isMissingNode()).as("property 'body[].format' is missing").isFalse();
            assertThat(rootNode.path("body").findPath("summary").isMissingNode()).as("property 'body[].summary' is missing").isFalse();

            Stream.of("field_description", "field_author", "field_contributors", "field_level", "field_published_date",
                    "field_resource_type", "field_technologies", "field_version").forEach(s -> {
                assertThat(rootNode.path(s).isMissingNode()).as("property " + s + " is missing").isFalse();
                assertThat(rootNode.path(s).isArray()).as("property " + s + " is not an array").isTrue();
                assertThat(rootNode.path(s).findPath("value").isMissingNode()).as("property " + s + "[].value' is missing").isFalse();
            });

            assertThat(rootNode.path("field_source_link").isMissingNode()).as("property 'field_source_link' is missing").isFalse();
            assertThat(rootNode.path("field_source_link").isArray()).as("property 'field_source_link' is not an array").isTrue();
            assertThat(rootNode.path("field_source_link").findPath("uri").isMissingNode()).as("property 'field_source_link[].uri' is missing").isFalse();
            assertThat(rootNode.path("field_source_link").findPath("title").isMissingNode()).as("property 'field_source_link[].title' is missing").isFalse();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            fail("caught unexpected exception");
        }
    }
}
