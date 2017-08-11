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
public class ProductDeserializer extends StdDeserializer<Product> {
    public ProductDeserializer() {
        this(null);
    }

    public ProductDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Product deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode rootNode = p.getCodec().readTree(p);

        String uuid = rootNode.path("uuid").textValue();
        String nid = rootNode.path("nid").textValue();
        String shortName = rootNode.path("field_product_short_name").textValue();
        String machineName = rootNode.path("field_product_machine_name").textValue();

        return new Product(nid, uuid, machineName, shortName);
    }
}
