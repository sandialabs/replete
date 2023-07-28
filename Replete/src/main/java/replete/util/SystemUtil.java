package replete.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import replete.errors.RuntimeConvertedException;

/**
 * @author Derek Trumbo
 */

public class SystemUtil {


    ////////////////////////////
    // SYSTEM EXIT PROTECTION //
    ////////////////////////////

    // TODO: Consider extending to halt?  Research other ways to accomplish same thing

    private static Map<Thread, List<SystemExitProtectionContext>> sepContexts = new ConcurrentHashMap<>();

    // synch not necessary since we're keying off of Thread & using CHM
    public static SystemExitProtectionContext addSystemExitProtectionContext() {
        List<SystemExitProtectionContext> contexts = sepContexts.get(Thread.currentThread());
        if(contexts == null) {
            contexts = new ArrayList<>();
            sepContexts.put(Thread.currentThread(), contexts);
        }
        SystemExitProtectionContext context = new SystemExitProtectionContext();
        contexts.add(context);
        return context;
    }
    public static SystemExitProtectionContext removeSystemExitProtectionContext() {
        List<SystemExitProtectionContext> contexts = sepContexts.get(Thread.currentThread());
        if(contexts == null) {
            return null;
        }
        SystemExitProtectionContext removed = null;
        if(!contexts.isEmpty()) {      // Should always be non-empty
            removed = contexts.remove(contexts.size() - 1);
        }
        if(contexts.isEmpty()) {
            sepContexts.remove(Thread.currentThread());
        }
        return removed;
    }
    public static int getSystemExitProtectionContextCount() {
        List<SystemExitProtectionContext> contexts = sepContexts.get(Thread.currentThread());
        if(contexts == null || contexts.isEmpty() /* empty shouldn't be possible */) {
            return 0;
        }
        return contexts.size();
    }
    public static void protectedExit(int status) {
        List<SystemExitProtectionContext> contexts = sepContexts.get(Thread.currentThread());
        if(contexts == null || contexts.isEmpty() /* empty shouldn't be possible */) {
            System.exit(status);
        }
        SystemExitProtectionContext context = contexts.get(contexts.size() - 1);    // Eclipse warning: contexts will not be null here
        context.setStatus(status);
        throw new SystemExitRequestedException();
    }

    public static File getWorkingDirectory() {
        return new File("").getAbsoluteFile();    // return new File(System.getProperty("user.dir")); works as well
    }

    public static List<URL> getClassPathUrls() {
        URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Object ucp = ReflectionUtil.get(sysLoader, "ucp");
        List<URL> paths = (List<URL>) ReflectionUtil.get(ucp, "path");
        return paths;
    }

    public static void addPathsToClassPath(URL url) {
        try {
            URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            ReflectionUtil.invoke(sysLoader, "addURL", new Object[] {url});
        } catch(Throwable t) {
            throw new RuntimeConvertedException("Could not add URL to system classloader", t);
        }
    }

    public static void addPathsToClassPath(File jarFileOrClassDir) {
        try {

            // Normalization is very important to not accidentally add two
            // lexicographically different paths that point to the same actual
            // file.  This can cause the library to be loaded twice under
            // two different paths, and confuse libraries.
            File normalized = jarFileOrClassDir.getCanonicalFile();

            URL url = normalized.toURI().toURL();
            addPathsToClassPath(url);

        } catch(Throwable t) {
            throw new RuntimeConvertedException("Could not add URL to system classloader", t);
        }
    }

    public static boolean isSystemPropertySet(String property) {
        String value = System.getProperty(property);
        return value != null && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase(""));
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) throws IOException {
        File f = new File("C:/Users/dtrumbo/work/eclipse-main/AvondaleBundler/../build");
        addPathsToClassPath(f.getCanonicalFile());

        URL url = new URL("jar:file:/C:/Users/dtrumbo/work/eclipse-main/AvondaleBundler/build/deploy/avondale-3.0.0/scripts/windows/../../bin/lib/tika-app-1.14.jar!/a");
        URL url2 = new URL("jar:file:/C:/Users/dtrumbo/work/eclipse-main/AvondaleBundler/build/deploy/avondale-3.0.0/bin/lib/tika-app-1.14.jar!/a");


        final URL url3;
        try
        {
            System.out.println(url.toURI().isOpaque());
            url3 = url.toURI().normalize().toURL();
            System.out.println(url3);
        }
        catch (URISyntaxException e) {
            throw new MalformedURLException(e.getMessage());
        }

        System.out.println(url);
        System.out.println(url2);

        System.out.println(url.toExternalForm());
    }
}
