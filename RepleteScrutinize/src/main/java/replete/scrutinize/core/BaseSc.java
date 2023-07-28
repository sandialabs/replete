package replete.scrutinize.core;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import replete.text.StringUtil;
import replete.util.ReflectionUtil;

public abstract class BaseSc implements IBaseSc {


    ////////////
    // FIELDS //
    ////////////

    // Per class bookkeeping just used during transformation phase.
    private transient static Map<Class<? extends BaseSc>, StaticInfo> allStaticInfo = new HashMap<>();
    private transient static Map<Class<?>, Map<Object, Object>> transformObjectMappings = new HashMap<>();

    // Per transformed object serialized fields.
    protected StaticInfo staticInfoSzLink = null;
    protected Map<String, ScFieldResult> fields = null;
    protected String handledObjectClass;
    protected int handledObjectHC;
    protected int handledObjectIHC;


    ///////////////
    // INTERFACE //
    ///////////////

    @Override
    public Map<String, Object> getCustomFields(Object nativeObj) {
        return null;
    }
    @Override
    public String[] getExtractedFields() {
        return null;
    }
    @Override
    public Class<?> getHandledClass() {
        return null;
    }

    @Override
    public Map<String, ScFieldResult> getFields() {
        return fields;
    }
    private Map<String, ScFieldResult> getOrCreateFields() {       // Internally-used
        if(fields == null) {
            fields = new TreeMap<>();
        }
        return fields;
    }
    public StaticInfo getStaticInfoSzLink() {
        return staticInfoSzLink;
    }

    private void registerStaticInfo(Class<?> nativeClass) {
        //debug("registering cl", nativeClass);
        if(nativeClass == null) {
            return;
        }

        // synchronize
        StaticInfo staticInfo = allStaticInfo.get(getClass());
        if(staticInfo != null) {
            staticInfoSzLink = staticInfo;
            return;
        }
        staticInfo = new StaticInfo();
        allStaticInfo.put(getClass(), staticInfo);
        staticInfoSzLink = staticInfo;
        // synchronize

        Map<String, List<Method>> allMethods = ReflectionUtil.getAllMethodsByName(nativeClass, false);
        staticInfo.setAllMethods(allMethods);
        Map<String, Field> allFields = ReflectionUtil.getAllFieldsByName(nativeClass, false);
        staticInfo.setAllFields(allFields);

        Set<String> extractedActions = new HashSet<>();
        String[] extactedFields = getExtractedFields();
        if(extactedFields != null) {
            for(String extractedField : extactedFields) {
                ExtractedField ef = new ExtractedField(extractedField);
                extractedField = ef.getFieldName();
                if(allMethods.containsKey(extractedField)) {
                    if(!allMethods.get(extractedField).get(0).getReturnType().equals(Void.TYPE)) {
                        extractedActions.add(extractedField);
                    }
                }
                String getMethod = "get" + StringUtil.capitalize(extractedField);
                if(allMethods.containsKey(getMethod)) {
                    if(!allMethods.get(getMethod).get(0).getReturnType().equals(Void.TYPE)) {
                        extractedActions.add(getMethod);
                    }
                }
                String isMethod = "is" + StringUtil.capitalize(extractedField);
                if(allMethods.containsKey(isMethod)) {
                    if(!allMethods.get(isMethod).get(0).getReturnType().equals(Void.TYPE)) {
                        extractedActions.add(isMethod);
                    }
                }
                String hasMethod = "has" + StringUtil.capitalize(extractedField);
                if(allMethods.containsKey(hasMethod)) {
                    if(!allMethods.get(hasMethod).get(0).getReturnType().equals(Void.TYPE)) {
                        extractedActions.add(hasMethod);
                    }
                }
            }
        }

        Set<String> objectActions = Scrutinizer.getObjectActions();
        Set<String> otherPublicActions = staticInfo.getOtherActions();
        for(String methodName : allMethods.keySet()) {
            List<Method> methods = allMethods.get(methodName);
            boolean atLeastOnePublic = false;
            for(Method method : methods) {
                if(Modifier.isPublic(method.getModifiers())) {
                    atLeastOnePublic = true;
                    break;
                }
            }
            if(atLeastOnePublic && !objectActions.contains(methodName) && !extractedActions.contains(methodName)) {
                otherPublicActions.add(methodName);
            }
        }
    }


    //////////
    // LOAD //
    //////////

    public void load() throws Exception {
        load(null);
    }
    public void load(Object nativeObj) throws Exception {
//        debug("loading obj", nativeObj);
        registerStaticInfo(getHandledClass());

        if(nativeObj != null) {
            handledObjectClass = nativeObj.getClass().getName();
            handledObjectHC = nativeObj.hashCode();
            handledObjectIHC = System.identityHashCode(nativeObj);
        }

        String[] eFields = getExtractedFields();

        // If there are fields to be extracted from either the instance
        // or the class statically...
        if(eFields != null && eFields.length != 0) {
            StaticInfo staticInfo = allStaticInfo.get(getClass());
            Map<String, List<Method>> allMethods = staticInfo.getAllMethods();
            Map<String, Field> allFields = staticInfo.getAllFields();

            // If we're extracting from a specific object instance...
            if(nativeObj != null) {
                fields = getOrCreateFields();
                populateFields(eFields, allMethods, allFields, fields, nativeObj, false);
            }

            // Populate the static fields for this class only once.
            Map<String, ScFieldResult> staticFields = staticInfo.getStaticFields();
            if(staticFields == null) {
                staticInfo.setStaticFields(staticFields = new TreeMap<>());
                populateFields(eFields, allMethods, allFields, staticFields, null, true);
            }
        }

        Map<String, Object> cFields = getCustomFields(nativeObj);
        if(cFields != null && !cFields.isEmpty()) {
            fields = getOrCreateFields();
            for(String cField : cFields.keySet()) {
                Object value = cFields.get(cField);

                ScFieldResult result = new ScFieldResult();
                if(value != null) {
                    Object transformedValue;
                    if(value instanceof Class<?>) {
                        transformedValue = transformClass((Class<?>) value);
                    } else {
                        transformedValue = transformObjectCollectionAware(value, false, false);
                    }
                    result.setValue(transformedValue);
                }

                result.setClassName(value == null ? null : value.getClass().getName());
                fields.put(cField, result);
            }
        }
    }

    private void populateFields(String[] eFields, Map<String, List<Method>> allMethods,
                                Map<String, Field> allFields,
                                Map<String, ScFieldResult> fields, Object nativeObj, boolean isStatic) {
        for(String eField : eFields) {
            ExtractedField ef = new ExtractedField(eField);
            ScFieldResult result = new ScFieldResult();

            try {
                Object value;
                String className;
                if(ef.isUseField()) {
                    boolean usePrivate = ef.isUsePrivate();
                    Field field = findField(allFields, ef.getFieldName(), isStatic, usePrivate);
                    if(field == null) {
                        continue;
                    }
                    fields.put(ef.getPrettyName() == null ? ef.getFieldName() : ef.getPrettyName(), result);
                    value = field.get(nativeObj);
                    className = field.getType().getName();
                } else {
                    Method topMethod = findMethod(allMethods, ef.getFieldName(), isStatic);
                    if(topMethod == null) {
                        continue;
                    }
                    fields.put(ef.getPrettyName() == null ? ef.getFieldName() : ef.getPrettyName(), result);
                    value = topMethod.invoke(nativeObj);
                    className = topMethod.getReturnType().getName();
                }

                if(value != null) {
                    Object transformedValue = transformObjectCollectionAware(value, ef.isCount(), ef.isSort());
                    result.setValue(transformedValue);
                }

                result.setClassName(className);

            } catch(Exception e) {
                result.setException(e);
            }
        }
    }

    private Object transformObjectCollectionAware(Object value, boolean countOnly, boolean sort) throws Exception {
        Object transformedValue;
        if(value.getClass().isArray()) {
            if(countOnly) {
                transformedValue = Array.getLength(value);
            } else {
                int len = Array.getLength(value);
                ListMapSc transformedArray = new ListMapSc(sort);
                for(int i = 0; i < len; i++) {
                    ScFieldResult elemResult = new ScFieldResult();
                    try {
                        Object element = Array.get(value, i);
                        if(element != null) {
                            Object transformedElement = transformObject(element);
                            elemResult.setValue(transformedElement);
                            elemResult.setClassName(element.getClass().getName());
                        } else {
                            elemResult.setClassName(value.getClass().getComponentType().getName());
                        }
                    } catch(Exception e) {
                        elemResult.setException(e);
                    }
                    transformedArray.set(i, elemResult);
                }
                transformedValue = transformedArray;
            }

        } else if(value instanceof Iterable) {
            Iterable iterable = (Iterable) value;
            Iterator it = iterable.iterator();
            if(countOnly) {
                int i = 0;
                while(it.hasNext()) {
                    try {
                        it.next();
                        i++;
                    } catch(Exception e) {
                        i = -1;
                        break;
                    }
                }
                transformedValue = i;
            } else {
                ListMapSc transformedArray = new ListMapSc(sort);
                int i = 0;
                while(it.hasNext()) {
                    ScFieldResult elemResult = new ScFieldResult();
                    try {
                        Object element = it.next();
                        if(element != null) {
                            Object transformedElement = transformObject(element);
                            elemResult.setValue(transformedElement);
                            elemResult.setClassName(element.getClass().getName());
                        }
                    } catch(ConcurrentModificationException e) {
                        elemResult.setException(e);
                        break;
                    } catch(Exception e) {
                        elemResult.setException(e);
                    } finally {
                        transformedArray.set(i++, elemResult);
                    }
                }
                transformedValue = transformedArray;
            }

        } else if(value instanceof Map) {
            Map m = (Map) value;
            if(countOnly) {
                transformedValue = m.size();
            } else {
                ListMapSc transformedArray = new ListMapSc(sort);
                for(Object key : m.keySet()) {
                    ScFieldResult elemResult = new ScFieldResult();
                    try {
                        Object mapValue = m.get(key);
                        if(mapValue != null) {
                            Object transformedElement = transformObject(mapValue);
                            elemResult.setValue(transformedElement);
                            elemResult.setClassName(mapValue.getClass().getName());
                        }
                    } catch(Exception e) {
                        elemResult.setException(e);
                    }
                    if(key == null) {
                        key = "(null)";
                    }
                    transformedArray.put(key.toString(), elemResult);
                }
                transformedValue = transformedArray;
            }

        } else if(value instanceof Enumeration) {
            Enumeration en = (Enumeration) value;
            if(!en.hasMoreElements()) {
                int i = 0;
                while(en.hasMoreElements()) {
                    try {
                        en.nextElement();
                        i++;
                    } catch(Exception e) {
                        i = -1;
                        break;
                    }
                }
                transformedValue = i;
            } else {
                ListMapSc transformedArray = new ListMapSc(sort);
                int i = 0;
                while(en.hasMoreElements()) {
                    ScFieldResult elemResult = new ScFieldResult();
                    try {
                        Object element = en.nextElement();
                        if(element != null) {
                            Object transformedElement = transformObject(element);
                            elemResult.setValue(transformedElement);
                            elemResult.setClassName(element.getClass().getName());
                        }
                    } catch(Exception e) {
                        elemResult.setException(e);
                    }
                    transformedArray.set(i++, elemResult);
                }
                transformedValue = transformedArray;
            }

        } else {
            transformedValue = transformObject(value);

        }

        return transformedValue;
    }

    private Field findField(Map<String, Field> allFields, String eField, boolean staticFlag, boolean usePrivate) {
        Field topField = allFields.get(eField);
        if(topField != null) {
            if(Modifier.isStatic(topField.getModifiers()) != staticFlag) {
                topField = null;
            }
        }
        return topField;
    }

    private Method findMethod(Map<String, List<Method>> allMethods, String eField, boolean staticFlag) {
        Method topMethod;

        if((topMethod = findNonVoidZeroArg(allMethods, eField)) == null) {
            String getMethod = "get" + StringUtil.capitalize(eField);
            if((topMethod = findNonVoidZeroArg(allMethods, getMethod)) == null) {
                String isMethod = "is" + StringUtil.capitalize(eField);
                if((topMethod = findNonVoidZeroArg(allMethods, isMethod)) == null) {
                    String hasMethod = "has" + StringUtil.capitalize(eField);
                    topMethod = findNonVoidZeroArg(allMethods, hasMethod);
                }
            }
        }

        if(topMethod != null) {
            if(Modifier.isStatic(topMethod.getModifiers()) != staticFlag) {
                topMethod = null;
            }
        }

        return topMethod;
    }

    private Method findNonVoidZeroArg(Map<String, List<Method>> allMethods, String eField) {
        if(allMethods.containsKey(eField)) {
            List<Method> methods = allMethods.get(eField);
            for(Method method : methods) {
                if(!method.getReturnType().equals(Void.TYPE)) {
                    if(method.getParameterCount() == 0) {
                        return method;
                    }
                }
            }
        }
        return null;
    }


    ///////////////
    // TRANSFORM //
    ///////////////

    protected Object transformClass(Class<?> nativeClass) throws Exception {
        //debug("xform cl", nativeClass);
        Class<?> scClass = Scrutinizer.getScrutinizeClassFor(nativeClass);
        if(scClass == null) {
            return "<Native Class: " + nativeClass.getName() + ">";
        }
        BaseSc scObject = (BaseSc) scClass.newInstance();
        scObject.load();
        return scObject;
    }
    protected Object transformObject(Object nativeObj) throws Exception {
        //debug("xform obj", nativeObj);
        if(nativeObj == null) {
            return null;
        }
        if(isPrimitive(nativeObj)) {
            return nativeObj;
        }
        Class<?> nativeClass = nativeObj.getClass();

        Map<Object, Object> classMappings = transformObjectMappings.get(nativeClass);
        if(classMappings == null) {
            classMappings = new HashMap<>();
            transformObjectMappings.put(nativeClass, classMappings);
        }
        if(classMappings.containsKey(nativeObj)) {
            return classMappings.get(nativeObj);
        }

        Class<?> scClass = Scrutinizer.getScrutinizeClassForObject(nativeClass);
        if(scClass == null) {
            String typeDesc, extra;
            if(nativeClass.isEnum()) {
                typeDesc = "Enum";
                extra = " \"" + nativeObj + "\"";
            } else {
                typeDesc = "Object";
                extra = "";
            }
            String strObject = "<Native " + typeDesc + ": " + nativeClass.getName() + "@" + nativeObj.hashCode() + extra + ">";
            classMappings.put(nativeObj, strObject);
            return strObject;
        }
        BaseSc scObject = (BaseSc) scClass.newInstance();
        String sts = scObject.getSimpleToString(nativeObj);
        if(sts != null) {
            String strObject = "<Native Object: " + nativeClass.getName() + "@" + nativeObj.hashCode() + " \"" + sts + "\">";
            classMappings.put(nativeObj, strObject);
            return strObject;
        }
        classMappings.put(nativeObj, scObject);
        scObject.load(nativeObj);
        return scObject;
    }

    public static boolean isPrimitive(Object V) {
        return
            V instanceof Character ||
            V instanceof String ||
            V instanceof Number ||
            V instanceof Boolean;
    }


    ///////////
    // PRINT //
    ///////////

    public void print() {
        Set<StaticInfo> printedStaticInfos = new HashSet<>();
        print(this, 0, printedStaticInfos);
    }
    private void print(BaseSc scObject, int level, Set<StaticInfo> printedStaticInfos) {

        StaticInfo staticInfo = scObject.getStaticInfoSzLink();
        if(staticInfo != null && !printedStaticInfos.contains(staticInfo)) {
            printedStaticInfos.add(staticInfo);
            String indent = StringUtil.spaces(4 * level);
            String indent2 = StringUtil.spaces(4 * (level + 1));
            System.out.println(indent + "[StaticInfo@" + hc(staticInfo) + "]");
            if(!staticInfo.getOtherActions().isEmpty()) {
                System.out.println(indent2 + "<OtherActions@" + hc(staticInfo.getOtherActions()) + "> = " + staticInfo.getOtherActions());
            }
            printFieldMap(level + 1, staticInfo.getStaticFields(), true, printedStaticInfos);

        }

        printFieldMap(level, scObject.getFields(), false, printedStaticInfos);
    }

    private void printFieldMap(int level, Map<String, ScFieldResult> fields, boolean isStatic, Set<StaticInfo> printedStaticInfos) {
        if(fields == null || fields.isEmpty()) {
            return;
        }
        String indent = StringUtil.spaces(4 * level);

        for(String field : fields.keySet()) {
            ScFieldResult result = fields.get(field);
            System.out.print(indent + (isStatic ? "^" : "") + field + " = ");

            Object value = result.getValue();
            if(result.getException() != null) {
                System.out.println("<Error: " + result.getException().getClass().getSimpleName() + ">");
            } else if(value == null) {
                System.out.println("(null) {" + result.getClassName() + "}");
            } else if(value instanceof IBaseSc) {
                BaseSc sc = (BaseSc) value;
                String extra = sc.handledObjectClass == null ?
                        "" : " >> " + sc.handledObjectClass + "@" + hc(sc.handledObjectHC, sc.handledObjectIHC);
                System.out.println("{" + sc.getClass().getSimpleName() + "@" + hc(sc) + extra + "-->}");
                print(sc, level + 1, printedStaticInfos);
            } else {
                String str = value.toString();
                str = StringUtil.cleanControl(str);
                if(value instanceof String) {
                    str = "\"" + str + "\"";
                }
                System.out.println(str + " {" + result.getClassName() + "}");
            }
        }
    }

    private String hc(Object o) {
        return hc(o.hashCode(), System.identityHashCode(o));
    }
    private String hc(int hc, int ihc) {
        return hc + "/" + ihc;
    }


    ///////////
    // COUNT //
    ///////////

    public int countNodes() {
        Set<StaticInfo> printedStaticInfos = new HashSet<>();
        int count = countNodes(this, printedStaticInfos);
        return count;
    }
    private int countNodes(BaseSc scObject, Set<StaticInfo> printedStaticInfos) {
        int count = 0;
        StaticInfo staticInfo = scObject.getStaticInfoSzLink();
        if(staticInfo != null && !printedStaticInfos.contains(staticInfo)) {
            printedStaticInfos.add(staticInfo);
            count += countFields(staticInfo.getStaticFields(), true, printedStaticInfos);
        }
        count += countFields(scObject.getFields(), false, printedStaticInfos);
        return count;
    }

    private int countFields(Map<String, ScFieldResult> fields, boolean isStatic, Set<StaticInfo> printedStaticInfos) {
        if(fields == null || fields.isEmpty()) {
            return 0;
        }
        int count = 0;
        for(String field : fields.keySet()) {
            ScFieldResult result = fields.get(field);
            Object value = result.getValue();
            count++;
            if(value instanceof IBaseSc) {
                BaseSc sc = (BaseSc) value;
                count += countNodes(sc, printedStaticInfos);
            }
        }
        return count;
    }


    //////////
    // MISC //
    //////////

    private void debug(String label, Object msg) {
        String m;
        if(msg == null) {
            m = "(NULL)";
        } else if(msg instanceof String) {
            m = msg.toString();
        } else {
            m = msg.getClass().getSimpleName() + "@" + hc(msg);
        }
        System.out.println("> " + this.getClass().getSimpleName() + " <" + label + "> " + m);
    }


    /////////
    // MAP //
    /////////

    @Override
    public int size() {
        return fields == null ? 0 : fields.size();
    }
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }
    @Override
    public boolean containsKey(Object key) {
        return fields == null ? false : fields.containsKey(key);
    }
    @Override
    public boolean containsValue(Object value) {
        return fields == null ? false : fields.containsValue(value);
    }
    @Override
    public Object get(Object key) {
        return fields == null ? null : fields.get(key);
    }
    @Override
    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException();
    }
    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void putAll(Map m) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
    @Override
    public Set keySet() {
        return fields == null ? new HashSet<>() : fields.keySet();
    }
    @Override
    public Collection values() {
        return fields == null ? new ArrayList<>() : fields.values();
    }
    @Override
    public Set entrySet() {
        return fields == null ? new HashSet<>() : fields.entrySet();
    }
}
