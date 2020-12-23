package org.wildfly.maven.plugins.quickstart.documentation;

import static org.asciidoctor.Asciidoctor.Factory.create;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;
import org.asciidoctor.ast.Document;

/**
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
public class MetaData {
    private final String name;
    private String author;
    private String level;
    private String summary;
    private String targetProduct;
    private String source;
    private String prerequisites;
    private String[] technologies;
    private boolean openshift;

    public static MetaData parseReadme(Path quickstartDir) throws IOException {
        Path path = quickstartDir.resolve("README.adoc");
        Asciidoctor asciidoctor = create();
        Options options = new Options();
        options.setSafe(SafeMode.UNSAFE);  //to enable includes
        Document doc = asciidoctor.loadFile(path.toFile(), options.map());
        MetaData metaData = new MetaData(quickstartDir.getFileName().toString());
        metaData.setAttributes(doc.getAttributes());
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            boolean shouldReadAbstract = false;
            for (; ; ) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (shouldReadAbstract) {
                    metaData.summary = line;
                    break;
                }
                if ("[abstract]".equals(line.trim())) {
                    shouldReadAbstract = true;
                    continue;
                }

            }
        }
        return metaData;
    }

    private MetaData(String name) {
        this.name = name;
    }

    private void setAttributes(Map<String, Object> attributes) {

        if (attributes.containsKey("author")) {
            author = attributes.get("author").toString();
        }
        if (attributes.containsKey("technologies")) {
            technologies = attributes.get("technologies").toString().split(",");
        } else {
            technologies = new String[]{};
        }
        if (attributes.containsKey("level")) {
            level = attributes.get("level").toString().trim();
        }
        if (attributes.containsKey("productName")) {
            targetProduct = attributes.get("productName").toString().trim();
        }
        if (attributes.containsKey("source")) {
            source = attributes.get("source").toString().trim().replaceAll("<", "").replaceAll(">", "");
        }
        if (attributes.containsKey("prerequisites")) {
            prerequisites = attributes.get("prerequisites").toString().trim();
        }
        if (attributes.containsKey("openshift")) {
            openshift = Boolean.parseBoolean(attributes.get("openshift").toString());
        }
      }

    String getTechnologiesAsString() {
        return Arrays.stream(technologies)
                        .map(String::trim)
                        .collect(Collectors.joining(", "));
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTargetProduct() {
        return targetProduct;
    }

    public void setTargetProduct(String targetProduct) {
        this.targetProduct = targetProduct;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }

    public String[] getTechnologies() {
        return technologies;
    }

    public void setTechnologies(String[] technologies) {
        this.technologies = technologies;
    }

    public boolean isOpenshift() {
        return openshift;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", level='" + level + '\'' +
                ", summary='" + summary + '\'' +
                ", targetProduct='" + targetProduct + '\'' +
                ", source='" + source + '\'' +
                ", prerequisites='" + prerequisites + '\'' +
                ", openshift='" + openshift + '\'' +
                ", technologies=" + Arrays.toString(technologies) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetaData metaData = (MetaData) o;
        return Objects.equals(getName(), metaData.getName()) &&
                Objects.equals(getAuthor(), metaData.getAuthor()) &&
                Objects.equals(getLevel(), metaData.getLevel()) &&
                Objects.equals(getSummary(), metaData.getSummary()) &&
                Objects.equals(getTargetProduct(), metaData.getTargetProduct()) &&
                Objects.equals(getSource(), metaData.getSource()) &&
                Objects.equals(getPrerequisites(), metaData.getPrerequisites()) &&
                Arrays.equals(getTechnologies(), metaData.getTechnologies());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getAuthor(), getLevel(), getSummary(), getTargetProduct(), getSource(), getPrerequisites(), getTechnologies());
    }
}
