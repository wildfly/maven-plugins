package org.wildfly.maven.plugins.quickstart.documentation.drupal;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.apache.maven.plugin.logging.Log;

/**
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
public class DrupalSitemapParser {
    private final InputStream sitemapInputStream;
    private final Log log;

    public DrupalSitemapParser(InputStream sitemapInputStream, Log log) {
        this.sitemapInputStream = sitemapInputStream;
        this.log = log;
    }

    public List<SitemapEntry> getAllLocationsOfType(final String type) {
        final ArrayList<SitemapEntry> sitemapEntries = new ArrayList<>();
        try {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(this.sitemapInputStream);

            // Loop over the xml returned and pull out all the loc from the sitemap looking for quickstarts
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("loc")) {
                    String location = reader.nextEvent().asCharacters().getData();
                    if (location.contains("/" + type)) {
                        // Get the lastmod (hopefully)
                        // basically until the event is a start element for lastmod
                        while (!(reader.peek().isStartElement()
                                && reader.peek().asStartElement().getName().getLocalPart().equals("lastmod"))) {
                            reader.nextEvent();
                        }
                        reader.nextEvent(); // Throw away, we want the text from this tag
                        final String lastmodText = reader.nextEvent().asCharacters().getData();
                        sitemapEntries.add(new SitemapEntry(URI.create(location).getPath(), lastmodText));
                    }
                }
            }
        } catch (XMLStreamException e) { // Don't see this happening, but if it doe,' return an empty list
            this.log.error(e);
            return Collections.emptyList();
        }

        return sitemapEntries;
    }
}
