package replete.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import replete.errors.RuntimeConvertedException;

public class ClassUtil {


    ///////////////////
    // CLASS CONTEXT //
    ///////////////////

    public static Class<?>[] getClassContext() {
        return new OpenContextSecurityManager().getClassContext();
    }

    // Must only be called by client code, no method overloading.
    public static Class<?> getCallingClass() {
        return getCallingClass(1);    // 1 = skips itself
    }
    public static Class<?> getCallingClass(int skip) {
        Class<?>[] classes = getClassContext();
        int CU_SM = 1;                          // replete.util.ClassUtil$OpenContextSecurityManager
        int CU_1  = 1;                          // replete.util.ClassUtil/getClassContext
        int CU_2  = 1;                          // replete.util.ClassUtil/getCallingClass(int)
        int index = CU_SM + CU_1 + CU_2 + skip;
        if(index < classes.length) {
            return classes[index];
        }
        return null;
    }

    // Must only be called by client code, no method overloading.
    // The method getCallingClass(int) is preferred to this one.
    public static Class<?> getCallingClass(Class<?>... ignoreTopLevelClasses) {
        Class<?>[] classes = getClassContext();

        for(int x = 0; x < classes.length; x++) {

            // Always ignore this class.
            boolean isIgnoreClass =
                isOrIsInsideClass(ClassUtil.class, classes[x]);

            // If there are other classes that should be ignored,
            // check to see if this one is one of those.
            if(ignoreTopLevelClasses != null && !isIgnoreClass) {
                for(int y = 0; y < ignoreTopLevelClasses.length; y++) {
                    if(isOrIsInsideClass(ignoreTopLevelClasses[y], classes[x])) {
                        isIgnoreClass = true;
                        break;
                    }
                }
            }

            if(!isIgnoreClass) {
                return classes[x];
            }
        }

        return null;
    }

    private static boolean isOrIsInsideClass(Class<?> enclosing, Class<?> inside) {
        if(inside == enclosing) {
            return true;
        }
        while(inside.getEnclosingClass() != null) {
            if(inside.getEnclosingClass() == enclosing) {
                return true;
            }
            inside = inside.getEnclosingClass();
        }
        return false;
    }

    // Must only be called by client code, no method overloading.
    public static String getCallingMethodName() {
        return getCallingMethodName(1);    // 1 = skips itself
    }
    public static String getCallingMethodName(int skip) {
        StackTraceElement[] elems = Thread.currentThread().getStackTrace();
        int T_ST = 1;                          // java.lang.Thread/getStackTrace
        int CU_1 = 1;                          // replete.util.ClassUtil/getCallingMethodName
        int index = T_ST + CU_1 + skip;
        if(index < elems.length) {
            return elems[index].getMethodName();
        }
        return null;
    }

    public static boolean isOnCallStack(String className, String methodName) {
        String cmLookFor = className + "." + methodName;
        StackTraceElement[] elems = Thread.currentThread().getStackTrace();
        for(int i = 0; i < elems.length; i++) {
            StackTraceElement elem = elems[i];
            String cm = elem.getClassName() + "." + elem.getMethodName();
            if(cm.equals(cmLookFor)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This class removes the protected nature of the
     * getClassContext call in order to get at the
     * method call class context.  This is not good
     * from an OO standpoint, but overall is just
     * another technique rather than using the typical:
     *
     * Class<?> someClass =
     *     Class.forName(
     *         new Throwable().getStackTrace()[n].getClassName());
     *
     * Constructing a new SecurityManager can only fail
     * if the current system security manager does not
     * allow it (i.e. as in an Applet).
     */

    private static class OpenContextSecurityManager extends SecurityManager {
        @Override
        public Class<?>[] getClassContext() {
            return super.getClassContext();
        }
    }


    ///////////////////
    // CLASS FINDING //
    ///////////////////

    /**
     * Returns a list of all class objects that are in the
     * namespace.
     */

    public static List<Class<?>> findAll() throws IOException, ClassNotFoundException {
        if(JarUtil.getEnclosingJarFile() != null) {
            return findAllInJar();
        }
        return findAllInDir();
    }

    public static List<Class<?>> findAllInJar() throws IOException, ClassNotFoundException {

        // Find all the class files in the JAR file.
        File jarFile = JarUtil.getEnclosingJarFile();
        List<String> classPaths = new ArrayList<>();
        findJarPaths(jarFile, classPaths);

        // Massage the class files into fully qualified class
        // name strings.
        List<String> qualified = new ArrayList<>();
        for(String classFullyQ : classPaths) {

            // Example: cc/event/ChangeNotifier
            classFullyQ = classFullyQ.substring(0, classFullyQ.length() - 6);

            // Example: cc.event.ChangeNotifier
            classFullyQ = classFullyQ.replace(File.separatorChar, '.');

            qualified.add(classFullyQ);
        }

        List<Class<?>> allClasses = new ArrayList<>();

        ClassLoader cl = ClassLoader.getSystemClassLoader();

        // Retrieve the Class object that corresponds to each
        // class file found in the output directory.
        for(String q : qualified) {
            Class<?> cls = cl.loadClass(q);
            allClasses.add(cls);
        }

        return allClasses;
    }

    public static void findJarPaths(File jarFile, List<String> classPaths) throws IOException {
        JarFile jar = new JarFile(jarFile);
        Enumeration<JarEntry> entries = jar.entries();
        while(entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if(entry.toString().endsWith(".class")) {
                classPaths.add(entry.toString());
            }
        }
    }

    public static List<Class<?>> findAllInDir() throws ClassNotFoundException {
        Class<?> thisClass = ClassUtil.class;

        // The following are examples of the variations that
        // the URL below could be and all need to be
        // appropriately handled.
        //    file:/Volumes/MyDrive/eclipse-ws/proj/bin/cc/util/ClassUtil.class
        //    file:/C:/Documents%20and%20Settings/user/eclipse-ws/proj/bin/cc/util/ClassUtil.class
        //    jar:file:/C:/Documents%20and%20Settings/user/eclipse-ws/proj/build/deploy/SampleAppInstaller.jar!/cc/util/ClassUtil.class
        //    jar:file:/C:/ABC/somejar.jar!/cc/util/ClassUtil.class
        URL url = thisClass.getResource(thisClass.getSimpleName() + ".class");

        // We actually don't want any of the file objects or
        // URL objects used past this point to have escaped
        // character sequences (i.e. %20).  This is because
        // we are just using these objects to refer to a
        // local file system or a file system on a networked
        // drive (and not using them in a web context).  So
        // we will decode the URL retrieved from getResource
        // and use the deprecated File.toURL to translate
        // a file into a URL without escaping the characters.
        // In other words, we want File.toURL's deprecated
        // behavior exactly how it is.

        // Example: /Volumes/MyDrive/eclipse-ws/proj/bin/cc/util/ClassUtil.class
        // Example: C:\Documents and Settings\\user\eclipse-ws\proj\bin\cc\\util\ClassUtil.class
        File baseOutputDir;
        try {
            baseOutputDir = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
        } catch(UnsupportedEncodingException e) {
            baseOutputDir = null;   // Can't happen should be a RuntimeException
        }

        // Example: [cc, util]
        String[] pathComponents = thisClass.getPackage().getName().split("\\.");

        for(int p = 0; p < pathComponents.length + 1; p++) {
            baseOutputDir = baseOutputDir.getParentFile();
        }

        // baseOutputDir Example: /Volumes/MyDrive/eclipse-ws/proj/bin
        // baseOutputDir Example: C:\Documents and Settings\\user\eclipse-ws\proj\bin

        // Find all directories and class files in the
        // output directory.
        List<File> classFiles = new ArrayList<>();
        findClassFiles(baseOutputDir, classFiles);

        // Massage the class files into fully qualified class
        // name strings.
        List<String> qualified = new ArrayList<>();
        for(File f : classFiles) {

            // Example: cc/event/ChangeNotifier.class
            String classFullyQ = f.toString().substring(baseOutputDir.getAbsolutePath().length() + 1);

            // Example: cc/event/ChangeNotifier
            classFullyQ = classFullyQ.substring(0, classFullyQ.length() - 6);

            // Example: cc.event.ChangeNotifier
            classFullyQ = classFullyQ.replace(File.separatorChar, '.');

            qualified.add(classFullyQ);
        }

        List<Class<?>> allClasses = new ArrayList<>();

        ClassLoader cl = ClassLoader.getSystemClassLoader();

        // Retrieve the Class object that corresponds to each
        // class file found in the output directory.
        for(String q : qualified) {
            Class<?> cls = cl.loadClass(q);
            allClasses.add(cls);
        }

        return allClasses;
    }

    protected static void findClassFiles(File dir, List<File> classFiles) {
        File[] files = dir.listFiles();
        for(File file : files) {
            if(file.isDirectory()) {
                findClassFiles(file, classFiles);
            } else if(file.getName().endsWith(".class")) {
                classFiles.add(file);
            }
        }
    }
    public static Set<Class<?>> getClasses(String pkgName) {
        Set<Class<?>> classesInternal = new HashSet<>();
        try {

            // get the class loader
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if(cld == null) {
                // reportBadPlugin("Cannot get class loader.");
                return classesInternal;
            }

            String path = pkgName.replace('.', '/');

            // find all resources pointing the the package
            Enumeration<URL> resources = cld.getResources(path);

            // process each resource
            while(resources.hasMoreElements()) {
                URL resource = resources.nextElement();

                if(resource.getPath().contains(".jar!")) {
                    classesInternal.addAll(getClassesFromArchive(resource, pkgName));
                } else {
                    // fix %20 problem
                    String dirName = resource.getFile();
                    dirName = dirName.replace("%20", " ");
                    classesInternal.addAll(getClassesFromDirectory(new File(dirName), pkgName));
                }
            }
        } catch(IOException e) {
            // reportBadPlugin(e, "Cannot get classes from path " + pckgname);
        }

        return classesInternal;
    }

    private static Set<Class<?>> getClassesFromArchive(URL url, String pckgname) throws IOException {

        if(url.getPath().contains("http")) {
            return getClassesFromRemoteJar(url, pckgname);
        }

        String jarFile = url.getPath().substring(5, url.getPath().indexOf("!"));
        jarFile = jarFile.replace("%20", " ");

        return getClassesFromLocalArchive(new File(jarFile), pckgname);
    }

    private static Set<Class<?>> getClassesFromLocalArchive(File pFile, String pckgname)
                                                                                        throws IOException {

        Set<Class<?>> classesInternal = new HashSet<>();

        ZipFile eZipFile = new ZipFile(pFile);
        Enumeration<? extends ZipEntry> eEntries = eZipFile.entries();

        while(eEntries.hasMoreElements()) {
            ZipEntry eZipEntry = eEntries.nextElement();
            String eFileName = eZipEntry.getName();
            if(eFileName.startsWith(pckgname.replace('.', '/'))) {
                int eLastSlashIndex = eFileName.lastIndexOf('/');
                if(eLastSlashIndex == pckgname.length()) {
                    String eFileNameOnly = eFileName.substring(eLastSlashIndex + 1);

                    Class<?> rtn = getClassFromFilename(pckgname, eFileNameOnly, true);

                    if(rtn != null) {
                        classesInternal.add(rtn);
                    }
                }
            }
        }

        return classesInternal;
    }

    private static Set<Class<?>> getClassesFromRemoteJar(URL url, String pckgname)
                                                                                  throws IOException {
        Set<Class<?>> classesInternal = new HashSet<>();

        JarURLConnection conn = (JarURLConnection) url.openConnection();

        JarFile jar = conn.getJarFile();

        Enumeration<JarEntry> entries = jar.entries();

        while(entries.hasMoreElements()) {

            JarEntry entry = entries.nextElement();

            String eFileName = entry.getName();

            if(eFileName.startsWith(pckgname.replace('.', '/'))) {
                int eLastSlashIndex = eFileName.lastIndexOf('/');
                if(eLastSlashIndex == pckgname.length()) {
                    String eFileNameOnly = eFileName.substring(eLastSlashIndex + 1);

                    Class<?> rtn = getClassFromFilename(pckgname, eFileNameOnly, true);

                    if(rtn != null) {
                        classesInternal.add(rtn);
                    }
                }
            }
        }

        return classesInternal;
    }

    private static Class<?> getClassFromFilename(String pckgname, String filename,
                                                 boolean skipDollarClasses) {

        // ignore non-class files
        if(filename.endsWith(".class")) {

            // get rid of the ".class" at the end
            String withoutclass = pckgname + '.' + filename.substring(0, filename.length() - 6);

            int dollar_pos = withoutclass.indexOf("$");

            if(skipDollarClasses && dollar_pos > -1) {
                return null;
            }

            try {
                Class<?> cl = Class.forName(withoutclass);
                return cl;
            }
            catch(ClassNotFoundException e) {
                // reportBadPlugin(e, "Cannot get class for " + withoutclass);
            }
            catch(Throwable th) {
                // This catch is here to handle the TAVS transforms. If the library is not loaded,
                // it will fail
                // when trying to load the plugin classe. This catch will allow it to just skip the
                // tavs classes
            }
        }

        return null;
    }


    private static Set<Class<?>> getClassesFromDirectory(File dir, String pckgname) {
        Set<Class<?>> classesInternal = new HashSet<>();

        if(dir.exists() && dir.isDirectory()) {

            String[] files = dir.list();

            for(int i = 0; i < files.length; i++) {
                Class<?> rtn = getClassFromFilename(pckgname, files[i], true);
                if(rtn != null) {
                    classesInternal.add(rtn);
                }
            }
        }

        return classesInternal;
    }

    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch(Exception e) {
            throw new RuntimeConvertedException(e);
        }
    }

    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        try {
            List<Class<?>> allClasses = ClassUtil.findAll();
            for(Class<?> cls : allClasses) {
                System.out.println(cls.getName());
            }
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

