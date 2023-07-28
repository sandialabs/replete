package replete.diff.generic;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import replete.collections.IdentityPair;
import replete.diff.Comparison;
import replete.diff.ContainerComparison;
import replete.diff.DiffResult;
import replete.diff.Differ;
import replete.diff.ListMapComparison;
import replete.diff.ObjectComparison;
import replete.diff.SimpleComparison;
import replete.text.StringLib;
import replete.util.ReflectionUtil;

// Recursion/Repeat detection is currently specific to the order that the objects are compares, so diff of
// o1, o2 and o2, o1 are currently neither identified as recursive or duplicate, and this is not ideal with our goals,
// but is currently necessary since we tie the result terminology to which object is on the left vs. the right.

public class GenericObjectDiffer extends Differ<GenericObjectDifferParams, Object> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public GenericObjectDiffer(GenericObjectDifferParams params) {
        super(params);
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    // Accessors (Computed)

    private static boolean isPrimitiveValue(Object value) {
        return
            value instanceof Boolean   ||
            value instanceof Byte      ||
            value instanceof Short     ||
            value instanceof Integer   ||
            value instanceof Long      ||
            value instanceof Float     ||
            value instanceof Double    ||
            value instanceof Character ||
            value instanceof String
        ;
    }
    private static boolean isArray(Object value) {
        return value.getClass().isArray();
    }
    private static boolean isList(Object obj) {
        return obj instanceof List;
    }
    private static boolean isSet(Object obj) {
        return obj instanceof Set;
    }
    private static boolean isMap(Object obj) {
        return obj instanceof Map;
    }

    public static String getContext(int i, String position, Object elem) {
        if(position.equals("")) {
            if(elem != null) {
                return "[" + i + "] : " + elem.getClass().getName();
            }
            return "[" + i + "] : " + StringLib.NULL;
        }
        return "[" + i + "] (Extra " + position + ") : " + elem.getClass().getName();
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public DiffResult diff(Object obj1, Object obj2) {
        Map<IdentityPair<Object, Object>, Comparison> prevComparisons = new HashMap();

        if(obj1 == null || obj2 == null) {
            throw new InvalidTypeException("Cannot compare top-level objects which are null");
        }
        if(isPrimitiveValue(obj1)) {
            throw new InvalidTypeException(
                "Cannot compare top-level objects which are primitives.  "
                + obj1.getClass().getName() + " is a primitive."
            );
        }

        DiffResult result = new DiffResult();
        result.setComparison((ContainerComparison) diffHandler(obj1, obj2, prevComparisons));
        return result;
    }


    ////////////
    // HELPER //
    ////////////

    private Comparison diffHandler(Object obj1, Object obj2, Map<IdentityPair<Object, Object>, Comparison> prevComparisons) {

        Comparison comparison;

        IdentityPair pair = new IdentityPair(obj1, obj2);
        if(prevComparisons.containsKey(pair)) {
            if(prevComparisons.get(pair) == null) {
                comparison = new SimpleComparison(true, "Cycle detected - comparison curtailed");
            } else {
                comparison = prevComparisons.get(pair);
            }
            return comparison;
        }
        prevComparisons.put(pair, null);

        if(obj1 == null || obj2 == null) {
            comparison = diffNulls(obj1, obj2, prevComparisons);
        } else if(!obj1.getClass().equals(obj2.getClass())) {
            comparison = diffDifferentClass(obj1, obj2, prevComparisons);
        } else if(isPrimitiveValue(obj1)) {
            comparison = diffPrimitive(obj1, obj2, prevComparisons);
        } else if(isArray(obj1)) {
            comparison = diffArrays(obj1, obj2, prevComparisons);
        } else if(isList(obj1)) {
            comparison = diffLists((List) obj1, (List) obj2, prevComparisons);
        } else if(isSet(obj1)) {
            comparison = diffSets((Set) obj1, (Set) obj2, prevComparisons);
        } else if(isMap(obj1)) {
            comparison = diffMaps((Map) obj1, (Map) obj2, prevComparisons);
        } else {
            comparison = diffReflection(obj1, obj2, prevComparisons);
        }

        prevComparisons.put(pair, comparison);
        return comparison;
    }

    private SimpleComparison diffNulls(Object obj1, Object obj2, Map<IdentityPair<Object, Object>, Comparison> prevComparisons) {
        if(obj1 != null) {
            return new SimpleComparison(true, obj1.toString(), StringLib.NULL);
        } else if(obj2 != null) {
            return new SimpleComparison(true, StringLib.NULL, obj2.toString());
        }

        return new SimpleComparison(false, StringLib.NULL, StringLib.NULL);
    }
    private ObjectComparison diffDifferentClass(Object obj1, Object obj2, Map<IdentityPair<Object, Object>, Comparison> prevComparisons) {
        Class<?> ancestor = FindClosestInheritanceAncestor.findClosestAncestor(obj1, obj2);
        ObjectComparison comparison = new ObjectComparison();

        if(ancestor != Object.class) {
            Field[] fields = ReflectionUtil.getFields(ancestor);
            compareFields(comparison, fields, obj1, obj2, prevComparisons);
        }

        comparison.addDifference("Type",
            new SimpleComparison(true, "Types do not match", obj1.getClass().getName(), obj2.getClass().getName())
        );

        return comparison;
    }
    private void compareFields(ObjectComparison comparison, Field[] fields, Object obj1, Object obj2, Map<IdentityPair<Object, Object>, Comparison> prevComparisons) {
        int f = 0;
        for(Field field : fields) {
            if(params.isFieldAllowed(field.getName()) && !Modifier.isStatic(field.getModifiers())) {
                Comparison cmp;
                try {
                    field.setAccessible(true);
                    Object value1 = field.get(obj1);
                    Object value2 = field.get(obj2);
                    cmp = diffHandler(value1, value2, prevComparisons);
                } catch(IllegalAccessException | SecurityException e) {
                    cmp = new SimpleComparison(true,
                        "Could not compare fields named '" + field.getName() + "'. Cause: " + e.getClass().getName());
                }

                String context = "Field [" + f + "] : " + field.getName();
                comparison.addDifference(context, cmp);

                f++;
            }
        }
    }

    private SimpleComparison diffPrimitive(Object value1, Object value2, Map<IdentityPair<Object, Object>, Comparison> prevComparisons) {
        return new SimpleComparison(!value1.equals(value2), value1.toString(), value2.toString());
    }

    private ObjectComparison diffReflection(Object obj1, Object obj2, Map<IdentityPair<Object, Object>, Comparison> prevComparisons) {
        ObjectComparison comparison = new ObjectComparison();
        Field[] fields = ReflectionUtil.getFields(obj1);

        compareFields(comparison, fields, obj1, obj2, prevComparisons);
        return comparison;
    }

    public ListMapComparison diffArrays(Object array1, Object array2, Map<IdentityPair<Object, Object>, Comparison> prevComparisons) {
        ListMapComparison arrayComparison = new ListMapComparison(Array.getLength(array1), Array.getLength(array2));

        int safeLength = Math.min(Array.getLength(array1), Array.getLength(array2));
        for(int i = 0; i < safeLength; i++) {
            String context = getArrayContext(i, array1);
            arrayComparison.addDifference(context, diffHandler(Array.get(array1, i), Array.get(array2, i), prevComparisons));
        }

        if(Array.getLength(array1) > safeLength) {
            for(int i = safeLength; i < Array.getLength(array1); i++) {
                String context = getArrayContext(i, array1);
                arrayComparison.addDifference(true,
                    context, Array.get(array1, i).toString(), "Right Array was not long enough");
            }
        } else if(Array.getLength(array2) > safeLength) {
            for(int i = safeLength; i < Array.getLength(array2); i++) {
                String context = getArrayContext(i, array1);
                arrayComparison.addDifference(true,
                    context, "Left Array was not long enough", Array.get(array2, i).toString());
            }
        }

        return arrayComparison;
    }
    private String getArrayContext(int i, Object array1) {
        String context = "[" + i + "] : ";
        context += getArrayTypeAsString(i, array1);
        return context;
    }
    private String getArrayTypeAsString(int i, Object array1) {
        String type = "";

        String className = Array.get(array1, 0).getClass().getName();
        int j = 0;
        while(className.startsWith("[")) {
            type += "Array of ";
            className = className.substring(1);
            j++;
        }
        Object value = array1;
        for(int k = 0; k <= j; k++) {
            value = Array.get(value, k);
        }
        type += value.getClass().getName();
        return type;
    }

    private ListMapComparison diffLists(List list1, List list2, Map<IdentityPair<Object, Object>, Comparison> prevComparisons) {
        ListMapComparison listComparison = new ListMapComparison(list1.size(), list2.size());

        int minSize = Math.min(list1.size(), list2.size());

        for(int i = 0; i < minSize; i++) {
            String context = GenericObjectDiffer.getContext(i, "", list1.get(i));
            listComparison.addDifference(context, diffHandler(list1.get(i), list2.get(i), prevComparisons));
        }

        if(list1.size() > minSize) {
            for(int i = minSize; i < list1.size(); i++) {
                String context = GenericObjectDiffer.getContext(i, "Left", list1.get(i));
                listComparison.addDifference(true, context, list1.get(i).toString(), "Right List was not long enough");
            }
        } else if(list2.size() > minSize) {
            for(int i = minSize; i < list2.size(); i++) {
                String context = GenericObjectDiffer.getContext(i, "Right", list2.get(i));
                listComparison.addDifference(true, context, "Left List was not long enough", list2.get(i).toString());
            }
        }

        return listComparison;
    }

    private ListMapComparison diffSets(Set set1, Set set2, Map<IdentityPair<Object, Object>, Comparison> prevComparisons) {
        ListMapComparison setComparison = new ListMapComparison(set1.size(), set2.size());

        int i = 0;
        for(Object elem1 : set1) {
            String context;
            if(set2.contains(elem1)) {
                context = GenericObjectDiffer.getContext(i, "", elem1);
                setComparison.addDifference(context, diffHandler(elem1, findMatchingElemInSet(elem1, set2), prevComparisons));
            } else {
                context = GenericObjectDiffer.getContext(i, "Left", elem1);
                setComparison.addDifference(true, context, elem1.toString(), "Right Set did not provide a match");
            }
            i++;
        }

        for(Object elem2 : set2) {
            if(!set1.contains(elem2)) {
                String context = GenericObjectDiffer.getContext(i, "Right", elem2);
                setComparison.addDifference(true, context, "Left Set did not provide a match", elem2.toString());
                i++;
            }
        }

        return setComparison;
    }
    private Object findMatchingElemInSet(Object otherElem, Set set) {
        for(Object elem : set) {
            if(elem.equals(otherElem)) {
                return elem;
            }
        }
        return null;
    }

    private ListMapComparison diffMaps(Map map1, Map map2, Map<IdentityPair<Object, Object>, Comparison> prevComparisons) {
        ListMapComparison mapComparison = new ListMapComparison(map1.size(), map2.size());

        int i = 0;
        for(Object key1 : map1.keySet()) {
            if(map2.containsKey(key1)) {
                String context = getMapContext(i, "", map1, key1);
                mapComparison.addDifference(context, diffHandler(map1.get(key1), map2.get(key1), prevComparisons));
            } else {
                String context = getMapContext(i, "Left", map1, key1);
                mapComparison.addDifference(true,
                    context, map1.get(key1).toString(), "Right Map did not provide a match");
            }
            i++;
        }
        i = 0;
        for(Object key2 : map2.keySet()) {
            if(!map1.containsKey(key2)) {
                String context = getMapContext(i, "Right", map2, key2);
                mapComparison.addDifference(true,
                    context, "Left Map did not provide a match", map2.get(key2).toString());
            }
            i++;
        }
        return mapComparison;
    }
    private String getMapContext(int index, String position, Map map1, Object key1) {
        return GenericObjectDiffer.getContext(index, position, map1.get(key1)) + " (Key - " + key1.toString() + ") :";
    }
}
