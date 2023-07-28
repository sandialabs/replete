package finio.core.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import finio.core.DefaultNonTerminal;
import finio.core.FUtil;
import finio.core.KeyPath;
import finio.core.KeyValue;
import finio.core.NonTerminal;
import finio.core.NonTerminalCreator;
import finio.core.NonTerminalProvider;
import finio.core.OrderingManager;
import finio.core.errors.FListException;
import finio.core.errors.FMapException;
import finio.core.events.KeyAddedEvent;
import finio.core.events.KeyAddedListener;
import finio.core.events.KeyChangedEvent;
import finio.core.events.KeyChangedListener;
import finio.core.events.KeyRemovedEvent;
import finio.core.events.KeyRemovedListener;
import finio.core.events.MapBatchChangedListener;
import finio.core.events.MapChangedEvent;
import finio.core.events.MapChangedListener;
import finio.core.events.MapClearedEvent;
import finio.core.events.MapClearedListener;
import finio.core.events.MapEvent;
import finio.core.events.ValueChangedEvent;
import finio.core.events.ValueChangedListener;
import finio.core.sorting.SorterPair;
import replete.collections.AList;
import replete.util.ReflectionUtil;

// Remember any AList can be a set, stack or queue
// depending on how you use it.

// TODO: VIEW layers on top of map

// Using 'super.' to make it a little clearer when the base
// class methods are being leveraged.

// We have an issue where a Java ArrayList subclass cannot implement
// Map<Object, Object> due to a signature conflict with
//  * boolean List.remove(Object)
//  * Object Map.remove(Object)
// This is quite disappointing.

public class FList extends AList<Object> implements DefaultNonTerminal, NonTerminalCreator, NonTerminalProvider {


    ////////////
    // FIELDS //
    ////////////

    private boolean suppressAllEvents = false;
    private String sorterName;
    private List<SorterPair> sorters;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public FList() {}
    public FList(Collection<? extends Object> c) {  // Essentially copy constructor too
        super(c);
    }
    public FList(int initialCapacity) {
        super(initialCapacity);
    }

    // Custom

    // [OPTION] not have the keys in the var args
    public FList(Object... args) {
        for(int a = 0; a < args.length; a += 2) {
           if(a + 1 == args.length) {
               put(args[a], null);
           } else {
               put(args[a], args[a + 1]);
           }
        }
    }
    public FList(Map<?, ?> M) {      // Need two separate constructors here
        putAll(M);
    }
    public FList(NonTerminal M) {   // Need two separate constructors here
        putAll(M);
    }


    /////////
    // MAP //
    /////////

    // public boolean isEmpty();
    // public int size();
    @Override
    public int sizeNoSysMeta() {
        return size();   // Can't have SysMeta
    }
    @Override
    public int Z() {
        return super.size();
    }
    @Override
    public Set<Object> getKeys() {                   // O(n) runtime
        // [OPTION] include . && .. in return set
        return keySet();
    }
    @Override
    public Collection<Object> getValues() {          // O(n) runtime
        return values();
    }
    @Override
    public Set<Object> K() {                         // O(n) runtime
        return getKeys();
    }
    @Override
    public Collection<Object> V() {                  // O(n) runtime
        return getValues();
    }
    @Override
    public Set<Object> keySet() {                    // O(n) runtime
//        if(sorterName == null) {
            return keySetSink();
//        }
//        Set set = getSorter(sorterName);
//        if(set == null) {
//            return keySetSink();
//        }
//        return new SortedKeySet(this, set);
    }
    @Override
    public Collection<Object> values() {             // O(n) runtime
//        if(sorterName == null) {
            return valuesSink();
//        }
//        Set set = getSorter(sorterName);
//        if(set == null) {
//            return valuesSink();
//        }
//        return new SortedValuesCollection(this, set);
    }
    @Override
    public void clear() {                            // Overriding to call event
        super.clear();
        fireMapClearedNotifier();
    }
    // [NT Core: 8/10 methods]


    /////////
    // GET //
    /////////

    // public Object get(int I);              // This method untouched for lists
    @Override
    public Object get(Object K) {
        checkKeyType(K);
        int I = (Integer) K;
        if(I < 0 || I >= Z()) {
            // [OPTION] Return null here to keep with map semantics
        }
        return super.get(I);     // Will throw IndexOutOfBoundsException if index invalid
    }
    @Override
    public Object getByIndex(int I) {
        if(I < 0 || I >= Z()) {
            // [OPTION] Return null here to keep with map semantics
        }
        return super.get(I);     // Will throw IndexOutOfBoundsException if index invalid
    }
    @Override
    public Object getByKey(Object K) {
        return get(K);
    }
    @Override
    public Object getKeyByIndex(int I) {
        if(I < 0 || I >= Z()) {
            throw new IndexOutOfBoundsException(
                "Index: " + I + ", Size: " + Z());
            // [OPTION] Return null here to keep with map semantics
        }
        return I;
    }
    @Override
    public Object getByPath(KeyPath P) {
        KeyPath Pcopy = KeyPath.KP(P); // So as not to change input parameter P
        return getByPathNoCopy(Pcopy);
    }
    @Override
    public Object getByPathNoCopy(KeyPath P) {
        if(KeyPath.isEmpty(P)) {
            return this;         // Empty path is valid ([OPTION] throw an exception)
        }
        Object S = P.removeFirst();
        // if(!has(S)) {
        //     [OPTION] Return null just like get("unknown key") does
        //     But then have to use has(KeyPath) just like you do with normal map
        //     operations.
        //     throw new AListException("Nope!");
        //     return null; (actually just go to next blocks).
        // }
        if(P.isEmpty()) {
            return get(S);       // Return value could be terminal or non-terminal.
        }
        Object Vx = get(S);
        if(!(Vx instanceof NonTerminal)) {
            // [OPTION] Return null
            throw new FListException("Cannot get a value at path due to terminal value in path.");
        }
        return ((NonTerminal) Vx).getByPathNoCopy(P);
    }
    @Override
    public Object getValid(Object K, Class type) {
        Object V = get(K);
        if(V == null || type.isAssignableFrom(V.getClass())) {
            return V;
        }
        return null;
    }
    @Override
    public Object getAndSet(Object K, Object Vdefault) {  // Makes less sense for lists
        Object V = null;
        try {
            if(!has(K)) {
                return V = Vdefault;
            }
            return V = get(K);
        } finally {
            put(K, V);             // [OPTION] Extend list as described in set
        }
    }
    @Override
    public Object getAndSetValid(Object K, Object Vdefault, Class type) {
        Object V = null;
        try {
            if(!has(K)) {
                return V = Vdefault;
            }
            Object ret = get(K);
            if(ret == null || type.isAssignableFrom(ret.getClass())) {
                return V = ret;
            }
            return V = Vdefault;
        } finally {
            put(K, V);
        }
    }


    /////////
    // PUT //
    /////////

    @Override
    public Object put(Object K, Object V) {
        checkKeyType(K);
        return put(((Integer) K).intValue(), V);
    }
    @Override
    public Object putByKey(Object K, Object V) {
        return put(K, V);
    }
    @Override
    public Object putByIndex(int I, Object E) {
        return put(I, E);
    }
    @Override
    public Object putByPath(KeyPath P, Object V) {
        KeyPath Pcopy = KeyPath.KP(P);       // So as not to change input parameter P
        return putByPathNoCopy(Pcopy, V);
    }
    @Override
    public Object putByPathNoCopy(KeyPath P, Object V) {
        if(KeyPath.isEmpty(P)) {
            throw new FListException("Cannot put a value at an empty path.");
        }
        Object S = P.removeFirst();
        // if(!has(S) && ignoreNewKeys) {
        //    [OPTION] Can prevent unknown keys from getting into the map
        // }
        if(P.isEmpty()) {
            return put(S, V);
        }
        Object Vx = get(S);
        if(!(Vx instanceof NonTerminal)) {
            throw new FListException("Cannot put a value at path due to terminal value in path.");
        }
        return ((NonTerminal) Vx).putByPathNoCopy(P, V);
    }
    @Override
    public void putAll(Map<? extends Object, ? extends Object> m) {
        for(Object K : m.keySet()) {
            put(K, m.get(K));
        }
    }
    @Override
    public void putAll(NonTerminal M) {
        for(Object K : M.K()) {
            put(K, M.get(K));
        }
    }


    ////////////
    // REMOVE //
    ////////////

    // public Object remove(int I);              // This method untouched for lists
    // public boolean remove(Object V);          // This method untouched for lists
    @Override
    public Object removeByKey(Object K) {
        checkKeyType(K);      // [OPTION] Return null if key of wrong type to keep with map semantics
        // [OPTION] Return null if key does not exist to keep with map semantics
        return super.remove(((Integer) K).intValue());
    }
    @Override
    public boolean removeValue(Object V) {       // O(n) runtime
        return super.remove(V);
    }
    @Override
    public Object removeByPath(KeyPath P) {
        KeyPath Pcopy = KeyPath.KP(P);       // So as not to change input parameter P
        return removeByPathNoCopy(Pcopy);
    }
    @Override
    public Object removeByPathNoCopy(KeyPath P) {
        if(KeyPath.isEmpty(P)) {
            throw new FListException("Cannot remove a value at an empty path.");
        }
        Object S = P.removeFirst();
        if(P.isEmpty()) {
            return removeByKey(S);
        }
        Object Vx = get(S);
        if(!(Vx instanceof NonTerminal)) {
            // [OPTION] Return null
            throw new FListException("Cannot remove a value at path due to terminal value in path.");
        }
        return ((NonTerminal) Vx).removeByPathNoCopy(P);
    }
    // [NT Remove: 4 methods]


    //////////////
    // CONTAINS //
    //////////////

    @Override
    public boolean has(Object K) {
        checkKeyType(K);    // [OPTION] is to return false similar to map containsKey method,
                            // (cares not of type) but exception does enforce concept of a
                            // beta-map.
        int I = (Integer) K;
        return I >= 0 && I < Z();
    }
    @Override
    public boolean hasKey(Object K) {
        return has(K);
    }
    @Override
    public boolean hasValue(Object V) {
        return super.contains(V);
    }
    @Override
    public boolean hasPath(KeyPath P) {
        KeyPath Pcopy = KeyPath.KP(P);       // So as not to change input parameter P
        return hasPathNoCopy(Pcopy);
    }
    @Override
    public boolean hasPathNoCopy(KeyPath P) {
        if(KeyPath.isEmpty(P)) {
            return true;            // Empty path is valid ([OPTION] throw an exception)
        }
        Object S = P.removeFirst();
        if(!has(S)) {
            return false;
        }
        if(P.isEmpty()) {
            return true;
        }
        Object Vx = get(S);
        if(!(Vx instanceof NonTerminal)) {
            // [OPTION] Return false
            throw new FListException("Cannot determine existence of path due to terminal value in path.");
        }
        return ((NonTerminal) Vx).hasPathNoCopy(P);
    }
    @Override
    public boolean containsKey(Object K) {
        return hasKey(K);
    }
    @Override
    public boolean containsValue(Object V) {
        return hasValue(V);
    }
    // public boolean contains(Object V);               // This method untouched for lists
    // public boolean containsAll(Collection<?> Vs);    // This method untouched for lists
    // [NT Contains: 7 methods]


    ////////////
    // EVENTS //
    ////////////

    // TODO: Think about these additional change notification topics:
    //   1. Events based on key spec:
    //       public void addMapChangedListener(MapChangedListener listener, KeySpec spec)
    //       public void addKeyAddedListener(KeyAddedListener listener, KeySpec spec)
    //       public void addKeyChangedListener(KeyChangedListener listener, KeySpec spec)
    //       public void addKeyRemovedListener(KeyRemovedListener listener, KeySpec spec)
    //   2. Recursive nature of a "change":
    //       AMap a = A(), b;
    //       a.put("submap", b = A());
    //       a.addKeyChangedListener(listener);
    //       b.put("value", 42);
    //      Should a's change notifier fire?  a did change in one sense, and
    //      possibly not in another.  Nothing about a changed in the hardware,
    //      in the computer.  No keys were added/removed and a.b still points
    //      to same memory address.  However, if a were a personnel record,
    //      and b were a felony arrest record, you would say that if b changes
    //      then so does a in effect.  However,  if a were a "part" record
    //      and a is a "reference" record, then the changing of the reference
    //      wouldn't imply that the part record changed.  I think this has to
    //      do with the contains/associations characteristic of a given link.
    //      Containment generally implies hierarchical changing notification
    //      and simple association does not (you'd expect the agent to separately
    //      subscribe to the changes of the associated map).

    protected transient List<MapChangedListener> mapChangedListeners = new ArrayList<MapChangedListener>();
    @Override
    public void addMapChangedListener(MapChangedListener listener) {
        mapChangedListeners.add(listener);
    }
    @Override
    public void removeMapChangedListener(MapChangedListener listener) {
        mapChangedListeners.remove(listener);
    }
    @Override
    public MapChangedListener[] getMapChangedListeners() {
        return mapChangedListeners.toArray(new MapChangedListener[0]);
    }
    private void fireMapChangedNotifier(MapEvent cause) {
        if(mapChangedListeners.size() != 0) {
            MapChangedEvent event = new MapChangedEvent(this, cause);
            for(MapChangedListener mapChangedListener : mapChangedListeners) {
                mapChangedListener.mapChanged(event);
            }
        }
    }

    protected transient List<MapBatchChangedListener> mapBatchChangedListeners = new ArrayList<>();
    @Override
    public void addMapBatchChangedListener(MapBatchChangedListener listener) {
        mapBatchChangedListeners.add(listener);
    }
    @Override
    public void removeMapBatchChangedListener(MapBatchChangedListener listener) {
        mapBatchChangedListeners.remove(listener);
    }
    @Override
    public MapBatchChangedListener[] getMapBatchChangedListeners() {
        return mapBatchChangedListeners.toArray(new MapBatchChangedListener[0]);
    }
    private void fireMapBatchChangedNotifier() {
        // TODO
    }

    protected transient List<MapClearedListener> mapClearedListeners = new ArrayList<MapClearedListener>();
    @Override
    public void addMapClearedListener(MapClearedListener listener) {
        mapClearedListeners.add(listener);
    }
    @Override
    public void removeMapClearedListener(MapClearedListener listener) {
        mapClearedListeners.remove(listener);
    }
    @Override
    public MapClearedListener[] getMapClearedListeners() {
        return mapClearedListeners.toArray(new MapClearedListener[0]);
    }
    private void fireMapClearedNotifier() {
        MapClearedEvent event = new MapClearedEvent(this);
        for(MapClearedListener mapClearedListener : mapClearedListeners) {
            mapClearedListener.mapCleared(event);
        }
        fireMapChangedNotifier(event);
    }

    protected transient List<KeyAddedListener> keyAddedListeners = new ArrayList<KeyAddedListener>();
    @Override
    public void addKeyAddedListener(KeyAddedListener listener) {
        keyAddedListeners.add(listener);
    }
    @Override
    public void removeKeyAddedListener(KeyAddedListener listener) {
        keyAddedListeners.remove(listener);
    }
    @Override
    public KeyAddedListener[] getKeyAddedListeners() {
        return keyAddedListeners.toArray(new KeyAddedListener[0]);
    }
    private void fireKeyAddedNotifier(Object K, Object V) {  // Could inline this.
        KeyAddedEvent event = new KeyAddedEvent(this, K, V);
        for(KeyAddedListener keyAddedListener : keyAddedListeners) {
            keyAddedListener.keyAdded(event);
        }
        fireMapChangedNotifier(event);
    }

    protected transient List<KeyRemovedListener> keyRemovedListeners = new ArrayList<KeyRemovedListener>();
    @Override
    public void addKeyRemovedListener(KeyRemovedListener listener) {
        keyRemovedListeners.add(listener);
    }
    @Override
    public void removeKeyRemovedListener(KeyRemovedListener listener) {
        keyRemovedListeners.remove(listener);
    }
    @Override
    public KeyRemovedListener[] getKeyRemovedListeners() {
        return keyRemovedListeners.toArray(new KeyRemovedListener[0]);
    }
    private void fireKeyRemovedNotifier(Object K, Object V) {  // Could inline this
        KeyRemovedEvent event = new KeyRemovedEvent(this, K, V);
        for(KeyRemovedListener keyRemovedListener : keyRemovedListeners) {
            keyRemovedListener.keyRemoved(event);
        }
        fireMapChangedNotifier(event);
    }

    protected transient List<KeyChangedListener> keyChangedListeners = new ArrayList<KeyChangedListener>();
    @Override
    public void addKeyChangedListener(KeyChangedListener listener) {
        keyChangedListeners.add(listener);
    }
    @Override
    public void removeKeyChangedListener(KeyChangedListener listener) {
        keyChangedListeners.remove(listener);
    }
    @Override
    public KeyChangedListener[] getKeyChangedListeners() {
        return keyChangedListeners.toArray(new KeyChangedListener[0]);
    }
    private void fireKeyChangedNotifier(Object K, Object Kdst) {
        KeyChangedEvent event = new KeyChangedEvent(this, K, Kdst);
        for(KeyChangedListener listener : keyChangedListeners) {
            listener.keyChanged(event);
        }
        fireMapChangedNotifier(event);
    }

    protected transient List<ValueChangedListener> valueChangedListeners = new ArrayList<ValueChangedListener>();
    @Override
    public void addValueChangedListener(ValueChangedListener listener) {
        valueChangedListeners.add(listener);
    }
    @Override
    public void removeValueChangedListener(ValueChangedListener listener) {
        valueChangedListeners.remove(listener);
    }
    @Override
    public ValueChangedListener[] getValueChangedListeners() {
        return valueChangedListeners.toArray(new ValueChangedListener[0]);
    }
    private void fireKeyChangedNotifier(Object K, Object Vprev, Object V) {
        ValueChangedEvent event = new ValueChangedEvent(this, K, Vprev, V);
        for(ValueChangedListener listener : valueChangedListeners) {
            listener.valueChanged(event);
        }
        fireMapChangedNotifier(event);
    }
    // [NT Events: 7 x 3 = 21 methods (not including fire* methods)]


    /////////////////////////
    // EVENTS SUPPLEMENTAL //
    /////////////////////////

    @Override
    public boolean isSuppressAllEvents() {
        return suppressAllEvents;
    }
    @Override
    public void setSuppressAllEvents(boolean suppressAllEvents) {
        this.suppressAllEvents = suppressAllEvents;
    }
    @Override
    public void notifyBatchUpdate() {
//        fireMapBatchChangedNotifier();
    }
    // 3 methods


    /////////////////////////////
    // CHANGE ALLOWED/PROBABLE //      This section not done yet (of course)
    /////////////////////////////

    public boolean canAdd(Object K, Object V) {
        return true;
    }
    public boolean canChangeValue(Object K, Object V) {
        return true;
    }
    public boolean canSetValue(Object K, Object V) {
        return canAdd(K, V) || canChangeValue(K, V);
    }
    public boolean canChangeKey(Object K1, Object K2) {
        return true;
    }
    public boolean canRemove(Object K) {
        return true;
    }
    public boolean canClearMap() {
        return true;
    }
    public boolean wouldAdd(Object K, Object V) {
        return canAdd(K, V) && !has(K);
    }
    public boolean wouldChangeValue(Object K, Object V) {
        return canChangeValue(K, V) && has(K);
    }
    public boolean wouldSetValue(Object K, Object V) {
        return wouldAdd(K, V) || wouldChangeValue(K, V);
    }
    public boolean wouldChangeKey(Object K1, Object K2) {
        return canChangeKey(K1, K2) && has(K1) && !has(K2);
    }
    public boolean wouldRemove(Object K) {
        return canRemove(K) && has(K);
    }
    public boolean wouldClearMap() {
        return canClearMap();
    }
    // 12 methods


    //////////
    // MISC //
    //////////

    @Override
    public NonTerminal getSysMeta() {
        throw new FListException("getSysMeta not supported");
    }
    @Override
    public Object getSysMeta(Object K) {
        // [OPTION] Technically you could support this in some limited
        // sense, either by giving every AList a Java field to store meta
        // map properties, or by using a special value indicating that it
        // is the special meta map.  However, both of these ideas
        // would still be in some way inconsistent with the other
        // non-terminal implementations.
        throw new FListException("getSysMeta not supported");
    }
    @Override
    public Object putSysMeta(Object K, Object V) {
        throw new FListException("putSysMeta not supported");
    }
    @Override
    public void compress() {
        trimToSize();
    }
    @Override
    public Set<Entry<Object, Object>> entrySet() {
        return null;  // TODO!!!!!!!!
    }
    @Override
    public Object getNextAvailableKey() {
        return size();
    }
    @Override
    public Object getNextAvailableKey(String prefix) {
        throw new UnsupportedOperationException("AList does not support getNextAvailableKey(prefix).");
    }
    @Override
    public void changeKey(Object Kcur, Object Knew) {
        throw new UnsupportedOperationException("AList does not support changeKey currently.");
    }
    @Override
    public int sizeAll() {
        int sz = size();     // Does not count itself.  Client applications must do this if they desire.
        for(Object K : K()) {
            Object V = get(K);
            if(V instanceof NonTerminal) {
                sz += ((NonTerminal) V).sizeAll();
            }
        }
        return sz;
    }
    @Override
    public void createAlternatesMap() {
        // todo
    }
    @Override
    public void createAlternatesMap(Object K) {
        // todo
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
        throw new UnsupportedOperationException();
    }
    @Override
    public NonTerminal flatten() {
        throw new UnsupportedOperationException();
    }
    // 15 methods


    ///////////////
    // RENDERING //
    ///////////////

    public String toStringObject() {
        return FUtil.toStringBase(this);
    }
    @Override
    public String toString() {
        return super.toString();  // TODO
    }
    @Override
    public String toString(int maxChars) {
        return toString();  // TODO
    }
    @Override
    public void toStringAppend(StringBuilder buffer, int maxChars) {
        // TODO
    }
    // 4 methods


    ///////////////
    // TRANSLATE //
    ///////////////

    // Would have been nice to have just implemented
    // Map via ANonTerminal...
    @Override
    public Map<Object, Object> asJavaMap() {
        return new Map<Object, Object>() {
            @Override
            public int size() {
                return FList.this.size();
            }
            @Override
            public boolean isEmpty() {
                return FList.this.isEmpty();
            }
            @Override
            public boolean containsKey(Object K) {
                return FList.this.containsKey(K);
            }
            @Override
            public boolean containsValue(Object V) {
                return FList.this.containsValue(V);
            }
            @Override
            public Object get(Object K) {
                return FList.this.get(K);
            }
            @Override
            public Object put(Object K, Object V) {
                return FList.this.put(K, V);
            }
            @Override
            public Object remove(Object K) {
                return FList.this.removeByKey(K);  // Switched to appropriate method
            }
            @Override
            public void putAll(Map<? extends Object, ? extends Object> M) {
                FList.this.putAll(M);
            }
            @Override
            public void clear() {
                FList.this.clear();
            }
            @Override
            public Set<Object> keySet() {
                return FList.this.keySet();
            }
            @Override
            public Collection<Object> values() {
                return FList.this.values();
            }
            @Override
            public Set<java.util.Map.Entry<Object, Object>> entrySet() {
                return FList.this.entrySet();
            }
            // Modifying from AbstractMap.toString since don't
            // have entrySet yet.
            @Override
            public String toString() {
                if(isEmpty()) {
                    return "{}";
                }
                StringBuilder sb = new StringBuilder();
                sb.append('{');
                for(Object K : keySet()) {
                    System.out.println(System.currentTimeMillis());
                    Object V = get(K);
                    sb.append(K == this ? "(this Map)" : K);
                    sb.append('=');
                    sb.append(V == this ? "(this Map)" : V);
                    sb.append(',').append(' ');
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.deleteCharAt(sb.length() - 1);
                return sb.append('}').toString();
            }
        };
    }


    /////////////
    // SORTING //
    /////////////

    @Override
    public void setOrdering(String name) {
    }
    @Override
    public void addOrdering(String name, OrderingManager orderingManager) {
    }
    @Override
    public OrderingManager getOrdering(String name) {
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
    public void move(KeyPath[] Psources, KeyPath Pdest, int position, boolean preventOverwrite) throws FMapException {

    }
    @Override
    public void copy(KeyPath[] psources, KeyPath pdest, int position) {

    }
    @Override
    public NonTerminal overlayByKeyPath(KeyPath P) {
        return null;
    }
    // 3 methods


    ////////////////////
    // AList SPECIFIC //
    ////////////////////

    private void initializeSorter(Set sorter) {
        sorter.clear();
        for(Object K : keySet()) {
            Object V = get(K);
            KeyValue KV = new KeyValue(K, V, true);
            sorter.add(KV);
        }
    }
    // Redundant method to make a list seem more like a map.
    public Object put(int I, Object E) {
        // [OPTION] is to stay close to the semantics of put by always allowing
        // an integer key to end up in the list, and we can do that by expanding
        // the list to accommodate and filling in the rest with null.
        //if(I >= size()) {
        //    while(I >= size()) {
        //        add(null);
        //    }
        //}
        if(I == size()) {   // At least allow for putting at end of list.
            add(E);
            return null;
        }
        return set(I, E);
    }
    @Override
    public void add(int index, Object element) {
        super.add(index, element);
//        fireKeyAddedNotifier(index, element);   // what about all the moving?!!?!??!?!
    }
    @Override
    public boolean add(Object e) {
        boolean result = super.add(e);
        fireKeyAddedNotifier(size() - 1, e);   // looks good
        return result;
    }
    @Override
    public boolean addAll(Collection<? extends Object> c) {
        int preSize = size();
        boolean result = super.addAll(c);
        for(Object o : c) {
            fireKeyAddedNotifier(preSize++, o);  // looks good
        }
        return result;
    }
    @Override
    public boolean addAll(int index, Collection<? extends Object> c) {
        // fire add keys
        boolean result = super.addAll(index, c);
//        for(Object o : c) {
//            fireKeyAddedNotifier(c, o);  //??????
//        }
        return result;
    }
    @Override
    public Object remove(int index) {
        // fire remove key
        return super.remove(index);
    }
    @Override
    public boolean remove(Object o) {
        // fire remove key
        return super.remove(o);
    }
    @Override
    public boolean removeAll(Collection<?> c) {
        // fire remove keys
        return super.removeAll(c);
    }
    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        // fire remove keys
        super.removeRange(fromIndex, toIndex);
    }
    @Override
    public boolean retainAll(Collection<?> c) {
        // fire remove keys
        return super.retainAll(c);
    }
    @Override
    public Object set(int index, Object element) {
        // fire key change key
        return super.set(index, element);
    }

    public int arraySize() {
        Object table = ReflectionUtil.get(this, "elementData");
        return Array.getLength(table);
    }

    @Override
    public FList prepended(Object E) {
        return (FList) super.prepended(E);
    }
    @Override
    public FList appended(Object E) {
        return (FList) super.appended(E);
    }
    @Override
    public FList removedFirst() {
        return (FList) super.removedFirst();
    }
    @Override
    public FList removedLast() {
        return (FList) super.removedLast();
    }
    @Override
    public FList withFirst(Object E) {
        return (FList) super.withFirst(E);
    }
    @Override
    public FList withLast(Object E) {
        return (FList) super.withLast(E);
    }
    @Override
    public FList withoutFirst() {
        return (FList) super.withoutFirst();
    }
    @Override
    public FList withoutLast() {
        return (FList) super.withoutLast();
    }
    @Override
    public FList reversed() {
        return (FList) super.reversed();
    }

    @Override
    public FList subList(int fromIndex, int toIndexNonIncl) {
        FList L = new FList();
        for(int I = fromIndex; I < toIndexNonIncl; I++) {
            L.add(super.get(I));
        }
        return L;
    }
    // Similar to String.substring(int)
    public FList subList(int fromIndex) {
        return subList(fromIndex, size());
    }

    private void checkKeyType(Object K) {
        if(!(K instanceof Integer)) {
            throw new FListException("Key must be an integer.");
        }
    }
    public Set<Object> keySetSink() {                // O(n) runtime
        Set<Object> Ks = new LinkedHashSet<Object>();
        for(int I = 0; I < Z(); I++) {
            Ks.add(I);
        }
        return Ks;
    }
    public Collection<Object> valuesSink() {         // O(n) runtime
        List<Object> Vs = new ArrayList<Object>();   // Could of course use different constructor with 'this'
        for(int I = 0; I < Z(); I++) {
            Vs.add(super.get(I));
        }
        return Vs;
    }


    /////////////////////////////////////
    // NonTerminal{Generator|Provider} //
    /////////////////////////////////////

    @Override
    public NonTerminal extract() {
        FMap M = new FMap();
        for(int I = 0; I < Z(); I++) {
            M.put(I, super.get(I));
        }
        return M;
    }
    @Override
    public NonTerminal provideNonTerminal() {
        return this;
    }

    private Object readResolve() {
        mapChangedListeners = new ArrayList<>();
        mapBatchChangedListeners = new ArrayList<>();
        mapClearedListeners = new ArrayList<>();
        keyAddedListeners = new ArrayList<>();
        keyRemovedListeners = new ArrayList<>();
        keyChangedListeners = new ArrayList<>();
        valueChangedListeners = new ArrayList<>();
        return this;
    }
}
