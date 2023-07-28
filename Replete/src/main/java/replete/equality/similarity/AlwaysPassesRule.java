package replete.equality.similarity;

public class AlwaysPassesRule<T> implements AcceptableSimilarityRule<T> {   // a.k.a NoRestrictionsRule
    public boolean test(T o1, T o2) {
        return true;
    }
}
