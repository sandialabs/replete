package replete.equality.similarity;

public class AndRule<T> implements AcceptableSimilarityRule<T> {
    private BinaryRule<T> left;
    private BinaryRule<T> right;
    public AndRule(BinaryRule<T> left, BinaryRule<T> right) {
        this.left = left;
        this.right = right;
    }
    @Override
    public boolean test(T o1, T o2) {
        if(!left.test(o1, o2)) {      // Short-circuiting
            return false;
        }
        if(!right.test(o1, o2)) {
            return false;
        }
        return true;
    }
}
