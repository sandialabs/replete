package replete.plugins;

import java.io.Serializable;

import replete.ui.validation.ValidationContext;

public class PluginInitializationResults implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    // Entry point initialization counts.
    int initInstance;
    int initClass;
    int initClassName;
    int initJarFile;
    int initJarDir;
    int initClassDir;

    // Low-level result counts.
    int jarFilesRequested;
    int jarFilesProcessed;
    int classesInspected;
    int pluginsFound;
    int pluginsLoaded;
    int extensionPointsDefined;
    int extensionsProvided;

    ValidationContext validationContext = new ValidationContext();



    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public int getClassesInspected() {
        return classesInspected;
    }
    public int getPluginsFound() {
        return pluginsFound;
    }
    public int getPluginsLoaded() {
        return pluginsLoaded;
    }
    public int getExtensionPointsDefined() {
        return extensionPointsDefined;
    }
    public int getExtensionsProvided() {
        return extensionsProvided;
    }
    public ValidationContext getValidationContext() {
        return validationContext;
    }

    // Mutators

    public void accumulate(PluginInitializationResults other) {
        initInstance           += other.initInstance;
        initClass              += other.initClass;
        initClassName          += other.initClassName;
        initJarFile            += other.initJarFile;
        initJarDir             += other.initJarDir;
        initClassDir           += other.initClassDir;

        jarFilesRequested      += other.jarFilesRequested;
        jarFilesProcessed      += other.jarFilesProcessed;
        classesInspected       += other.classesInspected;
        pluginsFound           += other.pluginsFound;
        pluginsLoaded          += other.pluginsLoaded;
        extensionPointsDefined += other.extensionPointsDefined;
        extensionsProvided     += other.extensionsProvided;

        validationContext.overlay(other.validationContext);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return
            "I{I:" + initInstance +
            ", C:" + initClass +
            ", CN:" + initClassName +
            ", JF:" + initJarFile +
            ", JD:" + initJarDir +
            ", CD:" + initClassDir +
            "}, R{JF:" + jarFilesProcessed + "/" + jarFilesRequested +
            ", C:" + classesInspected +
            ", PF:" + pluginsFound +
            ", PL:" + pluginsLoaded +
            ", XP:" + extensionPointsDefined +
            ", X:" + extensionsProvided +
            ", V:" + wrap(validationContext.getQuickCount()) +
            "}";
    }
    private String wrap(int[] counts) {
        return "[I:" + counts[0] + ", W:" + counts[1] + ", E:" + counts[2] + "]";
    }
}
