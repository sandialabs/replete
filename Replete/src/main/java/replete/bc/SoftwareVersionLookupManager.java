package replete.bc;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import replete.plugins.PluginManager;

public class SoftwareVersionLookupManager {


    ////////////
    // FIELDS //
    ////////////

    // Cache must be rebuilt every time the JVM launches, as the current state
    // of the classes will represent the specific versions of all the software
    // loaded in that JVM at that time.  This is lazily loaded as the classes'
    // versions are requested instead of populating all the versions at JVM
    // launch (since that would be a large waste of time since we don't know
    // which classes are going to be serialized just by inspecting the class).
    // The size of this map should never become an issue because only ever a
    // few hundred classes are ever going to be serialized to external sources
    // within a given software system.
    private static Map<Class, String> classVersionsCache = new ConcurrentHashMap<>();


    //////////
    // MISC //
    //////////

    public static String getVersion(Object obj) {
        if(obj == null) {
            return null;
        }
        return getVersion(obj.getClass());
    }

    public static String getVersion(Class clazz) {
        String cachedVersion = classVersionsCache.get(clazz);
        if(cachedVersion != null) {
            return cachedVersion;
        }
        List providers = PluginManager.getExtensionsForPoint(SoftwareVersionLookup.class);
        for(Object p : providers) {
            SoftwareVersionLookup provider = (SoftwareVersionLookup) p;
            String version = provider.getVersion(clazz);
            if(version != null) {
                classVersionsCache.put(clazz, version);
                return version;
            }
        }
        return null;
    }
}
