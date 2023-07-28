package finio.core.impl;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import finio.core.AbstractOrderingManager;
import finio.core.FConst;
import finio.core.FUtil;
import finio.core.KeyPath;
import finio.core.KeyValue;
import finio.core.NonTerminal;
import finio.core.OrderingManager;
import finio.core.errors.FMapException;
import finio.core.events.KeyAddedListener;
import finio.core.events.KeyChangedListener;
import finio.core.events.KeyRemovedListener;
import finio.core.events.MapBatchChangedListener;
import finio.core.events.MapChangedListener;
import finio.core.events.MapClearedListener;
import finio.core.events.ValueChangedListener;
import replete.util.DebugUtil;

public class NominalNonTerminal implements NonTerminal {

    // Could also be called: EmptySilentTolerantNonTerminal

    // This class exists as a thought experiment, to help
    // flush out the NonTerminal interface.  Although it
    // *is* a non-terminal, it is immutable and perpetually
    // empty.  Attempts to modify it are not met with
    // exceptions but rather silent inaction.  It also does
    // not do any parameter checking.  Even if a parameter
    // would have produced an error (e.g. putByPath) in a
    // normal, empty FMap, it does not throw an exception.
    // It is similar
    // to 'new Object()' and 'new String()'.  These are also
    // immutable & empty objects.  They have no intrinsic
    // utility except to serve as base cases/control conditions
    // in testing.


    ////////////
    // FIELDS //
    ////////////

    transient volatile Set<Object>          keySet = null;
    transient volatile Collection<Object>   values = null;
    transient Set<Map.Entry<Object,Object>> entrySet = null;
    private boolean suppressAllEvents = false;
    private NominalOrderingManager nominalOrderingManager = new NominalOrderingManager();


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public NominalNonTerminal() {
        nominalOrderingManager.initialize(this);   // TODO: Make sure this works
    }


    /////////
    // MAP //
    /////////

    @Override
    public boolean isEmpty() {
        return true;                              // Changed from 'false'.
    }
    @Override
    public int size() {
        return 0;
    }
    @Override
    public int sizeNoSysMeta() {
        return 0;
    }
    @Override
    public int Z() {
        return size();                            // Shorthand: Always returns size()
    }
    @Override
    public Set<Object> getKeys() {
        Set<Object> ks = keySet;
        return (ks != null ? ks : (keySet = new KeySet()));
    }
    @Override
    public Collection<Object> getValues() {
        Collection<Object> vs = values;
        return (vs != null ? vs : (values = new Values()));
    }
    @Override
    public Set<Object> K() {
        return getKeys();                         // Shorthand: Always returns getKeys()
    }
    @Override
    public Collection<Object> V() {
        return getValues();                       // Shorthand: Always returns getValues()
    }
    @Override
    public Set<Object> keySet() {
        return getKeys();                         // Map: Always returns getKeys()
    }
    @Override
    public Collection<Object> values() {
        return getValues();                       // Map: Always returns getValues()
    }
    @Override
    public void clear() {
    }
    // [NT Core: 11 methods]


    /////////
    // GET //
    /////////

    @Override
    public Object get(Object K) {
        return null;
    }
    @Override
    public Object getByKey(Object K) {
        return get(K);                            // Always returns get()
    }
    @Override
    public Object getByIndex(int I) {
        return null;                              // No parameter checking
    }
    @Override
    public Object getKeyByIndex(int I) {
        return null;                              // No parameter checking
    }
    @Override
    public Object getByPath(KeyPath P) {
        return null;                              // No parameter checking
    }
    @Override
    public Object getByPathNoCopy(KeyPath P) {
        return null;                              // No parameter checking
    }
    @Override
    public Object getValid(Object K, Class type) {
        return null;
    }
    @Override
    public Object getAndSet(Object K, Object Vdefault) {
        return null;                              // Ignores set just like put.
    }
    @Override
    public Object getAndSetValid(Object K, Object Vdefault, Class type) {
        return null;                              // Ignores set just like put.
    }
    // [NT Get: 9 methods]


    /////////
    // PUT //
    /////////

    @Override
    public Object put(Object K, Object V) {
        return null;
    }
    @Override
    public Object putByKey(Object K, Object V) {
        return put(K, V);                         // Always returns put()
    }
    @Override
    public Object putByIndex(int I, Object V) {
        return null;
    }
    @Override
    public Object putByPath(KeyPath P, Object V) {
        return null;                              // No parameter checking
    }
    @Override
    public Object putByPathNoCopy(KeyPath P, Object V) {
        return null;                              // No parameter checking
    }
    @Override
    public void putAll(Map<? extends Object, ? extends Object> m) {
    }
    @Override
    public void putAll(NonTerminal M) {
    }
    // [NT Put: 7 methods]


    ////////////
    // REMOVE //
    ////////////

    @Override
    public Object removeByKey(Object K) {
        return null;
    }
    @Override
    public boolean removeValue(Object V) {
        return false;
    }
    @Override
    public Object removeByPath(KeyPath P) {
        return null;                              // No parameter checking
    }
    @Override
    public Object removeByPathNoCopy(KeyPath P) {
        return null;                              // No parameter checking
    }
    // [NT Remove: 4 methods]


    //////////////
    // CONTAINS //
    //////////////

    @Override
    public boolean has(Object K) {
        return false;
    }
    @Override
    public boolean hasKey(Object K) {
        return has(K);                            // Always returns has()
    }
    @Override
    public boolean hasValue(Object V) {
        return false;
    }
    @Override
    public boolean hasPath(KeyPath P) {
        return false;                             // No parameter checking
    }
    @Override
    public boolean hasPathNoCopy(KeyPath P) {
        return false;                             // No parameter checking
    }
    @Override
    public boolean containsKey(Object K) {
        return hasKey(K);                         // Map: Always returns hasKey()
    }
    @Override
    public boolean containsValue(Object V) {
        return hasValue(V);                       // Map: Always returns hasValue()
    }
    // [NT Put: 7 methods]


    ////////////
    // EVENTS //
    ////////////

    // Does not record listeners because it will never
    // have anything to report for any event type.
    @Override
    public void addMapChangedListener(MapChangedListener listener) {
    }
    @Override
    public void addMapBatchChangedListener(MapBatchChangedListener listener) {
    }
    @Override
    public void addMapClearedListener(MapClearedListener listener) {
    }
    @Override
    public void addKeyAddedListener(KeyAddedListener listener) {
    }
    @Override
    public void addKeyRemovedListener(KeyRemovedListener listener) {
    }
    @Override
    public void addKeyChangedListener(KeyChangedListener listener) {
    }
    @Override
    public void addValueChangedListener(ValueChangedListener listener) {
    }
    @Override
    public void removeMapChangedListener(MapChangedListener listener) {
    }
    @Override
    public void removeMapBatchChangedListener(MapBatchChangedListener listener) {
    }
    @Override
    public void removeMapClearedListener(MapClearedListener listener) {
    }
    @Override
    public void removeKeyAddedListener(KeyAddedListener listener) {
    }
    @Override
    public void removeKeyRemovedListener(KeyRemovedListener listener) {
    }
    @Override
    public void removeKeyChangedListener(KeyChangedListener listener) {
    }
    @Override
    public void removeValueChangedListener(ValueChangedListener listener) {
    }
    @Override
    public MapChangedListener[] getMapChangedListeners() {
        return new MapChangedListener[0];         // Courteous to still return empty arrays
    }
    @Override
    public MapBatchChangedListener[] getMapBatchChangedListeners() {
        return new MapBatchChangedListener[0];
    }
    @Override
    public MapClearedListener[] getMapClearedListeners() {
        return new MapClearedListener[0];
    }
    @Override
    public KeyAddedListener[] getKeyAddedListeners() {
        return new KeyAddedListener[0];
    }
    @Override
    public KeyRemovedListener[] getKeyRemovedListeners() {
        return new KeyRemovedListener[0];
    }
    @Override
    public KeyChangedListener[] getKeyChangedListeners() {
        return new KeyChangedListener[0];
    }
    @Override
    public ValueChangedListener[] getValueChangedListeners() {
        return new ValueChangedListener[0];
    }
    // [NT Events: 7 x 3 = 21 methods]


    /////////////////////////
    // EVENTS SUPPLEMENTAL //
    /////////////////////////

    @Override
    public boolean isSuppressAllEvents() {
        return suppressAllEvents;
    }
    @Override
    public void setSuppressAllEvents(boolean suppress) {
        suppressAllEvents = suppress;             // Keeps track, just has no effect
    }
    @Override
    public void notifyBatchUpdate() {
    }
    // 3 methods


    /////////////////////////////
    // CHANGE ALLOWED/PROBABLE //
    /////////////////////////////

    @Override
    public boolean canAdd(Object K, Object V) {
        return false;
    }
    @Override
    public boolean canChangeValue(Object K, Object V) {
        return false;
    }
    @Override
    public boolean canSetValue(Object K, Object V) {
        return canAdd(K, V) || canChangeValue(K, V);      // Always returns canAdd(K, V) || canChangeValue(K, V)
    }
    @Override
    public boolean canChangeKey(Object K1, Object K2) {
        return false;
    }
    @Override
    public boolean canRemove(Object K) {
        return false;
    }
    @Override
    public boolean canClearMap() {
        return false;
    }
    @Override
    public boolean wouldAdd(Object K, Object V) {
        return canAdd(K, V) && !has(K);                      // Always returns canAdd(K, V) && !has(K)
    }
    @Override
    public boolean wouldChangeValue(Object K, Object V) {
        return canChangeValue(K, V) && has(K);               // Always returns canChangeValue(K, V) && has(K)
    }
    @Override
    public boolean wouldSetValue(Object K, Object V) {
        return wouldAdd(K, V) || wouldChangeValue(K, V);     // Always returns wouldAdd(K, V) || wouldChangeValue(K, V)
    }
    @Override
    public boolean wouldChangeKey(Object K1, Object K2) {
        return canChangeKey(K1, K2) && has(K1) && !has(K2);  // Always returns canChangeKey(K1, K2) && has(K1) && !has(K2)
    }
    @Override
    public boolean wouldRemove(Object K) {
        return canRemove(K) && has(K);                       // Always returns canRemove(K) && has(K)
    }
    @Override
    public boolean wouldClearMap() {
        return canClearMap();                                // Always returns canClearMap()
    }


    //////////
    // MISC //
    //////////

    @Override
    public NonTerminal getSysMeta() {
        return null;
    }
    @Override
    public Object getSysMeta(Object K) {
        return null;
    }
    @Override
    public Object putSysMeta(Object K, Object V) {
        return null;
    }
    @Override
    public void compress() {
    }
    @Override
    public Set<Entry<Object, Object>> entrySet() {       // Map
        Set<Map.Entry<Object,Object>> es = entrySet;
        return es != null ? es : (entrySet = new EntrySet());
    }
    @Override
    public Object getNextAvailableKey() {
        return null;
    }
    @Override
    public Object getNextAvailableKey(String prefix) {
        return null;
    }
    @Override
    public void changeKey(Object Kcur, Object Knew) {   // No parameter checking
    }
    @Override
    public int sizeAll() {
        return 0;
    }
    @Override
    public void createAlternatesMap() {
    }
    @Override
    public void createAlternatesMap(Object K) {
    }
    @Override
    public void describe(Object K) {
    }
    @Override
    public void promote(Object K) {
    }
    @Override
    public boolean isRefreshable() {
        return false;
    }
    @Override
    public void refresh() {
    }
    public NonTerminal flatten() {
        return null;
    }
    // 15 methods


    ///////////////
    // RENDERING //
    ///////////////

    @Override
    public String toStringObject() {
        return FUtil.toStringBaseIdent(this);
    }
    @Override
    public String toString() {
        return toString(40);
    }
    @Override
    public String toString(int maxChars) {
        StringBuilder buffer = new StringBuilder();
        toStringAppend(buffer, maxChars);
        return buffer.toString();
    }
    @Override
    public void toStringAppend(StringBuilder buffer, int maxChars) {
        buffer.append("{}");
    }
    // 4 methods


    ///////////////
    // TRANSLATE //
    ///////////////

    @Override
    public Map<Object, Object> asJavaMap() {
        return new HashMap<>();
    }
    // 1 method


    /////////////
    // SORTING //
    /////////////

    @Override
    public void setOrdering(String name) {
    }
    @Override
    public void addOrdering(String name, OrderingManager orderingManager) {
        // Cannot add more
    }
    @Override
    public OrderingManager getOrdering(String name) {
        if(name.equals(FConst.SYS_INHERENT_ORDERING)) {
            return nominalOrderingManager;
        }
        return null;
    }
    @Override
    public boolean removeOrdering(String name) {
        return false;
    }
    // 4 methods


    ///////////////////////////
    // MOVE / COPY / OVERLAY //
    ///////////////////////////

    @Override
    public void move(KeyPath[] Psources, KeyPath Pdest, int position, boolean preventOverwrite)
               throws FMapException {
        // No parameter checking
    }
    @Override
    public void copy(KeyPath[] psources, KeyPath pdest, int position) {
        // No parameter checking
    }
    @Override
    public NonTerminal overlayByKeyPath(KeyPath P) {
        return null;
    }
    // 3 methods


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class KeySet extends AbstractSet<Object> {

        @Override
        public Iterator<Object> iterator() {
            return new Iterator<Object>() {
                @Override
                public boolean hasNext() {
                    return false;
                }
                @Override
                public Object next() {
                    return null;
                }
                @Override
                public void remove() {
                }
            };
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {
        }
    }

    private class Values extends AbstractCollection<Object> {

        @Override
        public Iterator<Object> iterator() {
            return new Iterator<Object>() {
                @Override
                public boolean hasNext() {
                    return false;
                }
                @Override
                public Object next() {
                    return null;
                }
                @Override
                public void remove() {
                }
            };
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {
        }
    }

    private class EntrySet extends AbstractSet<Map.Entry<Object,Object>> {

        @Override
        public Iterator<Map.Entry<Object,Object>> iterator() {
            return new Iterator<Map.Entry<Object,Object>>() {
                @Override
                public boolean hasNext() {
                    return false;
                }
                @Override
                public Map.Entry<Object,Object> next() {
                    return null;
                }
                @Override
                public void remove() {
                }
            };
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public void clear() {
        }
    }

    private class NominalOrderingManager extends AbstractOrderingManager {
        @Override public void ntBatchChanged() {}
        @Override public void ntCleared() {}
        @Override public void ntKeyAdded(Object K, Object V) {}
        @Override public void ntKeyRemoved(Object K) {}
        @Override public void ntKeyChanged(Object Kold, Object Knew) {}
        @Override public void ntValueChanged(Object K, Object V) {}

        @Override
        public Object getKey(int I) {
            return null;
        }

        @Override
        public boolean has(int I) {
            return false;
        }

        // TODO: Revisit these 3
        @Override
        public Set<Object> getKeys() {
            return new HashSet<>();
        }
        @Override
        public Collection<Object> getValues() {
            return new ArrayList<>();
        }
        @Override
        public Iterator iterator() {
            return null;
        }

        @Override protected void addEntry(KeyValue KV) {}
        @Override protected void initOrdering() {}
        @Override protected void clearOrdering() {}
        @Override protected void resortOrdering() {}
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        NonTerminal m = new NominalNonTerminal();
//        Map m = new HashMap();
        System.out.println(DebugUtil.quick(m.keySet()));
//        m.put("a", 3);
        System.out.println(DebugUtil.quick(m.keySet()));
        System.out.println(DebugUtil.quick(m.keySet()));
    }
}
