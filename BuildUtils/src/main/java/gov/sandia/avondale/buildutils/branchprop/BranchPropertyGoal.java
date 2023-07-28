package gov.sandia.avondale.buildutils.branchprop;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import replete.io.FileUtil;
import replete.text.StringUtil;

@Mojo(name = "branch-property", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true)
public class BranchPropertyGoal extends AbstractMojo {


    ////////////
    // FIELDS //
    ////////////

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void execute() throws MojoExecutionException {
        File baseDir = project.getBasedir();
        File svnDir = new File(baseDir, ".svn");
        File wcDbPath = new File(svnDir, "wc.db");
        getLog().info("Checking SVN Database: " + wcDbPath);
        String branchLabel = getBranchFromSvnWcDb(wcDbPath);
        if(branchLabel == null) {
            File allWcPropsPath = new File(svnDir, "all-wcprops");
            getLog().info("Checking all-wcprops File: " + allWcPropsPath);
            branchLabel = getBranchFromAllWcProps(allWcPropsPath);
        }
        if(branchLabel == null) {
            branchLabel = "(Error)";
        }
        getLog().info("Extracted Branch Label: " + branchLabel);
        project.getProperties().put("svn.branch.label", branchLabel);

        // Code for debugging
//        File outputDir = new File(project.getBuild().getOutputDirectory());
//        File vFile = new File(outputDir, "replete/software.version");
//        System.out.println(vFile + " - Exists? " + vFile.exists());
//        if(vFile.exists()) {
//            System.out.println(FileUtil.getTextContent(vFile));
//        }
    }

    // Given a path to an SVN wc.db file, get the branch
    private String getBranchFromSvnWcDb(File wcDbPath) {
        try {
            if(!FileUtil.isReadableFile(wcDbPath)) {
                getLog().warn("File Not Readable: " + wcDbPath);
                return null;
            }
            String path = wcDbPath.getAbsolutePath();
            path = path.replaceAll("\\\\", "/");
            String reposPath = getFirstReposPath(path);
            if(reposPath == null) {
                getLog().warn("No repository path found: " + path);
                return null;
            }
            String svnBranch = parseSvnBranchName(reposPath);
            if(svnBranch == null) {
                getLog().warn("No SVN branch name found: " + reposPath);
                return null;
            }
            String label = parseBranchLabel(svnBranch);
            if(label == null) {
                getLog().warn("Can't parse out branch label: " + svnBranch);
                return null;
            }
            return label;
        } catch(Exception e) {
            getLog().error("Unknown Error", e);
            return null;
        }
    }

     // Get the "repository path" of the first path in the "NODES" table.
     // Any path will do, as they should all start with:
     //  - "branches/<BRANCH_NAME>/<PROJECT_NAME>"
     private String getFirstReposPath(String path) throws Exception {
         try(Connection cxn = DriverManager.getConnection("jdbc:sqlite:" + path)) {
             Statement stmt = cxn.createStatement();
             ResultSet rs = stmt.executeQuery("select repos_path from NODES limit 1");
             while(rs.next()) {
                 return rs.getString("repos_path");
             }
             return null;
         }
     }

     // Parse out the SVN branch name from the path.  Example string being parsed:
     // - "branches/Avondale_Maven/AvondaleBundler/supplemental/scripts"
     private String parseSvnBranchName(String reposPath) {
         int firstSl = reposPath.indexOf('/');
         if(firstSl == -1) {
             return null;
         }
         int secSl = reposPath.indexOf('/', firstSl + 1);
         if(secSl == -1) {
             return null;
         }
         return reposPath.substring(firstSl + 1, secSl);
     }

     // Parse out the team's short branch label.  Example strings being parsed:
     //  - "Avondale_Maven" -> "Maven"
     //  - "Cortext_For_Avondale_Maven" -> "Maven"
     //  - "Replete_For_Avondale_Maven" -> "Maven"
     private String parseBranchLabel(String svnBranch) {
         String token = "Avondale_";
         int av = svnBranch.indexOf(token);
         if(av == -1) {
             return null;
         }
         return svnBranch.substring(av + token.length());
     }

     private String getBranchFromAllWcProps(File allWcPropsPath) {
         try {
             if(!FileUtil.isReadableFile(allWcPropsPath)) {
                 getLog().warn("File Not Readable: " + allWcPropsPath);
                 return null;
             }
             String[] lines = getLinesFromAllProps(allWcPropsPath);
             String bLine = getBranchesLine(lines);
             if(bLine == null) {
                 getLog().warn("No /branches/ line found");
                 return null;
             }
             String svnBranch = parseSvnBranchNameAllWcProps(bLine);
             if(svnBranch == null) {
                 getLog().warn("No SVN branch name found: " + bLine);
                 return null;
             }
             String label = parseBranchLabel(svnBranch);
             if(label == null) {
                 getLog().warn("Can't parse out branch label: " + svnBranch);
                 return null;
             }
             return label;
         } catch(Exception e) {
             getLog().error("Unknown Error", e);
             return null;
         }
     }

     private String[] getLinesFromAllProps(File allWcPropsPath) {
         String content = FileUtil.getTextContent(allWcPropsPath);
         return StringUtil.lines(content);
     }
     private String getBranchesLine(String[] lines) {
         for(String line : lines) {
             if(line.contains("/branches/")) {
                 return line;
             }
         }
         return null;
     }

     // Parse out the SVN branch name from the path line.  Example string being parsed:
     // - "/svn/repos/cogscitext/!svn/ver/1166/branches/Cortext_For_Avondale_Maven/CortextImage"
     private String parseSvnBranchNameAllWcProps(String line) {
         String branches = "/branches/";
         int firstSl = line.indexOf(branches);
         if(firstSl == -1) {
             return null;
         }
         int secSl = line.indexOf('/', firstSl + branches.length());
         if(secSl == -1) {
             return null;
         }
         return line.substring(firstSl + branches.length(), secSl);
     }


     //////////
    // TEST //
    //////////

     public static void main(String[] args) {
         BranchPropertyGoal g = new BranchPropertyGoal();
         File f = new File("C:\\Users\\dtrumbo\\Downloads\\all-wcprops");
         String label = g.getBranchFromAllWcProps(f);
         System.out.println(label);
     }
}
