package replete.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import replete.plugins.ExtensionPoint;
import replete.plugins.PluginManager;

// The strategy taken with this class and the ClassNameSimplifier
// extension point is only a simple strategy where you allow
// client code to provide the entire simplified string (including
// possible HTML markup) for a given class name.  One could imagine
// other strategies that involve describing which class name
// components (separated by .) mean what and letting platform code
// help out even more, but this is working for now.

public class ClassNameSimplifierManager {


    ////////////
    // FIELDS //
    ////////////

    private static List<ClassNameSimplifier> simplifiers = new ArrayList<>();
    private static Map<String, String> simplifiedClassNames = new HashMap<>();


    ////////////////////
    // INITIALIZATION //
    ////////////////////

    static {
        List<ExtensionPoint> exts =
            PluginManager.getExtensionsForPoint(ClassNameSimplifier.class);
        for(ExtensionPoint ext : exts) {
            simplifiers.add((ClassNameSimplifier) ext);
        }
    }


    //////////
    // MISC //
    //////////

    public static synchronized String getSimplifiedMarkedUp(String className) {
        String simplified = simplifiedClassNames.get(className);
        if(simplified != null) {
            return simplified;
        }

        for(ClassNameSimplifier simplifier : simplifiers) {
            if(simplifier.appliesTo(className)) {
                simplified = simplifier.simplifyAndMarkupClassName(className);
                simplifiedClassNames.put(className, simplified);
                return simplified;
            }
        }

        // Storing the same className instead of a special
        // value like null isn't as clean as it could be
        // but saves a O(1) call at the top of the method
        // (which is an absolutely unnecessary optimization!)
        simplifiedClassNames.put(className, className);
        return className;
    }
}
