package replete.equality;

import java.lang.reflect.Array;
import java.util.Arrays;

public class EqualsUtil {
    public static boolean equals(Object v1, Object v2) {
        if(v1 == v2) {
            return true;
        }
        if(v1 == null || v2 == null) {
            return false;
        }
        if(v1.getClass().isArray()) {
            if(!v2.getClass().isArray()) {
                return false;
            }
            if(v1.getClass().getComponentType().isPrimitive()) {
                if(!v1.getClass().getComponentType().isPrimitive()) {
                    return false;
                }
                if(Array.getLength(v1) != Array.getLength(v2)) {
                    return false;
                }
                for(int e = 0; e < Array.getLength(v1); e++) {
                    if(!Array.get(v1, e).equals(Array.get(v2, e))) {
                        return false;
                    }
                }
                return true;
            }
            return Arrays.deepEquals((Object[]) v1, (Object[]) v2);
        }
        return v1.equals(v2);
    }
}
