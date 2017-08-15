package org.wildfly.maven.plugins.quickstart.documentation;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Jason Porter <jporter@redhat.com>
 *         Copyright 2017 Red Hat, Inc. and/or its affiliates.
 */
class MetaData {
    private final String name;
    private String author;
    private String level;
    private String summary;
    private String targetProduct;
    private String source;
    private String prerequisites;
    private String[] technologies;

    public static MetaData parseReadme(Path quickstartDir) throws IOException {
        Path path = quickstartDir.resolve("README.md");
        MetaData metaData = new MetaData(quickstartDir.getFileName().toString());
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            List<String> result = new ArrayList<>();
            for (; ; ) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                result.add(line);
                metaData.parseLine(line);
                if (result.size() > 10) {
                    break;
                }
            }
        }
        return metaData;
    }

    MetaData(String name) {
        this.name = name;
    }

    private void parseLine(String line) {
        if (line.toLowerCase(Locale.US).startsWith("author")) {
            author = line.substring(line.indexOf(" ")).trim();
        } else if (line.toLowerCase(Locale.US).startsWith("technologies")) {
            technologies = line.substring(line.indexOf(" ")).trim().split(",");
        } else if (line.toLowerCase(Locale.US).startsWith("level")) {
            level = line.substring(line.indexOf(" ")).trim();
        } else if (line.toLowerCase(Locale.US).startsWith("summary")) {
            summary = line.substring(line.indexOf(" ")).trim();
        } else if (line.toLowerCase(Locale.US).startsWith("target product")) {
            targetProduct = line.substring(line.indexOf(" ", 14)).trim();
        } else if (line.toLowerCase(Locale.US).startsWith("source")) {
            source = line.substring(line.indexOf(" ")).trim().replaceAll("<", "").replaceAll(">", "");
        } else if (line.toLowerCase(Locale.US).startsWith("prerequisites")) {
            prerequisites = line.substring(line.indexOf(" ")).trim();
        }
    }

    String getTechnologiesAsString() {
        return String.join(", ", Arrays.asList(technologies));
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
