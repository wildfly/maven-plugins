package org.wildfly.maven.plugins.quickstart.documentation.drupal;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.maven.plugin.logging.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.wildfly.maven.plugins.quickstart.documentation.MetaData;
import org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal.CodingResource;
import org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal.Product;
import org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal.Tag;

/**
 * @author Jason Porter <jporter@redhat.com>
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
public class CodingResourceGenerator {

    private Log log;
    private DrupalCommunication drupalCommunication;

    public CodingResourceGenerator(DrupalCommunication drupalCommunication, Log log) {
        this.log = log;
        this.drupalCommunication = drupalCommunication;
    }

    private static String escapeNonAscii(String str) {

        StringBuilder retStr = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            int cp = Character.codePointAt(str, i);
            int charCount = Character.charCount(cp);
            if (charCount > 1) {
                i += charCount - 1; // 2.
                if (i >= str.length()) {
                    throw new IllegalArgumentException("truncated unexpectedly");
                }
            }

            if (cp < 128) {
                retStr.appendCodePoint(cp);
            } else {

                System.out.println("cp = " + cp);
                //System.out.println("i = " + Character.);
                //retStr.append(String.format("\\u%x", cp));
            }
        }
        return retStr.toString();
    }

    public String escapeUnicode(String input) {
        StringBuilder b = new StringBuilder(input.length());
        Formatter f = new Formatter(b);
        for (char c : input.toCharArray()) {
            if (c < 128) {
                b.append(c);
            } else {
                System.out.println("c = " + c);
                //f.format("\\u%04x", (int) c);
            }
        }
        return b.toString();
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
            //Elements docContent = doc.select("h1 ~ *:not(p:first-of-type)");
            Element docContent = doc.getElementById("content");
            //String body = URLEncoder.encode(docContent.toString(),"utf-8");
            //String body = escapeUnicode(docContent.html());
            String body = docContent.html();

            final CodingResource newResource = new CodingResource(path, metaData.getName(), body);
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
