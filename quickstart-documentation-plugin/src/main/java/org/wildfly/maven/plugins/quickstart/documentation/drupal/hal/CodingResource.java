package org.wildfly.maven.plugins.quickstart.documentation.drupal.hal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

    @JsonProperty("field_published_date")
    private List<ValueWrapper> publishDate;

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
        this.publishDate = new ArrayList<>();
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

    public void addPath(String path) {
        this.path.add(new PathWrapper(path));
    }

    public Collection<ValueWrapper> getTitle() {
        return new ArrayList<>(this.title);
    }

    public void addTitle(String title) {
        this.title.add(new ValueWrapper(title));
    }

    public List<TextWrapper> getBody() {
        return new ArrayList<>(this.body);
    }

    public void addBody(String body) {
        this.body.add(new TextWithSummaryWrapper(body, ""));
    }

    public void addRelatedProduct(String nid, String uuid) {
        final Product p = new Product(nid, uuid);
        this.relatedProductLink.add(p.getSelf());
        this.relatedProduct.add(p);
    }

    public void addTag(String tid, String uuid) {
        final Tag t = new Tag(uuid, tid);
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

    public void addAuthor(String value) {
        this.author.add(new ValueWrapper(value));
    }

    public List<ValueWrapper> getAuthor() {
        return new ArrayList<>(this.author);
    }

    public void addContibutor(String value) {
        this.contributors.add(new ValueWrapper(value));
    }

    public List<ValueWrapper> getContributors() {
        return new ArrayList<>(this.contributors);
    }


    public void addLevel(String value) {
        this.level.add(new ValueWrapper(value));
    }

    public List<ValueWrapper> getLevel() {
        return new ArrayList<>(level);
    }

    public void addPublishDate(String value) {
        this.publishDate.add(new ValueWrapper(value));
    }

    public List<ValueWrapper> getPublishDate() {
        return new ArrayList<>(publishDate);
    }

    public void addResourceType(String value) {
        this.resourceType.add(new ValueWrapper(value));
    }

    public List<ValueWrapper> getResourceType() {
        return new ArrayList<>(resourceType);
    }

    public void addVersion(String value) {
        this.version.add(new ValueWrapper(value));
    }

    public List<ValueWrapper> getVersion() {
        return new ArrayList<>(version);
    }

    public void addTechnologies(String value) {
        this.technologies.add(new ValueWrapper(value));
    }

    public List<ValueWrapper> getTechnologies() {
        return new ArrayList<>(technologies);
    }

    public void addSourceLink(String url, String title) {
        this.sourceLink.add(new LinkWrapper(url, title));
    }

    public List<LinkWrapper> getSourceLink() {
        return new ArrayList<>(sourceLink);
    }
}
