package org.wildfly.maven.plugins.quickstart.documentation.drupal.hal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dk.nykredit.jackson.dataformat.hal.HALLink;
import dk.nykredit.jackson.dataformat.hal.annotation.Link;
import dk.nykredit.jackson.dataformat.hal.annotation.Resource;

@Resource
public class Tag {
    @Link
    private HALLink self;
    @Link
    private HALLink type;
    private List<ValueWrapper> uuid;
    private String lang;

    public Tag(String uuid, String tid) {
        this(uuid, tid, "en");
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
}
