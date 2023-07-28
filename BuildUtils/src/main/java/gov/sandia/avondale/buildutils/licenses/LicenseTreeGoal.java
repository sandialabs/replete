package gov.sandia.avondale.buildutils.licenses;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import replete.collections.Pair;
import replete.collections.RArrayList;
import replete.collections.RLinkedHashMap;
import replete.collections.RTreeMap;
import replete.io.FileUtil;
import replete.text.RStringBuilder;
import replete.text.StringUtil;

// This goal processes the output produced by:
//   https://www.mojohaus.org/license-maven-plugin/add-third-party-mojo.html
//   from the add-third-party goal.

// https://opensource.stackexchange.com/questions/2890/what-is-the-meaning-of-two-licenses-in-a-maven-pom

@Mojo(name = "license-tree", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class LicenseTreeGoal extends AbstractMojo {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    private static final String LICENSE_PLUGIN_GROUP_ID    = "org.codehaus.mojo";
    private static final String LICENSE_PLUGIN_ARTIFACT_ID = "license-maven-plugin";
    private static final String LICENSE_PLUGIN_GOAL        = "add-third-party";
    private static final String GOAL_CONFIG_DIR_PROP       = "outputDirectory";
    private static final String GOAL_CONFIG_DIR_DFLT       = "/generated-sources/license"; // Preceded by ${project.build.directory}
    private static final String GOAL_CONFIG_FILENAME_PROP  = "thirdPartyFilename";
    private static final String GOAL_CONFIG_FILENAME_DFLT  = "THIRD-PARTY.txt";

    // Injected by Maven

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void execute() throws MojoExecutionException {
        List<Plugin> buildPlugins = project.getBuildPlugins();
        File thirdPartyLicPath = null;
        for(Plugin plugin : buildPlugins) {
            if(plugin.getGroupId().equals(LICENSE_PLUGIN_GROUP_ID) &&
                    plugin.getArtifactId().equals(LICENSE_PLUGIN_ARTIFACT_ID)) {

                String outputDir = null;
                String outputFileName = null;
                Xpp3Dom config = (Xpp3Dom) plugin.getConfiguration();
                if(config == null) {
                    List<PluginExecution> exes = plugin.getExecutions();
                    for(PluginExecution exe : exes) {
                        for(String goal : exe.getGoals()) {
                            if(goal.equals(LICENSE_PLUGIN_GOAL)) {
                                config = (Xpp3Dom) exe.getConfiguration();
                                break;
                            }
                        }
                    }
                }

                if(config != null) {
                    Xpp3Dom xmlDir = config.getChild(GOAL_CONFIG_DIR_PROP);
                    if(xmlDir != null) {
                        outputDir = xmlDir.getValue();
                    }
                    Xpp3Dom xmlFile = config.getChild(GOAL_CONFIG_FILENAME_PROP);
                    if(xmlFile != null) {
                        outputFileName = xmlFile.getValue();
                    }
                }

                if(outputDir == null) {
                    Build build = project.getBuild();
                    String buildDir = build.getDirectory();
                    outputDir = buildDir + GOAL_CONFIG_DIR_DFLT;
                }

                if(outputFileName == null) {
                    outputFileName = GOAL_CONFIG_FILENAME_DFLT;
                }

                File f = new File(outputDir);
                thirdPartyLicPath = new File(f, outputFileName);
            }
        }

        if(thirdPartyLicPath == null) {
            getLog().info("No license manifest file found to process.");
        } else if(!FileUtil.isReadableFile(thirdPartyLicPath)) {
            getLog().warn("License manifest file does not exist: " + thirdPartyLicPath);
        } else {
            try {
                String content = FileUtil.getTextContent(thirdPartyLicPath);
                getLog().info("License manifest file found to process: " + thirdPartyLicPath);
                processContent(content, thirdPartyLicPath);
            } catch(Exception e) {
                getLog().error("An error occurred reading the content from the license manifest file.", e);
            }
        }
    }


    private void processContent(String content, File thirdPartyLicPath) {
//        System.out.println("Processing License Manifest Content");
//        System.out.println(content);
        String[] lines = StringUtil.lines(content);
        int licCount = 0;
        for(String line : lines) {
            if(line.matches("^\\s*\\(.*$")) {
//                System.out.println("LINE=" + line.trim());

                String[] parts =
                    StringUtil.extractCaptures(line.trim(),
                        "^((?:\\([^)]+\\)\\s*)+)(.*)\\s*\\((.*) - ([^)]+)\\)$");

                String licenses = parts[0].trim();
                String libName = parts[1].trim();
                String libId = parts[2].trim();
                String libUrl = parts[3].trim();

                String[] licParts = StringUtil.extractCaptures2(licenses, "\\([^)]+\\)");
                for(int i = 0; i < licParts.length; i++) {
                    licParts[i] = StringUtil.snip(licParts[i], "(");
                    licParts[i] = StringUtil.cut(licParts[i], ")");
                }

                LibraryReference ref = new LibraryReference(libName, libId, libUrl, licParts);
                addLibaryLicenseToTree(ref);

                licCount++;
            }
        }

        RStringBuilder buffer = new RStringBuilder();
        buffer.appendln("Total licenses analyzed: " + licCount);
        buffer.appendln();
        for(String name : licenses.keySet()) {
            buffer.appendln(name);
            Map<String, List<Pair<LibraryReference, String>>> groupVersions = licenses.get(name);
            for(String v : groupVersions.keySet()) {
                buffer.appendln("    " + v);
                List<Pair<LibraryReference, String>> libraryRefPairs = groupVersions.get(v);
                for(Pair<LibraryReference, String> pair : libraryRefPairs) {
                    LibraryReference ref = pair.getValue1();
                    buffer.appendln("        " + ref.libName + " (" + ref.libId + " - " + ref.libUrl + ")" + (ref.licenseNames.length > 1 ? " [Multiple Licenses]" : ""));
                    buffer.appendln("            \"" + pair.getValue2() + "\"");
                }
            }
        }

        String str = buffer.toString().trim();

        String[] bufferLines = StringUtil.lines(str);
        for(String bl : bufferLines) {
            getLog().info(bl);
        }

        File outputFile = FileUtil.sibling(thirdPartyLicPath, "license-tree.txt");
        FileUtil.writeTextContent(outputFile, str, false, true);
    }

    private class LibraryReference {
        private String libName;
        private String libId;
        private String libUrl;
        private String[] licenseNames;
        public LibraryReference(String libName, String libId, String libUrl,
                                String[] licenseNames) {
            this.libName = libName;
            this.libId = libId;
            this.libUrl = libUrl;
            this.licenseNames = licenseNames;
        }
    }

    private Map<String, Map<String, List<Pair<LibraryReference, String>>>> licenses =
        new RTreeMap<>(() -> new RTreeMap<>(() -> new RArrayList<>()));

    private void addLibaryLicenseToTree(LibraryReference libraryReference) {

        for(String licName : libraryReference.licenseNames) {

            String chosenName = null;
            for(String name : nameJoinMap.keySet()) {
                String[] pats = nameJoinMap.get(name);
                for(String pat : pats) {
                    if(StringUtil.matches(licName, pat, false)) {
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
            String v;
            if(licName.indexOf("1.1") != -1) {
                v = "1.1";
            } else if(licName.indexOf("2.1") != -1) {
                v = "2.1";
            } else if(licName.indexOf('1') != -1) {
                v = "1.0";
            } else if(licName.indexOf('2') != -1) {
                v = "2.0";
            } else if(licName.indexOf('3') != -1 && licName.indexOf("W3C") == -1) {
                v = "3.0";
            } else {
                v = "<VER?>";
            }
            Map<String, List<Pair<LibraryReference, String>>> groupVersions = licenses.get(chosenName);
            List<Pair<LibraryReference, String>> libraries = groupVersions.get(v);
            libraries.add(new Pair<>(libraryReference, licName));
        }
    }

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

}
