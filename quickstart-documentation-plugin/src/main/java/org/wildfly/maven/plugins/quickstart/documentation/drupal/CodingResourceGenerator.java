package org.wildfly.maven.plugins.quickstart.documentation.drupal;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.maven.plugin.logging.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.wildfly.maven.plugins.quickstart.documentation.MetaData;
import org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal.CodingResource;
import org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal.Product;
import org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal.Tag;

/**
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
public class CodingResourceGenerator {

    private Log log;
    private DrupalCommunication drupalCommunication;

    public CodingResourceGenerator(DrupalCommunication drupalCommunication, Log log) {
        this.log = log;
        this.drupalCommunication = drupalCommunication;
    }

    public CodingResource createResource(Path codingResourceDir, String resourceType) {
        try {
            final MetaData metaData = MetaData.parseReadme(codingResourceDir);
            final Optional<Product> targetProduct = drupalCommunication.getProducts().stream().filter(product -> product.getShortName().equals(metaData.getTargetProduct())).findFirst();
            String path = null;
            path = targetProduct
                    .map(product -> "/" + resourceType + "s/" + product.getMachineName() + "/" + codingResourceDir.getFileName())
                    .orElseGet(() -> "/" + resourceType + "s/" + codingResourceDir.getFileName());

            // Pull the body HTML
            InputStream file = new FileInputStream(codingResourceDir.resolve("README.html").toFile());
            Document doc = Jsoup.parse(file, "UTF-8", "");
            Elements docContent = doc.select("h1 ~ *:not(p:first-of-type)");

            final CodingResource newResource = new CodingResource(path, metaData.getName(), docContent.toString());
            newResource.addAuthor(metaData.getAuthor());
            newResource.addDescription(metaData.getSummary());
            newResource.addResourceType(resourceType);
            newResource.addLevel(metaData.getLevel());
            Arrays.stream(metaData.getTechnologies()).map(String::trim).forEach(newResource::addTechnologies);
            newResource.addSourceLink(metaData.getSource(), "");

            targetProduct.ifPresent(newResource::addRelatedProduct);
            final List<Tag> tags = drupalCommunication.getTags().stream().filter(tag -> Arrays.asList(metaData.getTechnologies()).contains(tag.getName())).collect(Collectors.toList());
            tags.forEach(newResource::addTag);

            return newResource;
        } catch (IOException e) {
            this.log.error(e);
        }
        return null; // I don't like returning null, but probably about as good as I can do
    }
}
