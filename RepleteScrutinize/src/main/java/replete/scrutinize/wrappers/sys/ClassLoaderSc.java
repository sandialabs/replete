package replete.scrutinize.wrappers.sys;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import replete.scrutinize.core.BaseSc;

public class ClassLoaderSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return ClassLoader.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "getSystemClassLoader",
        };
    }

    @Override
    public Map<String, Object> getCustomFields(Object nativeObj) {
        ClassLoader i = (ClassLoader) nativeObj;
        Map<String, Object> fields = new TreeMap<>();

        if(i instanceof URLClassLoader) {
            URL[] urls = ((URLClassLoader) i).getURLs();
            fields.put("Sources", urls);
//
            try {
                Field f = ClassLoader.class.getDeclaredField("classes");
                f.setAccessible(true);
                Vector classes = (Vector) f.get(i);
                Object[] classesArr = classes.toArray();
                fields.put("Classes Loaded", classesArr);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return fields;
    }
}
