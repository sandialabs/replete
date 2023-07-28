package gov.sandia.avondale.buildutils;


import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import replete.io.FileUtil;
import replete.text.StringUtil;

@Mojo(name = "test", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class TestGoal extends AbstractMojo {


    ////////////
    // FIELDS //
    ////////////

    // Injected by Maven

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(property = "scope")
    private String scope;


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void execute() throws MojoExecutionException {
        System.out.println(">>>>> CUSTOM--PLUGIN!!");
        System.out.println(">>>>> SCOPE=" + scope);
        getLog().info("DUDE SUP! CUSTOM PLUGIN HERE " + getPluginContext());

        Properties p = project.getProperties();
        System.out.println("PRops = " + p);
        System.out.println("reso="+project.getResources());
        System.out.println("compileroots="+project.getCompileSourceRoots());
        File noticeFile = new File(project.getBasedir(), "COPYRIGHT");
        String notice = noticeFile == null ? null : FileUtil.getTextContent(noticeFile, true);
        String dirS = (String) project.getCompileSourceRoots().get(0);
        File dir = new File(dirS);
        String[] extensions = {".java"};
        IOFileFilter filter = new SuffixFileFilter(extensions, IOCase.INSENSITIVE);
        Iterator iter = FileUtils.iterateFiles(dir, filter, TrueFileFilter.INSTANCE);
        Map<File, Integer> tabCounts = new TreeMap<>();
        Map<File, Integer> crCounts = new TreeMap<>();
        while(iter.hasNext()) {
            File file = (File) iter.next();
            getLog().info("CHECKING " + file);
            String content = FileUtil.getTextContent(file, true);
            int tabCount = 0;
            int crCount = 0;
            for(char ch : content.toCharArray()) {
                if(ch == '\t') {
                    tabCount++;
                }
            }
            if(tabCount != 0) {
                tabCounts.put(file, tabCount);
            }

            int a = content.indexOf("package");

            String header = content.substring(0, a);
            String newHeader = null;
            if(notice != null) {
                if(!header.trim().equals(notice.trim())) {
                    if(header.trim().isEmpty()) {
                        newHeader = notice + "\n\n";
                    } else {
                        if(header.startsWith("/**!!!") && header.endsWith(" */")) {
                            newHeader = notice + "\n\n";
                        } else {
                            // most complicated case......
                        }
                    }
                }
            }

            if(newHeader != null) {
                content = newHeader + content;
                getLog().info("WOULD WRITE TO " + file);
//                FileUtil.writeTextContent(file, content, false, true);
            }
        }
        if(tabCounts.isEmpty()) {
            getLog().info("Tab Character Results:");
            for(File f : tabCounts.keySet()) {
                int a = tabCounts.get(f);
                getLog().info("  " + String.format("%4d", a) + "  " + StringUtil.removeStart(f.getAbsolutePath(), dirS));
            }
        }
    }
}
