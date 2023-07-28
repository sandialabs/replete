package replete.plugins;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.event.ChangeListener;

import replete.errors.UnicornException;
import replete.event.ChangeNotifier;
import replete.io.FileUtil;
import replete.plugins.state.ExtensionPointState;
import replete.plugins.state.ExtensionState;
import replete.plugins.state.PluginManagerState;
import replete.plugins.state.PluginState;
import replete.text.StringUtil;
import replete.ui.validation.ValidationCheckDialog;
import replete.ui.validation.ValidationContext;
import replete.ui.validation.ValidationUtil;
import replete.util.JarUtil;
import replete.util.SystemUtil;

/**
 * This class is the heart of an extremely simplified plug-in
 * framework.  The goal of this framework is to enable small
 * to medium sized Java applications to leverage the benefits
 * of a plug-in architecture without having to endure the
 * complexity and size of a more standard and mature framework
 * like OSGi or JPF, both of which employ a declarative (XML-
 * based) representation of plug-ins that provides great
 * lazy-loading and interoperability characteristics.
 *
 * This plug-in framework ignores the benefits of a markup
 * language to describe plug-ins and instead falls back onto
 * basic interface/implementation mechanisms to achieve a
 * similar goal.  However, it does employ the similar,
 * high-level concept of "extension points" and "extensions".
 *
 * A plug-in is any class implementing the Plugin interface.
 * A plug-in thus declares both the extension points and
 * extensions that it contains.  An extension is a piece of
 * code that is supposed to provide functionality to the
 * platform or another plug-in, and an extension point is
 * the contract by which a plug-in can provide that
 * functionality.
 *
 * Thus, an extension point is created simply by creating
 * 1) a Java interface that extends ExtensionPoint, or
 * 2) a Java class that implements ExtensionPoint.  An
 * extension thus is any object that is an instance of either
 * such an interface or such a class.  In other words, it is
 * an object that is an instance of ExtensionPoint.  When
 * plug-in objects are registered upon application start-up,
 * extensions are mapped to the extension points they connect
 * to, and you can always look up the extensions for a given
 * extension point using the PluginManager class.
 *
 * What it means to be an instance of a plug-in or an extension
 * is fairly clear.  They are objects that inherit from Plugin
 * or ExtensionPoint respectively.  But what is an instance of
 * an extension point?  This framework defines that as an
 * object of type Class<? extends ExtensionPoint>.  This means
 * That if you have an extension point:
 *
 *     public interface Function extends ExtensionPoint {
 *
 *     }
 *
 * Then the extension point is the object Function.class.  This
 * is important because when you implement a plug-in, you have
 * to provide a list of extension points in this form.  For
 * example:
 *
 *     @Override
 *     public Class<? extends ExtensionPoint>[] getExtensionPoints() {
 *         return new Class[] {
 *             Function.class,
 *             Simulator.class,
 *             View.class,
 *             Editor.class
 *         };
 *     }
 *
 * This required method essentially declares which classes define
 * extension point "contracts", and thus are themselves considered
 * extension points.  The type Class<? extends ExtensionPoint> is
 * not the cleanest syntax for this but it was done that way to
 * be explicit about what it means to be an extension point.  This
 * method can return null to indicate the plug-in has no published
 * extension points.
 *
 * To initialize the plug-in framework, you may call the initialize
 * method on this class.  It takes various parameters that should
 * be common to initializing a plug-in framework upon startup of
 * an application.
 *
 * @author dtrumbo
 */

public class PluginManager {


    ////////////
    // FIELDS //
    ////////////

    private static final String PLUGIN_CLASS_NAME_SUFFIX = "Plugin";   // Required
    public static final String PLUGIN_VAL_MSG = "The software may be missing one or more components or this may be a minor configuration issue.";

    // Global / Map(ID -> Object)
    private static Map<String, Plugin> globalPlugins                            = new LinkedHashMap<>();
    private static Map<String, Class<? extends ExtensionPoint>> globalExtPoints = new LinkedHashMap<>();
    private static Map<String, ExtensionPoint> globalExts                       = new LinkedHashMap<>();

    // Ownership / Map(ID -> List<Object>)
    private static Map<String, List<Class<? extends ExtensionPoint>>> ownedPluginExtPoints = new LinkedHashMap<>();
    private static Map<String, List<ExtensionPoint>> ownedPluginExts                       = new LinkedHashMap<>();
    private static Map<String, List<ExtensionPoint>> ownedExtPointExts                     = new LinkedHashMap<>();

    // All stats related to what was initialized and the errors
    // encountered during initialization.
    private static PluginInitializationResults initializationResults = new PluginInitializationResults();

    private static Map<String, InitializationSource> sourceProcessors = new LinkedHashMap<>();
    private static AtomicInteger initCount = new AtomicInteger();

    private static List<String> ignoreClasses = new ArrayList<>();


    ////////////////////
    // INITIALIZATION //
    ////////////////////

    static {
        addSourceProcessor(
            "Instances",
            p -> p.initialPlugins,
            o -> o.getClass().getName(),
            (o, params, results) -> initByInstance((Plugin) o, params, results)
        );
        addSourceProcessor(
            "Classes",
            p -> p.classes,
            o -> ((Class) o).getName(),
            (o, params, results) -> initByClass((Class) o, params, results)
        );
        addSourceProcessor(
            "Class Names",
            p -> p.classNames,
            o -> o.toString(),
            (o, params, results) -> initByClassName((String) o, params, results)
        );
        addSourceProcessor(
            "JAR Files",
            p -> p.jarFiles,
            o -> ((File) o).getAbsolutePath(),
            (o, params, results) -> initByJarFile((File) o, params, results)
        );
        addSourceProcessor(
            "JAR Directories",
            p -> p.jarDirs,
            o -> ((File) o).getAbsolutePath(),
            (o, params, results) -> initByJarDir((File) o, params, results)
        );
        addSourceProcessor(
            "Class Directories",
            p -> p.classDirs,
            o -> ((File) o).getAbsolutePath(),
            (o, params, results) -> initByClassDir((File) o, params, results)
        );
    }

    private static void addSourceProcessor(String sourceName, SourceAccessor accessor,
                                           SourceIdCreator idCreator, SourceProcessor processor) {
        sourceProcessors.put(sourceName, new InitializationSource(accessor, idCreator, processor));
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    // Get All
    public static Map<String, Plugin> getAllPlugins() {
        return Collections.unmodifiableMap(globalPlugins);
    }
    public static Map<String, Class<? extends ExtensionPoint>> getAllExtensionPoints() {
        return Collections.unmodifiableMap(globalExtPoints);
    }
    public static Map<String, ExtensionPoint> getAllExtensions() {
        return Collections.unmodifiableMap(globalExts);
    }

    // ID -> Obj
    public static <T extends Plugin> T getPlugin(Class<? extends Plugin> clazz) {
        return (T) getPluginById(clazz.getName());
    }
    public static <T extends Plugin> T getPluginById(String pluginId) {
        return (T) globalPlugins.get(pluginId);
    }
    public static Class<? extends ExtensionPoint> getExtensionPointById(String extPointId) {
        return globalExtPoints.get(extPointId);
    }
    public static <E extends ExtensionPoint> E getExtensionByClass(Class<? extends ExtensionPoint> extPoint) {
        return (E) globalExts.get(getExtensionPointId(extPoint));
    }
    public static <E extends ExtensionPoint> E getExtensionById(String extId) {
        return (E) globalExts.get(extId);
    }

    // Obj -> ID
    public static String getPluginId(Plugin plugin) {
        return plugin.getClass().getName();
    }
    public static String getExtensionPointId(Class<? extends ExtensionPoint> extPoint) {
        return extPoint.getName();
    }
    public static String getExtensionId(ExtensionPoint ext) {
        return ext.getClass().getName();
    }

    // Ownership
    public static List<Class<? extends ExtensionPoint>> getExtensionPointsInPlugin(Plugin plugin) {
        String pluginId = getPluginId(plugin);
        if(!globalPlugins.containsKey(pluginId) || !ownedPluginExtPoints.containsKey(pluginId)) {
            throw new PluginNotLoadedException("The plugin '" + pluginId + "' has not been loaded.");
        }
        List<Class<? extends ExtensionPoint>> extPoints = ownedPluginExtPoints.get(pluginId);
        return Collections.unmodifiableList(extPoints);
    }
    public static List<ExtensionPoint> getExtensionsInPlugin(Plugin plugin) {
        String pluginId = getPluginId(plugin);
        if(!globalPlugins.containsKey(pluginId) || !ownedPluginExts.containsKey(pluginId)) {
            throw new PluginNotLoadedException("The plugin '" + pluginId + "' has not been loaded.");
        }
        List<ExtensionPoint> exts = ownedPluginExts.get(pluginId);
        return Collections.unmodifiableList(exts);
    }
    public static List<ExtensionPoint> getExtensionsForPoint(Class<? extends ExtensionPoint> extPoint) {
        String extPointId = getExtensionPointId(extPoint);
        if(!globalExtPoints.containsKey(extPointId) || !ownedExtPointExts.containsKey(extPointId)) {
            throw new ExtPointNotLoadedException("The extension point '" + extPointId + "' has not been loaded.");
        }
        List<ExtensionPoint> exts = ownedExtPointExts.get(extPointId);
        return Collections.unmodifiableList(exts);
    }
    public static Class<? extends ExtensionPoint> getPointForExtension(ExtensionPoint ext) {
        try {
            return findOneExtPointClass(ext.getClass(), false);
        } catch(LoadPluginException e) {
            // Should never happen since validation should happen at plug-in load time.
            throw new UnicornException("Invalid extension '" + getExtensionId(ext) + "'.", e);
        }
    }

    public static PluginInitializationResults getInitializationResults() {
        return initializationResults;
    }


    //////////////
    // MUTATORS //
    //////////////

    public static void addIgnoreClass(String className) {
        ignoreClasses.add(className);
    }


    ////////////////////
    // INITIALIZATION //
    ////////////////////

    // Convenience methods for simple cases.
    public static PluginInitializationResults initialize(Plugin... instances) {
        return initialize(
            new PluginInitializationParamsGroup()
                .setInitialPlugins(instances)
        );
    }
    public static PluginInitializationResults initialize(Class<? extends Plugin>... classes) {
        return initialize(
            new PluginInitializationParamsGroup()
                .setClasses(classes)
        );
    }
    public static PluginInitializationResults initialize(String... classNames) {
        return initialize(
            new PluginInitializationParamsGroup()
                .setClassNames(classNames)
        );
    }
    public static PluginInitializationResults initialize(File... jarFiles) {
        return initialize(
            new PluginInitializationParamsGroup()
                .setJarFiles(jarFiles)
        );
    }

    public static PluginInitializationResults initialize(PluginInitializationParamsGroup paramsGroup) {
        return initialize(new PluginInitializationParams().add(paramsGroup));
    }
    public static synchronized PluginInitializationResults initialize(PluginInitializationParams params) {
        PluginInitializationResults results = new PluginInitializationResults();

        ValidationUtil.registerThread(results.getValidationContext());
        ValidationUtil.get().push("Initialization #" + initCount.getAndIncrement());

        initializeInternal(params, results);

        ValidationUtil.get().pop();
        ValidationUtil.deregisterThread();

        initializationResults.accumulate(results);

        fireInitializationNotifier();

        return results;
    }

    public static void initializeInternal(PluginInitializationParams params,
                                          PluginInitializationResults results) {

        int g = 0;
        for(PluginInitializationParamsGroup paramsGroup : params.getGroups()) {
            ValidationUtil.get().push("Group #" + g);
            for(String sourceName : sourceProcessors.keySet()) {
                InitializationSource source = sourceProcessors.get(sourceName);

                ValidationUtil.get().push("Source '" + sourceName + "'");

                Object[] paramsArray = source.accessor.get(paramsGroup);
                if(paramsArray != null) {
                    for(int i = 0; i < paramsArray.length; i++) {
                        Object paramsElement = paramsArray[i];  // Will be either a Plugin, Class, String, or File object
                        if(paramsElement != null) {
                            String id = source.idCreator.createId(paramsElement);
                            ValidationUtil.get().push("Processing '" + id + "'");
                            source.processor.process(paramsElement, paramsGroup, results);
                            ValidationUtil.get().pop();
                        }
                    }
                }

                ValidationUtil.get().pop();
            }

            ValidationUtil.get().pop();
            g++;
        }

        validateExtPoints();       // Could be useless if not called after all initialize's ?  Need to clear first?? implications?

        for(String pName : globalPlugins.keySet()) {
            ValidationUtil.get().push("Starting '" + pName + "'");
            try {
                Plugin plugin = globalPlugins.get(pName);
                plugin.start();
            } catch(Exception e) {
                ValidationUtil.get().error("Could not start plug-in.", e);
            } finally {
                ValidationUtil.get().pop();
            }
        }
    }

    private static void initByInstance(Plugin plugin, PluginInitializationParamsGroup paramsGroup,
                                       PluginInitializationResults results) {
        results.initInstance++;
        ValidationUtil.get().push("Load Plug-in Instance");
        try {
            loadByInstance(plugin, results);
        } catch(Throwable t) {
            ValidationUtil.get().error("Could not load plug-in.", t);
        } finally {
            ValidationUtil.get().pop();
        }
    }
    private static void initByClass(Class<? extends Plugin> clazz, PluginInitializationParamsGroup paramsGroup,
                                    PluginInitializationResults results) {
        results.initClass++;
        ValidationUtil.get().push("Load By Class Name");
        try {
            loadByClassName(clazz.getName(), results);
        } finally {
            ValidationUtil.get().pop();
        }
    }
    private static void initByClassName(String className, PluginInitializationParamsGroup paramsGroup,
                                        PluginInitializationResults results) {
        results.initClassName++;
        ValidationUtil.get().push("Load By Class Name");
        try {
            loadByClassName(className, results);
        } finally {
            ValidationUtil.get().pop();
        }
    }
    private static void initByJarFile(File jarFile, PluginInitializationParamsGroup paramsGroup,
                                      PluginInitializationResults results) {
        results.initJarFile++;
        ValidationUtil.get().push("Load JAR File");
        try {
            loadByJarFile(jarFile, results);
        } catch(Throwable t) {
            ValidationUtil.get().error("Could not load JAR file.", t);
        } finally {
            ValidationUtil.get().pop();
        }
    }
    private static void initByJarDir(File jarDir, PluginInitializationParamsGroup paramsGroup,
                                     PluginInitializationResults results) {
        results.initJarDir++;
        ValidationUtil.get().push("Load JAR Directory");
        try {
            loadByJarDir(jarDir, paramsGroup, results);
        } catch(Throwable t) {
            ValidationUtil.get().error("Could not load JAR directory.", t);
        } finally {
            ValidationUtil.get().pop();
        }
    }
    private static void initByClassDir(File classDir, PluginInitializationParamsGroup paramsGroup,
                                       PluginInitializationResults results) {
        results.initClassDir++;
        ValidationUtil.get().push("Load Class Directory");
        try {
            loadByClassDir(classDir, paramsGroup, results);
        } catch(Throwable t) {
            ValidationUtil.get().error("Could not load class directory.", t);
        } finally {
            ValidationUtil.get().pop();
        }
    }

    private static void validateExtPoints() throws LoadPluginException {
        for(String extPointId : ownedExtPointExts.keySet()) {
            ValidationUtil.get().push("Validating '" + extPointId + "'");
            try {
                if(!globalExtPoints.keySet().contains(extPointId)) {
                    String s = "";
                    Set<String> extClassNames = new LinkedHashSet<>();
                    for(ExtensionPoint ext : ownedExtPointExts.get(extPointId)) {
                        extClassNames.add(ext.getClass().getName());
                    }
                    for(String name : extClassNames) {
                        s += name + ", ";
                    }
                    s = StringUtil.cut(s, ", ");
                    throw new LoadPluginException(
                        "The extension point '" + extPointId +
                        "' has not been declared by any plug-in.  However, it is used by " +
                        "the extension class(es): " + s
                    );
                }
            } catch(Exception e) {
                ValidationUtil.get().error("Extension point validation failed.", e);
            } finally {
                ValidationUtil.get().pop();
            }
        }
    }


    /////////////
    // LOADING //
    /////////////

    private static void loadByInstance(Plugin plugin, PluginInitializationResults results) throws LoadPluginException {
        results.pluginsFound++;

        // Validate the plug-in object.
        String pluginId = getPluginId(plugin);
        if(getPluginById(pluginId) != null) {
            throw new LoadPluginException("A plug-in with the ID '" + pluginId + "' has already been loaded.");
        }
        if(StringUtil.isBlank(plugin.getName()) || StringUtil.isBlank(plugin.getVersion())) {
            throw new LoadPluginException("Plug-in '" + pluginId + "' contains invalid values for name or version.");
        }

        // Record what extension points are in this plug-in.
        Class<? extends ExtensionPoint>[] eps = plugin.getExtensionPoints();
        List<Class<? extends ExtensionPoint>> myExtPoints = new ArrayList<>();

        if(eps != null) {
            for(Class<? extends ExtensionPoint> ep : eps) {

                // Validate the extension point classes provided by the plug-in.
                // Extension point classes should only inherit from a single
                // interface that extends ExtensionPoint for clarity's sake.
                Class<? extends ExtensionPoint> foundExtPoint = findOneExtPointClass(ep, true);

                // Make sure this extension point class hasn't already been
                // loaded by another plug-in.
                String extPointId = getExtensionPointId(foundExtPoint);
                if(getExtensionPointById(extPointId) != null) {
                    throw new LoadPluginException("An extension point with the ID '" + extPointId + "' has already been loaded.");
                }

                // Remember this valid extension point.
                myExtPoints.add(foundExtPoint);
            }
        }

        // Record what extensions are in this plug-in.
        ExtensionPoint[] es = plugin.getExtensions();
        List<ExtensionPoint> myExts = new ArrayList<>();

        if(es != null) {
            for(ExtensionPoint e : es) {

                // Validate the extension classes provided by the plug-in.
                // Extension classes should only implement a single
                // interface that extends ExtensionPoint for clarity's sake.
                findOneExtPointClass(e.getClass(), false);

                // Make sure this extension point class hasn't already been
                // loaded by another plug-in.
                String extId = getExtensionId(e);
                if(getExtensionById(extId) != null) {
                    throw new LoadPluginException("An extension with the ID '" + extId + "' has already been loaded.");
                }

                // Remember this valid extension.
                myExts.add(e);
            }
        }

        ValidationUtil.get().info("Loaded plug-in '" + pluginId + "'.");

        results.pluginsLoaded++;
        results.extensionPointsDefined += myExtPoints.size();
        results.extensionsProvided += myExts.size();

        // Plug-in class and instance are considered good at this point.

        // Remember the plug-in (global).
        globalPlugins.put(pluginId, plugin);

        // Remember all the extension points in this plug-in (global).
        for(Class<? extends ExtensionPoint> extPoint : myExtPoints) {
            String extPointId = getExtensionPointId(extPoint);
            globalExtPoints.put(extPointId, extPoint);
            getOwnedExtPointExts(extPoint);
        }

        // Remember all the extensions in this plug-in (global).
        for(ExtensionPoint ext : myExts) {
            String extId = getExtensionId(ext);
            globalExts.put(extId, ext);

            // Remember the extensions for the given extension points (ownership).
            Class<? extends ExtensionPoint> foundExtPoint = findOneExtPointClass(ext.getClass(), false);
            List<ExtensionPoint> myExtPointExts = getOwnedExtPointExts(foundExtPoint);
            myExtPointExts.add(ext);
        }

        // Remember the extension points and extensions for this plug-in (ownership).
        ownedPluginExtPoints.put(pluginId, myExtPoints);
        ownedPluginExts.put(pluginId, myExts);
    }

    private static List<ExtensionPoint> getOwnedExtPointExts(Class<? extends ExtensionPoint> extPoints) {
        String extPointId = getExtensionPointId(extPoints);
        List<ExtensionPoint> myExtPointExts = ownedExtPointExts.get(extPointId);
        if(myExtPointExts == null) {
            myExtPointExts = new ArrayList<>();
            ownedExtPointExts.put(extPointId, myExtPointExts);
        }
        return myExtPointExts;
    }

    private static void loadByClassName(String className, PluginInitializationResults results) throws LoadPluginException {
        ValidationUtil.get().push("Instantiate Plug-in");
        Plugin plugin;
        try {
            plugin = instantiatePluginFromClassName(className);
        } catch(Throwable t) {
            ValidationUtil.get().error("Could not instantiate plug-in.", t);
            return;
        } finally {
            ValidationUtil.get().pop();
        }

        ValidationUtil.get().push("Load Plug-in Instance");
        try {
            loadByInstance(plugin, results);
        } catch(Throwable t) {
            ValidationUtil.get().error("Could not load plug-in.", t);
        } finally {
            ValidationUtil.get().pop();
        }
    }

    private static Plugin instantiatePluginFromClassName(String className) throws LoadPluginException {
        try {

            // Attempt to instantiate the plug-in object from the
            // given fully-qualified class name.
            Class<?> pluginClass = Class.forName(className);
            Constructor<?> ctor = pluginClass.getConstructor(new Class<?>[0]);
            return (Plugin) ctor.newInstance(new Object[0]);

        } catch(ClassNotFoundException e) {
            throw new LoadPluginException("Could not find plug-in class '" + className + "'.", e);

        } catch(NoSuchMethodException e) {
            throw new LoadPluginException("Plug-in class '" + className + "' does not have a default constructor.", e);

        } catch(InstantiationException e) {
            throw new LoadPluginException("Plug-in class '" + className + "' must not be abstract.", e);

        } catch(ClassCastException e) {
            throw new LoadPluginException("Plug-in class '" + className + "' does not implement '" + Plugin.class.getName() + "'.", e);

        } catch(Exception e) {
            Class<?> cClass = e.getCause().getClass();
            if(cClass.equals(ClassNotFoundException.class) || cClass.equals(NoClassDefFoundError.class)) {
                throw new RuntimeException("Plug-in object '" + className +
                    "' failed to be instantiated.  The plug-in does not appear to have all its required classes loaded.", e);
            }
            throw new RuntimeException("Plug-in object '" + className + "' failed to be instantiated.", e);
        }
    }

    private static void loadByJarFile(File jarFile, PluginInitializationResults results) {
        if(!jarFile.isFile()) {
            ValidationUtil.get().error("Path is not a file.");
            return;
        }
        if(!JarUtil.isJar(jarFile)) {
            ValidationUtil.get().error("File is not a JAR file.");
            return;
        }

        SystemUtil.addPathsToClassPath(jarFile);      // Can throw exception
        Map<File, Exception> successMap = new HashMap<>();
        successMap.put(jarFile, null);
        loadFromJars(successMap, false, results);     // Single JAR file - don't print progress
    }

    private static void loadByJarDir(File jarDir, PluginInitializationParamsGroup paramsGroup,
                                     PluginInitializationResults results) {

        if(!jarDir.isDirectory()) {
            ValidationUtil.get().error("Path is not a directory.");
            return;
        }

        // Place all JAR files in this directory, or in one
        // level of subdirectories on the class path.
        Map<File, Exception> success = JarUtil.addAllToPlatform(jarDir, 1);   // Doesn't throw exception...
        loadFromJars(success, paramsGroup.showProgress, results);
    }

    private static void loadByClassDir(File classDir, PluginInitializationParamsGroup paramsGroup,
                                       PluginInitializationResults results) {
        boolean printProgress = paramsGroup.showProgress;

        if(!classDir.isDirectory()) {
            ValidationUtil.get().error("Path is not a directory.");
            return;
        }

        if(printProgress) {
            System.out.println("<------------------------------------------------------------>");
            System.out.print("Dynamic Plug-in Load Progress: (X=1K Classes Inspected)\n[");
        }

        int localClassCount = 0;

        try {
            SystemUtil.addPathsToClassPath(classDir);
            List<File> classFiles = FileUtil.find(classDir,
                f -> f.isDirectory() ||
                    (f.getName().endsWith(".class") && !f.getName().contains("$")));

            for(File classFile : classFiles) {
                String className = getClassNameFromFile(classDir, classFile);
                attemptProcessClass(results, className);
                results.classesInspected++;
                localClassCount++;
                if(printProgress && localClassCount % 1000 == 0) {
                    System.out.print("X");
                }
            }

        // Deal with potential exceptions from SystemUtil.addPathsToClassPath or
        // FileUtil.find before exiting.
        } catch(Throwable t) {
            System.out.print("!");
            throw t;

        // End the progress bar and print the profiler results.
        } finally {
            if(printProgress) {
                if(localClassCount < 1000) {
                    System.out.print("x");
                }
                System.out.println("] " + localClassCount + " Classes Inspected");
                System.out.println("Group Result: " + results);
                System.out.println("<------------------------------------------------------------>");
            }
        }
    }

    private static String getClassNameFromFile(File classDir, File classFile) {
        String base = classDir.getAbsolutePath();
        String rest = classFile.getAbsolutePath().substring(base.length());
        if(rest.startsWith("/") || rest.startsWith("\\")) {
            rest = rest.substring(1);   // Even possible?  Don't think so... but just in case for now...
        }
        rest = rest.replaceAll("[/\\\\]", ".");
        rest = rest.replaceAll("\\.class$", "");
        return rest;
    }

    private static void loadFromJars(Map<File, Exception> jarFiles, boolean printProgress,
                                     PluginInitializationResults results) {
        results.jarFilesRequested += jarFiles.size();

        if(printProgress) {
            System.out.println("<------------------------------------------------------------>");
            System.out.print("Dynamic Plug-in Load Progress: (X=1K Classes Inspected)\n[");
        }

        int localClassCount = 0;

        // For all of those JAR files that were successfully loaded...
        for(File jarFile : jarFiles.keySet()) {
            ValidationUtil.get().push("Processing JAR '" + jarFile.getAbsolutePath() + "'");
            try {
                if(jarFiles.get(jarFile) == null) {
                    results.jarFilesProcessed++;
                    try {
                        String[] classNames = JarUtil.listClasses(jarFile);
                        for(String className : classNames) {
                            attemptProcessClass(results, className);
                            results.classesInspected++;
                            localClassCount++;
                            if(printProgress && localClassCount % 1000 == 0) {
                                System.out.print("X");
                            }
                        }
                    } catch(Throwable t) {
                        ValidationUtil.get().error("Could not list JAR classes.", t);
                    }
                } else {
                    ValidationUtil.get().error("Could not add JAR file to classpath.", jarFiles.get(jarFile));
                }
            } finally {
                ValidationUtil.get().pop();
            }
        }

        // End the progress bar and print the profiler results.
        if(printProgress) {
            if(localClassCount < 1000) {
                System.out.print("x");
            }
            System.out.println("] " + localClassCount + " Classes Inspected");
            System.out.println("Group Result: " + results);
            System.out.println("<------------------------------------------------------------>");
        }
    }

    private static void attemptProcessClass(PluginInitializationResults results, String className) {
        if(className.endsWith(PLUGIN_CLASS_NAME_SUFFIX) && !ignoreClasses.contains(className)) {
            ValidationUtil.get().push("Processing Class '" + className + "'");
            try {
                processClass(className, results);       // Can throw exceptions from Class.forName
            } catch(Throwable t) {
                ValidationUtil.get().error("Could not process class.", t);
            } finally {
                ValidationUtil.get().pop();
            }
        }
    }

    private static void processClass(String className, PluginInitializationResults results) throws Throwable {

        // NOTE: During dynamic loading (post-deployment)
        // we've had problems where the following statement
        // causes an ExceptionInInitializerError for classes
        // with statements similar to:
        //     protected static ImageIcon icon =
        //                GuiUtil.getImageLocal("../images/schema.gif");
        // Even though the file does indeed exist in the JAR
        // at the correct place, and the application runs
        // fine at runtime.  There must be some sort of
        // load order related to classes vs. static resources.
        // This would need to be looked into for a more
        // robust PluginManager. BTW if the above code is
        // replaced with
        //     protected static ImageIcon icon =
        //                ImageUtil.getImage("schema.gif");
        // where ImageUtil is a class in the same directory
        // as the static resources, then everything works
        // fine at deployment/dynamic loading time.

        // NOTE: During dynamic loading (post-deployment)
        // if a class being loaded by the above statement
        // depends on a class that is not currently on the
        // class path, a NoClassDefFoundError will be thrown.
        // This would be caused by having a TPL JAR on
        // the class path in Eclipse, but then your build
        // process does not place that JAR into the final
        // deployment lib directory.  You can choose how
        // you want to handle this.

        Class<?> pluginClass = Class.forName(className);

        boolean isPlugin    = Plugin.class.isAssignableFrom(pluginClass);
        boolean isInterface = pluginClass.isInterface();
        boolean isAbstract  = Modifier.isAbstract(pluginClass.getModifiers());
        boolean isNoAuto    = AutomaticPluginLoadingDisallowed.class.isAssignableFrom(pluginClass);

        // If the class implements the Plugin interface but is a
        // concrete class that is allowed to be loaded automatically,
        // attempt to load the class.
        if(isPlugin && !isInterface && !isAbstract && !isNoAuto) {
            ValidationUtil.get().push("Load By Class Name");
            try {
                loadByClassName(className, results);
            } finally {
                ValidationUtil.get().pop();
            }
        }
    }


    ///////////
    // RESET //
    ///////////

    // Advanced usage currently only used for unit testing.
    // However, it's possible this might one day service
    // a user-facing feature.
    public static synchronized void reset() {

        // Global / Map(ID -> Object)
        globalPlugins   = new LinkedHashMap<>();
        globalExtPoints = new LinkedHashMap<>();
        globalExts      = new LinkedHashMap<>();

        // Ownership / Map(ID -> List<Object>)
        ownedPluginExtPoints = new LinkedHashMap<>();
        ownedPluginExts      = new LinkedHashMap<>();
        ownedExtPointExts    = new LinkedHashMap<>();

        // All stats related to what was initialized and the errors
        // encountered during initialization.
        initializationResults = new PluginInitializationResults();
        initCount             = new AtomicInteger();
    }


    //////////
    // MISC //
    //////////

    private static Class<? extends ExtensionPoint> findOneExtPointClass(
            Class<? extends ExtensionPoint> clazz, boolean isExtPoint)
            throws LoadPluginException {
        String objName = isExtPoint ? "extension point" : "extension";
        String actionName = isExtPoint ? "extend" : "implement";

        List<Class<? extends ExtensionPoint>> foundExtPoints = findExtPoints(clazz);
        if(foundExtPoints.size() == 0) {
            throw new LoadPluginException("The " + objName + " class '" + clazz.getName() + "' does not " + actionName + " any extension point interface.");
        } else if(foundExtPoints.size() > 1) {
            String s = "";
            for(Class<?> foundExtPoint : foundExtPoints) {
                s += foundExtPoint.getName() + ", ";
            }
            s = StringUtil.cut(s, ", ");
            throw new LoadPluginException("The " + objName + " class '" + clazz.getName() + "' " + actionName + "s more than one extension point interface (" + s + ").");
        }

        return foundExtPoints.get(0);
    }

    private static List<Class<? extends ExtensionPoint>> findExtPoints(Class<? extends ExtensionPoint> clazz) {
        List<Class<? extends ExtensionPoint>> extPoints = new ArrayList<>();
        findExtPoints(clazz, extPoints);
        return extPoints;
    }

    private static void findExtPoints(Class<?> clazz, List<Class<? extends ExtensionPoint>> found) {
        Class<?> clazzParent = clazz.getSuperclass();
        Class<?>[] clazzImpl = clazz.getInterfaces();
        if(clazzParent != null) {
            if(clazzParent.equals(ExtensionPoint.class)) {
                found.add((Class<? extends ExtensionPoint>) clazz);
            } else {
                findExtPoints(clazzParent, found);
            }
        }
        if(clazzImpl != null) {
            for(Class<?> impl : clazzImpl) {
                if(impl.equals(ExtensionPoint.class)) {
                    found.add((Class<? extends ExtensionPoint>) clazz);
                } else {
                    findExtPoints(impl, found);
                }
            }
        }
    }

    public static void list() {
        for(String pluginId : globalPlugins.keySet()) {
            Plugin plugin = globalPlugins.get(pluginId);
            System.out.println("Plugin => " + pluginId + " / " + plugin.getName() + " / " + plugin.getVersion());
            for(Class<? extends ExtensionPoint> extPoint : ownedPluginExtPoints.get(pluginId)) {
                System.out.println("    ExtPoint => " + getExtensionPointId(extPoint));
            }
            for(ExtensionPoint ext : ownedPluginExts.get(pluginId)) {
                System.out.println("    Ext => " + getExtensionId(ext));
                System.out.println("        Point => " + getExtensionPointId(getPointForExtension(ext)));
            }
        }
    }


    ///////////////////
    // SUMMARY STATE //
    ///////////////////

    // Produces a serializable version of the information
    // contained by the PluginManager for the purposes
    // of transferring a summary of the plug-in configuration
    // contained on this machine to another machine.

    public static PluginManagerState getSummaryState() {
        Map<String, ExtensionPointState> extPointStates = new HashMap<>();
        Map<String, ExtensionState> extStates = new HashMap<>();
        PluginManagerState mgrState = new PluginManagerState();
        mgrState.setInitializationResults(initializationResults);

        // Global

        for(String pluginId : globalPlugins.keySet()) {
            Plugin plugin = globalPlugins.get(pluginId);
            List<ExtensionPointState> extPoints = new ArrayList<>();
            List<ExtensionState> exts = new ArrayList<>();
            if(plugin.getExtensionPoints() != null) {
                for(Class<? extends ExtensionPoint> extPoint : plugin.getExtensionPoints()) {
                    extPoints.add(addState(extPointStates, extPoint));
                }
            }
            if(plugin.getExtensions() != null) {
                for(ExtensionPoint ext : plugin.getExtensions()) {
                    exts.add(addState(extStates, extPointStates, ext));
                }
            }
            PluginState pluginState = new PluginState(
                pluginId,
                plugin.getName(),
                plugin.getDescription(),
                plugin.getVersion(),
                plugin.getProvider(),
                plugin.getIcon(),
                extPoints,
                exts
            );
            mgrState.getGlobalPlugins().put(pluginId, pluginState);
        }
        for(String extPointId : globalExtPoints.keySet()) {
            Class<? extends ExtensionPoint> extPoint = globalExtPoints.get(extPointId);
            mgrState.getGlobalExtPoints().put(extPointId, addState(extPointStates, extPoint));
        }
        for(String extId : globalExts.keySet()) {
            ExtensionPoint ext = globalExts.get(extId);
            mgrState.getGlobalExts().put(extId, addState(extStates, extPointStates, ext));
        }

        // Owned

        for(String pluginId : ownedPluginExtPoints.keySet()) {
            List<Class<? extends ExtensionPoint>> extPoints = ownedPluginExtPoints.get(pluginId);
            List<ExtensionPointState> extPointStatesList = new ArrayList<>();
            for(Class<? extends ExtensionPoint> extPoint : extPoints) {
                extPointStatesList.add(addState(extPointStates, extPoint));
            }
            mgrState.getOwnedPluginExtPoints().put(pluginId, extPointStatesList);
        }
        for(String pluginId : ownedPluginExts.keySet()) {
            List<ExtensionPoint> exts = ownedPluginExts.get(pluginId);
            List<ExtensionState> extStatesList = new ArrayList<>();
            for(ExtensionPoint ext : exts) {
                extStatesList.add(addState(extStates, extPointStates, ext));
            }
            mgrState.getOwnedPluginExts().put(pluginId, extStatesList);
        }
        for(String extPointId : ownedExtPointExts.keySet()) {
            List<ExtensionPoint> exts = ownedExtPointExts.get(extPointId);
            List<ExtensionState> extStatesList = new ArrayList<>();
            for(ExtensionPoint ext : exts) {
                extStatesList.add(addState(extStates, extPointStates, ext));
            }
            mgrState.getOwnedExtPointExts().put(extPointId, extStatesList);
        }

        return mgrState;
    }

    private static ExtensionPointState addState(Map<String, ExtensionPointState> extPointStates, Class<? extends ExtensionPoint> extPoint) {
        String extPointId = PluginManager.getExtensionPointId(extPoint);
        ExtensionPointState extPointState = extPointStates.get(extPointId);
        if(extPointState == null) {
            extPointState = new ExtensionPointState(extPointId);
            extPointStates.put(extPointId, extPointState);
        }
        return extPointState;
    }

    private static ExtensionState addState(Map<String, ExtensionState> extStates, Map<String, ExtensionPointState> extPointStates, ExtensionPoint ext) {
        String extId = PluginManager.getExtensionId(ext);
        ExtensionState extState = extStates.get(extId);
        if(extState == null) {
            ValidationResult vResult = null;
            if(ext instanceof Validatable) {
                vResult = ((Validatable) ext).validate();
            }
            Class<? extends ExtensionPoint> extPoint = null;
            try {
                extPoint = findOneExtPointClass(ext.getClass(), false);
            } catch(LoadPluginException e) {
                e.printStackTrace();  // This shouldn't happen.
            }
            ExtensionPointState extPointState = addState(extPointStates, extPoint);
            extState = new ExtensionState(extId, vResult, extPointState);
            extStates.put(extId, extState);
        }
        return extState;
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    private static transient ChangeNotifier initializationNotifier = new ChangeNotifier(PluginManager.class);
    public static void addInitializationListener(ChangeListener listener) {
        initializationNotifier.addListener(listener);
    }
    private static void fireInitializationNotifier() {
        initializationNotifier.fireStateChanged();
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    // These simply allow us to more strongly represent where
    // plug-ins can be loaded from.

    private static class InitializationSource {
        SourceAccessor accessor;
        SourceIdCreator idCreator;
        SourceProcessor processor;
        public InitializationSource(SourceAccessor accessor, SourceIdCreator idCreator, SourceProcessor processor) {
            this.accessor = accessor;
            this.idCreator = idCreator;
            this.processor = processor;
        }
    }
    private static interface SourceAccessor {
        Object[] get(PluginInitializationParamsGroup params);
    }
    private static interface SourceIdCreator {
        String createId(Object object);
    }
    private static interface SourceProcessor {
        void process(Object object, PluginInitializationParamsGroup params, PluginInitializationResults results);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
//        File f = CodeUtil.getCodeSourcePath();
//        PluginInitializationResults results = initialize(new PluginInitializationParams().setClassDirs(f));
//        System.out.println(results);

//        f = new File("C:\\Users\\dtrumbo\\work\\eclipse-main\\WebComms\\build.xml");
//        results = initialize(new PluginInitializationParams().setJarFiles(f));
//        System.out.println(results);
//
//        f = new File("C:\\mongox");
//        results = initialize(new PluginInitializationParams().setClassDirs(f));
//        System.out.println(results);

        File f = new File("C:\\Users\\dtrumbo\\work\\eclipse-main\\AvondaleBundler\\build\\deploy\\avondale-2.3.0\\bin\\lib");
        PluginInitializationResults results = initialize(new PluginInitializationParamsGroup().setJarDirs(f));
        System.out.println(results);

        System.out.println(initializationResults);
        System.out.println(initializationResults.getValidationContext().toString());

        ValidationContext vContext = initializationResults.getValidationContext();
        ValidationCheckDialog dlg =
            new ValidationCheckDialog(
                null, vContext, false, "Plug-in", PluginManager.PLUGIN_VAL_MSG);
        dlg.setVisible(true);
    }
}
