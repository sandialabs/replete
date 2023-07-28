package replete.equality.similarity;

public class EqualsRule<T> implements AcceptableSimilarityRule<T> {
    boolean allowDualNull;
    public EqualsRule() {
        this(false);          // false seems to be the most reasonable default here.
    }                         // nulls being "equal" seems to violate the name of the
                              // class, which implies literally, "equals" will be called
    public EqualsRule(boolean allowDualNull) {
        this.allowDualNull = allowDualNull;
    }
    public boolean test(T o1, T o2) {
        if(allowDualNull && o1 == null && o2 == null) {
            return true;
        }
        return o1 != null && o1.equals(o2);
    }
}
