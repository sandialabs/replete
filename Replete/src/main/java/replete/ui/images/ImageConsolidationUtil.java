package replete.ui.images;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import replete.hash.Md5Util;
import replete.io.FileUtil;
import replete.text.StringUtil;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.images.concepts.SharedImageProducer;
import replete.ui.images.shared.SharedImage;
import replete.ui.windows.Dialogs;
import replete.util.OsUtil;
import replete.util.ReflectionUtil;

public class ImageConsolidationUtil {

    private static String[] IMAGE_EXTS = {"gif", "jpg", "jpeg", "png", "tif", "tiff"};
    private static List<String> exts = Arrays.asList(IMAGE_EXTS);
    private static StringBuilder buffer = new StringBuilder();
    private static boolean longMode = true;
    private static Dimension maxDim = new Dimension(40, 40);
    private static boolean examineMode = false;    // true no longer a useful value

    private static void examine() throws IllegalArgumentException, IllegalAccessException {
        File f = new File("C:\\Users\\dtrumbo\\work\\eclipse-main\\RepleteFinio\\examples\\finio\\examples\\rules\\ui\\images");
        File f2 = new File("C:\\Users\\dtrumbo\\work\\eclipse-main\\Replete\\src\\ui\\replete\\ui\\images\\shared");
//        File f2 = new File("C:\\Users\\dtrumbo\\work\\eclipse-main\\JobTaskLinkerPhaseIII\\src\\gov\\sandia\\jtl\\ui\\images");

        Map<File, String> h = new TreeMap<>();
        Map<File, String> h2 = new TreeMap<>();

        for(File fc : f.listFiles()) {
            h.put(fc, Md5Util.getMd5(fc));
        }
        for(File fc2 : f2.listFiles()) {
            h2.put(fc2, Md5Util.getMd5(fc2));
        }

        List<String[]> data = new ArrayList<>();

        for(File x : h.keySet()) {
            String xh = h.get(x);
            for(File y : h2.keySet()) {
                String yh = h2.get(y);
                if(xh.equals(yh)) {
                    String Q = "(UNK)";
                    String R = "(UNK)";
                    for(SharedImage si : SharedImage.values()) {
                        if(si.getFileName().equals(y.getName())) {
                            Q = si.name();
                            Field[] origFields = ReflectionUtil.getFields(CommonConcepts.class);
                            for(int i = 0; i < origFields.length; i++) {
                                Field field = origFields[i];
                                Class<?> fieldType = field.getType();
                                if(ImageModelConcept.class.isAssignableFrom(fieldType)) {
                                    ImageModelConcept concept = (ImageModelConcept) field.get(CommonConcepts.class);
                                    if(concept.getProducer() instanceof SharedImageProducer) {
                                        SharedImageProducer producer = (SharedImageProducer) concept.getProducer();
                                        String fileName = producer.getFileName();
                                        if(fileName.equals(y.getName())) {
                                            R = field.getName();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    data.add(new String[] {
                        x.getName(),
                        y.getName(),
                        "SharedImage." + Q,
                        R.equals("(UNK)") ? R : "CommonConcepts." + R
                    });
                }
            }
        }

        int[] ml = new int[4];
        for(String[] row : data) {
            for(int c = 0; c < row.length; c++) {
                if(row[c].length() > ml[c]) {
                    ml[c] = row[c].length();
                }
            }
        }

        for(String[] row : data) {
            System.out.printf("%-" + ml[0] + "s | %-" + ml[1] + "s | %-" + ml[2] + "s | %-" + ml[3] + "s%n", row[0], row[1], row[2], row[3]);
        }
        System.out.println(" (" + data.size() + " Known Images)");
    }

    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
        if(examineMode) {
            examine();
            return;
        }
        File source = new File("C:\\Users\\dtrumbo\\work\\eclipse-2");
        File target = new File("C:\\Users\\dtrumbo\\Desktop\\allimages");
        target.mkdirs();
        Map<File, File> files = findImages(source, target);
        Map<File, File> reverseFiles = new TreeMap<>();

        Set<String> hashes = new HashSet<>();
        Map<File, String> hashMap = new HashMap<>();
        for(File sourceFile : files.keySet()) {
            File destFile = files.get(sourceFile);
            FileUtil.copy(sourceFile, destFile);
            reverseFiles.put(destFile, sourceFile);
            String md5 = Md5Util.getMd5(sourceFile);
            if(hashes.contains(md5)) {
                System.out.println("Already Have: " + sourceFile);
            }
            hashes.add(md5);
            hashMap.put(destFile, md5);
        }

        Map<String, Integer> projectCounts = new TreeMap<>();

        buffer.append("<html><body>");
        for(File destFile : reverseFiles.keySet()) {
            File sourceFile = reverseFiles.get(destFile);
            String sourcePath = sourceFile.getAbsolutePath();
            sourcePath = StringUtil.removeStart(sourcePath, source.getAbsolutePath() + "\\");
            updateProjectCounts(projectCounts, sourcePath);
            buffer.append("<img src='" + destFile + "' title='" + sourcePath + "'/>\n");
            if(longMode) {
                int i = sourcePath.lastIndexOf('\\');
                String left = sourcePath.substring(0, i + 1);
                String right = sourcePath.substring(i + 1);
                String hash = hashMap.get(destFile);
                String f0 = "<font face='Courier New' color='blue'>";
                String f1 = "</font>";
                buffer.append(" &nbsp;" + left + "<b>" + right + "</b> " + f0 + "(" + hash.substring(hash.length() - 3) + ")" + f1 + "<br>");
            }
        }
        buffer.append(files.size() + " Files Copied<br>");
        buffer.append(hashes.size() + " Unique Files<br>");
        buffer.append((files.size() - hashes.size()) + " Remaining<br>");
        buffer.append("</body></html>");

        File html = new File(target, "_index.html");
        FileUtil.writeTextContent(html, buffer.toString());

        Map<String, String> doneProjects = new HashMap<>();
        doneProjects.put("Avondale",                    "(many), including: large servers, many variations of cog, a few others");
        doneProjects.put("AvondaleBrancher",            "svn.gif");
        doneProjects.put("CognitiveTaskAnalysis",       "a few diagram images from JGraphCustom");
        doneProjects.put("Cortext",                     "calcdv.gif, model-create.gif, word cloud examples & cutouts");
        doneProjects.put("EntityExtraction",            "app.gif (calcdv.gif), blacklist.gif");
        doneProjects.put("GotStyle",                    "lots of custom arrows, neo-*.gif, a few others");
        doneProjects.put("JGraphCustom",                "some jgraph icons");
        doneProjects.put("JobTaskLinker",               "4 large arrows, 1 large check mark, export.gif, linker.gif (logo)");
        doneProjects.put("JobTaskLinkerPhaseII",        "4 large arrows, 1 large check mark, export.gif, linker.gif (logo)");
        doneProjects.put("JobTaskLinkerPhaseIII",       "4 large arrows, 1 large check mark, export.gif, linker.gif (logo)");
        doneProjects.put("Learning",                    "eclipse-icon.gif");
        doneProjects.put("MDetLFA",                     "graph.gif");
        doneProjects.put("Orbweaver",                   "web-big.png & web-small.png (logos), urldoc.gif, loading.gif, empty.gif");
        doneProjects.put("OrientDb",                    "classgroup.gif, orient.png");
        doneProjects.put("PerformanceDecrement",        "arrow.gif, graph.gif (logo), source simulator images");
        doneProjects.put("PerformanceDecrementPhaseII", "arrow.gif, sectask.gif, xray.png (logo)");
        doneProjects.put("Podium",                      "podium.png (logo) sectask.gif, simulator images");
        doneProjects.put("Reframe",                     "custom arrows, adobe reader logo, variations on standard images for rules/hierarchies");
        doneProjects.put("Replete",                     "slice & dice, property set tree, and example icons");
        doneProjects.put("RepleteFinio",                "a lot of custom, specific icons");
        doneProjects.put("RepleteGraph",                "arrow-diag-*.gif, check/dirty/error.png");
        doneProjects.put("RepleteScripting",            "kv.gif");
        doneProjects.put("RunEnsemble",                 "noresults.gif, runensadd.gif");
        doneProjects.put("SpecialStopCondition",        "clock-red.png");
        doneProjects.put("Subtext",                     "subtext.gif (calcdv.gif), Uses a standard icon for its logo though");

        System.out.println(files.size() + " Files Copied");
        System.out.println(hashes.size() + " Unique Files");
        System.out.println((files.size() - hashes.size()) + " Remaining");
        System.out.println();
        int ml = StringUtil.maxLength(projectCounts.keySet());
        for(String project : projectCounts.keySet()) {
            int count = projectCounts.get(project);
            boolean done = doneProjects.containsKey(project);
            String rem = doneProjects.get(project);
            System.out.printf("%3s %-"+ml+"s = %3d  %s%n", done ? "[X]" : "", project, count, done ? rem : "");
        }

        if(Dialogs.showConfirm(null, "Open Web Page?", true)) {
            OsUtil.openSystemEditor(html);
        }
    }

    private static void updateProjectCounts(Map<String, Integer> projectCounts, String sourcePath) {
        int j = sourcePath.indexOf('\\');
        String project = sourcePath.substring(0, j);
        Integer count = projectCounts.get(project);
        if(count == null) {
            count = 0;
        }
        projectCounts.put(project, count + 1);
    }

    public static Map<File, File> findImages(File sourceParent, File destParent) {
        Map<File, File> files = new TreeMap<>();
        findImages(sourceParent, destParent, 0, files);
        return files;
    }

    private static void findImages(File sourceParent, File destParent, int level, Map<File, File> files) {
        for(File sourceChild : sourceParent.listFiles()) {
            if(sourceChild.isDirectory()) {
                if(
                        // Workspace paths
                        sourceChild.getName().equals(".metadata")     && level == 0 ||
                        sourceChild.getName().equals(".recommenders") && level == 0 ||
                        sourceChild.getName().equals("n2a")           && level == 0 ||
                        sourceChild.getName().equals("Images")        && level == 0 ||

                        // Project paths
                        sourceChild.getName().equals(".settings")    && level == 1 ||
                        sourceChild.getName().equals(".svn")         && level == 1 ||
                        sourceChild.getName().equals("bin")          && level == 1 ||
                        sourceChild.getName().equals("target")       && level == 1 ||
                        sourceChild.getName().equals("build")        && level == 1 ||
                        sourceChild.getName().equals("docs")         && level == 1 ||
                        sourceChild.getName().equals("examples")     && level == 1 ||
                        sourceChild.getName().equals("supplemental") && level == 1 ||
                        sourceChild.getAbsolutePath().contains("images\\flags") ||
                        sourceChild.getAbsolutePath().contains("src\\main\\resources\\replete\\ui\\images\\shared")) {
                    continue;
                }

                findImages(sourceChild, destParent, level + 1, files);

            } else {
                String ext = FileUtil.getExtension(sourceChild);
                if(checkSourceFile(sourceChild, ext)) {
                    File destChild = new File(destParent,
                        FileUtil.getNameWithoutExtension(sourceChild) + "-" + files.size() + "." + ext);
                    files.put(sourceChild, destChild);
                }
            }
        }
    }

    private static boolean checkSourceFile(File sourceChild, String ext) {
        if(!sourceChild.isFile()) {
            return false;
        }
        if(!exts.contains(ext)) {
            return false;
        }
        if(sourceChild.getAbsolutePath().contains("images\\neo-")) {
            return false;
        }

        BufferedImage img = null;
        try {
            img = ImageIO.read(sourceChild);
        } catch(IOException e) {
            System.out.println("Error: " + sourceChild + " " + e.getMessage());
            return false;
        }

        if(img.getWidth() > maxDim.width || img.getHeight() > maxDim.height) {
            return false;
        }
        return true;
    }
}
