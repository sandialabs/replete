package replete.equality.similarity;

public class SameClassRule<T> implements AcceptableSimilarityRule<T> {
    boolean allowDualNull;
    public SameClassRule() {
        this(true);          // true seems to be the most reasonable default here
    }                        // If both objects are null, it's like they are of
                             // the same "class" - the class of lack of information,
    public SameClassRule(boolean allowDualNull) {
        this.allowDualNull = allowDualNull;
    }
    public boolean test(T o1, T o2) {
        if(o1 == null && o2 == null) {
            return allowDualNull;
        }
        return
            o1 != null && o2 != null  &&
            o1.getClass().equals(o2.getClass())
        ;
    }
}
