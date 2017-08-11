package org.wildfly.maven.plugins.quickstart.documentation.drupal.hal;

import java.util.Collections;
import java.util.List;

import dk.nykredit.jackson.dataformat.hal.HALLink;
import dk.nykredit.jackson.dataformat.hal.annotation.Link;
import dk.nykredit.jackson.dataformat.hal.annotation.Resource;

@Resource
public class Product {
    @Link
    private HALLink self;

    @Link
    private HALLink type;

    private List<ValueWrapper> uuid;

    public Product(String nid, String uuid) {
        this.uuid = Collections.singletonList(new ValueWrapper(uuid));
        this.self = new HALLink.Builder((String.format("%%drupalLocation%%/node/%s?_format=hal_json", nid))).build();
        this.type = new HALLink.Builder("%drupalLocation%/rest/type/node/product").build();
    }

    public HALLink getSelf() {
        return self;
    }

    public void setSelf(HALLink self) {
        this.self = self;
    }

    public HALLink getType() {
        return type;
    }

    public void setType(HALLink type) {
        this.type = type;
    }

    public List<ValueWrapper> getUuid() {
        return uuid;
    }

    public void addUuid(String uuid) {
        this.uuid.add(new ValueWrapper(uuid));
    }
}
