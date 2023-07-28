package replete.equality.similarity;

public class LeftNullRightNonNullRule<T> implements AcceptableSimilarityRule<T> {
    public boolean test(T o1, T o2) {
        return o1 == null && o2 != null;
    }
}
