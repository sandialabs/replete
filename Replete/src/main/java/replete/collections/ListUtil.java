package replete.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import replete.util.ReflectionUtil;

public class ListUtil {
    public static List<?> array(Object... items) {
        return Arrays.asList(items);
    }
    public static List convertByField(List list, String field) {
        List ret = new ArrayList();
        for(Object o : list) {
            ret.add(ReflectionUtil.get(o, field));
        }
        return ret;
    }
    public static boolean isBlank(List list) {
        return list == null || list.isEmpty();
    }

    // A common practice is removing the "[" and "]" added by the
    // standard toString methods of List, Set, and Arrays for
    // a cleaner presentation to the user.
    public static String toString(List list) {
        return list.toString().replaceAll("[\\[\\]]", "");
    }
}
