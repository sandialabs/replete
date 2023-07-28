package replete.collections;

import java.util.HashMap;
import java.util.Map;

public class RHashMap<K, V> extends HashMap<K, V> {


    ////////////
    // FIELDS //
    ////////////

    private DefaultCreator<V> creator;
    private boolean failOnNothingToGet = false;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RHashMap() {
        super();
    }
    public RHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    public RHashMap(int initialCapacity) {
        super(initialCapacity);
    }
    public RHashMap(Map<? extends K, ? extends V> m) {
        super(m);
    }
    public RHashMap(Object... init) {
        for(int i = 0; i < init.length; i += 2) {
            put((K) init[i], (V) init[i + 1]);
        }
    }
    public RHashMap(DefaultCreator<V> creator) {
        this.creator = creator;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public DefaultCreator<V> getCreator() {
        return creator;
    }
    public boolean isFailOnNothingToGet() {
        return failOnNothingToGet;
    }

    // Mutators

    public RHashMap<K, V> setCreator(DefaultCreator<V> creator) {
        this.creator = creator;
        return this;
    }
    public RHashMap<K, V> setFailOnNothingToGet(boolean failOnNothingToGet) {
        this.failOnNothingToGet = failOnNothingToGet;
        return this;
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

    public RHashMap<K, V> put(Object... init) {
        for(int i = 0; i < init.length; i += 2) {
            put((K) init[i], (V) init[i + 1]);
        }
        return this;
    }
    public RHashMap<K, V> p(Object... init) {
        return put(init);
    }
    public RHashMap<K, V> p(K key, V value) {
        put(key ,value);
        return this;
    }


    //////////
    // MISC //
    //////////

    public V getAndPutIfAbsent(K key, V dflt) {
        V value;
        if(!containsKey(key)) {
            put(key, dflt);
            value = dflt;
        } else {
            value = get(key);
        }
        return value;
    }

    public void putAllIfNotNull(Map<? extends K, ? extends V> m) {
        if(m != null) {
            super.putAll(m);
        }
    }
}
