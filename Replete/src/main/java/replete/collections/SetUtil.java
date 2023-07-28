package replete.collections;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class SetUtil {
    public static Set<?> hash(Object... items) {
        Set set = new HashSet();
        for(Object item : items) {
            set.add(item);
        }
        return set;
    }

    public static <T> Pair<Set<T>, Set<T>> diff(Set<T> left, Set<T> right) {
        Set<T> rightMissing = new LinkedHashSet<>();
        Set<T> rightExtra   = new LinkedHashSet<>();

        for(T leftElem : left) {
            if(!right.contains(leftElem)) {
                rightMissing.add(leftElem);
            }
        }
        for(T rightElem : right) {
            if(!left.contains(rightElem)) {
                rightExtra.add(rightElem);
            }
        }

        return new Pair<>(rightMissing, rightExtra);
    }
}
