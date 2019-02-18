package org.wildfly.maven.plugins.quickstart.documentation;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.stream.Collectors;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

/**
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
public class DrupalPushMojoTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Log mavenLog;

    @Mock
    private MavenProject project;

    @Mock
    private Settings settings;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Before
    public void setupClassUnderTest() {
        this.basicWiremockSetup();
    }

    @Test
    public void execute() throws Exception {
        DrupalPushMojo cut = new DrupalPushMojo();
        String serverName = "drupal";

        Server drupalServer = new Server();
        drupalServer.setId(serverName);
        drupalServer.setUsername("username");
        drupalServer.setPassword("password");

        when(project.getVersion()).thenReturn("7.0.0");
        when(settings.getServer(serverName)).thenReturn(drupalServer);

        Field projectField = DrupalPushMojo.class.getDeclaredField("project");
        projectField.setAccessible(true);
        projectField.set(cut, project);

        Field rootDirectoryField = DrupalPushMojo.class.getDeclaredField("rootDirectory");
        rootDirectoryField.setAccessible(true);
        rootDirectoryField.set(cut, new File(this.getClass().getClassLoader().getResource("helloworld").getFile()).getParentFile());

        Field settingsField = DrupalPushMojo.class.getDeclaredField("settings");
        settingsField.setAccessible(true);
        settingsField.set(cut, settings);

        Field drupalUrlField = DrupalPushMojo.class.getDeclaredField("drupalUrl");
        drupalUrlField.setAccessible(true);
        drupalUrlField.set(cut, "http://localhost:8089");

        Field resourceTypeField = DrupalPushMojo.class.getDeclaredField("resourceType");
        resourceTypeField.setAccessible(true);
        resourceTypeField.set(cut, "quickstart");

        Field serverNameField = DrupalPushMojo.class.getDeclaredField("serverName");
        serverNameField.setAccessible(true);
        serverNameField.set(cut, "drupal");

        cut.execute();
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

        givenThat(post("/entity/node?_format=hal_json")
                .withHeader("X-CSRF-Token", new EqualToPattern("mytoken"))
                .withRequestBody(new EqualToJsonPattern("{\"_links\":{\"http://localhost:8089/rest/relation/node/coding_resource/field_related_product\":[{\"href\":\"http://localhost:8089/node/33895?_format=hal_json\",\"templated\":true}],\"http://localhost:8089/rest/relation/node/coding_resource/field_tags\":[],\"type\":{\"href\":\"http://localhost:8089/rest/type/node/coding_resource\",\"templated\":true}},\"_embedded\":{\"http://localhost:8089/rest/relation/node/coding_resource/field_related_product\":[{\"_links\":{\"self\":{\"href\":\"http://localhost:8089/node/33895?_format=hal_json\",\"templated\":true},\"type\":{\"href\":\"http://localhost:8089/rest/type/node/product\",\"templated\":true}},\"uuid\":[{\"value\":\"48a3f108-d582-4507-b168-89619ac708f7\"}],\"machineName\":\"eap\",\"shortName\":\"JBoss EAP\"}],\"http://localhost:8089/rest/relation/node/coding_resource/field_tags\":[]},\"path\":[{\"alias\":\"/quickstarts/helloworld\"}],\"title\":[{\"value\":\"helloworld\"}],\"body\":[{\"value\":\"<a href=\\\"#what-is-it\\\" id=\\\"what-is-it\\\">What is it?</a>\\nThe <code>helloworld</code> quickstart demonstrates a simple CDI portable extension and some of the SPI classes used to complete that task in an application deployed to Red Hat JBoss Enterprise Application Platform. This particular extension explores the <code>ProcessInjectionTarget</code> and <code>InjectionTarget</code> SPI classes of CDI to demonstrate removing a bean from CDI's knowledge and correctly injecting JPA entities in your application.\\nA portable extension is an extension to Java EE 6 and above, which is tailored to a specific use case and will run on any Java EE 6 or later implementation. Portable extensions can implement features not yet supported by the specifications, such as type-safe messages or external configuration of beans.\\nThe project contains very simple domain model classes, an extension class, the service provider configuration file, and an Arquillian test to verify the extension is working correctly.\\nThis quickstart does not contain any user interface. The tests must be run to verify everything is working correctly.\\n<em>Note: This quickstart uses the H2 database included with Red Hat JBoss Enterprise Application Platform 7.1. It is a lightweight, relational example datasource that is used for examples only. It is not robust or scalable, is not supported, and should NOT be used in a production environment!</em>\\n<em>Note: This quickstart uses a <code>*-ds.xml</code> datasource configuration file for convenience and ease of database configuration. These files are deprecated in JBoss EAP and should not be used in a production environment. Instead, you should configure the datasource using the Management CLI or Management Console. Datasource configuration is documented in the <a href=\\\"https://access.redhat.com/documentation/en/red-hat-jboss-enterprise-application-platform/\\\">Configuration Guide</a> for Red Hat JBoss Enterprise Application Platform.</em>\\n<a href=\\\"#system-requirements\\\" id=\\\"system-requirements\\\">System Requirements</a>\\nThe application this project produces is designed to be run on Red Hat JBoss Enterprise Application Platform 7.1 or later.\\nAll you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.3.1 or later. See <a href=\\\"https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN_JBOSS_EAP7.md#configure-maven-to-build-and-deploy-the-quickstarts\\\">Configure Maven for JBoss EAP 7.1</a> to make sure you are configured correctly for testing the quickstarts.\\n<a href=\\\"#use-of-eap7-home\\\" id=\\\"use-of-eap7-home\\\">Use of EAP7_HOME</a>\\nIn the following instructions, replace <code>EAP7_HOME</code> with the actual path to your JBoss EAP installation. The installation path is described in detail here: <a href=\\\"https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/USE_OF_EAP7_HOME.md#use-of-eap_home-and-jboss_home-variables\\\">Use of EAP7_HOME and JBOSS_HOME Variables</a>.\\n<a href=\\\"#start-the-server\\\" id=\\\"start-the-server\\\">Start the Server</a>\\n<li>Open a command prompt and navigate to the root of the JBoss EAP directory.</li> \\n<li>The following shows the command line to start the server: <pre><code>For Linux:   EAP7_HOME/bin/standalone.sh\\nFor Windows: EAP7_HOME\\\\bin\\\\standalone.bat\\n</code></pre> </li>\\n<a href=\\\"#run-the-arquillian-tests\\\" id=\\\"run-the-arquillian-tests\\\">Run the Arquillian Tests</a>\\nThis quickstart provides Arquillian tests. By default, these tests are configured to be skipped as Arquillian tests require the use of a container.\\n<li>Make sure you have started the JBoss EAP server as described above.</li> \\n<li>Open a command prompt and navigate to the root directory of this quickstart.</li> \\n<li>Type the following command to run the test goal with the following profile activated: <pre><code>mvn clean verify -Parq-remote\\n</code></pre> </li>\\nYou can also let Arquillian manage the JBoss EAP server by using the <code>arq-managed</code> profile. For more information about how to run the Arquillian tests, see <a href=\\\"https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/RUN_ARQUILLIAN_TESTS.md#run-the-arquillian-tests\\\">Run the Arquillian Tests</a>.\\n<a href=\\\"#investigate-the-console-output\\\" id=\\\"investigate-the-console-output\\\">Investigate the Console Output</a>\\nMaven prints summary of the 4 performed tests to the console.\\n<code>-------------------------------------------------------\\n T E S T S\\n-------------------------------------------------------\\nRunning org.jboss.as.quickstart.cdi.veto.test.InjectionWithoutVetoExtensionWithManagerTest\\nTests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.492 sec\\nRunning org.jboss.as.quickstart.cdi.veto.test.InjectionWithVetoExtensionAndManagerTest\\nTests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 7.988 sec\\nRunning org.jboss.as.quickstart.cdi.veto.test.InjectionWithVetoExtensionWithoutManagerTest\\nTests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 3.093 sec\\n\\nResults :\\n\\nTests run: 4, Failures: 0, Errors: 0, Skipped: 0\\n</code>\\nIn the server log you see a few lines similar to\\n<code>         INFO  [VetoExtension] (MSC service thread 1-8) Vetoed class class org.jboss.as.quickstart.cdi.veto.model.Car\\n         INFO  [CarManager] (http--127.0.0.1-8080-2) Returning new instance of Car\\n</code>\\nThat will let you know the extension is working. To really see what is going on and understand this example, please read the source and the tests.\\n<a href=\\\"#server-log-expected-warnings-and-errors\\\" id=\\\"server-log-expected-warnings-and-errors\\\">Server Log: Expected Warnings and Errors</a>\\n<em>Note:</em> You will see the following warnings in the server log. You can ignore these warnings.\\n<code>WFLYJCA0091: -ds.xml file deployments are deprecated. Support may be removed in a future version.\\n\\nHHH000431: Unable to determine H2 database version, certain features may not work\\n</code>\\n<a href=\\\"#run-the-quickstart-in-red-hat-jboss-developer-studio-or-eclipse\\\" id=\\\"run-the-quickstart-in-red-hat-jboss-developer-studio-or-eclipse\\\">Run the Quickstart in Red Hat JBoss Developer Studio or Eclipse</a>\\nYou can also start the server and deploy the quickstarts or run the Arquillian tests from Eclipse using JBoss tools. For general information about how to import a quickstart, add a JBoss EAP server, and build and deploy a quickstart, see <a href=\\\"https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/USE_JBDS.md#use-jboss-developer-studio-or-eclipse-to-run-the-quickstarts\\\">Use JBoss Developer Studio or Eclipse to Run the Quickstarts</a>.\\n<a href=\\\"#debug-the-application\\\" id=\\\"debug-the-application\\\">Debug the Application</a>\\nIf you want to debug the source code of any library in the project, run the following command to pull the source into your local repository. The IDE should then detect it.\\n<code>    mvn dependency:sources\\n</code>\",\"format\":\"rhd_html\",\"summary\":\"\"}],\"field_description\":[{\"value\":\"The `helloworld` quickstart is a simple CDI Portable Extension that uses SPI classes to show how to remove beans and inject JPA entities into an application.\",\"format\":\"rhd_html\"}],\"field_author\":[{\"value\":\"Jason Porter\"}],\"field_contributors\":[],\"field_level\":[{\"value\":\"Intermediate\"}],\"field_resource_type\":[{\"value\":\"quickstart\"}],\"field_version\":[{\"value\":\"7.0.0\"}],\"field_technologies\":[{\"value\":\"CDI\"}],\"field_source_link\":[{\"uri\":\"https://github.com/jbossas/eap-quickstarts/\",\"title\":\"\",\"options\":[]}]}",
                        true, false))
                .willReturn(aResponse().withStatus(201))
        );
        givenThat(patch(urlPathEqualTo("/quickstarts/eap/helloworld"))
                .withHeader("X-CSRF-Token", new EqualToPattern("mytoken"))
                .willReturn(aResponse().withStatus(201))
        );
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
