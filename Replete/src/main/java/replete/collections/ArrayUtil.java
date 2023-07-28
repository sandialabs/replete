package replete.collections;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import replete.equality.EqualsUtil;

public class ArrayUtil {

    public static List<Integer> asList(int... values) {
        List<Integer> list = new ArrayList<>();
        for(int v : values) {
            list.add(v);
        }
        return list;
    }

    public static boolean isBlank(Object arr) {
        return arr == null || Array.getLength(arr) == 0;
    }

    public static Object[] subset(Object[] arr, int start, int endNonIncl) {
        Object[] newArr = new Object[endNonIncl - start];
        for(int e = start; e < endNonIncl; e++) {
            newArr[e - start] = arr[e];
        }
        return newArr;
    }

    public static <T> String render(T[] x, RenderOne<T> renderer) {
        String ret = "";
        boolean first = true;
        for(T t : x) {
            if(!first) {
                ret += ", ";
            }
            ret += renderer.render(t);
        }
        return ret;
    }

    // TODO: Make this generic one day
    public static File[] cat(File[] srcArray, File addMe) {
        if(srcArray == null) {
            srcArray = new File[0];
        }
        File[] dstArray = new File[srcArray.length + 1];
        dstArray[dstArray.length - 1] = addMe;
        return dstArray;
    }

    public static interface ArrayTranslator {
        public Object translate(Object o);
    }
    public static <T> T[] translate(Class<T> clazz, Object srcArray) {
        return translate(clazz, srcArray, null);
    }
    public static <T> T[] translate(Class<T> clazz, Object srcArray, ArrayTranslator xlator) {
        if(srcArray == null) {
            return null;
        }
        int len = Array.getLength(srcArray);
        Object newArray = Array.newInstance(clazz, len);
        for(int i = 0; i < len; i++) {
            Object srcElem = Array.get(srcArray, i);
            Object dstElem;
            if(xlator == null) {
                dstElem = srcElem;
            } else {
                dstElem = xlator.translate(srcElem);
            }
            Array.set(newArray, i, dstElem);
        }
        return (T[]) newArray;
    }

    public static interface RenderOne<T> {
        public String render(T t);
    }

    public static <T> T[] ensureSize(Class<T> clazz, T[] array, int size) {
        return ensureSize(clazz, array, size, null, true);
    }
    public static <T> T[] ensureSize(Class<T> clazz, T[] array, int size, T dflt) {
        return ensureSize(clazz, array, size, dflt, false);
    }

    private static <T> T[] ensureSize(Class<T> clazz, T[] array, int size, T dflt, boolean newInstances) {
        if(array.length < size) {
            Object newArray = Array.newInstance(clazz, size);
            System.arraycopy(array, 0, newArray, 0, array.length);
            for(int i = array.length; i < size; i++) {
                Array.set(newArray, i, getDefault(dflt, clazz, newInstances));
            }
            return (T[]) newArray;
        }
        return array;
    }

    public static <T> T[] newArray(Class<T> clazz, int size) {
        return newArray(clazz, size, null, true);
    }
    public static <T> T[] newArray(Class<T> clazz, int size, T dflt) {
        return newArray(clazz, size, dflt, false);
    }
    private static <T> T[] newArray(Class<T> clazz, int size, T dflt, boolean newInstances) {
        Object output = Array.newInstance(clazz, size);
        for(int i = 0; i < size; i++) {
            Array.set(output, i, getDefault(dflt, clazz, newInstances));
        }
        return (T[]) output;
    }

    private static Object getDefault(Object dflt, Class<?> clazz, boolean newInstances) {
        Object value = dflt;
        if(newInstances) {
            try {
                value = clazz.getConstructor(new Class<?>[0]).newInstance(new Object[0]);
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
        return value;
    }

    public static <T> T[] addElements(Class<T> clazz, T[] input, int start, int end) {
        return addElements(clazz, input, start, end, null, true);
    }
    public static <T> T[] addElements(Class<T> clazz, T[] input, int start, int end, T dflt) {
        return addElements(clazz, input, start, end, dflt, false);
    }
    private static <T> T[] addElements(Class<T> clazz, T[] input, int start, int end, T dflt, boolean newInstances) {
        int added = end - start + 1;
        Object output = Array.newInstance(clazz, input.length + added);
        int out = 0;
        for(int in = 0; in < input.length + 1; in++) {  // + 1 captures adding at end
            if(in == start) {
                for(int n = 0; n < added; n++) {
                    Array.set(output, out++, getDefault(dflt, clazz, newInstances));
                }
            }
            if(in != input.length) {
                Array.set(output, out++, input[in]);
            }
        }
        return (T[]) output;
    }

    public static <T> T[] removeElements(Class<T> clazz, T[] input, int start, int end) {
        int removed = end - start + 1;
        Object output = Array.newInstance(clazz, input.length - removed);
        int out = 0;
        for(int in = 0; in < input.length; in++) {
            if(in < start || in > end) {
                Array.set(output, out++, input[in]);
            }
        }
        return (T[]) output;
    }
    public static <T> T[] concatenate (T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen+bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

//    public static String toStringNoBrackets(Object[] obj) {
//        String o
//    }

    public static void main(String[] args) {
        String[] names = new String[] {"Mercury", "Venus", "Earth", "Mars", "Jupiter"};
        Object[] someNames = subset(names, 1, 3);
        System.out.println(Arrays.toString(subset(names, 0, names.length - 1)));
        System.out.println(Arrays.toString(someNames));
        if(true) {
            return;
        }
        Boolean[] b = new Boolean[] {true, false, true};
        b = ensureSize(Boolean.class, b, 6, true);
        System.out.println(Arrays.toString(removeElements(Boolean.class, b, 2, 3)));
        int arrsize = 22;
        int matsize = 7;
        boolean sizeIsCols = false;
        boolean colfirst = false;
        int[][] matrix = ArrayUtil.convertToMatrix(arrsize, matsize, sizeIsCols, colfirst);
        ArrayUtil.printMatrix(arrsize, matrix);
    }

    // precond: size > 0,
    public static int[][] convertToMatrix(int arrSize, int size, boolean sizeIsCols, boolean columnFirst) {
        int rows, cols;
        if(sizeIsCols) {
            cols = size;
            rows = arrSize / cols;
            if(arrSize % cols != 0) {
                rows++;
            }
        } else {
            rows = size;
            cols = arrSize / rows;
            if(arrSize % rows != 0) {
                cols++;
            }
        }
        int[][] matrix = new int[rows][cols];
        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < cols; c++) {
                int index;
                if(columnFirst) {
                    index = c + r * cols;
                } else {
                    index = r + c * rows;
                }
                matrix[r][c] = index >= arrSize ? -1 : index;
            }
        }
        return matrix;
    }
    public static void printMatrix(int arrsize, int[][] matrix) {
        for(int a = 0; a < arrsize; a++) {
            System.out.printf("%2d", a);
        }
        System.out.println();
        for(int r = 0; r < matrix.length; r++) {
            System.out.print("|");
            for(int c = 0; c < matrix[0].length; c++) {
                System.out.printf("%3d", matrix[r][c]);
            }
            System.out.println(" |");
        }
    }

    /////////////
    // REVERSE //
    /////////////

    public static <T> void reverse(T[] arr) {
        for(int i = 0; i < arr.length / 2; i++) {
            T temp = arr[i];
            arr[i] = arr[arr.length - 1 - i];
            arr[arr.length - 1 - i] = temp;
        }
    }


    //////////////
    // CONTAINS //
    //////////////

   public static boolean contains(char[] a, char v) {
       if(a == null) {
           return false;
       }
       for(char e : a) {
           if(e == v) {
               return true;
           }
       }
       return false;
   }
   public static boolean contains(boolean[] a, boolean v) {
       if(a == null) {
           return false;
       }
       for(boolean e : a) {
           if(e == v) {
               return true;
           }
       }
       return false;
   }
   public static boolean contains(byte[] a, byte v) {
       if(a == null) {
           return false;
       }
       for(byte e : a) {
           if(e == v) {
               return true;
           }
       }
       return false;
   }
   public static boolean contains(short[] a, short v) {
       if(a == null) {
           return false;
       }
       for(short e : a) {
           if(e == v) {
               return true;
           }
       }
       return false;
   }
   public static boolean contains(int[] a, int v) {
       if(a == null) {
           return false;
       }
       for(int e : a) {
           if(e == v) {
               return true;
           }
       }
       return false;
   }
   public static boolean contains(float[] a, float v) {
       if(a == null) {
           return false;
       }
       for(float e : a) {
           if(e == v) {
               return true;
           }
       }
       return false;
   }
   public static boolean contains(long[] a, long v) {
       if(a == null) {
           return false;
       }
       for(long e : a) {
           if(e == v) {
               return true;
           }
       }
       return false;
   }
   public static boolean contains(double[] a, double v) {
       if(a == null) {
           return false;
       }
       for(double e : a) {
           if(e == v) {
               return true;
           }
       }
       return false;
   }
   public static boolean contains(Object[] a, Object v) {
       return indexOf(a, v) != -1;
   }
   public static int indexOf(Object[] a, Object v) {
       if(a == null) {
           return -1;
       }
       int i = 0;
       for(Object e : a) {
           if(EqualsUtil.equals(e, v)) {
               return i;
           }
           i++;
       }
       return -1;
   }

   // A common practice is removing the "[" and "]" added by the
   // standard toString methods of List, Set, and Arrays for
   // a cleaner presentation to the user.
   public static String toString(Object[] array) {
       return Arrays.toString(array).replaceAll("[\\[\\]]", "");
   }
}
