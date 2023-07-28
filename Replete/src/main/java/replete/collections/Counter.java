package replete.collections;

public interface Counter<T> {
    public void inc(T t);
    public int getTotal();
}
