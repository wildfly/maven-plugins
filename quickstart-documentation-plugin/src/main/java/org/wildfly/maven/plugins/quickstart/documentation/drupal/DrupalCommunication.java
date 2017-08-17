package org.wildfly.maven.plugins.quickstart.documentation.drupal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.nykredit.jackson.dataformat.hal.HALMapper;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.maven.plugin.logging.Log;
import org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal.CodingResource;
import org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal.Product;
import org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal.Tag;

/**
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
public class DrupalCommunication {
    private volatile String csrfToken;
    private Executor executor;
    private volatile List<Product> products;
    private volatile List<Tag> tags;
    private volatile List<SitemapEntry> sitemapEntries;
    private Log log;
    private String drupalLocation;

    public DrupalCommunication(String username, String password, String drupalLocation, Log log) {
        this.executor = Executor.newInstance()
                .auth(new UsernamePasswordCredentials(username, password))
                .authPreemptive(drupalLocation);
        this.log = log;
        this.drupalLocation = drupalLocation;

        try {
            final Response response = executor.execute(Request.Get(drupalLocation + "/session/token"));
            this.csrfToken = response.returnContent().asString();
        } catch (IOException e) {
            this.log.error("Could not obtain a security token from Drupal.", e);
            throw new SecurityException("Could not obtain a token from Drupal. Cannot continue.", e);
        }

        this.products = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.sitemapEntries = new ArrayList<>();
    }

    public List<Product> getProducts() {
        if (!this.products.isEmpty()) {
            return this.products;
        }

        try {
            final Request sitemapRequest = Request.Get(this.drupalLocation + "/drupal/products")
                    .addHeader("X-CSRF-Token", this.csrfToken);
            final String jsonProducts = executor.execute(sitemapRequest).returnContent().asString(Charset.forName("UTF-8"));

            final ObjectMapper mapper = new ObjectMapper();
            this.products = mapper.readValue(jsonProducts, new TypeReference<List<Product>>() {
            });
        } catch (IOException e) {
            this.log.error("Error retrieving products from Drupal", e);
        }

        return this.products;
    }

    public List<Tag> getTags() {
        if (!this.tags.isEmpty()) {
            return this.tags;
        }

        try {
            final Request sitemapRequest = Request.Get(this.drupalLocation + "/drupal/taxonomy/tags")
                    .addHeader("X-CSRF-Token", this.csrfToken);
            final String jsonTags = executor.execute(sitemapRequest).returnContent().asString(Charset.forName("UTF-8"));
            final ObjectMapper mapper = new ObjectMapper();
            this.tags = mapper.readValue(jsonTags, new TypeReference<List<Tag>>() {
            });
        } catch (IOException e) {
            this.log.error("Error retrieving products from Drupal", e);
        }

        return this.tags;
    }

    public boolean postNewCodingResource(CodingResource resource) {
        ObjectMapper halMapper = new HALMapper();
        try {
            String json = halMapper.writeValueAsString(resource);
            json = json.replaceAll("%drupalLocation%", this.drupalLocation);

            Request postQuickstart = Request.Post(String.format("%s/entity/node?_format=hal_json", this.drupalLocation))
                    .addHeader("X-CSRF-Token", this.csrfToken)
                    .addHeader("accept", "*/*")
                    .bodyString(json, ContentType.create("application/hal+json"));

            return executor.execute(postQuickstart).handleResponse(response -> response.getStatusLine().getStatusCode() == 201);
        } catch (IOException e) {
            this.log.error("Error POSTing new coding resource to Drupal", e);
        }

        return false;
    }

    public boolean updateCodingResource(CodingResource resource) {
        ObjectMapper halMapper = new HALMapper();
        try {
            String json = halMapper.writeValueAsString(resource);
            json = json.replaceAll("%drupalLocation%", this.drupalLocation);

            Request postQuickstart = Request.Patch(String.format("%s%s?_format=hal_json", this.drupalLocation, resource.getPath().get(0)))
                    .addHeader("X-CSRF-Token", this.csrfToken)
                    .addHeader("accept", "*/*")
                    .bodyString(json, ContentType.create("application/hal+json"));

            return executor.execute(postQuickstart).handleResponse(response -> response.getStatusLine().getStatusCode() == 200);
        } catch (IOException e) {
            this.log.error("Error POSTing new coding resource to Drupal", e);
        }

        return false;
    }

    public List<SitemapEntry> getEntriesOfType(String type) {
        if (!this.sitemapEntries.isEmpty()) {
            return this.sitemapEntries;
        }

        try {
            final Request sitemapRequest = Request.Get(this.drupalLocation + "/sitemap.xml");
            final InputStream sitemapInputStream;
            sitemapInputStream = executor.execute(sitemapRequest).returnContent().asStream();

            final DrupalSitemapParser parser = new DrupalSitemapParser(sitemapInputStream, this.log);
            this.sitemapEntries = parser.getAllLocationsOfType(type);
        } catch (IOException e) {
            this.log.error(e);
        }

        return this.sitemapEntries;
    }

}
