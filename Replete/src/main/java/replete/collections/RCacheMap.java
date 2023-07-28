package replete.collections;

import java.util.Map;

public class RCacheMap<K, V> extends RLinkedHashMap<K, V> {


    ////////////
    // FIELDS //
    ////////////

    public static final int DEFAULT_MAX_CACHE_SIZE = 1000;
    private int maxCacheSize = DEFAULT_MAX_CACHE_SIZE;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RCacheMap() {
        super(16, 0.75F, true);
    }
    public RCacheMap(int initialCapacity) {
        super(initialCapacity, 0.75F, true);
    }
    public RCacheMap(int initialCapacity, int maxCacheSize) {                      // New
        super(initialCapacity, 0.75F, true);
        this.maxCacheSize = maxCacheSize;
    }
    public RCacheMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor, true);
    }
    public RCacheMap(int initialCapacity, float loadFactor, int maxCacheSize) {    // New
        super(initialCapacity, loadFactor, true);
        this.maxCacheSize = maxCacheSize;
    }
//    public RCacheMap(int initialCapacity, float loadFactor, boolean accessOrder) {
//        super(initialCapacity, loadFactor, accessOrder);    // Not applicable here (accessOrder must always be true)
//    }
//    public RCacheMap(Map<? extends K, ? extends V> m) {
//        super(m);                                           // Not applicable here (accessOrder must always be true)
//    }
//    public RCacheMap(Object... init) {
//        super(init);                                        // Not useful
//    }
//    public RCacheMap(DefaultCreator<V> creator) {
//        super(creator);                                     // Not useful
//    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public int getMaxCacheSize() {
        return maxCacheSize;
    }

    // Mutators

    public RCacheMap setMaxCacheSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxCacheSize;
    }
}
