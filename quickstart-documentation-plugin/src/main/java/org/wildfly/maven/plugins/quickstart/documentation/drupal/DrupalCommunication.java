package org.wildfly.maven.plugins.quickstart.documentation.drupal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.nykredit.jackson.dataformat.hal.HALMapper;
import org.apache.http.client.CookieStore;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.maven.plugin.logging.Log;
import org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal.CodingResource;
import org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal.Product;
import org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal.Tag;

/**
 * Helper class which encapsulates all communication with Drupal.
 *
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
    private String username;
    private String password;

    public DrupalCommunication(String username, String password, String drupalLocation, Log log) {
        final CookieStore cookieStore = new BasicCookieStore();
        final CloseableHttpClient client = HttpClientBuilder.create()
                .setDefaultCookieStore(cookieStore)
                .build();
        this.executor = Executor.newInstance(client);
        this.log = log;
        this.drupalLocation = drupalLocation;
        this.username = username;
        this.password = password;

        try {
            this.log.debug("Obtaining token from Drupal");
            executor.execute(Request.Post(drupalLocation + "/user/login")
                    .bodyForm(new BasicNameValuePair("name", username),
                            new BasicNameValuePair("pass", password),
                            new BasicNameValuePair("form_id", "user_login_form"))).discardContent();

            if (cookieStore.getCookies().size() < 1) {
                throw new SecurityException("Could not login to Drupal");
            }

            ((BasicClientCookie) cookieStore.getCookies().get(0)).setPath(null);

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

    synchronized List<Product> getProducts() {
        if (!this.products.isEmpty()) {
            return this.products;
        }

        try {
            this.log.debug("Retrieving products from Drupal");
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

    synchronized List<Tag> getTags() {
        if (!this.tags.isEmpty()) {
            return this.tags;
        }

        try {
            this.log.debug("Retrieving tags from Drupal");
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

            this.log.debug("Posting to Drupal using json: " + json);

            Request postQuickstart = Request.Post(String.format("%s/entity/node?_format=hal_json", this.drupalLocation))
                    .addHeader("X-CSRF-Token", this.csrfToken)
                    .addHeader("accept", "*/*")
                    .bodyString(json, ContentType.create("application/hal+json"));

            executor.auth(this.username, this.password);
            executor.authPreemptive(this.drupalLocation);
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

            this.log.debug("Patching existing entry to Drupal using json: " + json);

            Request postQuickstart = Request.Patch(String.format("%s%s?_format=hal_json", this.drupalLocation, resource.getPath().get(0)))
                    .addHeader("X-CSRF-Token", this.csrfToken)
                    .addHeader("accept", "*/*")
                    .bodyString(json, ContentType.create("application/hal+json"));

            executor.auth(this.username, this.password);
            executor.authPreemptive(this.drupalLocation);
            return executor.execute(postQuickstart).handleResponse(response -> response.getStatusLine().getStatusCode() == 200);
        } catch (IOException e) {
            this.log.error("Error POSTing new coding resource to Drupal", e);
        }

        return false;
    }

    public synchronized List<SitemapEntry> getEntriesOfType(String type) {
        if (!this.sitemapEntries.isEmpty()) {
            return this.sitemapEntries;
        }

        try {
            this.log.debug("Obtaining sitemap.xml from Drupal");
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
