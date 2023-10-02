package org.wildfly.maven.plugins.licenses;

import org.apache.maven.model.License;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wildfly.maven.plugins.licenses.model.KnownLicenseInfo;
import org.wildfly.maven.plugins.licenses.model.ProjectInfo;
import org.wildfly.maven.plugins.licenses.model.ProjectLicenseInfo;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LicensesFileReader {

  /**
   * Read a *-licenses.xml from an input stream into a ProjectInfo object.
   *
   * @param licSummaryIS Input stream containing the license data
   * @return ProjectInfo (a list of ProjectLicenseInfo objects and a list of KnownLicenseInfo objects)
   * @throws IOException                  if there is a problem reading the InputStream
   * @throws ParserConfigurationException if there is a problem parsing the XML stream
   * @throws SAXException                 if there is a problem parsing the XML stream
   */
  public ProjectInfo parseLicenseSummary(InputStream licSummaryIS)
          throws IOException, ParserConfigurationException, SAXException {
    ProjectInfo projectInfo = new ProjectInfo();
    List<ProjectLicenseInfo> dependencies = new ArrayList<ProjectLicenseInfo>();
    List<KnownLicenseInfo> knownLicenses = new ArrayList<>();

    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
    Document doc = docBuilder.parse(licSummaryIS);

    // normalize text representation
    doc.getDocumentElement().normalize();
    Element documentElement = doc.getDocumentElement();

    Node dependenciesNode = documentElement.getElementsByTagName("dependencies").item(0);
    NodeList dependencyNodes = dependenciesNode.getChildNodes();

    for (int i = 0; i < dependencyNodes.getLength(); ++i) {
      Node dependencyNode = dependencyNodes.item(i);
      if (dependencyNode.getNodeType() == Node.ELEMENT_NODE) {
        dependencies.add(parseDependencyNode(dependencyNode));
      }
    }

    projectInfo.setDependenciesList(dependencies);

    Node knownLicensesNode = documentElement.getElementsByTagName("knownLicenses").item(0);
    if (knownLicensesNode != null) {
      NodeList licensesNodes = knownLicensesNode.getChildNodes();

      for (int i = 0; i < licensesNodes.getLength(); ++i) {
        Node licenseNode = licensesNodes.item(i);
        if (licenseNode.getNodeType() == Node.ELEMENT_NODE) {
          License license = parseLicenseFromAttrs(licenseNode);
          List<String> aliasList = parseAliases(licenseNode);
          knownLicenses.add(new KnownLicenseInfo(license, aliasList));
        }
      }
    }

    projectInfo.setKnownLicensesList(knownLicenses);

    return projectInfo;

  }

  private ProjectLicenseInfo parseDependencyNode(Node dependencyNode) {
    ProjectLicenseInfo dependency = new ProjectLicenseInfo();
    NodeList depElements = dependencyNode.getChildNodes();
    for (int i = 0; i < depElements.getLength(); ++i) {
      Node node = depElements.item(i);

      if (node.getNodeName().equals("groupId")) {
        dependency.setGroupId(node.getTextContent());
      } else if (node.getNodeName().equals("artifactId")) {
        dependency.setArtifactId(node.getTextContent());
      } else if (node.getNodeName().equals("version")) {
        dependency.setVersion(node.getTextContent());
      } else if (node.getNodeName().equals("licenses")) {
        NodeList licensesChildNodes = node.getChildNodes();
        for (int j = 0; j < licensesChildNodes.getLength(); ++j) {
          Node licensesChildNode = licensesChildNodes.item(j);
          if (licensesChildNode.getNodeName().equals("license")) {
            License license = parseLicense(licensesChildNode);
            dependency.addLicense(license);
          }
        }
      }
    }
    return dependency;
  }

  private License parseLicense(Node licenseNode) {
    License license = new License();
    NodeList licenseElements = licenseNode.getChildNodes();
    for (int i = 0; i < licenseElements.getLength(); ++i) {
      Node node = licenseElements.item(i);
      if (node.getNodeName().equals("name")) {
        license.setName(node.getTextContent());
      } else if (node.getNodeName().equals("url")) {
        license.setUrl(node.getTextContent());
      } else if (node.getNodeName().equals("distribution")) {
        license.setDistribution(node.getTextContent());
      } else if (node.getNodeName().equals("comments")) {
        license.setComments(node.getTextContent());
      }
    }
    return license;
  }

  private License parseLicenseFromAttrs(Node licenseNode) {
    License license = new License();
    NamedNodeMap map = licenseNode.getAttributes();
    license.setName(map.getNamedItem("name").getTextContent());
    license.setUrl(map.getNamedItem("url").getTextContent());
    return license;
  }

  private List<String> parseAliases(Node licenseNode) {
    List<String> aliases = new ArrayList<>();
    NodeList aliasElements = licenseNode.getChildNodes();
    for (int i = 0; i < aliasElements.getLength(); ++i) {
      Node node = aliasElements.item(i);
      aliases.add(node.getTextContent());
    }
    return aliases;
  }
}
