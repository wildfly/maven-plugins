package org.wildfly.maven.plugins.quickstart.documentation.drupal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import dk.nykredit.jackson.dataformat.hal.HALMapper;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal.CodingResource;
import org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal.Product;
import org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal.Tag;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.unauthorized;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;

/**
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
public class DrupalCommunicationTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);
    @Mock
    private Log mavenLog;

    private String drupalUsername = "drupal";
    private String drupalPassword = "drupal";
    private String drupalLocation = "http://localhost:8089";
    private DrupalCommunication cut;

    @Before
    public void setupClassUnderTest() {
        this.basicWiremockSetup();
        this.cut = new DrupalCommunication(this.drupalUsername, this.drupalPassword, this.drupalLocation, this.mavenLog);
    }

    @Test
    public void assertIncorrectAuthFails() {
        wireMockRule.resetAll();
        givenThat(get(urlEqualTo("/session/token"))
                .withBasicAuth("blah", "blah")
                .willReturn(unauthorized()));

        assertThatExceptionOfType(SecurityException.class)
                .isThrownBy(() -> new DrupalCommunication("blah", "blah", this.drupalLocation, this.mavenLog));
        verify(mavenLog, atMost(1)).error(isA(String.class), isA(SecurityException.class));
    }

    @Test
    public void getProducts() throws Exception {
        givenThat(get(urlPathEqualTo("/drupal/products"))
                .withHeader("X-CSRF-Token", new EqualToPattern("mytoken"))
                .willReturn(aResponse().withStatus(200).withBody(this.requestBodyFor("products.json"))));

        final List<Product> results = this.cut.getProducts();

        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(19);
    }

    @Test
    public void getTags() throws Exception {
        givenThat(get(urlPathEqualTo("/drupal/taxonomy/tags"))
                .withHeader("X-CSRF-Token", new EqualToPattern("mytoken"))
                .willReturn(aResponse().withStatus(200).withBody(this.requestBodyFor("tags.json"))));

        final List<Tag> results = this.cut.getTags();

        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(861);
    }

    @Test
    public void postNewCodingResource() throws Exception {
        CodingResource newResource = getCodingResource();

        ObjectMapper halMapper = new HALMapper();
        String json = halMapper.writeValueAsString(newResource);
        json = json.replaceAll("%drupalLocation%", this.drupalLocation); // Set the drupal location

        givenThat(post("/entity/node?_format=hal_json")
                .withHeader("X-CSRF-Token", new EqualToPattern("mytoken"))
                .withBasicAuth(this.drupalUsername, this.drupalPassword)
                .withRequestBody(new EqualToJsonPattern(json, true, true))
                .willReturn(aResponse().withStatus(201))
        );

        assertThat(this.cut.postNewCodingResource(newResource)).isTrue();
    }

    @Test
    public void updateCodingResource() throws Exception {
        CodingResource newResource = getCodingResource();

        ObjectMapper halMapper = new HALMapper();
        String json = halMapper.writeValueAsString(newResource);
        json = json.replaceAll("%drupalLocation%", this.drupalLocation); // Set the drupal location

        givenThat(patch(new UrlPattern(new EqualToPattern(newResource.getPath().get(0) + "?_format=hal_json"), false))
                .withHeader("X-CSRF-Token", new EqualToPattern("mytoken"))
                .withBasicAuth(this.drupalUsername, this.drupalPassword)
                .withRequestBody(new EqualToJsonPattern(json, true, true))
                .willReturn(aResponse().withStatus(200))
        );

        assertThat(this.cut.updateCodingResource(newResource)).isTrue();
    }

    private CodingResource getCodingResource() {
        CodingResource newResource = new CodingResource("/quickstarts/new-resource", "Hello World", "Check");
        newResource.addTag("12", UUID.randomUUID().toString());
        newResource.addTag("13", UUID.randomUUID().toString());
        newResource.addRelatedProduct("1245", UUID.randomUUID().toString());
        newResource.addAuthor("Jason Porter");
        newResource.addResourceType("quickstart");
        newResource.addContibutor("Luke Dary");
        newResource.addDescription("Pushing a new quickstart");
        newResource.addDescription("Some description");
        newResource.addContibutor("Sande Gilda");
        newResource.addLevel("Intermediate");
        newResource.addPublishDate("2017-08-10");
        newResource.addTechnologies("Web Components, Java, Maven");
        newResource.addVersion("7.0.1.");
        newResource.addSourceLink("https://github.com/jboss-developer/jboss-eap-quickstarts", "");
        return newResource;
    }

    @Test
    public void getEntriesOfType() throws Exception {
        givenThat(get(urlPathEqualTo("/sitemap.xml"))
                .willReturn(aResponse().withStatus(200).withBody(this.requestBodyFor("sitemap.xml"))));

        List<SitemapEntry> entries = this.cut.getEntriesOfType("quickstarts");

        assertThat(entries).isNotEmpty();
    }

    private void basicWiremockSetup() {
        givenThat(get(urlPathEqualTo("/session/token"))
                .withBasicAuth(this.drupalUsername, this.drupalPassword)
                .willReturn(ok("mytoken")));
    }

    private String requestBodyFor(String resourceRequest) {
        InputStream resource = this.getClass().getClassLoader().getResourceAsStream(resourceRequest);
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(resource))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return "";
        }
    }

}
