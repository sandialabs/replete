package replete.collections.simq;

public class DataGlob<T> {
    public int index;
    public long when;
    public T object;
    public DataGlob(int index, long when, T object) {
        this.index = index;
        this.when = when;
        this.object = object;
    }
    @Override
    public String toString() {
        return "I=" + index + "/W=" + when + "/O=" + object;
    }
}
