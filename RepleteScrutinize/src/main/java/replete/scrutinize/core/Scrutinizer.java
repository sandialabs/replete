package replete.scrutinize.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import replete.plugins.ExtensionPoint;
import replete.plugins.PluginManager;
import replete.util.ReflectionUtil;

public class Scrutinizer {


    ////////////
    // FIELDS //
    ////////////

    private static Map<Class<?>, Class<?>> handledClasses = new HashMap<>();
    private static Set<String> objectActions = new HashSet<>();


    ////////////////////
    // INITIALIZATION //
    ////////////////////

    static {
        Method[] methods = ReflectionUtil.getMethods(Object.class);
        for(Method method : methods) {
            objectActions.add(method.getName());
        }
    }

    public static Set<String> getObjectActions() {
        return objectActions;
    }

    public static void initialize() {
        List<ExtensionPoint> eps = PluginManager.getExtensionsForPoint(IBaseSc.class);
        for(ExtensionPoint ep : eps) {
            IBaseSc sc = (IBaseSc) ep;
            handledClasses.put(sc.getHandledClass(), ep.getClass());
        }
    }

    public static Class<?> getScrutinizeClassFor(Class<?> nativeClass) {
        return handledClasses.get(nativeClass);
    }
    public static Class<?> getScrutinizeClassForObject(Class<?> nativeClass) {
        for(Class<?> nativeClassBase : handledClasses.keySet()) {
            if(nativeClassBase.isAssignableFrom(nativeClass)) {
                return handledClasses.get(nativeClassBase);
            }
        }
        return null;
    }
}
