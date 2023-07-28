package replete.util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import replete.errors.RuntimeConvertedException;
import replete.text.StringLib;
import replete.text.StringUtil;
import sun.reflect.ReflectionFactory;

public class ReflectionUtil {


    //////////////////
    // FIELD VALUES //
    //////////////////

    // Class variable/static field
    public static <T> T get(Class<?> clazz, String fieldName) {
        return get(fieldName, false, null, clazz);
    }

    // Instance variable/field
    public static <T> T get(Object target, String fieldName) {
        return get(fieldName, true, target, null);
    }

    private static <T> T get(String fieldName, boolean isObj, Object target, Class<?> clazz) {
        try {
            if(isObj) {
                clazz = target.getClass();
            }
            Field f = getField(clazz, fieldName);
            f.setAccessible(true);
            return (T) f.get(target);
        } catch(Exception e) {
            throw new RuntimeConvertedException(
                "Could not get field's value for " +
                (isObj ? describe(target) : describe(clazz)) + ".", e);
        }
    }

    // Class variable/static field
    public static void set(Class<?> clazz, String fieldName, Object value) {
        set(fieldName, false, null, clazz, value);
    }

    // Instance variable/field
    public static void set(Object target, String fieldName, Object value) {
        set(fieldName, true, target, null, value);
    }

    private static void set(String fieldName, boolean isObj, Object target, Class<?> clazz, Object value) {
        try {
            if(isObj) {
                clazz = target.getClass();
            }
            Field f = getField(clazz, fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch(Exception e) {
            throw new RuntimeConvertedException(
                "Could not set field's value for " +
                (isObj ? describe(target) : describe(clazz)) + ".", e);
        }
    }


    ////////////////////////
    // METHOD INVOCATIONS //
    ////////////////////////

    // Class method/static method
    public static <T> T invoke(Class<?> clazz, String methodName) {        // Helps with content assist
        return invoke(methodName, false, null, clazz, new Object[0]);
    }
    public static <T> T invoke(Class<?> clazz, String methodName, Object... args) {
        return invoke(methodName, false, null, clazz, args);
    }

    // Instance method/method
    public static <T> T invoke(Object target, String methodName) {         // Helps with content assist
        return invoke(methodName, true, target, null, new Object[0]);
    }
    public static <T> T invoke(Object target, String methodName, Object... args) {
        return invoke(methodName, true, target, null, args);
    }

    private static <T> T invoke(String methodName, boolean isObj, Object target, Class<?> clazz, Object... args) {
        try {
            if(isObj) {
                clazz = target.getClass();
            }
            Class<?>[] argTypes = new Class<?>[args.length];
            for(int a = 0; a < args.length; a++) {
                if(args[a] != null) {
                    argTypes[a] = args[a].getClass();
                }
            }
            Method m = getMethod(clazz, methodName, argTypes);
            m.setAccessible(true);
            return (T) m.invoke(target, args);
        } catch(Exception e) {
            throw new RuntimeConvertedException(
                "Could not invoke method on " +
                (isObj ? describe(target) : describe(clazz)) +
                " with the given parameters.", e);
        }
    }


    ///////////////
    // EXISTENCE //
    ///////////////

    public static boolean hasField(Object target, String name) {
        return hasField(target.getClass(), name);
    }
    public static boolean hasField(Class<?> target, String name) {
        try {
            getField(target, name);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public static boolean hasMethod(Object target, String name, Class<?>... argTypes) {
        return hasMethod(target.getClass(), name, argTypes);
    }
    public static boolean hasMethod(Class<?> target, String name, Class<?>... argTypes) {
        try {
            getMethod(target, name, argTypes);
            return true;
        } catch(Exception e) {
            return false;
        }
    }


    ///////////////////
    // FIELD OBJECTS //
    ///////////////////

    public static Field[] getFields(Object o) {
        return getFields(o, true);
    }
    public static Field[] getFields(Object o, boolean ignoreOuterInstanceReferences) {
        return getFields(o.getClass(), ignoreOuterInstanceReferences);
    }
    public static Field[] getFields(Class<?> clazz) {
        return getFields(clazz, true);
    }
    public static Field[] getFields(Class<?> clazz, boolean ignoreOuterInstanceReferences) {
        Set<Field> fields = new TreeSet<>((f1, f2) -> {
            String longName1 = f1.getDeclaringClass().getName() + "." + f1.getName();
            String longName2 = f2.getDeclaringClass().getName() + "." + f2.getName();
            return longName1.compareTo(longName2);
        });
        while(clazz != null) {
            try {
                List<Field> list = new ArrayList<>();
                Field[] dfields = clazz.getDeclaredFields();
                for(Field field : dfields) {
                    String name = field.getName();
                    if(name.startsWith("this$")) {
                        if(!ignoreOuterInstanceReferences) {
                            list.add(field);
                        }
                    } else {
                        list.add(field);
                    }
                    field.setAccessible(true);
                }
                fields.addAll(list);
            } catch(SecurityException se) {
                throw new RuntimeConvertedException(
                    "Could not access fields in " + describe(clazz) + ".", se);
            }
            clazz = clazz.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }
    public static Map<String, Field> getAllFieldsByName(Class<?> clazz, boolean publicOnly) {
        Map<String, Field> all = new TreeMap<>();
        while(clazz != null) {
            try {
                Field[] df = clazz.getDeclaredFields();
                for(Field f : df) {
                    if(publicOnly && !Modifier.isPublic(f.getModifiers())) {
                        continue;
                    }
                    f.setAccessible(true);
                    all.put(f.getName(), f);
                }
            } catch(SecurityException se) {
                throw new RuntimeConvertedException(
                    "Could not access methods in " + describe(clazz) + ".", se);
            }
            clazz = clazz.getSuperclass();
        }
        return all;
    }


    ////////////////////
    // METHOD OBJECTS //
    ////////////////////

    public static Method[] getMethods(Object o) {
        return getMethods(o.getClass());
    }
    public static Method[] getMethods(Class<?> clazz) {
        Set<Method> methods = new TreeSet<>((m1, m2) -> {
            String longName1 = m1.getDeclaringClass().getName() + "." + m1.getName();
            String longName2 = m2.getDeclaringClass().getName() + "." + m2.getName();
            return longName1.compareTo(longName2);
        });
        while(clazz != null) {
            try {
                methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
            } catch(SecurityException se) {
                throw new RuntimeConvertedException(
                    "Could not access methods in " + describe(clazz) + ".", se);
            }
            clazz = clazz.getSuperclass();
        }
        return methods.toArray(new Method[0]);
    }
    public static Map<String, List<Method>> getAllMethodsByName(Class<?> clazz, boolean publicOnly) {
        Map<String, List<Method>> all = new TreeMap<>();
        while(clazz != null) {
            try {
                Method[] dm = clazz.getDeclaredMethods();
                for(Method m : dm) {
                    if(publicOnly && !Modifier.isPublic(m.getModifiers())) {
                        continue;
                    }
                    m.setAccessible(true);
                    List<Method> ms = all.get(m.getName());
                    if(ms == null) {
                        ms = new ArrayList<>();
                        all.put(m.getName(), ms);
                    }
                    ms.add(m);
                }
            } catch(SecurityException se) {
                throw new RuntimeConvertedException(
                    "Could not access methods in " + describe(clazz) + ".", se);
            }
            clazz = clazz.getSuperclass();
        }
        return all;
    }


    /////////////////////////
    // CONSTRUCTOR OBJECTS //
    /////////////////////////

    // Pure Java method of getting the no-argument constructor.
    public static Constructor getNoArgConstructor(Class<?> clazz) {
        try {
            for(Constructor constructor : clazz.getDeclaredConstructors()) {
                if(constructor.getParameterTypes().length == 0) {
                    constructor.setAccessible(true);
                    return constructor;
                }
            }
        } catch(Exception e) {
            throw new RuntimeConvertedException(
                "Could not access constructors in " + describe(clazz) + ".", e);
        }
        throw new RuntimeException(
            "Could not find no-argument constructor in " + describe(clazz) + ".");
    }

    // This is the infamous workaround for classes not having default (no-arg) constructors.
    // This technique was dug out of XStream's code.  In "enahnced mode" it uses this
    // code to not require classes to have a default constructor.
    //   com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider
    // This technically isn't official Java API and thus could be removed someday
    // though they don't currently have any plans to do so as they don't have a
    // replacement in mind.  This isn't guaranteed to work on really, really old
    // JVM's or extremely off-brand JVM's.
    public static Constructor getMungedConstructor(Class clazz) {
        try {
            ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
            return reflectionFactory.newConstructorForSerialization(clazz,
                Object.class.getDeclaredConstructor(new Class[0]));
        } catch(Exception e) {
            throw new RuntimeConvertedException(
                "Could not access constructors in " + describe(clazz) + ".", e);
        }
    }


    ////////////
    // CREATE //
    ////////////

    public static <T> T create(Class clazz) {
        return create(false, null, clazz);
    }
    public static <T> T create(String className) {
        return create(true, className, null);
    }

    private static <T> T create(boolean isStr, String className, Class clazz) {
        try {
            if(isStr) {
                clazz = Class.forName(className);
            }
            Constructor ctor = getNoArgConstructor(clazz);
            ctor.setAccessible(true);
            return (T) ctor.newInstance(new Object[0]);
        } catch(Exception e) {
            throw new RuntimeConvertedException(
                "Could not create object for " +
                (isStr ? describe(className) : describe(clazz)), e);
        }
    }

    public static <T> T createUninit(Class clazz) {
        return createUninit(false, null, clazz);
    }
    public static <T> T createUninit(String className) {
        return createUninit(true, className, null);
    }

    // Using the munged constructor has the effect of ensuring that
    // NO other constructors are called (this might be obvious but
    // not even no-argument constructors - with any scope modifier -
    // are called on the object) AND that no instance variables are
    // initialized either.  In other words, a field in your object,
    //     private int age = 100;
    // will not get the value 100 and instead be left at 0.
    private static <T> T createUninit(boolean isStr, String className, Class clazz) {
        try {
            if(isStr) {
                clazz = Class.forName(className);
            }
            Constructor ctor = getMungedConstructor(clazz);
            ctor.setAccessible(true);
            return (T) ctor.newInstance(new Object[0]);
        } catch(Exception e) {
            throw new RuntimeConvertedException(
                "Could not create object for " +
                (isStr ? describe(className) : describe(clazz)), e);
        }
    }


    //////////
    // MISC //
    //////////

    public static <T> T getOuterInstance(Object inner) {
        try {
            Class<?> x = inner.getClass();
            if(!x.isMemberClass()) {
                throw new IllegalArgumentException("instance must be from a member class");
            }
            Field field = x.getDeclaredField("this$0");
            field.setAccessible(true);
            return (T) field.get(inner);
        } catch(Exception e) {
            throw new RuntimeConvertedException(e);
        }
    }

    public static Map<String, Object> getFieldValues(Object obj) {
        Map<String, Field> fields = getAllFieldsByName(obj.getClass(), false);
        try {
            Map<String, Object> fieldValues = new LinkedHashMap<>();
            for(String key : fields.keySet()) {
                Field field = fields.get(key);
                Object value = field.get(obj);
                fieldValues.put(key, value);
            }
            return fieldValues;
        } catch(Exception e) {
            throw new RuntimeConvertedException("Could not create object", e);
        }
    }

    private static String describe(Object obj) {
        return "object " + (obj == null ? StringLib.NULL : StringUtil.toStringObject(obj));
    }
    private static String describe(Class clazz) {
        return "class " + (clazz == null ? StringLib.NULL : clazz.getName());
    }
    private static String describe(String className) {
        return "class " + (className == null ? StringLib.NULL : className);
    }


    ////////////
    // HELPER //
    ////////////

    public static Field getField(Object target, String fieldName) {
        return getField(target.getClass(), fieldName);
    }
    public static Field getField(Class<?> clazz, String fieldName) {
        Class<?> orig = clazz;
        while(clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch(SecurityException se) {
                throw new RuntimeConvertedException("Could not access field '" + fieldName +
                    "' in class '" + orig.getName() + "'.", se);
            } catch(NoSuchFieldException nsfe) {}
            clazz = clazz.getSuperclass();
        }
        throw new RuntimeException("Could not find field '" + fieldName +
            "' in class '" + orig.getName() + "'.");
    }

    public static Method getMethod(Object obj, String methodName, Class<?>... argTypes) {
        return getMethod(obj.getClass(), methodName, argTypes);
    }
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... argTypes) {
        Method[] all = getMethods(clazz);
        for(Method m : all) {
            if(m.getName().equals(methodName)) {
                Class<?>[] pTypes = m.getParameterTypes();
                boolean good = true;
                int a = 0;
                for(Class<?> type : pTypes) {
                    if(type != null && argTypes[a] != null && !type.isAssignableFrom(argTypes[a])) {
                        good = false;
                        break;
                    }
                    a++;
                }
                if(good) {
                    return m;
                }
            }
        }
        throw new RuntimeException("Could not find method '" + methodName +
            "' in class '" + clazz.getName() + "' with the given parameter types.");
    }


    ///////////
    // DEBUG //
    ///////////

    public static void printFields(Object o) {
        printValues(o, true, false);
    }
    public static void printMethods(Object o) {
        printValues(o, false, true);
    }
    public static void printValues(Object o) {
        printValues(o, true, true);
    }
    public static void printValues(Object o, boolean fields, boolean methods) {
        if(fields) {
            System.out.println("Field Values:");
            boolean has = false;
            for(Field field : ReflectionUtil.getFields(o)) {
                field.setAccessible(true);
                try {
                    String ts = getValTS(field.get(o));
                    System.out.println("   " + field.getName() + " = " + ts);
                } catch(Exception e) {
                    System.out.println("   " + field.getName() + " = <REFLECTION ERROR>");
                }
                has = true;
            }
            if(!has) {
                System.out.println("   <none>");
            }
        }

        if(methods) {
            System.out.println("Method Values:");
            boolean has = false;
            for(Method method : ReflectionUtil.getMethods(o)) {
                if(method.getName().contains("Contents")) {
                    System.out.println();
                }
                if((method.getName().matches("^get[A-Z].*$") || method.getName().matches("^is[A-Z].*$")) &&
                        method.getParameterTypes().length == 0 &&
                        !method.getReturnType().equals(void.class)) {
                    method.setAccessible(true);
                    try {
                        String ts = getValTS(method.invoke(o));
                        System.out.println("   " + method.getName() + "() = " + ts);
                    } catch(Exception e) {
                        System.out.println("   " + method.getName() + "() = <REFLECTION ERROR>");
                    }
                }
                has = true;
            }
            if(!has) {
                System.out.println("   <none>");
            }
        }
    }
    private static String getValTS(Object val) {
        String ts;
        if(val == null) {
            ts = "null";
        } else if(val.getClass().isArray()) {
            ts = "[";
            int len = Array.getLength(val);
            for(int i = 0; i < len; i++) {
                ts += Array.get(val, i);
                if(i != len - 1) {
                    ts += ", ";
                }
            }
            ts += "]";
        } else {
            ts = val.toString();
        }
        return ts;
    }
    public static void printMembers(Object o) {
        printMembers(o.getClass());
    }
    public static void printMembers(Class<?> clazz) {
        System.out.println("Class: " + clazz);
        System.out.println("Fields:");
        for(Field f : getFields(clazz)) {
            System.out.println("   " + f);
        }
        System.out.println("Methods:");
        for(Method m : getMethods(clazz)) {
            System.out.println("   " + m);
        }
    }

    // TODO: This method unsafe for reasons noted in Map section.
    // Suggest using -Dsun.io.serialization.extendedDebugInfo=true
    // instead.
    public static List<String> findNonSerializableFields(Object o) {
        List<String> paths = new ArrayList<>();
        Set<Object> visited = new HashSet<>();
        try {
            findNonSerializableFields(o, o == null ? null : o.getClass(), paths, visited, "<ROOT>");
        } catch(Exception e) {
            e.printStackTrace();
        }
        Collections.sort(paths);
        return paths;
    }
    private static void findNonSerializableFields(Object obj, Class<?> type, List<String> paths,
                                                  Set<Object> visited, String prefix) throws Exception {
        if(obj != null) {

            // Infinite recursion detection
            if(visited.contains(obj)) {
                return;
            }
            visited.add(obj);

            // Arrays
            if(type.isArray()) {
                for(int i = 0; i < Array.getLength(obj); i++) {
                    Object newObj = Array.get(obj, i);
                    Class<?> newType = newObj == null ? type.getComponentType() : newObj.getClass();
                    findNonSerializableFields(newObj, newType, paths, visited,
                        prefix + "[" + i + "]");
                }

            } else if(Collection.class.isAssignableFrom(type)) {
                Collection c = (Collection) obj;
                if(!Serializable.class.isAssignableFrom(obj.getClass())) {
                    paths.add("(DEFINITE) NON-SERIALIZABLE: " + prefix + " (" + obj.getClass().getName() + ")");
                    //return;  // Optional: no need to recurse if this object non-serializable.
                }
                Object[] arr = c.toArray();
                for(int i = 0; i < arr.length; i++) {
                    Object newObj = arr[i];
                    Class<?> newType = newObj == null ? null : newObj.getClass();
                    findNonSerializableFields(newObj, newType, paths, visited,
                        prefix + "[" + i + "]");
                }

            } else if(Map.class.isAssignableFrom(type)) {
                // TODO: Does not properly find non-serializable fields.
                // The map object itself has fields that might be
                // non-serializable.  But, just using pure reflection
                // on object with custom serialization, is problematic.
                // The information you seek is probably in transient fields.
                // But it's almost impossible to know which transient fields
                // are referenced in the custom serialization/deserialization.
                Map m = (Map) obj;
                if(!Serializable.class.isAssignableFrom(obj.getClass())) {
                    paths.add("(DEFINITE) NON-SERIALIZABLE: " + prefix + " (" + obj.getClass().getName() + ")");
                    //return;  // Optional: no need to recurse if this object non-serializable.
                }
                for(Object key : m.keySet()) {
                    Object val = m.get(key);
                    Class<?> newType = key == null ? null : key.getClass();
                    findNonSerializableFields(key, newType, paths, visited,
                        prefix + "{K~" + key + "}");
                    newType = val == null ? null : val.getClass();
                    findNonSerializableFields(val, newType, paths, visited,
                        prefix + "{V~" + key + "}");
                }

            } else {
                if(!Serializable.class.isAssignableFrom(obj.getClass())) {
                    paths.add("(DEFINITE) NON-SERIALIZABLE: " + prefix + " (" + obj.getClass().getName() + ")");
                    //return;  // Optional: no need to recurse if this object non-serializable.
                }

                Field[] fields = getFields(obj, false);
                for(Field field : fields) {
                    field.setAccessible(true);
                    int mod = field.getModifiers();
                    if(Modifier.isStatic(mod) || Modifier.isTransient(mod) ) {
                        continue;
                    }
                    Object newObj = field.get(obj);
                    Class<?> newType = field.getType();
                    findNonSerializableFields(newObj, newType, paths, visited,
                        prefix + "." + field.getName());
                }

            }

        } else if(type != null) {
            addNullObjTypeToPaths(paths, type, prefix, type.isArray());
        }
    }

    private static void addNullObjTypeToPaths(List<String> paths, Class<?> type, String prefix, boolean array) {
        if(array) {
            type = type.getComponentType();
        }
        if(!type.isPrimitive() && !Serializable.class.isAssignableFrom(type)) {
            String bk = array ? "[]" : "";
            paths.add("(POSSIBLE) NON-SERIALIZABLE: " + prefix + " (" + type.getName() + bk + ") [currently = null]");
        }
    }

    // "discoverySummaryState.sourceSummaryStates.table[14].value.elementData[6].httpSummaryState.avgActualSize"
    // "disc*.so*["A"][4].httpSumma*.avgActualSize"
    // Map<String, Person> x = new HashMap<>();
//    public static Object getFieldByPath(Object target, String path) {
//        Object current = target;
//        String[] components = path.split("\\.");
//        String debug = "";
//        for(String comp : components) {
//            String compRegex = PatternUtil.convertWildcardToRegex(comp);
//            Field[] fields = ReflectionUtil.getFields(current);
//            boolean found = false;
//            for(Field field : fields) {
//                String nm = field.getName();
//                if(StringUtil.matches(nm, compRegex, true)) {
//                    current = field.get(current);
//                    found = true;
//                    debug += "[" + comp + "=" + (current == null ? "(NULL)" : "<OBJ>") + "]";
//                }
//            }
//            if(!found) {
//                debug += ".........
//            }
//        }
//    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        Bob bob = createUninit(Bob.class);
        bob.getMonkey();
//        Bob bobbiees = new Bob();
//        printValues(bobbiees);
//        if(true) {
//            return;
//        }
//        Root root = new Root();
//        root.list.add("asdfas");
//        root.list.add(new Dog());
//        root.m.put("A", new Dog());
//        root.m.put(new Dog(), "Y");
//        for(String s : findNonSerializableFields(root)) {
//            System.out.println(s);
//        }
//        if(true) {
//            return;
//        }
//
//        System.out.println(get("pua", new Abbot()));
//        System.out.println(get("pva", new Abbot()));
//        System.out.println(get("pux", new Barney()));
//        System.out.println(get("pvx", new Barney()));
//        System.out.println(get("pua", new Barney()));
//        System.out.println(get("pva", new Barney()));
//        System.out.println(get("pub", new Barney()));
//        System.out.println(get("pvb", new Barney()));
////        System.out.println(invoke("dos",new Barney()));  // Broken
////        System.out.println(invoke("what",new Barney())); // Broken
//        System.out.println(invoke("method", StaticClass.class, 3, "What", 'a'));
//        printMembers(new Barney());

        Map<String, List<Method>> x = getAllMethodsByName(new Abbot().getClass(), true);
        for(String s : x.keySet()) {
            System.out.println(s);
            List<Method> m = x.get(s);
            for(Method y : m) {
                System.out.println("    " + y.getDeclaringClass().getName() + "." + y.getName() + "(" + y.hashCode() + ")");
            }
        }
    }

    static class Root  {
        int[] a;
        Object[] o;
//        Object[] y = new Object[] {new Object(), new Object(), new String(), new Dog()};
//        Object[] z = new Object();
//        Object[] x = {new Object(), new String(), new Object()};
        Set list = new LinkedHashSet();
        Map m = new HashMap();
//        int x;
//        Dog dog;
//        Dog rover = new Dog();
//        Object[] arr = {new Dog(), new Dog()};
    }
    static class Dog implements Serializable {
        Cat cat = new Cat();
        Map d = new HashMap();
        public Dog() {
            d.put("Q", new Cat());
            d.put("Q2", new Cat());
        }
    }
    static class Cat {
        Object z = "asd";
    }

    static class Bob {
        private int age = 77;
        public Bob() {
            System.out.println("BOB CREATE");
        }
        public void getMonkey() {
            System.out.println("HI I'm " + age + " years old.");
        }
    }
}

class Xray {
    public int pux = 6;
    protected int pvx = 66;
    private int what() {
        return pux * 3;
    }
}
class Abbot extends Xray {
    public int pua = 7;
    protected int pva = 77;
    @Override
    public String toString() {
        return super.toString();
    }
    public void wait(String x) {

    }
}
class Barney extends Abbot {
    protected int pva = 999;
    public int pub = 8;
    protected int pvb = 88;

    public void dos(Object... args) {
        for(Object arg : args) {
            System.out.println(arg);
        }
        System.out.println("dos" + (pub*10));
    }
}
class StaticClass {
    public static String method(Integer y, String what, Character x) {
        return "Hello World " + y + " " + what + " " + x;
    }
}