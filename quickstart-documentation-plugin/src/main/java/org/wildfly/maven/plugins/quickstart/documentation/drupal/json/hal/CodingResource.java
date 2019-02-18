package org.wildfly.maven.plugins.quickstart.documentation.drupal.json.hal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.nykredit.jackson.dataformat.hal.HALLink;
import dk.nykredit.jackson.dataformat.hal.annotation.EmbeddedResource;
import dk.nykredit.jackson.dataformat.hal.annotation.Link;
import dk.nykredit.jackson.dataformat.hal.annotation.Resource;

@Resource
public class CodingResource {
    @Link
    private HALLink type;

    @EmbeddedResource("%drupalLocation%/rest/relation/node/coding_resource/field_related_product")
    private List<Product> relatedProduct;

    @EmbeddedResource("%drupalLocation%/rest/relation/node/coding_resource/field_tags")
    private List<Tag> fieldTags;

    @Link("%drupalLocation%/rest/relation/node/coding_resource/field_related_product")
    private List<HALLink> relatedProductLink;

    @Link("%drupalLocation%/rest/relation/node/coding_resource/field_tags")
    private List<HALLink> tagsLink;

    private List<PathWrapper> path;
    private List<ValueWrapper> title;
    private List<TextWithSummaryWrapper> body;

    @JsonProperty("field_description")
    private List<TextWrapper> description;

    @JsonProperty("field_author")
    private List<ValueWrapper> author;

    @JsonProperty("field_contributors")
    private List<ValueWrapper> contributors;

    @JsonProperty("field_level")
    private List<ValueWrapper> level;

    @JsonProperty("field_resource_type")
    private List<ValueWrapper> resourceType;

    @JsonProperty("field_version")
    private List<ValueWrapper> version;

    @JsonProperty("field_technologies")
    private List<ValueWrapper> technologies;

    @JsonProperty("field_source_link")
    private List<LinkWrapper> sourceLink;

    public CodingResource(String path, String title, String body) {
        this.path = Collections.singletonList(new PathWrapper(path));
        this.title = Collections.singletonList(new ValueWrapper(title));
        this.body = Collections.singletonList(new TextWithSummaryWrapper(body, ""));
        this.description = new ArrayList<>();
        this.author = new ArrayList<ValueWrapper>();
        this.contributors = new ArrayList<>();
        this.resourceType = new ArrayList<>();
        this.version = new ArrayList<>();
        this.level = new ArrayList<>();
        this.technologies = new ArrayList<>();
        this.sourceLink = new ArrayList<>();

        this.type = new HALLink.Builder("%drupalLocation%/rest/type/node/coding_resource").build();
        this.relatedProductLink = new ArrayList<>();
        this.tagsLink = new ArrayList<>();

        this.relatedProduct = new ArrayList<>();
        this.fieldTags = new ArrayList<>();
    }

    public HALLink getType() {
        return type;
    }

    public void setType(HALLink type) {
        this.type = type;
    }

    public List<HALLink> getRelatedProductLink() {
        return relatedProductLink;
    }

    public void setRelatedProductLink(List<HALLink> relatedProductLink) {
        this.relatedProductLink = relatedProductLink;
    }

    public List<HALLink> getTagsLink() {
        return new ArrayList<>(tagsLink);
    }

    public void setTagsLink(List<HALLink> tagsLink) {
        this.tagsLink = tagsLink;
    }

    public List<PathWrapper> getPath() {
        return new ArrayList<>(this.path);
    }

    @JsonIgnore
    public String getPathValue() {
        if (this.path.isEmpty()) {
            return "";
        }
        return this.path.get(0).getAlias();
    }

    public void addPath(String path) {
        this.path.add(new PathWrapper(path));
    }

    public Collection<ValueWrapper> getTitle() {
        return new ArrayList<>(this.title);
    }

    @JsonIgnore
    public String getTitleValue() {
        if (this.title.isEmpty()) {
            return "";
        }
        return this.title.get(0).getValue();
    }

    public void addTitle(String title) {
        this.title.add(new ValueWrapper(title));
    }

    public List<TextWrapper> getBody() {
        return new ArrayList<>(this.body);
    }

    @JsonIgnore
    public String getBodyValue() {
        if (this.body.isEmpty()) {
            return "";
        }
        return this.body.get(0).getValue();
    }

    public void addBody(String body) {
        this.body.add(new TextWithSummaryWrapper(body, ""));
    }

    public void addRelatedProduct(Product p) {
        this.relatedProductLink.add(p.getSelf());
        this.relatedProduct.add(p);
    }

    public void addRelatedProduct(String nid, String uuid) {
        final Product p = new Product(nid, uuid);
        this.addRelatedProduct(p);
    }

    public void addTag(String tid, String uuid) {
        final Tag t = new Tag(uuid, tid);
        this.addTag(t);
    }

    public void addTag(Tag t) {
        this.fieldTags.add(t);
        this.tagsLink.add(t.getSelf());
    }

    public List<Product> getRelatedProduct() {
        return new ArrayList<>(this.relatedProduct);
    }

    public List<Tag> getFieldTags() {
        return fieldTags;
    }

    public void addDescription(String value) {
        this.description.add(new TextWrapper(value));
    }

    public List<TextWrapper> getDescription() {
        return new ArrayList<>(this.description);
    }

    @JsonIgnore
    public String getDescriptionValue() {
        if (this.description.isEmpty()) {
            return "";
        }
        return this.description.get(0).getValue();
    }

    public void addAuthor(String value) {
        this.author.add(new ValueWrapper(value));
    }

    public List<ValueWrapper> getAuthor() {
        return new ArrayList<>(this.author);
    }

    @JsonIgnore
    public String getAuthorValue() {
        if (this.author.isEmpty()) {
            return "";
        }
        return this.author.get(0).getValue();
    }

    public void addContibutor(String value) {
        this.contributors.add(new ValueWrapper(value));
    }

    public List<ValueWrapper> getContributors() {
        return new ArrayList<>(this.contributors);
    }

    @JsonIgnore
    public String getContributorValue() {
        if (this.contributors.isEmpty()) {
            return "";
        }
        return this.contributors.get(0).getValue();
    }

    public void addLevel(String value) {
        this.level.add(new ValueWrapper(value));
    }

    public List<ValueWrapper> getLevel() {
        return new ArrayList<>(level);
    }

    @JsonIgnore
    public String getLevelValue() {
        if (this.level.isEmpty()) {
            return "";
        }
        return this.level.get(0).getValue();
    }

    public void addResourceType(String value) {
        this.resourceType.add(new ValueWrapper(value));
    }

    public List<ValueWrapper> getResourceType() {
        return new ArrayList<>(resourceType);
    }

    @JsonIgnore
    public String getResourceTypeValue() {
        if (this.resourceType.isEmpty()) {
            return "";
        }
        return this.resourceType.get(0).getValue();
    }

    public void addVersion(String value) {
        this.version.add(new ValueWrapper(value));
    }

    public List<ValueWrapper> getVersion() {
        return new ArrayList<>(version);
    }

    @JsonIgnore
    public String getVersionValue() {
        if (this.version.isEmpty()) {
            return "";
        }
        return this.version.get(0).getValue();
    }

    public void addTechnologies(String value) {
        if (this.technologies.size() > 0) {
            final String joined = String.join(", ", Arrays.asList(this.technologies.get(0).getValue(), value));
            this.technologies.set(0, new ValueWrapper(joined));
        } else {
            this.technologies.add(new ValueWrapper(value));
        }
    }

    public List<ValueWrapper> getTechnologies() {
        return new ArrayList<>(technologies);
    }

    @JsonIgnore
    public String getTechnologiesValue() {
        if (this.technologies.isEmpty()) {
            return "";
        }
        return this.technologies.get(0).getValue();
    }

    public void addSourceLink(String url, String title) {
        this.sourceLink.add(new LinkWrapper(url, title));
    }

    public List<LinkWrapper> getSourceLink() {
        return new ArrayList<>(sourceLink);
    }

    @JsonIgnore
    public String getSourceValue() {
        if (this.sourceLink.isEmpty()) {
            return "";
        }
        return this.sourceLink.get(0).getUri();
    }
}
