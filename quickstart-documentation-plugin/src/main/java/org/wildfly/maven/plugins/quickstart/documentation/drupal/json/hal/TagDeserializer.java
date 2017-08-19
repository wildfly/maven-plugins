package org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
public class TagDeserializer extends StdDeserializer<Tag> {

    public TagDeserializer() {
        this(null);
    }

    public TagDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Tag deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode rootNode = p.getCodec().readTree(p);

        String uuid = rootNode.path("uuid").textValue();
        String tid = rootNode.path("tid").textValue();
        String name = rootNode.path("name").textValue();

        Tag t = new Tag(uuid, tid, "en", name);
        return t;
    }
}
