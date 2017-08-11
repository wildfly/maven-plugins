package org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal;

import java.io.IOException;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.nykredit.jackson.dataformat.hal.HALMapper;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
            assertFalse("property '_links' is missing", rootNode.path("_links").isMissingNode());
            assertTrue(rootNode.path("_links").size() == 3);
            assertTrue(rootNode.path("_links").hasNonNull("type"));

            assertFalse("property '_embedded' is missing", rootNode.path("_embedded").isMissingNode());
            assertTrue("'_embedded size is not 2", rootNode.path("_embedded").size() == 2);
            assertTrue("'_embedded[].http://127.0.0.1:8888/rest/relation/node/coding_resource/field_tags size is not 2",
                    rootNode.path("_embedded").findPath("http://127.0.0.1:8888/rest/relation/node/coding_resource/field_tags").size() == 2);

            assertFalse("property 'path' is missing", rootNode.path("path").isMissingNode());
            assertTrue("property 'path' is not an array", rootNode.path("path").isArray());
            assertFalse("property 'path[].alias' is missing", rootNode.path("path").findPath("alias").isMissingNode());

            assertFalse("property 'title' is missing", rootNode.path("title").isMissingNode());
            assertTrue("property 'title' is not an array", rootNode.path("title").isArray());
            assertFalse("property 'title[].value' is missing", rootNode.path("title").findPath("value").isMissingNode());

            assertFalse("property 'body' is missing", rootNode.path("body").isMissingNode());
            assertTrue("property 'body' is not an array", rootNode.path("body").isArray());
            assertFalse("property 'body[].value' is missing", rootNode.path("body").findPath("value").isMissingNode());
            assertFalse("property 'body[].format' is missing", rootNode.path("body").findPath("format").isMissingNode());
            assertFalse("property 'body[].summary' is missing", rootNode.path("body").findPath("summary").isMissingNode());

            Stream.of("field_description", "field_author", "field_contributors", "field_level", "field_published_date",
                    "field_resource_type", "field_technologies", "field_version").forEach(s -> {
                assertFalse(String.format("property '%s' is missing", s), rootNode.path(s).isMissingNode());
                assertTrue(String.format("property '%s' is not an array", s), rootNode.path(s).isArray());
                assertFalse(String.format("property '%s[].value' is missing", s), rootNode.path(s).findPath("value").isMissingNode());
            });

            assertFalse("property 'field_source_link' is missing", rootNode.path("field_source_link").isMissingNode());
            assertTrue("property 'field_source_link' is not an array", rootNode.path("field_source_link").isArray());
            assertFalse("property 'field_source_link[].uri' is missing", rootNode.path("field_source_link").findPath("uri").isMissingNode());
            assertFalse("property 'field_source_link[].title' is missing", rootNode.path("field_source_link").findPath("title").isMissingNode());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }
}
