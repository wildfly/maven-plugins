package org.wildfly.maven.plugins.licenses.model;

import org.apache.maven.model.License;

import java.util.List;

public class KnownLicenseInfo {
    private License license;
    private List<String> aliases;

    public KnownLicenseInfo(License license, List<String> aliases) {
        this.license = license;
        this.aliases = aliases;
    }

    public License getLicense() {
        return license;
    }

    public List<String> getAliases() {
        return aliases;
    }
}
