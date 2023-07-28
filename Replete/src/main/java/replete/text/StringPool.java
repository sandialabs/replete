package replete.text;

import java.util.HashMap;

// Fastest possible implementation.  Not thread-safe yet.
// Could add counts for hits & misses.  This class,
// with its 1 method, can work WONDERS in certain
// situations.
public class StringPool extends HashMap<String, String> {
//    private AtomicLong hit  = new AtomicLong(0);
//    private AtomicLong miss = new AtomicLong(0);

    public String resolve(String candidate) {   // Not synchronized!
        if(containsKey(candidate)) {
//            hit.incrementAndGet();
            return get(candidate);
        }
//        miss.incrementAndGet();
        put(candidate, candidate);
        return candidate;
    }
}
