package replete.equality.similarity;

public interface BinaryRule<T> {
    public boolean test(T o1, T o2);
}
