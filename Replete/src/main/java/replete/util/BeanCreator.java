package replete.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Derek Trumbo
 */

public class BeanCreator {

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> clazz, Map map) throws Exception {
        return create(clazz, map, "");
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> clazz, Map map, String prefix) throws Exception {
        Object bean = instantiateBean(clazz);

        String actualPrefix = (!prefix.equals("") ? prefix + "." : "");
        populateFields(bean, convertRequestMap(map), actualPrefix);
        //populateFields(bean, map, actualPrefix);   // USE THIS ONE TO RUN TESTS LOCALLY
        return (T) bean;
    }

    private static Map<String, String> convertRequestMap(Map requestMap) {
        Map<String, String> newMap = new LinkedHashMap<String, String>();
        for(Object key : requestMap.keySet()) {
            String[] values = (String[]) requestMap.get(key);
            newMap.put((String) key, values[0]);
        }
        return newMap;
    }

    public static Object instantiateBean(Class<?> clazz) throws Exception {
        Constructor<?> ctor = clazz.getConstructor((Class<?>[]) null);
        Object o = ctor.newInstance((Object[]) null);
        return o;
    }

    private static void populateFields(Object bean, Map<String, String> map, String prefix) throws Exception {

        Field[] fields = bean.getClass().getDeclaredFields();

        for(Field f : fields) {

            f.setAccessible(true);

            // If the field is a primitive or a string...
            if(isSimpleType(f.getType())) {
                String value = map.get(prefix + f.getName());
                boolean hasValue = (value != null && !value.equals(""));
                if(hasValue) {
                    f.set(bean, instantiateSimpleType(f.getType(), value));
                }

            // Else... it's some object other than a primitive or a String.
            } else {

                boolean isCollection = Collection.class.isAssignableFrom(f.getType());
                boolean usesGenerics = f.getGenericType() instanceof ParameterizedType;
                Collection collection = instantiateCollection(f.getType());

                // If this object is a Java collection, then don't just try to
                // deserialize the specific instance variables of that object, as
                // that is generally not what you want.  Instead look for all the
                // objects in the map that exist in this collection, and recursively
                // deserialize them, before adding them to a newly-instantiated
                // collection object and setting the collection field to this object.
                // NOTE: Without generics, we wouldn't know what object to deserialize
                // from the map, so that is a requirement.  Does NOT handle
                // List<String>, List<Integer> yet -- only List<Non-Simple-Object>.
                if(isCollection && usesGenerics && collection != null) {

                    int current = 0;
                    String newPrefix = prefix + f.getName() + "-" + current + ".";
                    boolean nextElement = mapContainsKeyWithPrefix(map, newPrefix);

                    if(nextElement) {
                        while(nextElement) {
                            ParameterizedType type = (ParameterizedType) f.getGenericType();
                            Object beanInBean = instantiateBean((Class<?>) type.getActualTypeArguments()[0]);
                            populateFields(beanInBean, map, newPrefix);
                            collection.add(beanInBean);

                            newPrefix = prefix + f.getName() + "-" + (++current) + ".";
                            nextElement = mapContainsKeyWithPrefix(map, newPrefix);
                        }
                        f.set(bean, collection);
                    }

                // Else it's a regular object whose instance variables are expected
                // to be directly provided in the parameter map.
                } else {
                    String newPrefix = prefix + f.getName() + ".";

                    // If there's at least on member in the map for this bean, recursively populate it.
                    if(mapContainsKeyWithPrefix(map, newPrefix)) {
                        Object beanInBean = instantiateBean(f.getType());
                        populateFields(beanInBean, map, newPrefix);
                        f.set(bean, beanInBean);
                    }
                }
            }
        }
    }

    private static boolean isSimpleType(Class<?> type) {
        return type.equals(boolean.class) || type.equals(Boolean.class) ||
               type.equals(byte.class) || type.equals(Byte.class) ||
               type.equals(char.class) || type.equals(Character.class) ||
               type.equals(short.class) || type.equals(Short.class) ||
               type.equals(int.class) || type.equals(Integer.class) ||
               type.equals(float.class) || type.equals(Float.class) ||
               type.equals(long.class) || type.equals(Long.class) ||
               type.equals(double.class) || type.equals(Double.class) ||
               type.equals(String.class);
    }

    private static Object instantiateSimpleType(Class<?> type, String value) {

        if(value == null) {
            throw new NullPointerException("instantiateSimpleType must have non-null value");
        }

        if(type.equals(boolean.class) || type.equals(Boolean.class)) {
            return new Boolean(value);
        } else if(type.equals(byte.class) || type.equals(Byte.class)) {
            return  new Byte(value);
        } else if(type.equals(char.class) || type.equals(Character.class)) {
            return value.charAt(0);
        } else if(type.equals(short.class) || type.equals(Short.class)) {
            return new Short(value);
        } else if(type.equals(int.class) || type.equals(Integer.class)) {
            return new Integer(value);
        } else if(type.equals(float.class) || type.equals(Float.class)) {
            return new Float(value);
        } else if(type.equals(long.class) || type.equals(Long.class)) {
            return new Long(value);
        } else if(type.equals(double.class) || type.equals(Double.class)) {
            return new Double(value);
        } else if(type.equals(String.class)) {
            return value;
        }

        return null;
    }

    private static Collection<?> instantiateCollection(Class<?> type) {
        if(type.equals(List.class) || type.equals(ArrayList.class)) {
            return new ArrayList();
        } else if(type.equals(Set.class) || type.equals(HashSet.class)) {
            return new HashSet();
        }
        return null;
    }

    private static boolean mapContainsKeyWithPrefix(Map<String, String> map, String prefix) {
        for(String key : map.keySet()) {
            if(key.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    //////////
    // TEST //
    //////////

    public static void main(String[] args) throws Exception {
        String requestString = "http://what.why/who.jsp?unknownValue=what";
        runTest(requestString);

        requestString = "http://what.why/who.jsp?ii=6&b.c.ch=Z&b.bool=true";
        runTest(requestString);

//        requestString = "http://what.why/who.jsp?ii=6&b.str=dog";
//        runTest(requestString);
//
//        requestString = "http://what.why/who.jsp?ii=6&b.str=dog";
//        runTest(requestString);
//
        requestString = "http://what.why/who.jsp?x=90&f=4.5&s-0.ch=%&s-1.ch=+&bb-0.bool=true&bb-1.c.ch=$";
        runTestList(requestString);
    }

    private static void runTest(String requestString) throws Exception {
        Map<String, String> map = getMap(getParams(requestString));
        A a = create(A.class, map);
        System.out.println("Resulting A ==> [" + a + "]\n");
    }

    private static void runTestList(String requestString) throws Exception {
        Map<String, String> map = getMap(getParams(requestString));
        DList d = create(DList.class, map);
        System.out.println("Resulting DList ==> [" + d + "]\n");
    }

    public static String getParams(String request) {
        int question = request.indexOf("?");
        String params = request.substring(question + 1);
        return params;
    }

    public static Map<String, String> getMap(String params) {
        Map<String, String> map = new HashMap<String, String>();
        String[] keyValuePairs = params.split("&");
        for(String keyValuePair : keyValuePairs) {
            int equals = keyValuePair.indexOf("=");
            String key = keyValuePair.substring(0, equals);
            String value = keyValuePair.substring(equals + 1);
            map.put(key, value);
        }
        return map;
    }

    static class A {
        int ii;
        float ff;
        B b;
        double dd;

        public A() {}

        @Override
        public String toString() {
            return "ii=" + ii + ",ff=" + ff + ",b=[" + b + "],dd=" + dd;
        }
    }

    static class B {
        String str;
        boolean bool;
        C c;

        public B() {}

        @Override
        public String toString() {
            return "str=" + str + ",bool=" + bool + ",c=[" + c + "]";
        }
    }

    static class C {
        char ch;

        public C() {}

        @Override
        public String toString() {
            return "ch=" + ch;
        }
    }

    static class DList {
        int x;
        float f;
        List<C> s;
        Set<B> bb;

        public DList() {}

        @Override
        public String toString() {
            return "x=" + x +",f=" + f + ",s=" + s + ",bb=" + bb;
        }
    }
}
