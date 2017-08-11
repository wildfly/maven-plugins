package org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dk.nykredit.jackson.dataformat.hal.HALLink;
import dk.nykredit.jackson.dataformat.hal.annotation.Link;
import dk.nykredit.jackson.dataformat.hal.annotation.Resource;

@Resource
@JsonDeserialize(using = ProductDeserializer.class)
public class Product {
    @Link
    private HALLink self;

    @Link
    private HALLink type;

    private List<ValueWrapper> uuid;
    private String machineName;
    private String shortName;


    public Product(String nid, String uuid) {
        this.uuid = Collections.singletonList(new ValueWrapper(uuid));
        this.self = new HALLink.Builder((String.format("%%drupalLocation%%/node/%s?_format=hal_json", nid))).build();
        this.type = new HALLink.Builder("%drupalLocation%/rest/type/node/product").build();
    }

    public Product(String nid, String uuid, String machineName, String shortName) {
        this(nid, uuid);
        this.machineName = machineName;
        this.shortName = shortName;
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

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(getUuid(), product.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid());
    }
}
