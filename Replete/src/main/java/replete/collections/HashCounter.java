package replete.collections;

import java.util.HashMap;

public class HashCounter<T> extends HashMap<T, Integer>
        implements Counter<T> {
    public void inc(T t) {
        Integer count = get(t);
        if(count == null) {
            count = 0;
        }
        put(t, count + 1);
    }
    public int getTotal() {
        int total = 0;
        for(Integer count : values()) {
            total += count;
        }
        return total;
    }
}
