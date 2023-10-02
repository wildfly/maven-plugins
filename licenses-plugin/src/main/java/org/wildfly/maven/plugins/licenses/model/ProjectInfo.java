package org.wildfly.maven.plugins.licenses.model;

import java.util.List;

public class ProjectInfo {

    private List<KnownLicenseInfo> knownLicensesList;
    private List<ProjectLicenseInfo> dependenciesList;

    public List<KnownLicenseInfo> getKnownLicensesList() {
        return knownLicensesList;
    }

    public void setKnownLicensesList(List<KnownLicenseInfo> knownLicensesList) {
        this.knownLicensesList = knownLicensesList;
    }

    public List<ProjectLicenseInfo> getDependenciesList() {
        return dependenciesList;
    }

    public void setDependenciesList(List<ProjectLicenseInfo> dependenciesList) {
        this.dependenciesList = dependenciesList;
    }
}
