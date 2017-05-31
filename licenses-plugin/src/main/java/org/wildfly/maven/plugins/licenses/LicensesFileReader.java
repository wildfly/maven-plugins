package org.wildfly.maven.plugins.licenses;

import org.apache.maven.model.License;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
   * Read a component-info.xml from an input stream into a ComponentInfo object.
   *
   * @param licSummaryIS Input stream containing the license data
   * @return List of DependencyProject objects
   * @throws IOException                  if there is a problem reading the InputStream
   * @throws ParserConfigurationException if there is a problem parsing the XML stream
   * @throws SAXException                 if there is a problem parsing the XML stream
   */
  public List<ProjectLicenseInfo> parseLicenseSummary(InputStream licSummaryIS)
          throws IOException, ParserConfigurationException, SAXException {
    List<ProjectLicenseInfo> dependencies = new ArrayList<ProjectLicenseInfo>();

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

    return dependencies;

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

}
