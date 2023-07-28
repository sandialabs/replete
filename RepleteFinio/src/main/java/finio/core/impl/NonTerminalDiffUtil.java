package finio.core.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import finio.core.FUtil;
import finio.core.NonTerminal;
import finio.example.ExampleDataGenerator;
import finio.renderers.map.FMapRenderer;
import finio.renderers.map.StandardAMapRenderer;
import replete.text.StringUtil;
import replete.util.ReflectionUtil;

public class NonTerminalDiffUtil {

    public static void main(String[] args) {
        NonTerminal Mdiffable = ExampleDataGenerator.createDiffableMaps();
        NonTerminal Mleft = (NonTerminal) Mdiffable.get("Left");
        NonTerminal Mright = (NonTerminal) Mdiffable.get("Right");

        FMapRenderer R = new StandardAMapRenderer();
        System.out.println(R.renderValue(Mleft));
        System.out.println(R.renderValue(Mright));

        Map<String, Object> m1 = ExampleDataGenerator.createTestJavaMap();
        Map<String, Object> m2 = ExampleDataGenerator.createTestJavaMap();

        Object K1 = null;
        Object V1 = m1; //new Boolean(false);
        Object K2 = null;
        Object V2 = m2; //new Boolean(false);

        DiffResult result = diff(K1, V1, K2, V2);
//        System.out.println("Diff btw " + K1 + " = " + V1 + ", " + K2 + " = " + V2);
//        for(DiffCharacteristic chrx : result.chrxs) {
//            System.out.println("  Chrx = " + chrx);
//        }
        print(result);
    }

    private static void print(DiffResult result) {
        print(result, 0);
    }
    private static void print(DiffResult result, int level) {
        String sp = StringUtil.spaces(level * 4);
        String sp2 = StringUtil.spaces((level + 1) * 4);
        System.out.println(sp + "Characteristics:");
        for(DiffCharacteristic chrx : result.chrxs) {
            System.out.println(sp2 + chrx);
        }
        if(!result.childrenResults.isEmpty()) {
            System.out.println(sp + "Children:");
            for(Object K : result.childrenResults.keySet()) {
                System.out.println(sp2 + K + " => ");
                DiffResult R = result.childrenResults.get(K);
                print(R, level + 2);
            }
        }
    }

    enum SimilarityLevel {
        NULL(true),
        NULL_ONE(false),
        NON_NULL_BOTH(true),

        IDENTICAL(true),       // Holds same memory address e.g. a = new Integer(2) vs a, Implies all other possible, conceivable definitions of equality
        NOT_IDENTICAL(false),  // Only implies not the same memory address (not equal by strictest sense of the word)

        TYPE_EQ(true),         // Doesn't imply equality at all
        TYPE_NE(false),        // Possibly can still compare (Number, String, Character, Iterable, Map, NonTerminal)

        BOOL_EQ(true),         // new Boolean(false) vs. new Boolean(false) [IDENTICAL supercedes]
        BOOL_NE(false),        // new Boolean(false) vs. new Boolean(true)

        NUMBER_EQ(true),
        NUMBER_NE(false),

        STRING_EQ(true),
        STRING_IC_EQ(true),
        STRING_NE(false),

        COLL_SIZE_EQ(true),
        COLL_SIZE_NE(false),

        ARRAY_TYPE_EQ(true),
        ARRAY_TYPE_NE(false),

        MISSING_KEY(false),

        OBJECT_EQ(true),
        OBJECT_NE(false),

        HASHCODE_EQ(true),
        HASHCODE_NE(false),

        IDENT_HASHCODE_EQ(true),
        IDENT_HASHCODE_NE(false),

        TYPE_INCOMPATIBLE(false);   // Impossible to compare

        private boolean equality;
        private SimilarityLevel(boolean equality) {
            this.equality = equality;
        }
//
//        Array, Iterable, Map, NonTerminal, Unrecognized....
    }

    private static String t(Object O) {
        if(FUtil.isNull(O)) {
            return "(NULL)";
        }
        if(FUtil.isPrimitive(O)) {
            return O.getClass().getSimpleName();
        }
        return O.getClass().getName();
    }

    private static String tmsg(Object Ol, Object Or) {
        return "$L is " + t(Ol) + ", $R is " + t(Or);
    }
    private static String vmsg(Object Ol, Object Or) {
        return "$L = " + Ol + ", $R = " + Or;
    }
    private static String kmsg(Object k1, Object k2) {
        if(k2 == null) {
            return "Only in $L: " + k1;
        }
        return "Only in $R: " + k2;
    }
    private static String smsg(int l1, int l2) {
        return "#$L = " + l1 + ", #$R = " + l2;
    }

    private static interface DiffHandler {
        boolean handle(Object Kx1, Object V1, Object Kx2, Object V2, DiffResult result);
    }

    public static DiffResult diff(Object Kx1, Object V1, Object Kx2, Object V2) {
        DiffResult result = new DiffResult();

        DiffHandler[] handlers = {
            (aKx1, aV1, aKx2, aV2, aresult) -> handleNull(aV1, aV2, result),
            (aKx1, aV1, aKx2, aV2, aresult) -> handleIdent(aV1, aV2, result),
            (aKx1, aV1, aKx2, aV2, aresult) -> handleType(aV1, aV2, result),
            (aKx1, aV1, aKx2, aV2, aresult) -> handleBoolean(aV1, aV2, result),
            (aKx1, aV1, aKx2, aV2, aresult) -> handleStringOrChar(aV1, aV2, result),
            (aKx1, aV1, aKx2, aV2, aresult) -> handleNumber(aV1, aV2, result),
            (aKx1, aV1, aKx2, aV2, aresult) -> handleArray(aV1, aV2, result),
            (aKx1, aV1, aKx2, aV2, aresult) -> handleJavaIterable(aV1, aV2, result),
            (aKx1, aV1, aKx2, aV2, aresult) -> handleJavaMap(aV1, aV2, result),
            (aKx1, aV1, aKx2, aV2, aresult) -> handleNonTerminal(aV1, aV2, result),
            (aKx1, aV1, aKx2, aV2, aresult) -> handleUnrec(aV1, aV2, result),
        };

        for(DiffHandler handler : handlers) {
            if(handler.handle(Kx1, V1, Kx2, V2, result)) {
                return result;
            }
        }

        throw new RuntimeException("asfdasf");
    }

    public static boolean handleNull(Object V1, Object V2, DiffResult result) { // maybe add keys
        boolean c1 = FUtil.isNull(V1);
        boolean c2 = FUtil.isNull(V2);

        if(c1 && c2) {
            result.chrxs.add(new DiffCharacteristic(SimilarityLevel.NULL));

        } else if(c1 ^ c2) {
            result.chrxs.add(new DiffCharacteristic(SimilarityLevel.NULL_ONE, tmsg(V1, V2)));

        } else {
            result.chrxs.add(new DiffCharacteristic(SimilarityLevel.NON_NULL_BOTH));
            return false;    // Allows further differentiation to proceed
        }

        return true;

    }

    public static boolean handleIdent(Object V1, Object V2, DiffResult result) { // maybe add keys

        if(V1 == V2) {
            result.chrxs.add(new DiffCharacteristic(SimilarityLevel.IDENTICAL));

        } else {
            result.chrxs.add(new DiffCharacteristic(SimilarityLevel.NOT_IDENTICAL));
            return false;    // Allows further differentiation to proceed
        }

        return true;
    }

    public static boolean handleType(Object V1, Object V2, DiffResult result) { // maybe add keys

        if(V1.getClass().equals(V2.getClass())) {
            result.chrxs.add(new DiffCharacteristic(SimilarityLevel.TYPE_EQ));
        } else {
            result.chrxs.add(new DiffCharacteristic(SimilarityLevel.TYPE_NE));
        }

        return false;    // Always allows further differentiation to proceed
    }

    public static boolean handleBoolean(Object V1, Object V2, DiffResult result) { // maybe add keys
        boolean c1 = FUtil.isBoolean(V1);
        boolean c2 = FUtil.isBoolean(V2);

        if(c1 && c2) {
            if(V1.equals(V2)) {
                result.chrxs.add(new DiffCharacteristic(SimilarityLevel.BOOL_EQ));
            } else {
                result.chrxs.add(new DiffCharacteristic(SimilarityLevel.BOOL_NE, vmsg(V1, V2)));
            }

        } else if(c1 ^ c2) {
            result.chrxs.add(new DiffCharacteristic(SimilarityLevel.TYPE_INCOMPATIBLE, tmsg(V1, V2)));

        } else {
            return false;    // Allows further differentiation to proceed
        }

        return true;
    }

    public static boolean handleNumber(Object V1, Object V2, DiffResult result) { // maybe add keys
        boolean c1 = FUtil.isNumber(V1);
        boolean c2 = FUtil.isNumber(V2);

        if(c1 && c2) {
            Number s1 = (Number) V1;
            Number s2 = (Number) V2;

            if(s1.doubleValue() == s2.doubleValue()) {
                result.chrxs.add(new DiffCharacteristic(SimilarityLevel.NUMBER_EQ));
            } else {
                result.chrxs.add(new DiffCharacteristic(SimilarityLevel.NUMBER_NE, vmsg(V1, V2)));
            }

        } else if(c1 ^ c2) {
            result.chrxs.add(new DiffCharacteristic(SimilarityLevel.TYPE_INCOMPATIBLE, tmsg(V1, V2)));

        } else {
            return false;    // Allows further differentiation to proceed
        }

        return true;
    }

    public static boolean handleStringOrChar(Object V1, Object V2, DiffResult result) { // maybe add keys
        boolean c1 = FUtil.isStringOrChar(V1);
        boolean c2 = FUtil.isStringOrChar(V2);

        if(c1 && c2) {
            String s1 = "" + V1;
            String s2 = "" + V2;

            if(s1.equals(s2)) {
                result.chrxs.add(new DiffCharacteristic(SimilarityLevel.STRING_EQ));
            } else {
                result.chrxs.add(new DiffCharacteristic(SimilarityLevel.STRING_NE, vmsg(V1, V2)));
                if(s1.equalsIgnoreCase(s2)) {
                    result.chrxs.add(new DiffCharacteristic(SimilarityLevel.STRING_IC_EQ));
                }
            }

        } else if(c1 ^ c2) {
            result.chrxs.add(new DiffCharacteristic(SimilarityLevel.TYPE_INCOMPATIBLE, tmsg(V1, V2)));

        } else {
            return false;    // Allows further differentiation to proceed
        }

        return true;
    }

    public static boolean handleArray(Object V1, Object V2, DiffResult result) {
        boolean c1 = FUtil.isJavaArray(V1);
        boolean c2 = FUtil.isJavaArray(V2);

        if(c1 && c2) {
            int l1 = Array.getLength(V1);
            int l2 = Array.getLength(V2);
            boolean sameLen = (l1 == l2);
            boolean sameType = V1.getClass().getComponentType().equals(V2.getClass().getComponentType());

            if(sameLen) {
                result.chrxs.add(new DiffCharacteristic(SimilarityLevel.COLL_SIZE_EQ));
            } else {
                result.chrxs.add(new DiffCharacteristic(SimilarityLevel.COLL_SIZE_NE, smsg(l1, l2)));
            }

            if(sameType) {
                result.chrxs.add(new DiffCharacteristic(SimilarityLevel.ARRAY_TYPE_EQ));
            } else {
                result.chrxs.add(new DiffCharacteristic(SimilarityLevel.ARRAY_TYPE_NE, tmsg(V1, V2)));
            }

            // Could have option to aggregate here
            if(sameLen && sameType) {             // Technically you could compare int[] && long[]....
                Map<Object, DiffResult> childrenResults = new LinkedHashMap<>();
                for(int i = 0; i < l1; i++) {
                    Object E1 = Array.get(V1, i);
                    Object E2 = Array.get(V2, i);
                    DiffResult resultChild = diff(i, E1, i, E2);
                    childrenResults.put(i, resultChild);
                }

                boolean aggregate = false;   // future configurable idea
                if(aggregate) {
                    List<DiffCharacteristic> aggregateChrxs = aggregateChildResults(childrenResults);
                    result.chrxs.addAll(aggregateChrxs);
                } else {
                    result.childrenResults.putAll(childrenResults);
                }
            }

        } else if(c1 ^ c2) {
            result.chrxs.add(new DiffCharacteristic(SimilarityLevel.TYPE_INCOMPATIBLE, tmsg(V1, V2)));

        } else {
            return false;    // Allows further differentiation to proceed
        }

        return true;
    }


    public static boolean handleJavaIterable(Object V1, Object V2, DiffResult result) {
        boolean c1 = FUtil.isJavaIterable(V1);
        boolean c2 = FUtil.isJavaIterable(V2);

        if(c1 && c2) {
            Iterable I1 = (Iterable) V1;
            Iterable I2 = (Iterable) V2;
            Iterator it1 = I1.iterator();
            Iterator it2 = I2.iterator();
            List list1 = new ArrayList();
            while(it1.hasNext()) {
                list1.add(it1.next());
            }
            List list2 = new ArrayList();
            while(it2.hasNext()) {
                list2.add(it2.next());
            }
            int l1 = list1.size();
            int l2 = list2.size();
            boolean sameLen = (l1 == l2);

            if(sameLen) {
                result.chrxs.add(new DiffCharacteristic(SimilarityLevel.COLL_SIZE_EQ));
            } else {
                result.chrxs.add(new DiffCharacteristic(SimilarityLevel.COLL_SIZE_NE, smsg(l1, l2)));
            }

            if(sameLen) {             // Technically you could compare int[] && long[]....
                Map<Object, DiffResult> childrenResults = new LinkedHashMap<>();
                for(int i = 0; i < l1; i++) {
                    Object E1 = list1.get(i);
                    Object E2 = list2.get(i);
                    DiffResult resultChild = diff(i, E1, i, E2);
                    childrenResults.put(i, resultChild);
                }

                boolean aggregate = false;   // future configurable idea
                if(aggregate) {
                    List<DiffCharacteristic> aggregateChrxs = aggregateChildResults(childrenResults);
                    result.chrxs.addAll(aggregateChrxs);
                } else {
                    result.childrenResults.putAll(childrenResults);
                }
            }

        } else if(c1 ^ c2) {
            result.chrxs.add(new DiffCharacteristic(SimilarityLevel.TYPE_INCOMPATIBLE, tmsg(V1, V2)));

        } else {
            return false;    // Allows further differentiation to proceed
        }

        return true;
    }

    public static boolean handleJavaMap(Object V1, Object V2, DiffResult result) {
        boolean c1 = FUtil.isJavaMap(V1);
        boolean c2 = FUtil.isJavaMap(V2);

        if(c1 && c2) {
            Map M1 = (Map) V1;
            Map M2 = (Map) V2;
            int l1 = M1.size();
            int l2 = M2.size();
            boolean sameLen = (l1 == l2);

            if(sameLen) {
                result.chrxs.add(new DiffCharacteristic(SimilarityLevel.COLL_SIZE_EQ));
            } else {
                result.chrxs.add(new DiffCharacteristic(SimilarityLevel.COLL_SIZE_NE, smsg(l1, l2)));
            }

            Set<Object> K1 = new LinkedHashSet<>(M1.keySet());
            Set<Object> K2 = new LinkedHashSet<>(M2.keySet());
            Set<Object> I = new LinkedHashSet<>();

            for(Object k1 : K1) {
                if(K2.contains(k1)) {
                    I.add(k1);
                } else {
                    result.chrxs.add(new DiffCharacteristic(SimilarityLevel.MISSING_KEY, kmsg(k1, null)));
                }
            }
            for(Object k2 : K2) {
                if(K1.contains(k2)) {
                    I.add(k2);
                } else {
                    result.chrxs.add(new DiffCharacteristic(SimilarityLevel.MISSING_KEY, kmsg(null, k2)));
                }
            }

            Map<Object, DiffResult> childrenResults = new LinkedHashMap<>();
            for(Object ik : I) {
                Object V1child = M1.get(ik);
                Object V2child = M2.get(ik);
                DiffResult resultChild = diff(ik, V1child, ik, V2child);
                childrenResults.put(ik, resultChild);
            }

            boolean aggregate = false;   // future configurable idea
            if(aggregate) {
                List<DiffCharacteristic> aggregateChrxs = aggregateChildResults(childrenResults);
                result.chrxs.addAll(aggregateChrxs);
            } else {
                result.childrenResults.putAll(childrenResults);
            }

        } else if(c1 ^ c2) {
            result.chrxs.add(new DiffCharacteristic(SimilarityLevel.TYPE_INCOMPATIBLE, tmsg(V1, V2)));

        } else {
            return false;    // Allows further differentiation to proceed
        }

        return true;
    }

    public static boolean handleNonTerminal(Object V1, Object V2, DiffResult result) {
        boolean c1 = FUtil.isNonTerminal(V1);
        boolean c2 = FUtil.isNonTerminal(V2);

        if(c1 && c2) {
            NonTerminal M1 = (NonTerminal) V1;
            NonTerminal M2 = (NonTerminal) V2;
            int l1 = M1.sizeNoSysMeta();     //  Configurable
            int l2 = M2.sizeNoSysMeta();
            boolean sameLen = (l1 == l2);

            if(sameLen) {
                result.chrxs.add(new DiffCharacteristic(SimilarityLevel.COLL_SIZE_EQ));
            } else {
                result.chrxs.add(new DiffCharacteristic(SimilarityLevel.COLL_SIZE_NE, smsg(l1, l2)));
            }

            Set<Object> K1 = new LinkedHashSet<>(M1.keySet());
            Set<Object> K2 = new LinkedHashSet<>(M2.keySet());
            Set<Object> I = new LinkedHashSet<>();

            for(Object k1 : K1) {
                if(K2.contains(k1)) {
                    I.add(k1);
                } else {
                    result.chrxs.add(new DiffCharacteristic(SimilarityLevel.MISSING_KEY, kmsg(k1, null)));
                }
            }
            for(Object k2 : K2) {
                if(K1.contains(k2)) {
                    I.add(k2);
                } else {
                    result.chrxs.add(new DiffCharacteristic(SimilarityLevel.MISSING_KEY, kmsg(null, k2)));
                }
            }

            Map<Object, DiffResult> childrenResults = new LinkedHashMap<>();
            for(Object ik : I) {
                if(FUtil.isSysMetaKey(ik)) {    // Configurable?
                    continue;
                }
                Object V1child = M1.get(ik);
                Object V2child = M2.get(ik);
                DiffResult resultChild = diff(ik, V1child, ik, V2child);
                childrenResults.put(ik, resultChild);
            }

            boolean aggregate = false;   // future configurable idea
            if(aggregate) {
                List<DiffCharacteristic> aggregateChrxs = aggregateChildResults(childrenResults);
                result.chrxs.addAll(aggregateChrxs);
            } else {
                result.childrenResults.putAll(childrenResults);
            }

        } else if(c1 ^ c2) {
            result.chrxs.add(new DiffCharacteristic(SimilarityLevel.TYPE_INCOMPATIBLE, tmsg(V1, V2)));

        } else {
            return false;    // Allows further differentiation to proceed
        }

        return true;
    }

    public static boolean handleUnrec(Object V1, Object V2, DiffResult result) { // maybe add keys
        boolean eq = V1.equals(V2);    // Configurable whether to call this or not
        int hc1 = V1.hashCode();
        int hc2 = V2.hashCode();
        int ihc1 = System.identityHashCode(V1);
        int ihc2 = System.identityHashCode(V2);

        if(eq) {
            result.chrxs.add(new DiffCharacteristic(SimilarityLevel.OBJECT_EQ));
        } else {
            result.chrxs.add(new DiffCharacteristic(SimilarityLevel.OBJECT_NE));
        }

        if(hc1 == hc2) {
            result.chrxs.add(new DiffCharacteristic(SimilarityLevel.HASHCODE_EQ));
        } else {
            result.chrxs.add(new DiffCharacteristic(SimilarityLevel.HASHCODE_NE, vmsg(hc1, hc2)));
        }

        if(ihc1 == ihc2) {
            result.chrxs.add(new DiffCharacteristic(SimilarityLevel.IDENT_HASHCODE_EQ));
        } else {
            result.chrxs.add(new DiffCharacteristic(SimilarityLevel.IDENT_HASHCODE_NE, vmsg(ihc1, ihc2)));
        }

        if(V1.getClass().equals(V2.getClass())) {
            Field[] Fs = ReflectionUtil.getFields(V1);
            Map<Object, DiffResult> childrenResults = new LinkedHashMap<>();
            for(Field F : Fs) {
                try {
                    Object f1 = F.get(V1);
                    Object f2 = F.get(V2);
                    String k = F.getName();
                    DiffResult resultChild = diff(k, f1, k, f2);
                    childrenResults.put(k, resultChild);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }

            boolean aggregate = false;   // future configurable idea
            // aggregation option here is just to "believe" the
            // .equals(...) result and not compare fields
            // using reflection.
            if(aggregate) {
                List<DiffCharacteristic> aggregateChrxs = aggregateChildResults(childrenResults);
                result.chrxs.addAll(aggregateChrxs);
            } else {
                result.childrenResults.putAll(childrenResults);
            }
        }

        return true;
    }

    private static List<DiffCharacteristic> aggregateChildResults(Map<Object, DiffResult> childrenResults) {
        return null;
    }
}
