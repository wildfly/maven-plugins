package org.wildfly.maven.plugins.licenses;

import org.apache.maven.model.License;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class KnownLicenses {
    private static final String APACHE_1_1_NAME = "Apache License 1.1";
    private static final String APACHE_1_1_URL = "https://www.apache.org/licenses/LICENSE-1.1";
    private static final License APACHE_1_1_LICENSE = createLicense(APACHE_1_1_NAME, APACHE_1_1_URL);

    private static final String APACHE_2_0_NAME = "Apache License 2.0";
    private static final String APACHE_2_0_URL = "https://www.apache.org/licenses/LICENSE-2.0";
    private static final License APACHE_2_0_LICENSE = createLicense(APACHE_2_0_NAME, APACHE_2_0_URL);

    private static final String BSD_2_CLAUSE_NAME = "BSD 2-clause \"Simplified\" License";
    private static final String BSD_2_CLAUSE_URL = "https://opensource.org/license/BSD-2-Clause/";
    private static final License BSD_2_CLAUSE_LICENSE = createLicense(BSD_2_CLAUSE_NAME, BSD_2_CLAUSE_URL);

    private static final String BSD_3_CLAUSE_NAME = "BSD 3-Clause \"New\" or \"Revised\" License";
    private static final String BSD_3_CLAUSE_URL = "https://www.opensource.org/licenses/BSD-3-Clause";
    private static final License BSD_3_CLAUSE_LICENSE = createLicense(BSD_3_CLAUSE_NAME, BSD_3_CLAUSE_URL);

    private static final String CPL_NAME = "Common Public License 1.0";
    private static final String CPL_URL = "https://www.eclipse.org/legal/cpl-v10.html";
    private static final License CPL_LICENSE = createLicense(CPL_NAME, CPL_URL);

    private static final String CC0_NAME = "Creative Commons Zero v1.0 Universal";
    private static final String CC0_URL = "https://creativecommons.org/publicdomain/zero/1.0/legalcode";
    private static final License CC0_LICENSE = createLicense(CC0_NAME, CC0_URL);

    private static final String CC_2_5_NAME = "Creative Commons Attribution 2.5";
    private static final String CC_2_5_URL = "https://creativecommons.org/licenses/by/2.5/legalcode";
    private static final License CC_2_5_LICENSE = createLicense(CC_2_5_NAME, CC_2_5_URL);

    private static final String EDL_1_0_NAME = "Eclipse Distribution License, Version 1.0";
    private static final String EDL_1_0_URL = "https://repository.jboss.org/licenses/edl-1.0.txt";
    private static final License EDL_1_0_LICENSE = createLicense(EDL_1_0_NAME, EDL_1_0_URL);

    private static final String EPL_1_0_NAME = "Eclipse Public License 1.0";
    private static final String EPL_1_0_URL = "https://repository.jboss.org/licenses/epl-1.0.txt";
    private static final License EPL_1_0_LICENSE = createLicense(EPL_1_0_NAME, EPL_1_0_URL);

    private static final String EPL_2_0_NAME = "Eclipse Public License 2.0";
    private static final String EPL_2_0_URL = "https://www.eclipse.org/legal/epl-v20.html";
    private static final License EPL_2_0_LICENSE = createLicense(EPL_2_0_NAME, EPL_2_0_URL);

    private static final String GPL_2_0_W_CPE_NAME = "GNU General Public License v2.0 only, with Classpath exception";
    private static final String GPL_2_0_W_CPE_URL = "https://fedoraproject.org/wiki/Licensing/GPL_Classpath_Exception";
    private static final License GPL_2_0_W_CPE_LICENSE = createLicense(GPL_2_0_W_CPE_NAME, GPL_2_0_W_CPE_URL);

    private static final String LGPL_2_0_ONLY_NAME = "GNU Library General Public License v2 only";
    private static final String LGPL_2_0_ONLY_URL = "https://www.gnu.org/licenses/old-licenses/lgpl-2.0-standalone.html";
    private static final License LGPL_2_0_ONLY_LICENSE = createLicense(LGPL_2_0_ONLY_NAME, LGPL_2_0_ONLY_URL);

    private static final String LGPL_2_1_ONLY_NAME = "GNU Lesser General Public License v2.1 only";
    private static final String LGPL_2_1_ONLY_URL = "https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html";
    private static final License LGPL_2_1_ONLY_LICENSE = createLicense(LGPL_2_1_ONLY_NAME, LGPL_2_1_ONLY_URL);

    private static final String LGPL_2_1_LATER_NAME = "GNU Lesser General Public License v2.1 or later";
    private static final String LGPL_2_1_LATER_URL = "https://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html";
    private static final License LGPL_2_1_LATER_LICENSE = createLicense(LGPL_2_1_LATER_NAME, LGPL_2_1_LATER_URL);

    private static final String LGPL_3_0_ONLY_NAME = "GNU Lesser General Public License v3.0 only";
    private static final String LGPL_3_0_ONLY_URL = "https://www.gnu.org/licenses/lgpl-3.0-standalone.html";
    private static final License LGPL_3_0_ONLY_LICENSE = createLicense(LGPL_3_0_ONLY_NAME, LGPL_3_0_ONLY_URL);

    private static final String LGPL_3_0_LATER_NAME = "GNU Lesser General Public License v3.0 or later";
    private static final String LGPL_3_0_LATER_URL = "https://spdx.org/licenses/LGPL-3.0+.html";
    private static final License LGPL_3_0_LATER_LICENSE = createLicense(LGPL_3_0_LATER_NAME, LGPL_3_0_LATER_URL);

    private static final String INDIANA_UNIVERSITY_EXTREME_NAME = "Indiana University Extreme! Lab Software License 1.1.1";
    private static final String INDIANA_UNIVERSITY_EXTREME_URL = "https://enterprise.dejacode.com/licenses/public/indiana-extreme/?_list_filters=q%3Dindiana%2Bextreme#license-text";
    private static final License INDIANA_UNIVERSITY_EXTREME_LICENSE = createLicense(INDIANA_UNIVERSITY_EXTREME_NAME, INDIANA_UNIVERSITY_EXTREME_URL);

    private static final String MIT_0_NAME = "MIT-0";
    private static final String MIT_0_URL = "https://spdx.org/licenses/MIT-0.html";
    private static final License MIT_0_LICENSE = createLicense(MIT_0_NAME, MIT_0_URL);

    private static final String MIT_NAME = "MIT License";
    private static final String MIT_URL = "https://www.opensource.org/licenses/MIT";
    private static final License MIT_LICENSE = createLicense(MIT_NAME, MIT_URL);

    private static final String MPL_1_1_NAME = "Mozilla Public License 1.1";
    private static final String MPL_1_1_URL = "https://www.mozilla.org/MPL/MPL-1.1.html";
    private static final License MPL_1_1_LICENSE = createLicense(MPL_1_1_NAME, MPL_1_1_URL);

    private static final String MPL_2_0_NAME = "Mozilla Public License 2.0";
    private static final String MPL_2_0_URL = "https://fedoraproject.org/wiki/Licensing/MPLv2.0";
    private static final License MPL_2_0_LICENSE = createLicense(MPL_2_0_NAME, MPL_2_0_URL);

    private static final String PUBLIC_DOMAIN_NAME = "Public Domain";
    private static final License PUBLIC_DOMAIN_LICENSE = createLicense(PUBLIC_DOMAIN_NAME, null);

    private static final Map<String, License> KNOWN_LICENSES = new HashMap<>();

    static {
        KNOWN_LICENSES.put(APACHE_1_1_NAME.toLowerCase(), APACHE_1_1_LICENSE);
        KNOWN_LICENSES.put("apache software license, version 1.1", APACHE_1_1_LICENSE);

        KNOWN_LICENSES.put(APACHE_2_0_NAME.toLowerCase(), APACHE_2_0_LICENSE);
        KNOWN_LICENSES.put("asl 2.0", APACHE_2_0_LICENSE);
        KNOWN_LICENSES.put("apache 2", APACHE_2_0_LICENSE);
        KNOWN_LICENSES.put("apache 2.0", APACHE_2_0_LICENSE);
        KNOWN_LICENSES.put("apache-2.0", APACHE_2_0_LICENSE);
        KNOWN_LICENSES.put("apache 2.0 license", APACHE_2_0_LICENSE);
        KNOWN_LICENSES.put("apache license version 2.0", APACHE_2_0_LICENSE);
        KNOWN_LICENSES.put("apache license, version 2.0", APACHE_2_0_LICENSE);
        KNOWN_LICENSES.put("apache software license, version 2.0", APACHE_2_0_LICENSE);
        KNOWN_LICENSES.put("the apache license, version 2.0", APACHE_2_0_LICENSE);
        KNOWN_LICENSES.put("the apache software license, version 2.0", APACHE_2_0_LICENSE);

        KNOWN_LICENSES.put(BSD_2_CLAUSE_NAME.toLowerCase(), BSD_2_CLAUSE_LICENSE);

        KNOWN_LICENSES.put(BSD_3_CLAUSE_NAME.toLowerCase(), BSD_3_CLAUSE_LICENSE);
        KNOWN_LICENSES.put("bsd-3-clause", BSD_3_CLAUSE_LICENSE);
        KNOWN_LICENSES.put("3-clause bsd license", BSD_3_CLAUSE_LICENSE);

        KNOWN_LICENSES.put(CPL_NAME.toLowerCase(), CPL_LICENSE);
        KNOWN_LICENSES.put("cpl", CPL_LICENSE);

        KNOWN_LICENSES.put(CC0_NAME.toLowerCase(), CC0_LICENSE);
        KNOWN_LICENSES.put("cc0", CC0_LICENSE);
        KNOWN_LICENSES.put("public domain, per creative commons cc0", CC0_LICENSE);

        KNOWN_LICENSES.put(CC_2_5_NAME.toLowerCase(), CC_2_5_LICENSE);

        KNOWN_LICENSES.put(EDL_1_0_NAME.toLowerCase(), EDL_1_0_LICENSE);
        KNOWN_LICENSES.put("eclipse distribution license - v 1.0", EDL_1_0_LICENSE);
        KNOWN_LICENSES.put("eclipse distribution license v. 1.0", EDL_1_0_LICENSE);
        KNOWN_LICENSES.put("eclipse distribution license (new bsd license)", EDL_1_0_LICENSE);
        KNOWN_LICENSES.put("edl 1.0", EDL_1_0_LICENSE);

        KNOWN_LICENSES.put(EPL_1_0_NAME.toLowerCase(), EPL_1_0_LICENSE);
        KNOWN_LICENSES.put("eclipse public license v1.0", EPL_1_0_LICENSE);
        KNOWN_LICENSES.put("eclipse public license - v 1.0", EPL_1_0_LICENSE);
        KNOWN_LICENSES.put("epl 1.0", EPL_1_0_LICENSE);

        KNOWN_LICENSES.put(EPL_2_0_NAME.toLowerCase(), EPL_2_0_LICENSE);
        KNOWN_LICENSES.put("eclipse public license v. 2.0", EPL_2_0_LICENSE);
        KNOWN_LICENSES.put("eclipse public license - v 2.0", EPL_2_0_LICENSE);
        KNOWN_LICENSES.put("epl 2.0", EPL_2_0_LICENSE);
        KNOWN_LICENSES.put("epl-2.0", EPL_2_0_LICENSE);

        KNOWN_LICENSES.put(GPL_2_0_W_CPE_NAME.toLowerCase(), GPL_2_0_W_CPE_LICENSE);
        KNOWN_LICENSES.put("gpl2 w/ cpe", GPL_2_0_W_CPE_LICENSE);
        KNOWN_LICENSES.put("gpl-2.0-with-classpath-exception", GPL_2_0_W_CPE_LICENSE);
        KNOWN_LICENSES.put("gnu general public license, version 2 with the classpath exception", GPL_2_0_W_CPE_LICENSE);
        KNOWN_LICENSES.put("gnu general public license, version 2 with the gnu classpath exception", GPL_2_0_W_CPE_LICENSE);

        KNOWN_LICENSES.put(LGPL_2_0_ONLY_NAME.toLowerCase(), LGPL_2_0_ONLY_LICENSE);

        KNOWN_LICENSES.put(LGPL_2_1_ONLY_NAME.toLowerCase(), LGPL_2_1_ONLY_LICENSE);
        KNOWN_LICENSES.put("lgpl 2.1", LGPL_2_1_ONLY_LICENSE);
        KNOWN_LICENSES.put("the gnu lesser general public license, version 2.1", LGPL_2_1_ONLY_LICENSE);

        KNOWN_LICENSES.put(LGPL_2_1_LATER_NAME.toLowerCase(), LGPL_2_1_LATER_LICENSE);
        KNOWN_LICENSES.put("lgpl 2.1 or later", LGPL_2_1_LATER_LICENSE);
        KNOWN_LICENSES.put("gnu library general public license v2.1 or later", LGPL_2_1_LATER_LICENSE);

        KNOWN_LICENSES.put(LGPL_3_0_ONLY_NAME.toLowerCase(), LGPL_3_0_ONLY_LICENSE);

        KNOWN_LICENSES.put(LGPL_3_0_LATER_NAME.toLowerCase(), LGPL_3_0_LATER_LICENSE);
        KNOWN_LICENSES.put("lesser general public license, version 3 or greater", LGPL_3_0_LATER_LICENSE);

        KNOWN_LICENSES.put(INDIANA_UNIVERSITY_EXTREME_NAME.toLowerCase(), INDIANA_UNIVERSITY_EXTREME_LICENSE);

        KNOWN_LICENSES.put(MIT_0_NAME.toLowerCase(), MIT_0_LICENSE);

        KNOWN_LICENSES.put(MIT_NAME.toLowerCase(), MIT_LICENSE);
        KNOWN_LICENSES.put("bouncy castle licence", MIT_LICENSE);

        KNOWN_LICENSES.put(MPL_1_1_NAME.toLowerCase(), MPL_1_1_LICENSE);
        KNOWN_LICENSES.put("mpl 1.1", MPL_1_1_LICENSE);

        KNOWN_LICENSES.put(MPL_2_0_NAME.toLowerCase(), MPL_2_0_LICENSE);
        KNOWN_LICENSES.put("mpl 2.0", MPL_2_0_LICENSE);

        KNOWN_LICENSES.put(PUBLIC_DOMAIN_NAME.toLowerCase(), PUBLIC_DOMAIN_LICENSE);
    }

    public static Map<String, License> get() {
        return Collections.unmodifiableMap(KNOWN_LICENSES);
    }

    private static License createLicense(String name, String url) {
        License license = new License();
        license.setName(name);
        license.setUrl(url);
        return license;
    }
}
