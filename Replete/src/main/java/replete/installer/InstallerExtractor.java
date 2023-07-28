package replete.installer;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

/**
 * @author Derek Trumbo
 */

public class InstallerExtractor {

    ////////////////
    // PRINCIPALS //
    ////////////////

    public static void extractOneAddToPath(String extractMe) throws Exception {
        JFrame frame = null;
        try {
            frame = createProgressFrame();
            File jarFile = getEnclosingJarFile();
            File curDir = jarFile.getParentFile();
            File[] extractedFile = extract(jarFile, curDir, "^.*cc.jar$", true, true);
            addToClassPath(extractedFile[0].toURI().toURL());
            frame.dispose();
        } finally {
            // Make sure that the frame is gone by the end of this
            // method no matter what.
            if(frame != null && !frame.isShowing()) {
                frame.dispose();
            }
        }
    }

    // Not used anymore given current deployment strategy.
    /*public static void extractAllAndRun(String runThisJar) throws Exception {
        JFrame frame = null;
        try {
            frame = createProgressFrame();
            File jarFile = getEnclosingJarFile();
            File curDir = jarFile.getParentFile();
            extract(jarFile, curDir, "^.*\\.jar$", true, true);
            frame.dispose();
            Process p = Runtime.getRuntime().exec(
                "javaw -jar " + runThisJar, null, curDir);
            int result = p.waitFor();
            if(result != 0) {
                InputStream s = p.getErrorStream();
                BufferedReader read = new BufferedReader(new InputStreamReader(s));
                String line;
                String errorText = "";
                while((line = read.readLine()) != null) {
                    errorText += line + "\n";
                }
                throw new RuntimeException("java -jar " + runThisJar + " has failed.\n" + errorText);
            }
        } finally {
            // Make sure that the frame is gone by the end of this
            // method no matter what.
            if(frame != null && !frame.isShowing()) {
                frame.dispose();
            }
        }
    }*/

    protected static JFrame createProgressFrame() {
        JProgressBar pgb = new JProgressBar();
        pgb.setIndeterminate(true);
        JFrame frame = new JFrame("Extracting Installation JARs...");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setResizable(false);
        frame.add(pgb);
        frame.setSize(400, 60);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }

    public static void addToClassPath(URL url) throws Exception {
        URLClassLoader systemLoader = (URLClassLoader) Thread
            .currentThread().getContextClassLoader();
        Class<?> systemLoaderClass = URLClassLoader.class;
        Method method = systemLoaderClass.getDeclaredMethod("addURL",
            new Class[] { URL.class });
        method.setAccessible(true);
        method.invoke(systemLoader, new Object[] { url });
    }

    /////////////////////////////
    // JAR FILE IDENTIFICATION //
    /////////////////////////////

    // Returns the file object representing the JAR file
    // that this .class file is executing within.  If
    // this code is not in a JAR file, null is returned.
    public static File getEnclosingJarFile() {
        Class<?> thisClass = InstallerExtractor.class;

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

            try {

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
                try {
                    return new File(URLDecoder.decode(file, "UTF-8"));
                } catch(UnsupportedEncodingException e) {
                    return null;   // Can't happen should be a RuntimeException
                }

            // Should not happen since the Java libraries actually
            // generated the URL.
            } catch(MalformedURLException e) {
                e.printStackTrace();
            }
        }

        // Not executing from within a JAR file.
        return null;
    }

    ////////////////
    // EXTRACTION //
    ////////////////

    // Copy of JarUtil.extract  Due to JAR file deployment
    // strategy this cannot be imported, must be copied.

    public static File[] extract(File jarFile, File destDir, String filter, boolean overwrite, boolean keepFullPath) throws IOException {
        JarFile jar = new JarFile(jarFile);
        Enumeration<JarEntry> entries = jar.entries();
        List<File> extractedFiles = new ArrayList<File>();

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

        return extractedFiles.toArray(new File[0]);
    }
}
