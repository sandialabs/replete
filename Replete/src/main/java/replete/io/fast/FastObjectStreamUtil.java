package replete.io.fast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import replete.util.ReflectionUtil;

public class FastObjectStreamUtil {


    ////////////
    // FIELDS //
    ////////////

    private static Map<Class, Map<String, Field>> allFields = new HashMap<>();
    private static Map<Class, Method> writeObjectMethods = new HashMap<>();
    private static Map<Class, Method> writeReplaceMethods = new HashMap<>();
    private static Map<Class, Method> readObjectMethods = new HashMap<>();
    private static Map<Class, Method> readResolveMethods = new HashMap<>();

    public static Method getWriteObjectMethod(Class clazz) {
        Method writeObject;
        if(writeObjectMethods.containsKey(clazz)) {
            writeObject = writeObjectMethods.get(clazz);
        } else {
            writeObject = getWriteObjectMethodInner(clazz);
            writeObjectMethods.put(clazz, writeObject);
        }
        return writeObject;
    }
    public static Method getWriteReplaceMethod(Class clazz) {
        Method writeReplace;
        if(writeReplaceMethods.containsKey(clazz)) {
            writeReplace = writeReplaceMethods.get(clazz);
        } else {
            writeReplace = getWriteReplaceMethodInner(clazz);
            writeReplaceMethods.put(clazz, writeReplace);
        }
        return writeReplace;
    }

    private static Method getWriteObjectMethodInner(Class clazz) {
        try {
            Method m = clazz.getDeclaredMethod("writeObject", new Class<?>[] { ObjectOutputStream.class });
            m.setAccessible(true);
            int mods = m.getModifiers();
            return ((m.getReturnType() == Void.TYPE) &&
                    ((mods & Modifier.STATIC) == 0) &&
                    ((mods & Modifier.PRIVATE) != 0)) ? m : null;
        } catch(Exception e) {
            return null;
        }
    }
    private static Method getWriteReplaceMethodInner(Class clazz) {
        try {
            Method m = clazz.getDeclaredMethod("writeReplace");
            if(m.getReturnType().equals(Void.TYPE)) {
                return null;
            }
            m.setAccessible(true);
            return m;
        } catch(Exception e) {
            return null;
        }
    }

    public static Method getReadObjectMethod(Class clazz) {
        Method readObject;
        if(readObjectMethods.containsKey(clazz)) {
            readObject = readObjectMethods.get(clazz);
        } else {
            readObject = getReadObjectMethodInner(clazz);
            readObjectMethods.put(clazz, readObject);
        }
        return readObject;
    }
    public static Method getReadResolveMethod(Class clazz) {
        Method readResolve;
        if(readResolveMethods.containsKey(clazz)) {
            readResolve = readResolveMethods.get(clazz);
        } else {
            readResolve = getReadResolveMethodInner(clazz);
            readResolveMethods.put(clazz, readResolve);
        }
        return readResolve;
    }

    private static Method getReadObjectMethodInner(Class clazz) {
        try {
            Method m = clazz.getDeclaredMethod("readObject", new Class<?>[] { ObjectInputStream.class });
            m.setAccessible(true);
            int mods = m.getModifiers();
            return ((m.getReturnType() == Void.TYPE) &&
                    ((mods & Modifier.STATIC) == 0) &&
                    ((mods & Modifier.PRIVATE) != 0)) ? m : null;
        } catch(Exception e) {
            return null;
        }
    }
    private static Method getReadResolveMethodInner(Class clazz) {
        try {
            Method m = clazz.getDeclaredMethod("readResolve");
            if(m.getReturnType().equals(Void.TYPE)) {
                return null;
            }
            m.setAccessible(true);
            return m;
        } catch(Exception e) {
            return null;
        }
    }

    public static Map<String, Field> getFields(Class clazz) {
        Map<String, Field> fields = allFields.get(clazz);
        if(fields == null) {
            Field[] fieldArr = ReflectionUtil.getFields(clazz);
            fields = new LinkedHashMap<String, Field>();
            for(Field field : fieldArr) {
                field.setAccessible(true);
                fields.put(field.getName(), field);
            }
            allFields.put(clazz, fields);
        }
        return fields;
    }
    public static Map<String, Field> getFieldsNoStaticTransient(Class clazz) {
        Map<String, Field> fields = allFields.get(clazz);
        if(fields == null) {
            Field[] fieldArr = ReflectionUtil.getFields(clazz);
            fields = new LinkedHashMap<String, Field>();
            for(Field field : fieldArr) {
                if(Modifier.isStatic(field.getModifiers()) ||
                                Modifier.isTransient(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                fields.put(field.getName(), field);
            }
            allFields.put(clazz, fields);
        }
        return fields;
    }
}
