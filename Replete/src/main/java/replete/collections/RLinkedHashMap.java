package replete.collections;

import java.util.LinkedHashMap;
import java.util.Map;

public class RLinkedHashMap<K, V> extends LinkedHashMap<K, V> {


    ////////////
    // FIELDS //
    ////////////

    private DefaultCreator<V> creator;
    private boolean failOnNothingToGet = false;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RLinkedHashMap() {
        super();
    }
    public RLinkedHashMap(int initialCapacity) {
        super(initialCapacity);
    }
    public RLinkedHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    public RLinkedHashMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }
    public RLinkedHashMap(Map<? extends K, ? extends V> m) {
        super(m);
    }
    public RLinkedHashMap(Object... init) {
        for(int i = 0; i < init.length; i += 2) {
            put((K) init[i], (V) init[i + 1]);
        }
    }
    public RLinkedHashMap(DefaultCreator<V> creator) {
        this.creator = creator;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public V get(Object key) {
        if(!containsKey(key)) {
            if(creator != null) {
                V dflt = creator.create();
                put((K) key, dflt);
                return dflt;
            }
            if(failOnNothingToGet) {
                throw new IllegalStateException("No element available for " + key);
            }
        }
        return super.get(key);
    }

    public RLinkedHashMap put(Object... init) {
        for(int i = 0; i < init.length; i += 2) {
            put((K) init[i], (V) init[i + 1]);
        }
        return this;
    }
    public RLinkedHashMap p(Object... init) {   // Different name allows for the
        return put(init);                       // modified builder pattern
    }
    public RLinkedHashMap p(K key, V value) {   // Different name allows for the
        put(key ,value);                        // modified builder pattern
        return this;
    }


    //////////
    // MISC //
    //////////

    public V getAndPutIfAbsent(K key, V dflt) {      // Never used yet
        V value;
        if(!containsKey(key)) {
            put(key, dflt);
            value = dflt;
        } else {
            value = get(key);
        }
        return value;
    }

    public V getAndPutIfNull(K key, V dflt) {        // Probably never should have existed... Migrate to getAndPutIfAbsent someday
        V value = get(key);
        if(value == null) {
            put(key, dflt);
            value = dflt;
        }
        return value;
    }

    public boolean isFailOnNothingToGet() {
        return failOnNothingToGet;
    }
    public RLinkedHashMap setFailOnNothingToGet(boolean failOnNothingToGet) {
        this.failOnNothingToGet = failOnNothingToGet;
        return this;
    }

    public void putAllIfNotNull(Map<? extends K, ? extends V> m) {
        if(m != null) {
            super.putAll(m);
        }
    }
}
