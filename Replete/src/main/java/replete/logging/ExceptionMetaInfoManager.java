package replete.logging;

import java.util.Set;
import java.util.TreeSet;

public class ExceptionMetaInfoManager {


    ////////////
    // FIELDS //
    ////////////

    private static Set<ExceptionMetaInfo> infos = new TreeSet<>();


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessor

    public static Set<ExceptionMetaInfo> getInfos() {
        return infos;
    }

    // Mutators

    public static ExceptionMetaInfo describe(Class<? extends Throwable> clazz, String description) {
        return describe(clazz, description, false);
    }
    public static ExceptionMetaInfo describe(Class<? extends Throwable> clazz, String description, boolean important) {
        ExceptionMetaInfo info = new ExceptionMetaInfo(clazz.getName(), description, important);
        synchronized(infos) {
            infos.add(info);
        }
        return info;
    }
}
