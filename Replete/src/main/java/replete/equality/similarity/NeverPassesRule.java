package replete.equality.similarity;

public class NeverPassesRule<T> implements AcceptableSimilarityRule<T> {
    public boolean test(T o1, T o2) {
        return false;
    }
}
