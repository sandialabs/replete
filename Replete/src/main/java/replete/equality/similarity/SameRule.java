package replete.equality.similarity;

public class SameRule<T> implements AcceptableSimilarityRule<T> {
    boolean allowDualNull;
    public SameRule() {
        this(true);          // true seems to be the most reasonable default here
    }
    public SameRule(boolean allowDualNull) {
        this.allowDualNull = allowDualNull;
    }
    public boolean test(T o1, T o2) {
        if(o1 == null && o2 == null) {
            return allowDualNull;
        }
        return o1 == o2;
    }
}
