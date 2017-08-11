package org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dk.nykredit.jackson.dataformat.hal.HALLink;
import dk.nykredit.jackson.dataformat.hal.annotation.Link;
import dk.nykredit.jackson.dataformat.hal.annotation.Resource;

@Resource
@JsonDeserialize(using = TagDeserializer.class)
public class Tag {
    @Link
    private HALLink self;
    @Link
    private HALLink type;
    private List<ValueWrapper> uuid;
    private String lang;
    private String name;

    public Tag(String uuid, String tid) {
        this(uuid, tid, "en");
    }

    public Tag(String uuid, String tid, String lang, String name) {
        this(uuid, tid, lang);
        this.name = name;
    }

    public Tag(String uuid, String tid, String lang) {
        this.lang = lang;
        this.uuid = Collections.singletonList(new ValueWrapper(uuid));
        this.self = new HALLink.Builder((String.format("%%drupalLocation%%/taxonomy/term/%s?_format=hal_json", tid))).build();
        this.type = new HALLink.Builder("%drupalLocation%/rest/type/taxonomy_term/tags").build();
    }

    public HALLink getSelf() {
        return self;
    }

    public HALLink getType() {
        return type;
    }

    public List<ValueWrapper> getUuid() {
        return new ArrayList<>(uuid);
    }

    public void addUuid(ValueWrapper uuid) {
        this.uuid.add(new ValueWrapper(uuid));
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "uuid=" + uuid.get(0).getValue().toString() +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(getUuid().get(0).getValue(), tag.getUuid().get(0).getValue()) &&
                Objects.equals(getName(), tag.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid().get(0).getValue(), getName());
    }
}
