package org.wildfly.maven.plugins.quickstart.documentation.drupal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import org.apache.maven.plugin.logging.Log;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal.CodingResource;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
public class CodingResourceGeneratorTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8090);

    @Mock
    private Log mavenLog;

    private String drupalUsername = "drupal";
    private String drupalPassword = "drupal";


    @Test
    public void createResource() throws Exception {
        String drupalLocation = "http://localhost:8090";
        this.basicWiremockSetup();
        DrupalCommunication drupalCommunication = new DrupalCommunication(this.drupalUsername, this.drupalPassword,
                drupalLocation, this.mavenLog);
        CodingResourceGenerator cut = new CodingResourceGenerator(drupalCommunication, mavenLog);

        Path markdown = Paths.get(this.getClass().getClassLoader().getResource("cdi-veto").toURI());
        CodingResource result = cut.createResource(markdown, "quickstart");

        assertThat(result).isNotNull();
        assertThat(result.getPathValue()).isEqualToIgnoringCase("/quickstarts/eap/cdi-veto");
        assertThat(result.getAuthorValue()).isEqualToIgnoringCase("Jason Porter");
        assertThat(result.getTechnologiesValue()).isEqualToIgnoringCase("CDI");
    }

    private void basicWiremockSetup() {
        givenThat(post(urlEqualTo("/user/login"))
                .willReturn(ok()
                        .withHeader("set-cookie", "SESSION=cookie")));

        givenThat(get(urlPathEqualTo("/session/token"))
                .withCookie("SESSION", new EqualToPattern("cookie"))
                .willReturn(ok("mytoken")));

        givenThat(get(urlPathEqualTo("/sitemap.xml"))
                .willReturn(aResponse().withStatus(200).withBody(this.requestBodyFor("sitemap.xml"))));

        givenThat(get(urlPathEqualTo("/drupal/taxonomy/tags"))
                .withHeader("X-CSRF-Token", new EqualToPattern("mytoken"))
                .willReturn(aResponse().withStatus(200).withBody(this.requestBodyFor("tags.json"))));

        givenThat(get(urlPathEqualTo("/drupal/products"))
                .withHeader("X-CSRF-Token", new EqualToPattern("mytoken"))
                .willReturn(aResponse().withStatus(200).withBody(this.requestBodyFor("products.json"))));
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
