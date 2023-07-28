package finio.core;

import static finio.core.impl.FMap.A;

import java.util.Map;

import finio.core.impl.FMap;
import finio.core.managed.ManagedNonTerminal;
import finio.core.managed.ManagedValueManager;
import finio.core.warnings.UnexpandableWarning;
import replete.text.StringUtil;

public class FUtil {

    public static final String NULL_TEXT = "(NULL)";

    public static boolean equals(Object O1, Object O2) {
        if(O1 == null || O2 == null) {
            return O1 == O2;
        }
        return O1 == O2 || O1.equals(O2);
    }

    public static boolean isNull(Object O) {
        return
            O == null;
    }

    public static boolean isBoolean(Object O) {
        return
            O instanceof Boolean;
    }

    public static boolean isNumber(Object O) {
        return
            O instanceof Number;
    }

    public static boolean isByte(Object O) {
        return
            O instanceof Byte;
    }

    public static boolean isShort(Object O) {
        return
            O instanceof Short;
    }

    public static boolean isInteger(Object O) {
        return
            O instanceof Integer;
    }

    public static boolean isLong(Object O) {
        return
            O instanceof Long;
    }

    public static boolean isIntegral(Object O) {
        return
            isByte(O)    ||
            isShort(O)   ||
            isInteger(O) ||
            isLong(O);
    }

    public static boolean isFloat(Object O) {
        return
            O instanceof Float;
    }

    public static boolean isDouble(Object O) {
        return
            O instanceof Double;
    }

    public static boolean isFloatingPoint(Object O) {
        return
            isFloat(O)   ||
            isDouble(O);
    }

    public static boolean isString(Object O) {
        return
            O instanceof String;         // [OPTION] Include StringBuffer/StringBuilder too
    }

    public static boolean isStringOrChar(Object O) {
        return
            O instanceof Character ||
            O instanceof String;         // [OPTION] Include StringBuffer/StringBuilder too
    }

    public static boolean isPrimitive(Object O) {
        return
            O instanceof Character ||
            O instanceof String    ||
            O instanceof Number    ||
            O instanceof Boolean;
    }

    public static boolean isPrimitive(Class<?> T) {
        return
            T.equals(char.class)    || T.equals(Character.class) ||
            T.equals(String.class)  ||
            T.equals(boolean.class) || T.equals(Boolean.class)   ||
            T.equals(byte.class)    ||
            T.equals(short.class)   ||
            T.equals(int.class)     ||
            T.equals(float.class)   ||
            T.equals(long.class)    ||
            T.equals(double.class)  ||
            Number.class.isAssignableFrom(T);
    }

    public static boolean isPrimitiveOrNull(Object O) {
        return
            isNull(O)       ||
            isPrimitive(O);
    }

    // e.g. StringBuffer, HashMap, ArrayList, Company, Person, byte[], Company[], AirPortPowerDownController
    public static boolean isUnrecognizedNativeObject(Object O) {
        return
            !isPrimitiveOrNull(O) &&
            !isNonTerminal(O);
    }

    // Remember that whether or not something is truly "Terminal" is
    // subjective.  Strings could contain XML/JSON.  Integers could
    // be linear combinations of other sets of numbers or indices
    // into supplemental arrays/lists.  Java objects can clearly be
    // expanded into a non-terminal of their fields.  If the Java
    // object represents a large chunk of arbitrary data itself
    // (File, URL), then that data itself has structure and can
    // be expanded into a non-terminal.  Something is considered
    // "Terminal" if no further semantic expansion is necessary
    // for the given human/software to perform its task.

    public static boolean isTerminal(Object O) {
        return
            isPrimitiveOrNull(O)           ||
            isUnrecognizedNativeObject(O);          // Arrays fall into here for now.
    }

    // Equivalent to isMap(V) || isList(V), which is equivalent to V instanceof NonTerminal
    public static boolean isNonTerminal(Object O) {
        return
            O instanceof NonTerminal;
    }

    public static boolean isSemiTerminal(Object O) {
        return
            isNonTerminal(O) &&
            ((NonTerminal) O).containsKey(FConst.SYS_VALUE_KEY) &&
            isTerminal(((NonTerminal) O).containsKey(FConst.SYS_VALUE_KEY));  // ??
    }

    // Considered a terminal for the purposes of this application (I think...)
    public static boolean isJavaArray(Object O) {
        return
            !isNull(O)              &&
            O.getClass().isArray();
    }
    public static boolean isJavaMap(Object O) {
        return
            O instanceof Map && !(O instanceof NonTerminal);
    }
    public static boolean isJavaIterable(Object O) {
        return
            O instanceof Iterable && !(O instanceof NonTerminal);
    }

    public static boolean isNonTerminalLike(Object O) {
        return
            isJavaArray(O)    ||
            isJavaIterable(O) ||
            isJavaMap(O);
    }

    public static boolean isBinary(Object V) {
        return
            V instanceof byte[]  ||
            V instanceof Byte[];
    }

    public static boolean isManagedNonTerminal(Object V) {
        return
            V instanceof ManagedNonTerminal;
    }

    public static boolean isManagedValueManager(Object V) {
        return
            V instanceof ManagedValueManager;  // Are ManagedNonTerminals ManagedValues or is there a naming issue?
    }

    public static boolean isSysMetaKey(Object K) {
        return
            !isNull(K) &&
            K.toString().startsWith(FConst.SYS_PREFIX);
    }

    public static boolean isUnexpandableWarning(Object V) {
        return
            V instanceof UnexpandableWarning;
    }

    // public static boolean isPossiblyParsableForStructure(Object V) {
    //     return isStringOrChar(V) || isArray(V) || isBinary(V);
    // }
    // public static boolean isExpandable(Object V) {  // Very subjective
    // }
    // public static boolean isFile(Object V) {   // "Extra" storage not yet read off of disk.
    //     return V instanceof File;
    // }
    // public static boolean isRemoteStorage(Object V) {
    //     return V instanceof ValueRemoteStorage;
    // }
    // Maybe remote storage is just an FMap:
    // {
    //     source = File
    //     sourceContent = (String|byte[])  // Fetch
    //     tree = {...}                     // Extract
    // }
    // {
    //     source = http://somesite.org/get/some/json
    //     sourceContent = String (e.g. "{"a": 3, "b":6}")
    //     tree = {...}
    // }
    // The only downside is that now you have these "proxy" maps, it's a minor
    // layer of disconnected-ness within the FMap/tree.  For most use cases, you
    // would want to ignore these and pretend that the "value" of the key pointing
    // to this "remote storage proxy map/node" actually points to the value that
    // tree is pointing too.

    public static String toDiagnosticString(Object V) {
        if(FUtil.isNull(V)) {
            return NULL_TEXT;
        }
        String Vstr = V.toString();
        StringBuilder buffer = new StringBuilder();
        buffer.append('<');
        buffer.append(StringUtil.toStringObject(V, true));
        buffer.append("> \"");
        buffer.append(Vstr);
        buffer.append('"');
        return buffer.toString();
    }

    public static String renderKey(Object K, boolean includeDiagInfo) {
        StringBuilder buffer = new StringBuilder();
        if(includeDiagInfo) {
            String Cstr = K.getClass().getName();
            buffer.append('[');
            buffer.append(Cstr);
            buffer.append('@');
            buffer.append(K.hashCode());
            buffer.append("] ");
        }
        String Kstr = K.toString();
        if(FUtil.isSysMetaKey(K)) {
            buffer.append(Kstr, FConst.SYS_PREFIX.length(), Kstr.length());   // For UI
        } else {
            buffer.append(Kstr);
        }
        return buffer.toString();
    }

    public static String toStringBase(Object O) {
        return O.getClass().getName() + "@" + Integer.toHexString(O.hashCode());
    }
    public static String toStringBaseIdent(Object O) {
        return O.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(O));
    }

    public static void recordJavaSource(NonTerminal M, Object O) {
        M.putSysMeta(FConst.JAVA_CLASS_KEY, O.getClass().getName());
        M.putSysMeta(FConst.JAVA_HASH_KEY,  O.hashCode());
        M.putSysMeta(FConst.JAVA_IHASH_KEY, System.identityHashCode(O));
        M.putSysMeta(FConst.JAVA_REF_KEY,   O);
    }

    public static void concat(NonTerminal M, Object K, String Vapp, String delim) {
        Object V = M.get(K);
        Object Vnew;
        if(V == null) {
            Vnew = Vapp;
        } else if(V instanceof String) {
            Vnew = V + delim + Vapp;
        } else {
            Vnew = V;
        }
        M.put(K, Vnew);
    }

    // Technically any "Primitive" is also a Java object and could be "basic expandable".
    // However for simplification we won't include that yet.  If you include those though,
    // you could say that an AMap or an AList is also "basic expandable", which would just
    // become overly convoluted!!
//    public static boolean isBasicExpandable(Object O) {
//        return isUnrecognizedNativeObject(O);  // || isPrimitive() ?
//    }

    // Technically a String that contains XML/JSON could also be considered "deep expandable"
    // Could an array that is an index into a supplemental array be as well?
//    public static boolean isDeepExpandable(Object O) {
//        return O instanceof File        ||
//               O instanceof URL         ||
//               O instanceof Map         ||
//               O instanceof Collection;
//    }


    //////////
    // TEST //
    //////////

    public static void mainx(String[] args) {
        System.out.println(isNonTerminal(new FMap()));

//        Object[][] o = new Object[][] {{"hi", "there"}, {"how", "ru"}};
//        System.out.println(getDimension(o));
    }

    // -4 = No Info
    // -3 = No Arrays
    // -2 = Some Arrays
    // -1 = All Arrays, Diff Sizes
    // 0-INF = All Arrays, Same Size
    private static FMap xform = A(
        -3, A(-4, -3, -3, -3, -2, -2, -1, -2, 0, -2),
        -2, A(-4, -2, -3, -2, -2, -2, -1, -2, 0, -1),
        -1, A(-4, -1, -3, -3, -2, -2, -1, -1, 0, -1),
         0, A(-4, -9, -3, -2, -2, -2, -1, -1, 0, -9)  // -9 is special, won't be used
    );

    /*
    private static int getDimension(Object A, List<Integer> Zs) {
        if(A == null || !A.getClass().isArray()) {
            return -3;
        }

        int Z = Array.getLength(A);
        Zs.add(Z);

        int allDim = -4;

        for(int i = 0; i < Z; i++) {
            Object E = Array.get(A, i);
            int dim = getDimension(E);
            int sdim = dim > 0 ? 0 : dim;
            AMap next = xform.getA(sdim);
            if(sdim == 0) {
                if(allDim == -4) {
                    allDim = dim;
                } else if(allDim >= 0) {
                    if(allDim != dim) {
                        allDim = -1;
                    }
                } else {
                    allDim = (Integer) next.get(allDim);
                }
            } else {
                allDim = (Integer) next.get(allDim);
            }
        }

        //????? return Z!!!

        return allDim;
    }*/
}
