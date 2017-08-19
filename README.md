# maven-plugins
Various maven plugins

## DrupalPushMojo
This plugin will create/update coding resources (quickstarts, demos, etc) on the developers.redhat.com website.
It will search for the md and html files (both must be present) for the resource.
The following properties are used in the plugin:

* project - The maven project, defaults to `${project}`
* rootDirectory - The root of the project, defaults to `${project.basedir}`
* settings - Maven settings, defaults to `${settings}`
* drupalUrl - Required. URL of the drupal instance
* resourceType - Type of resource being created/updated (quickstart, demo, etc). Defaults to `quickstart`
* serverName - Required. Server id in settings.xml for Drupal authentication storage.

### Usage
Within the `pom.xml` file:

    ...
    <plugin>
        <groupId>org.wildfly.plugins</groupId>
        <artifactId>wildfly-maven-plugin</artifactId>
        <version>${version.wildfly.maven.plugin}</version>
    </plugin>
    ...
    <build>
        <plugin>
            <groupId>com.vladsch.flexmark</groupId>
            <artifactId>markdown-page-generator-plugin</artifactId>
            <executions>
                <execution>
                    <phase>process-resources</phase>
                    <goals>
                        <goal>generate</goal>
                    </goals>
                </execution>
            </executions>
            ...
         </plugin>
         <plugin>
                <groupId>org.wildfly.maven.plugins</groupId>
                <artifactId>quickstart-documentation-plugin</artifactId>
            <executions>
                <execution>
                    <phase>process-resources</phase>
                    <goals>
                        <goal>drupal-push</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <drupalUrl>http://drupal.mycompany.com</drupalUrl>
                <serverName>drupal</serverName>
            </configuration>
         </plugin>
    </build>
    
NOTE: This plugin must be placed after any other plugins that generate the HTML from markdown!
    
Your `settings.xml` file will need to have a corresponding `server` entry with the username and password set for the Drupal server.

