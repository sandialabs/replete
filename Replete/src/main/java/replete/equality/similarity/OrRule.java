package replete.equality.similarity;

public class OrRule<T> implements AcceptableSimilarityRule<T> {
    private BinaryRule<T> left;
    private BinaryRule<T> right;
    public OrRule(BinaryRule<T> left, BinaryRule<T> right) {
        this.left = left;
        this.right = right;
    }
    @Override
    public boolean test(T o1, T o2) {
        if(left.test(o1, o2)) {      // Short-circuiting
            return true;
        }
        if(right.test(o1, o2)) {
            return true;
        }
        return false;
    }
}
