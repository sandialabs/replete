package replete.equality.similarity;

import replete.equality.EqualsUtil;

public class EqualsUtilRule<T> implements AcceptableSimilarityRule<T> {
    public boolean test(T o1, T o2) {
        return EqualsUtil.equals(o1, o2);
    }
}
