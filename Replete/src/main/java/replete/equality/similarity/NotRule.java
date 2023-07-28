package replete.equality.similarity;

public class NotRule<T> implements AcceptableSimilarityRule<T> {
    private BinaryRule<T> target;
    public NotRule(BinaryRule<T> target) {
        this.target = target;
    }
    @Override
    public boolean test(T o1, T o2) {
        return !target.test(o1, o2);
    }
}
