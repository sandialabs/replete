package gov.sandia.avondale.buildutils.licenses;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import replete.collections.Pair;
import replete.collections.RArrayList;
import replete.collections.RLinkedHashMap;
import replete.collections.RTreeMap;
import replete.io.FileUtil;
import replete.text.RStringBuilder;
import replete.text.StringUtil;

// https://spdx.org/licenses/
// https://maven.apache.org/pom.html#Licenses

public class LicenseParser {
    private static Map<String, String[]> nameJoinMap = new RLinkedHashMap<>(

        // http://www.apache.org/licenses/LICENSE-2.0
        "Apache", new String[] {
            ".*apache.*",
            ".*asf.*",
            ".*asl.*"
        },

        // https://www.eclipse.org/org/documents/edl-v10.php
        "Eclipse Distribution License", new String[] {
            ".*eclipse dist.*",
            ".*EDL.*"
        },

        // https://www.eclipse.org/legal/epl-2.0/
        "Eclipse Public License", new String[] {
            ".*eclipse.*",
            ".*EPL.*"
        },

        // https://opensource.org/licenses/BSD-3-Clause
        // https://en.wikipedia.org/wiki/BSD_licenses
        "BSD", new String[] {
            ".*BSD.*"
        },

        // https://en.wikipedia.org/wiki/Public-domain_software
        "Public Domain", new String[] {
            ".*public domain.*"
        },

        // https://www.gnu.org/licenses/lgpl-3.0.html
        "LGPL", new String[] {
            ".*lesser.*",
            ".*lgpl.*"
        },

        // https://www.gnu.org/licenses/agpl-3.0.html
        "Affero GPL", new String[] {
            ".*affero.*",
            ".*agpl.*"
        },

        // https://www.gnu.org/licenses/old-licenses/gpl-2.0.html
        // https://www.gnu.org/licenses/gpl-3.0.html
        "GPL", new String[] {
            ".*gpl.*"
        },

        // https://www.mozilla.org/en-US/MPL/1.1/
        // https://www.mozilla.org/en-US/MPL/2.0/
        "Mozilla Public License", new String[] {
            ".*mpl.*",
            ".*Mozilla.*"
        },

        // https://en.wikipedia.org/wiki/MIT_License
        "MIT License", new String[] {
            ".*MIT.*"
        },

        // https://opensource.org/licenses/CDDL-1.0
        "Common Development and Distribution License", new String[] {
            ".*Common Development and Distribution License.*",
            ".*cddl.*"
        },
        "Individual", new String[] {
            ".*jython.*",           // https://github.com/jython/jython/blob/master/LICENSE.txt
            ".*unrar.*",            // https://github.com/jukka/java-unrar/blob/master/license.txt
            ".*indiana univ.*",     // https://enterprise.dejacode.com/licenses/public/indiana-extreme/
            ".*bouncy castle.*",    // https://www.bouncycastle.org/licence.html
            ".*w3c.*",              // https://search.maven.org/artifact/xml-apis/xml-apis/1.4.01/jar
            ".*sax lic.*",          // https://search.maven.org/artifact/xml-apis/xml-apis/1.4.01/jar
            "OGC Copyright"         // https://www.ogc.org/ogc/legal
        }
    );

    List<String> ours = new RArrayList<>(
        "Avondale",
        "AvondaleBundler",
        "Cortext",
        "Cortext Code",
        "Cortext Image",
        "Cortext Image OCR",
        "Mongo Utils",
        "Orbweaver",
        "Replete",
        "Replete Externals",
        "Replete Pipeline",
        "WebComms",
        "YaraJava"
    );

    public static void main(String[] args) throws IOException {
        String allLicStr = FileUtil.getTextContent(LicenseParser.class.getResourceAsStream("all-licenses.txt"));
        String[] lines2 = StringUtil.lines(allLicStr);
        Map<String, Map<String, List<String>>> licenses =
            new RTreeMap<>(() -> new RTreeMap<>(() -> new RArrayList<>()));
        Arrays.stream(lines2)
            .filter(l -> !StringUtil.isBlank(l))
            .map(l -> {
                int c = l.indexOf(':');
                String left = c == -1 ? l.trim() : l.substring(0, c).trim();
                String right = c == -1 ? null : l.substring(c + 1).trim();
//                String[] libs = right.split("\\s*,\\s*");
//                for(String lb : libs) {
//                    System.out.println(lb);
//                }
                return new Pair<String, String>(left, right);
            })
            .forEach(p -> {
                String chosenName = null;
                for(String name : nameJoinMap.keySet()) {
                    String[] pats = nameJoinMap.get(name);
                    for(String pat : pats) {
                        if(StringUtil.matches(p.getValue1(), pat, false)) {
                            chosenName = name;
                            break;
                        }
                    }
                    if(chosenName != null) {
                        break;
                    }
                }
                if(chosenName == null) {
                    chosenName = "<UNK>";
                }
                Map<String, List<String>> groupVersions = licenses.get(chosenName);
                String v;
                if(p.getValue1().indexOf("1.1") != -1) {
                    v = "1.1";
                } else if(p.getValue1().indexOf("2.1") != -1) {
                    v = "2.1";
                } else if(p.getValue1().indexOf('1') != -1) {
                    v = "1.0";
                } else if(p.getValue1().indexOf('2') != -1) {
                    v = "2.0";
                } else if(p.getValue1().indexOf('3') != -1 && p.getValue1().indexOf("W3C") == -1) {
                    v = "3.0";
                } else {
                    v = "<VER?>";
                }
                List<String> libraries = groupVersions.get(v);
                libraries.add(p.getValue2() + " (" + p.getValue1() + ")");
            });

        RStringBuilder buffer = new RStringBuilder();
        for(String name : licenses.keySet()) {
            buffer.appendln(name);
            Map<String, List<String>> groupVersions = licenses.get(name);
            for(String v : groupVersions.keySet()) {
                buffer.appendln("    " + v);
                List<String> libraries = groupVersions.get(v);
                for(String lib : libraries) {
                    buffer.appendln("        " + lib);
                }
            }
        }

        FileUtil.writeTextContent(new File("C:\\Users\\dtrumbo\\work\\eclipse-feature-maven\\LicenseMavenPlugin\\src\\main\\resources\\gov\\sandia\\avondale\\maven\\licenses\\org-licenses.txt"), buffer.toString());
    }
}
