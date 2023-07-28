package replete.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

import replete.collections.Pair;
import replete.collections.RTreeMap;
import replete.collections.mm.MembershipMap;
import replete.errors.RuntimeConvertedException;
import replete.errors.UnicornException;
import replete.io.FileUtil;


public class JarUtil {


    ////////////
    // FIELDS //
    ////////////

    public static final String DEFAULT_PACKAGE = "(default package)";

    public static boolean isJar(File file) {
        String ext = FileUtil.getExtension(file);
        if(!ext.toLowerCase().equals("jar")) {
            return false;
        }
        try(ZipFile zip = new ZipFile(file)) {
            zip.entries();
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    // TODO: Could augment scope of method with developer-provided filters
    public static String[] listClasses(File jarFile) {
        if(!isJar(jarFile)) {
            throw new RuntimeException("Not a JAR file");
        }
        String filter = "[^\\$]*\\.class";
        try(JarFile jar = new JarFile(jarFile)) {
            Set<String> extractedClasses = new TreeSet<>();
            Enumeration<JarEntry> entries = jar.entries();
            while(entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String path = entry.toString();
                if(path.startsWith("META-INF/versions")) {
                    continue;
                }
                if(path.startsWith("module-info")) {
                    continue;
                }
                if(path.matches(filter)) {
                    path = path.replaceAll("/", ".");
                    path = path.replaceAll("\\.class$", "");
                    extractedClasses.add(path);
                }
            }
            return extractedClasses.toArray(new String[0]);
        } catch(Exception e) {
            throw new RuntimeConvertedException(e);
        }
    }

    // Doesn't count packages that only have resources, as this
    // processes the output from listClasses, which only returns
    // classes' fully qualified names.
    public static MembershipMap<String, String> countTopLevelPackages(String[] classes) {
        MembershipMap<String, String> mMap = new MembershipMap<>();
        for(String clazz : classes) {
            String[] parts = clazz.split("\\.");
            if(parts.length == 1) {
//                mMap.addMembership(DEFAULT_PACKAGE, "(root)");  // Not standard and so doesn't count as a package
            } else if(parts.length == 2) {
                mMap.addMembership(parts[0], "(root)");
            } else {
                mMap.addMembership(parts[0], parts[1]);
            }
        }
        return mMap;
    }


    ///////////////
    // ENCLOSING //
    ///////////////

    // Returns the file object representing the JAR file
    // that this .class file is executing within.  If
    // this code is not in a JAR file, null is returned.
    public static File getEnclosingJarFile() {
        try {
            Class<?> thisClass = ClassUtil.getCallingClass(1);

            // The following are examples of the variations that
            // the URL below could be and all need to be
            // appropriately handled.
            //    file:/C:/Documents%20and%20Settings/user/eclipse-ws/proj/src/cc/util/JarUtil.class
            //    jar:file:/C:/Documents%20and%20Settings/user/eclipse-ws/proj/build/deploy/SampleAppInstaller.jar!/cc/util/JarUtil.class
            //    jar:file://server/path/to/jar%20file/SampleAppInstaller.jar!/cc/util/JarUtil.class
            URL url = thisClass.getResource(thisClass.getSimpleName() + ".class");

            if(url.getProtocol().equals("jar")) {

                // Remove the first protocol and the class within.
                String jarPath = url.toString();
                int bangIdx = jarPath.indexOf('!');
                jarPath = jarPath.substring(4, bangIdx);

                // Turn the remaining portion into another URL
                // and ask it for what its file part is.
                URL jarURL = new URL(jarPath);
                String file = jarURL.getFile();

                // If there is a host then most likely the JAR is located
                // on a remote server and the URL is a UNC pathname.
                // Example:  \\server\path\to\file.jar
                if(jarURL.getHost() != null && !jarURL.getHost().equals("")) {
                    file = "//" + jarURL.getHost() + file;
                }

                // Remove the "%20", etc. that will exist in a URL
                // but cannot exist in a File object.
                return new File(URLDecoder.decode(file, "UTF-8"));
            }

            // Not executing from within a JAR file.
            return null;

        // UnsupportedEncodingException & MalformedURLException shouldn't happen
        } catch(Exception e) {
            throw new UnicornException(e);
        }
    }


    ////////////////
    // EXTRACTION //
    ////////////////

    public static File[] extract(File jarFile, File destDir) {
        return extract(jarFile, destDir, null, true, true);
    }
    public static File[] extract(File jarFile, File destDir, boolean overwrite) {
        return extract(jarFile, destDir, null, overwrite, true);
    }
    public static File[] extract(File jarFile, File destDir, String filter) {
        return extract(jarFile, destDir, filter, true, true);
    }
    public static File[] extract(File jarFile, File destDir, String filter, boolean overwrite, boolean keepFullPath) {
        List<File> extractedFiles = new ArrayList<>();
        try(JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            while(entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();

                if(filter != null) {
                    boolean passesFilter = entry.toString().equals(filter) ||
                        entry.toString().matches(filter);

                    if(!passesFilter) {
                        continue;
                    }
                }

                File entryFile = new File(entry.toString());
                File output;
                if(keepFullPath) {
                    output = new File(destDir, entryFile.getPath());
                } else {
                    output = new File(destDir, entryFile.getName());
                }

                if(entry.isDirectory()) {
                    output.mkdirs();
                    continue;
                }

                output.getParentFile().mkdirs();

                if(!output.exists() || overwrite) {
                    InputStream is = jar.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(output);

                    byte[] rgb = new byte[1000];
                    int n;
                    while((n = is.read(rgb)) > -1) {
                        fos.write(rgb, 0, n);
                    }

                    fos.close();
                    is.close();

                    extractedFiles.add(output);
                }
            }
        } catch(Exception e) {
            throw new RuntimeConvertedException(e);
        }

        return extractedFiles.toArray(new File[0]);
    }


    /////////////////////
    // ADD TO PLATFORM //
    /////////////////////

    public static Map<File, Exception> addAllToPlatform(File directory) {
        return addAllToPlatform(directory, 0);
    }
    public static Map<File, Exception> addAllToPlatform(File directory, int maxRecurseDepth) {
        Map<File, Exception> success = new HashMap<>();
        addAllToPlatformInternal(directory, maxRecurseDepth, 0, success);
        return success;
    }
    private static void addAllToPlatformInternal(File directory, int maxRecurseDepth, int curDepth, Map<File, Exception> success) {
        if(directory == null || !directory.isDirectory()) {
            return;
        }
        FileFilter jarFilter = path -> path.isDirectory() || path.getName().endsWith(".jar");
        for(File path : directory.listFiles(jarFilter)) {
            if(path.isDirectory()) {
                if(maxRecurseDepth > curDepth) {
                    addAllToPlatformInternal(path, maxRecurseDepth, curDepth + 1, success);
                }
            } else {
                try {
                    SystemUtil.addPathsToClassPath(path);
                    success.put(path, null);
                } catch(Exception e) {
                    success.put(path, e);
                }
            }
        }
    }


    ///////////////
    // CONFLICTS //
    ///////////////

    public static Pair<Map<File, List<String>>, Map<String, List<File>>> getClassJarMap(List<File> jarPaths) {
        Map<File, List<String>> jarPathClasses = new RTreeMap<>(ArrayList::new);
        Map<String, List<File>> classJarPaths = new RTreeMap<>(ArrayList::new);
        for(File jarPath : jarPaths) {
            String[] classes = listClasses(jarPath);
            jarPathClasses.put(jarPath, Arrays.asList(classes));
            for(String clazz : classes) {
                List<File> paths = classJarPaths.get(clazz);
                paths.add(jarPath);
            }
        }
        return new Pair<>(jarPathClasses, classJarPaths);
    }

    public static Map<Integer, Set<String>> getCounts(Map<String, List<File>> classJarPaths) {
        Map<Integer, Set<String>> counts = new RTreeMap<>(() -> new TreeSet<>());
        for(String clazz : classJarPaths.keySet()) {
            List<File> paths = classJarPaths.get(clazz);
            Set<String> countClasses = counts.get(paths.size());
            countClasses.add(clazz);
        }
        return counts;
    }


    //////////
    // TEST //
    //////////

    private static final String[] projectNames = {
        "Avondale",          //avondale
        "MongoUtils",        //mongo-utils
        "WebComms",          //webcomms
        "YaraJava",          //yara-java
        "Chrono",            //chrono-thirdparty
        "Cortext",           //cortext
        "CortextImage",      //cortext-image
        "CortextImageOcr",   //cortext-image-ocr
        "Kumo",              //kumo-thirdparty
        "Orbweaver",         //orbweaver
        "Replete",           //replete
        "RepleteExternals",  //replete-externals
        "RepletePipeline",   //replete-pipeline
        "RepleteScrutinize", //replete-scrutinize
        "Subtext"            //subtext
    };

    public static void main(String[] args) {
        File f0 = new File("C:\\Users\\dtrumbo\\work\\eclipse-feature-maven\\AvondaleBundler\\target\\avondale-3.5.0-SNAPSHOT\\bin\\lib\\xmpcore-shaded-6.1.11.jar");
        File f1 = new File("C:\\Users\\dtrumbo\\Desktop\\teams-outlook-issue.png");

        System.out.println(isJar(f0));
        System.out.println(isJar(f1));

        if(true) {
            return;
        }

        File fx = new File("C:\\Users\\dtrumbo\\work\\eclipse-2\\Avondale\\target\\avondale-3.5.0-SNAPSHOT\\bin\\lib\\jaxb-api-2.3.0.jar");

        String[] classes2 = listClasses(fx);

        System.out.println(
            Arrays.asList(classes2).stream()
                .collect(Collectors.joining("\n"))
        );

        if(true) {
            return;
        }

//        File jarFile = new File("C:\\Users\\dtrumbo\\work\\eclipse-2\\CortextImage\\lib\\dom4j-1.6.1.jar");
        File jarFile = new File("C:\\Users\\dtrumbo\\work\\eclipse-2\\CortextImage\\lib\\selenium-server-standalone-2.33.0.jar");
        String[] classes = listClasses(jarFile);
        MembershipMap<String, String> mMap = countTopLevelPackages(classes);
        System.out.println(mMap);
        mMap.printTable();
        System.out.println(mMap.toShortString());
        System.out.println(mMap.toLongString());
        if(true) {
            return;
        }

//        File parent = new File("C:\\Users\\dtrumbo\\work\\eclipse-1");
//        List<File> allJarPaths = new ArrayList<>();
//        Arrays.asList(projectNames).stream()
//            .map(n -> new File(parent, n))
//            .map(f -> new File(f, "lib"))
//            .forEach(f -> {
//                List<File> jarPaths = FileUtil.find(f, g -> g.isDirectory() || g.getName().endsWith(".jar"));
//                allJarPaths.addAll(jarPaths);
//            });
        File parent = new File("C:\\Users\\dtrumbo\\work\\eclipse-1\\AvondaleBundler\\build\\deploy\\avondale-3.5.0\\bin\\lib");
//        List<File> allJarPaths = FileUtil.find(parent, g -> g.isDirectory() || g.getName().endsWith(".jar"));
//        File parent = new File("C:/Users/dtrumbo/work/eclipse-2/Avondale/target/avondale-3.5.0-SNAPSHOT/bin/lib");
        List<File> allJarPaths = FileUtil.find(parent, g -> g.isDirectory() || g.getName().endsWith(".jar"));

        Pair<Map<File, List<String>>, Map<String, List<File>>> classJarPaths = getClassJarMap(allJarPaths);

        Map<Integer, Set<String>> counts = getCounts(classJarPaths.getValue2());
        counts.keySet().stream()
            .forEach(k -> System.out.println(k + " => " + counts.get(k).size()));

        classJarPaths.getValue2().keySet().stream()
            .forEach(k -> System.out.println(k + " => " + classJarPaths.getValue2().get(k).stream().map(f -> f.getName()).collect(Collectors.joining(","))));

        counts.keySet().stream()
            .forEach(k -> System.out.println(k + " => " + counts.get(k).size()));

        System.out.println("Total JARs:    " + allJarPaths.size());
        System.out.println("Total Classes: " + classJarPaths.getValue2().size());

//        System.out.println("6  = " + counts.get(6));
//        System.out.println("7 = " + counts.get(7));
//        System.out.println("14 = " + counts.get(14));
//        System.out.println("16 = " + counts.get(16));
//        System.out.println("18 = " + counts.get(18));
//        System.out.println("19 = " + counts.get(19));

//        classJarPaths.keySet().stream()
//            .filter(k -> k.startsWith("org.apache"))
//            .filter(k -> k.contains("httpclient") || k.contains("http") | k.contains("client"))
//            .filter(k -> classJarPaths.get(k).size() > 1)
//            .forEach(k -> System.out.println(k + " => " + classJarPaths.get(k)));
    }
}
