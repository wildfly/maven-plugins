package org.wildfly.maven.plugins.quickstart.documentation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Tomaz Cerar (c) 2017 Red Hat Inc.
 */
public class TOCGenerator {
    //private static final List<String> IGNORED_DIRS = Arrays.asList("target", "dist", "template", "guide");
    private final List<String> ignoredDirs;

    public static void main(String[] args) throws IOException {
        Path root = Paths.get(".").normalize();
      new TOCGenerator(Arrays.asList("target", "dist", "template", "guide")).generate(root, "[TOC-quickstart]", Paths.get("README.adoc"), false);
    }

    public TOCGenerator(List<String> ignoredDirs) {
        this.ignoredDirs = ignoredDirs;
    }

    public void generate(Path root, String tocMarker, Path targetDoc, boolean includeOpenshift) throws IOException {
        Set<MetaData> allMetaData = new TreeSet<>(Comparator.comparing(MetaData::getName));
        try (DirectoryStream<Path> dirs = Files.newDirectoryStream(root, entry -> Files.isDirectory(entry)
            && (!entry.getFileName().toString().startsWith("."))
            && (!ignoredDirs.contains(entry.getFileName().toString())))
        ) {
            dirs.forEach(path -> {
                if (Files.exists(path.resolve("README.adoc"))){
                    try {
                        allMetaData.add(MetaData.parseReadme(path));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    System.out.println(String.format("Directory %s doesn't contain README.adoc, skipping", path));
                }
            });
        }
        StringBuffer sb = generateTOC(allMetaData, includeOpenshift);
        Path tocFile = root.resolve(targetDoc);
        String tocFileContent = new String(Files.readAllBytes(tocFile), StandardCharsets.UTF_8);
        tocFileContent = tocFileContent.replaceAll("<TOC>[\\s\\S]*<\\/TOC>", "<TOC>\n"+sb.toString()+"\n//</TOC>");
        Files.write(tocFile, tocFileContent.getBytes(StandardCharsets.UTF_8));
    }

    private static StringBuffer generateTOC(Set<MetaData> metaDataList, boolean includeOpenshift) {
        /*

| Tables        | Are           | Cool  |
| ------------- |:-------------:| -----:|
| col 3 is      | right-aligned | $1600 |
         */
        StringBuffer sb = new StringBuffer();
        if (includeOpenshift) {
            sb.append("[cols=\"1,1,1,2,1,1\", options=\"header\"]\n");
        } else {
            sb.append("[cols=\"1,1,2,1,1\", options=\"header\"]\n");
        }
        sb.append("|===\n");
        if (includeOpenshift) {
            sb.append("| Quickstart Name | Demonstrated Technologies | Openshift Compatible | Description | Experience Level Required | Prerequisites \n");
        } else {
            sb.append("| Quickstart Name | Demonstrated Technologies | Description | Experience Level Required | Prerequisites \n");
        }

        //sb.append("| --- | --- | --- | --- | --- \n");
        for (MetaData md : metaDataList) {
            sb.append("| ")
                    .append("link:").append(md.getName()).append("/README{outfilesuffix}[").append(md.getName()).append("]|")
                    .append(md.getTechnologiesAsString()).append(" | ")
                    .append(includeOpenshift ? (md.isOpenshift() ? "Yes | " : "No | ") : "")
                    .append(md.getSummary()).append(" | ")
                    .append(md.getLevel()).append(" | ")
                    .append(md.getPrerequisites() == null ? "_none_" : md.getPrerequisites())
                .append("\n");

        }
        sb.append("|===");

        return sb;
    }
}
