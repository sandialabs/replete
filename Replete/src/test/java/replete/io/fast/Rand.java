package replete.io.fast;

import java.lang.reflect.Array;
import java.util.Random;

import org.junit.Ignore;

import replete.util.ReflectionUtil;

@Ignore
public class Rand {
    public static final int SEED = 100;
    public static final Random R = new Random(100);

    public static int randInt() {
        return R.nextInt();
    }
    public static int randInt(int max) {
        return R.nextInt(max);
    }
    public static long randLong() {
        return R.nextLong();
    }
    public static float randFloat() {
        return R.nextFloat();
    }
    public static double randDouble() {
        return R.nextDouble();
    }
    public static char randChar() {
        int range = 126 - 32 + 1;
        int which = randInt(range);
        return (char) (32 + which);
    }
    public static String randString() {
        int length = randInt(101);
        return randString(length);
    }
    public static String randString(int length) {
        StringBuilder buffer = new StringBuilder();
        for(int i = 0; i < length; i++) {
            buffer.append(randChar());
        }
        return buffer.toString();
    }
    public static Object makeArray(Class clazz, int n) {
        Object array = Array.newInstance(clazz, n);
        for(int i = 0; i < n; i++) {
            Array.set(array, i, ReflectionUtil.create(clazz));
        }
        return array;
    }
    public static Object makePrimitiveArray(Class clazz, int n) {
        Object array = Array.newInstance(clazz, n);
        for(int i = 0; i < n; i++) {
            Object value;
            if(clazz == boolean.class) {
                value = (randInt() % 2 == 0);
            } else if(clazz == byte.class) {
                value = (byte) randInt();
            } else if(clazz == short.class) {
                value = (short) randInt();
            } else if(clazz == int.class) {
                value = randInt();
            } else if(clazz == long.class) {
                value = randLong();
            } else if(clazz == float.class) {
                value = randFloat();
            } else if(clazz == double.class) {
                value = randDouble();
            } else {
                value = 0;
            }
            Array.set(array, i, value);
        }
        return array;
    }
}

