package replete.plugins;

import java.io.File;

public class PluginInitializationParamsGroup {


    ////////////
    // FIELDS //
    ////////////

    Plugin[] initialPlugins;         // e.g. new PlatformPlugin()
    Class[]  classes;                // e.g. PlatformPlugin.class
    String[] classNames;             // e.g. "gov.sandia.webcomms.plugin.WebCommsPlugin"
    File[]   jarFiles;               // e.g. "xxx/plugin.jar"   (individual file files)
    File[]   jarDirs;                // e.g. "xxx/lib"   (directory with a bunch of *.jar files)
    File[]   classDirs;              // e.g. "xxx/bin"   (directory with a bunch of *.class files)
    boolean showProgress;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public Plugin[] getInitialPlugins() {
        return initialPlugins;
    }
    public Class[] getClasses() {
        return classes;
    }
    public String[] getClassNames() {
        return classNames;
    }
    public File[] getJarFiles() {
        return jarFiles;
    }
    public File[] getJarDirs() {
        return jarDirs;
    }
    public File[] getClassDirs() {
        return classDirs;
    }
    public boolean isShowProgress() {
        return showProgress;
    }

    // Mutators

    public PluginInitializationParamsGroup setInitialPlugins(Plugin... initialPlugins) {
        this.initialPlugins = initialPlugins;
        return this;
    }
    public PluginInitializationParamsGroup setClasses(Class... classes) {
        this.classes = classes;
        return this;
    }
    public PluginInitializationParamsGroup setClassNames(String... classNames) {
        this.classNames = classNames;
        return this;
    }
    public PluginInitializationParamsGroup setJarFiles(File... jarFiles) {
        this.jarFiles = jarFiles;
        return this;
    }
    public PluginInitializationParamsGroup setJarDirs(File... jarDirs) {
        this.jarDirs = jarDirs;
        return this;
    }
    public PluginInitializationParamsGroup setClassDirs(File... classDirs) {
        this.classDirs = classDirs;
        return this;
    }
    public PluginInitializationParamsGroup setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
        return this;
    }
}
