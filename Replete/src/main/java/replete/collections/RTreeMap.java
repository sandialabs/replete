package replete.collections;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class RTreeMap<K, V> extends TreeMap<K, V> {


    ////////////
    // FIELDS //
    ////////////

    private DefaultCreator<V> creator;
    private boolean failOnNothingToGet = false;
    private boolean dummy = true;  // for JSON serialization testing


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RTreeMap() {
        super();
    }
    public RTreeMap(Comparator<? super K> comparator) {
        super(comparator);
    }
    public RTreeMap(Map<? extends K, ? extends V> m) {
        super(m);
    }
    public RTreeMap(Map<? extends K, ? extends V> m, Comparator<? super K> comparator) {
        super(comparator);
        putAll(m);
    }
    public RTreeMap(SortedMap<K, ? extends V> m) {
        super(m);
    }
    public RTreeMap(Object... init) {
        for(int i = 0; i < init.length; i += 2) {
            put((K) init[i], (V) init[i + 1]);
        }
    }
    public RTreeMap(DefaultCreator<V> creator) {
        this.creator = creator;
    }
    public RTreeMap(Comparator<? super K> comparator, DefaultCreator<V> creator) {
        super(comparator);
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

    public RTreeMap<K, V> setCreator(DefaultCreator<V> creator) {
        this.creator = creator;
        return this;
    }
    public RTreeMap<K, V> setFailOnNothingToGet(boolean failOnNothingToGet) {
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

    public RTreeMap<K, V> put(Object... init) {
        for(int i = 0; i < init.length; i += 2) {
            put((K) init[i], (V) init[i + 1]);
        }
        return this;
    }
    public RTreeMap<K, V> p(Object... init) {
        return put(init);
    }
    public RTreeMap<K, V> p(K key, V value) {
        put(key ,value);
        return this;
    }


    //////////
    // MISC //
    //////////

    public V getAndPutIfNull(K key, V dflt) {        // Probably never should have existed... Migrate to getAndPutIfAbsent someday
        V value = get(key);
        if(value == null) {
            put(key, dflt);
            value = dflt;
        }
        return value;
    }
}
