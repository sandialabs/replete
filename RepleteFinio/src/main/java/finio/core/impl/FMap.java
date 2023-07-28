package finio.core.impl;

import static finio.core.KeyPath.KP;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import finio.core.ArrayListInsertionOrderOrderingManager;
import finio.core.DefaultNonTerminal;
import finio.core.FConst;
import finio.core.FUtil;
import finio.core.KeyPath;
import finio.core.KeySpec;
import finio.core.NonTerminal;
import finio.core.NonTerminalCreator;
import finio.core.NonTerminalProvider;
import finio.core.OrderingManager;
import finio.core.Util;
import finio.core.ValueOverrider;
import finio.core.errors.FMapCompositeException;
import finio.core.errors.FMapException;
import finio.core.errors.KeyPathException;
import finio.core.errors.KeyPathException.Type;
import finio.core.errors.KeyPathExistenceException;
import finio.core.errors.KeyPathHierarchyException;
import finio.core.errors.KeyPathValueTypeException;
import finio.core.errors.MoveOverwriteException;
import finio.core.events.KeyAddedEvent;
import finio.core.events.KeyAddedListener;
import finio.core.events.KeyChangedEvent;
import finio.core.events.KeyChangedListener;
import finio.core.events.KeyRemovedEvent;
import finio.core.events.KeyRemovedListener;
import finio.core.events.MapBatchChangedEvent;
import finio.core.events.MapBatchChangedListener;
import finio.core.events.MapChangedEvent;
import finio.core.events.MapChangedListener;
import finio.core.events.MapClearedEvent;
import finio.core.events.MapClearedListener;
import finio.core.events.MapEvent;
import finio.core.events.ValueChangedEvent;
import finio.core.events.ValueChangedListener;
import finio.core.syntax.FMapSyntax;
import finio.core.syntax.FMapSyntaxLibrary;
import finio.renderers.map.FMapRenderer;
import finio.renderers.map.StandardAMapRenderer;
import replete.text.StringUtil;
import replete.util.ReflectionUtil;

// TODO: Ability for per-value get overrides on an AMap
// TODO: KeyPath-based change events using a KeySpec.
// THOUGHT: A KeyPath is just a KeySpec identifying a single value.
// NOTE: Current path is usually an application concept and not
// stored at this level, within a given map.
// TODO: Allow string/number conversion to happen under the hood, so if a new Integer(3)
// is a KEY in the map, then "3" is an alias and get("3") returns the same as get(3).
// NOTE: Right now, we're just going to make sure ALL keys are strings at the moment.
// NOTE:
// A BIG concern - mutability of the keys in a map on certain copy/subset/slice
// operations.  You don't want copies/transformations of a map to use the same
// object references for the keys, should the source or dest change the CONTENTS
// of a MUTABLE key, it would affect the copy/source/dest as well.  Think about this.
// How could a map in Java possibly know when the contents of one of its keys
// changes (and thus the return values of either the hashCode or equals methods)?
// It can't. KEYS MUST BE IMMUTABLE!
// TODO: Someday look at functional languages, they might provide the "AMap" of verbs
// http://www.joelonsoftware.com/items/2006/08/01.html
// http://www.databasedesign-resource.com/null-values-in-a-database.html
// TODO: You can apply a "filter" query to any map in the view., it shows as "filtered".
// TODO: You can designate that certain parts of the world map are "controlled" by a given
// agent (piece of software in this case).  That piece of code is responsible
// for the managing of that map and submaps.  However, delegation to sub-agents is possible.
// TODO: Think about whether or not a "read only" AMap makes sense.

public class FMap extends /*REMOVE-LINKED*/ LinkedHashMap<Object, Object> implements
        DefaultNonTerminal, NonTerminalCreator, NonTerminalProvider {

    // Ideas
    // =====
    // private List<AMap> referenceMe;  myParents; // i.e. incoming edges
    private FMap vov;
    private void ensure(String field) {
        if(ReflectionUtil.hasField(this, field)) {
            if(ReflectionUtil.get(this, field) == null) {
                ReflectionUtil.set(this, field, new FMap());
            }
        }
    }
    public void addValueOverrider(Object K /*key criteria / range - KeySpec */, ValueOverrider vo) {
        ensure("vov");  // Because we don't want every AMap to have this instance unnecessarily
        vov.put(K, vo);
    }

    private static int constructedCount = 0;
    private static List<FMap> maps = new ArrayList<>();


    ////////////
    // FIELDS //
    ////////////

    // Constants

    private static final boolean AUTO_SYS_META = false;

    // Other

    private boolean suppressAllEvents = false;
    private String activeOrderingManager;
    private Map<String, OrderingManager> orderingManagers;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    // Such varying parameters that all need to be present before
    // construction incentivizes a builder pattern some day.
    public FMap() {
        super();
        init();
    }
    public FMap(boolean suppressAutoSysMeta) {
        super();
        init(suppressAutoSysMeta);
    }
    public FMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
        init();
    }
    public FMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        init();
    }
    public FMap(int initialCapacity) {
        super(initialCapacity);
        init();
    }
    public FMap(Map<? extends Object, ? extends Object> m) {
        super(m);   // Note: Not a recursive copy
        init();
    }
//    public AMap(AMap M) {
        // TODO: recursive copy constructor
//    }

    // Custom

    public FMap(Object... args) {
        for(int a = 0; a < args.length; a += 2) {
           if(a + 1 == args.length) {
               put(args[a], null);
           } else {
               put(args[a], args[a + 1]);
           }
        }
        init();
    }
    public FMap(String K, Map<? extends Object, ? extends Object> m) {
        FMap M = A(m);
        put(K, M);
        init();
    }

    private void init() {
        init(false);
    }
    private void init(boolean suppressAutoSysMeta) {
        constructedCount++;   // Debug
        maps.add(this);
        if(AUTO_SYS_META && !suppressAutoSysMeta) {
            putSysMeta("NT-Type", "AMap");
        }
        initAllOrderings();
    }
    public static int getConstructedCount() {    // Debug
        return constructedCount;
    }
    public static void analyzeMaps() {   // Debug
        int total = 0;
        for(FMap map : maps) {
            Object table = ReflectionUtil.get(map, "table");
            total += Array.getLength(table);
        }
        System.out.println((double) total / maps.size());
    }

    public int hashTableSize() {
        Object table = ReflectionUtil.get(this, "table");
        return Array.getLength(table);
    }


    //////////////////////
    // STATIC SHORTHAND //
    //////////////////////

    // Super

    public static FMap A() {
        return new FMap();
    }
    public static FMap A(boolean suppressAutoSysMeta) {
        return new FMap(suppressAutoSysMeta);
    }
    public static FMap A(int initialCapacity, float loadFactor, boolean accessOrder) {
        return new FMap(initialCapacity, loadFactor, accessOrder);
    }
    public static FMap A(int initialCapacity, float loadFactor) {
        return new FMap(initialCapacity, loadFactor);
    }
    public static FMap A(int initialCapacity) {
        return new FMap(initialCapacity);
    }
    public static FMap A(Map<? extends Object, ? extends Object> m) {
        if(m == null) {
            return null;  // Seems reasonable...
        }
        return new FMap(m);
    }

    // Custom

    public static FMap A(Object... args) {
        return new FMap(args);
    }
    public static FMap A(String K, Map<? extends Object, ? extends Object> m) {
        return new FMap(K, m);
    }


    /////////
    // MAP //
    /////////

    // public boolean isEmpty();
    // public int size();
    @Override
    public int sizeNoSysMeta() {
        int sub = containsKey(FConst.SYS_META_KEY) ? 1 : 0;
        return size() - sub;
    }
    @Override
    public int Z() {
        return super.size();
    }
    // [OPTION] include . && .. in return set (implies that incoming references are being recorded and that this map can only have at most one incoming reference)
    @Override
    public Set<Object> getKeys() {
//        if(sorterName == null) {
            return super.keySet();
//        }
//        Set set = getSorter(sorterName);
//        if(set == null) {
//            return super.keySet();
//        }
//        return new SortedKeySet(this, set);
    }
    @Override
    public Collection<Object> getValues() {
//        if(sorterName == null) {
            return super.values();
//        }
//        Set set = getSorter(sorterName);
//        if(set == null) {
//            return super.values();
//        }
//        return new SortedValuesCollection(this, set);
    }
    @Override
    public Set<Object> K() {
        return getKeys();
    }
    @Override
    public Collection<Object> V() {
        return getValues();
    }
    @Override
    public Set<Object> keySet() {
        return getKeys();
    }
    @Override
    public Collection<Object> values() {
        return getValues();
    }
    @Override
    public void clear() {
        super.clear();
        clearAllOrderings();
        fireMapClearedNotifier();
    }
    // [NT Core: 9/11 methods]


    /////////
    // GET //
    /////////

    /// LOCAL GET SINK ///
    @Override
    public Object get(Object K) {
        // [OPTION] Throw exception if !has(K).
        K = forceString(K);//TEMPORARY?
        Object V = super.get(K);
        V = valueOverride(K, V);
        return V;
    }
    @Override
    public Object getByKey(Object K) {
        return get(K);
    }
    @Override
    public Object getByIndex(int I) {
        // [OPTION] Cache keyset & values arrays for on change for optimization
        Object K = getKeyByIndex(I);
        return get(K);
    }
    @Override
    public Object getKeyByIndex(int I) {
        // [OPTION] Cache keyset & values arrays for on change for optimization
        Object[] Ks = super.keySet().toArray();
        return Ks[I];
    }
    // Need separate name so that get(null) still goes to get(Object) and not get(KeyPath)
    // Also, you might very justifiably want to use key paths as keys in a map!
    @Override
    public Object getByPath(KeyPath P) {
        KeyPath Pcopy = KeyPath.KP(P);       // So as not to change input parameter P
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
        //     throw new AMapException("Nope!");
        //     return null; (actually just go to next blocks).
        // }
        if(P.isEmpty()) {
            return get(S);       // Return value Could be terminal or map.
        }
        Object Vx = get(S);
        if(!(Vx instanceof NonTerminal)) {
            // [OPTION] Return null
            throw new FMapException("Cannot get a value at path due to terminal value in path.");
        }
        return ((NonTerminal) Vx).getByPathNoCopy(P);
    }
    // Returns null if doesn't exist, or value is not cast-able to given type,
    // value otherwise.
    @Override
    public Object getValid(Object K, Class type) {
        Object V = get(K);
        if(V == null || type.isAssignableFrom(V.getClass())) {
            return V;
        }
        return null;
    }
    @Override
    public Object getAndSet(Object K, Object Vdefault) {
        Object V = null;
        try {
            if(!has(K)) {
                return V = Vdefault;
            }
            return V = get(K);
        } finally {
            put(K, V);
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
    // [NT Get: 9 methods]


    /////////
    // PUT //
    /////////

    // TODO: What if value you set is same as what was there before?
    /// LOCAL PUT SINK ///
    // Alternate name: set
    @Override
    public Object put(Object K, Object V) {
        K = forceString(K);//TEMPORARY?

        // Key exists, changing value.
        if(has(K)) {
            Object Vprev = super.put(K, V);
            fireValueChangedNotifier(K, Vprev, V);
            updateAllOrderingsOnChange(K, V);
            return Vprev;
        }

        // Key does not exist, adding key-value pair.
        Object Vprev = super.put(K, V);
        fireKeyAddedNotifier(K, V);
        updateAllOrderingsOnAdd(K, V);
        return Vprev;              // Will be null
    }
    @Override
    public Object putByKey(Object K, Object V) {
        return put(K, V);
    }
    @Override
    public Object putByIndex(int I, Object V) {
        Object K = getKeyByIndex(I);
        return put(K, V);
    }
    // Need separate name so that put(null, V) still goes to put(Object, V) and not put(KeyPath, V)
    // Also, you might very justifiably want to use key paths as keys in a map!
    @Override
    public Object putByPath(KeyPath P, Object V) {
        KeyPath Pcopy = KeyPath.KP(P);       // So as not to change input parameter P
        return putByPathNoCopy(Pcopy, V);
    }
    @Override
    public Object putByPathNoCopy(KeyPath P, Object V) {
        if(KeyPath.isEmpty(P)) {
            throw new FMapException("Cannot put a value at an empty path.");
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
            throw new FMapException("Cannot put a value at path due to terminal value in path.");
        }
        return ((NonTerminal) Vx).putByPathNoCopy(P, V);
    }
    // public void putAll(Map<? extends Object, ? extends Object> m);
    @Override
    public void putAll(NonTerminal M) {
        for(Object K : M.K()) {
            put(K, M.get(K));
        }
    }
    // [NT Put: 6/7 methods]


    ////////////
    // REMOVE //
    ////////////

    @Override
    public Object removeByKey(Object K) {
        return remove(K);
    }
    @Override
    public boolean removeValue(Object V) {
        Object Kfound = null;
        for(Object K : K()) {
            if(get(K).equals(V)) {   // Could be affected by value overriders... !
                Kfound = K;
                break;
            }
        }
        if(Kfound != null) {
            remove(Kfound);
            return true;
        }
        return false;
    }
    @Override
    public Object removeByPath(KeyPath P) {
        KeyPath Pcopy = KeyPath.KP(P);       // So as not to change input parameter P
        return removeByPathNoCopy(Pcopy);
    }
    @Override
    public Object removeByPathNoCopy(KeyPath P) {
        if(KeyPath.isEmpty(P)) {
            throw new FMapException("Cannot remove a value at an empty path.");
        }
        Object S = P.removeFirst();
        if(P.isEmpty()) {
            return removeByKey(S);
        }
        Object Vx = get(S);
        if(!(Vx instanceof NonTerminal)) {
            // [OPTION] Return null
            throw new FMapException("Cannot remove a value at path due to terminal value in path.");
        }
        return ((NonTerminal) Vx).removeByPathNoCopy(P);
    }
    // [NT Remove: 4 methods]


    //////////////
    // CONTAINS //
    //////////////

    public boolean has(Object K) {
        return /*super.*/containsKey(K);
    }
    @Override
    public boolean hasKey(Object K) {
        return has(K);
    }
    @Override
    public boolean hasValue(Object V) {
        return super.containsValue(V);
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
            throw new FMapException("Cannot determine existence of path due to terminal value in path.");
        }
        return ((NonTerminal) Vx).hasPathNoCopy(P);
    }
    /// LOCAL CONTAINS SINK ///
    @Override
    public boolean containsKey(Object K) { // Temporary method until something done about key types
        K = forceString(K);//TEMPORARY?
        return super.containsKey(K);
    }
    // public boolean containsValue(Object V);
    // Could potentially implement index & lastIndexOf for K & V
    // [NT Put: 6/7 methods]


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

    protected transient List<MapChangedListener> mapChangedListeners = new ArrayList<>();
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
        if(suppressAllEvents) {
            return;
        }
        if(mapChangedListeners.size() != 0) {
            MapChangedEvent event = new MapChangedEvent(this, cause);
            for(MapChangedListener listener : mapChangedListeners) {
                listener.mapChanged(event);
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
        if(suppressAllEvents) {
            return;
        }
        if(mapBatchChangedListeners.size() != 0) {
            MapBatchChangedEvent event = new MapBatchChangedEvent(this);
            for(MapBatchChangedListener listener : mapBatchChangedListeners) {
                listener.mapBatchChanged(event);
            }
        }
    }

public static int mcAdded = 0;
public static int mcRemoved = 0;
    protected transient List<MapClearedListener> mapClearedListeners = new ArrayList<>();
    @Override
    public void addMapClearedListener(MapClearedListener listener) {
        mapClearedListeners.add(listener);
        synchronized(FMap.class) {
            mcAdded++;
        }
    }
    @Override
    public void removeMapClearedListener(MapClearedListener listener) {
        mapClearedListeners.remove(listener);
        synchronized(FMap.class) {
            mcRemoved++;
        }
    }
    @Override
    public MapClearedListener[] getMapClearedListeners() {
        return mapClearedListeners.toArray(new MapClearedListener[0]);
    }
    private void fireMapClearedNotifier() {
        if(suppressAllEvents) {
            return;
        }
        MapClearedEvent event = new MapClearedEvent(this);
        for(MapClearedListener listener : mapClearedListeners) {
            listener.mapCleared(event);
        }
        fireMapChangedNotifier(event);
    }

    protected transient List<KeyAddedListener> keyAddedListeners = new ArrayList<>();
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
        if(suppressAllEvents) {
            return;
        }
        KeyAddedEvent event = new KeyAddedEvent(this, K, V);
        for(KeyAddedListener listener : keyAddedListeners) {
            listener.keyAdded(event);
        }
        fireMapChangedNotifier(event);
    }

    protected transient List<KeyRemovedListener> keyRemovedListeners = new ArrayList<>();
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
        if(suppressAllEvents) {
            return;
        }
        KeyRemovedEvent event = new KeyRemovedEvent(this, K, V);
        for(KeyRemovedListener listener : keyRemovedListeners) {
            listener.keyRemoved(event);
        }
        fireMapChangedNotifier(event);
    }

    protected transient List<KeyChangedListener> keyChangedListeners = new ArrayList<>();
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
        if(suppressAllEvents) {
            return;
        }
        KeyChangedEvent event = new KeyChangedEvent(this, K, Kdst);
        for(KeyChangedListener listener : keyChangedListeners) {
            listener.keyChanged(event);
        }
        fireMapChangedNotifier(event);
    }

    protected transient List<ValueChangedListener> valueChangedListeners = new ArrayList<>();
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
    private void fireValueChangedNotifier(Object K, Object Vprev, Object V) {  // Could inline this.
        if(suppressAllEvents) {
            return;
        }
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
        fireMapBatchChangedNotifier();
    }
    // 3 methods


    /////////////////////////////
    // CHANGE ALLOWED/PROBABLE //
    /////////////////////////////

    @Override
    public boolean canAdd(Object K, Object V) {
        return true;
    }
    @Override
    public boolean canChangeValue(Object K, Object V) {
        return true;
    }
    @Override
    public boolean canSetValue(Object K, Object V) {
        return canAdd(K, V) || canChangeValue(K, V);
    }
    @Override
    public boolean canChangeKey(Object K1, Object K2) {
        return true;
    }
    @Override
    public boolean canRemove(Object K) {
        return true;
    }
    @Override
    public boolean canClearMap() {
        return true;
    }
    @Override
    public boolean wouldAdd(Object K, Object V) {
        return canAdd(K, V) && !has(K);
    }
    @Override
    public boolean wouldChangeValue(Object K, Object V) {
        return canChangeValue(K, V) && has(K);
    }
    @Override
    public boolean wouldSetValue(Object K, Object V) {
        return wouldAdd(K, V) || wouldChangeValue(K, V);
    }
    @Override
    public boolean wouldChangeKey(Object K1, Object K2) {
        return canChangeKey(K1, K2) && has(K1) && !has(K2);
    }
    @Override
    public boolean wouldRemove(Object K) {
        return canRemove(K) && has(K);
    }
    @Override
    public boolean wouldClearMap() {
        return canClearMap();
    }
    // 12 methods


    //////////
    // MISC //
    //////////

    public NonTerminal getSysMeta() {
        NonTerminal sysMeta = (NonTerminal) getAndSetValid(
            FConst.SYS_META_KEY, A(true), NonTerminal.class);
        return sysMeta;
    }
    @Override
    public Object getSysMeta(Object K) {
        Object O = get(FConst.SYS_META_KEY);
        if(O instanceof NonTerminal) {
            NonTerminal sysMeta = (NonTerminal) O;
            return sysMeta.get(K);
        }
        return null;
    }
    public Object putSysMeta(Object K, Object V) {
        return getSysMeta().put(K, V);
    }
    @Override
    public void compress() {
        // Do nothing, already compressed as far as possible.
    }
    // public Set<Entry<Object, Object>> entrySet();
    @Override
    public Object getNextAvailableKey() {
        return getNextAvailableKeyInternal(null);
    }
    @Override
    public Object getNextAvailableKey(String prefix) {
        return getNextAvailableKeyInternal(prefix);
    }
    @Override
    public void changeKey(Object Ksrc, Object Kdst) {
        if(!hasKey(Ksrc)) {         // [OPTION] Don't throw exception, just do nothing
            throw new FMapException("Cannot change key because source key does not exist.");
        }
        if(hasKey(Kdst)) {          // [OPTION] Don't throw exception, just blow away existing value like put
            throw new FMapException("Cannot change key because destination key already exists.");
        }
        suppressAllEvents = true;
        Object V = get(Ksrc);
        put(Kdst, V);               // TODO: preserve the order of these keys!  Kdst placed at "end"
        removeByKey(Ksrc);
        suppressAllEvents = false;
        fireKeyChangedNotifier(Ksrc, Kdst);
    }
    @Override
    public int sizeAll() {
        int Zall = Z();     // Does not count itself.  Client applications must do this if they desire.
        for(Object K : K()) {
            Object V = get(K);
            if(V instanceof NonTerminal) {
                NonTerminal M = (NonTerminal) V;
                Zall += M.sizeAll();
            }
        }
        return Zall;
    }
    @Override
    public void createAlternatesMap() {
        if(has(FConst.SYS_ALT_KEY)) {
            return;
        }
        FMap Malt0 = A();
        Object[] Karr = K().toArray();
        for(int k = 0; k < Karr.length; k++) {
            Object K = Karr[k];
            if(!FUtil.isSysMetaKey(K)) {
                Object V = removeByKey(K);
                Malt0.put(K, V);
            }
        }
        FMap Malt = A();
        if(!Malt0.isEmpty()) {
            Object K = 0;            // Default key is 0.
            Malt.put(K, Malt0);
        }
        put(FConst.SYS_ALT_KEY, Malt);
    }
    @Override
    public void createAlternatesMap(Object K) {
        if(K.equals(FConst.SYS_ALT_KEY)) {
            if(FUtil.isNonTerminal(get(K))) {
                return;
            }
            Object V = get(K);
            FMap Malt = A();
            put(K, Malt);
            Malt.put(0, V);
            return;
        }
        Object V = removeByKey(K);
        FMap Malt = A();
        Malt.put(0, V);
        FMap Mnew = A();
        Mnew.put(FConst.SYS_ALT_KEY, Malt);
        put(K, Mnew);
    }
    public void describe(Object K) {
        if(containsKey(K)) {
            Object V = get(K);
            FMap M = A();
            M.put(FConst.SYS_VALUE_KEY, V);
            put(K, M);
        }
    }
    @Override
    public void promote(Object K) {
        if(containsKey(K)) {
            Object V = get(K);
            if(FUtil.isNonTerminal(V)) {
                NonTerminal M = (NonTerminal) V;
                if(M.containsKey(FConst.SYS_VALUE_KEY)) {
                    Object V2 = M.get(FConst.SYS_VALUE_KEY);
                    put(K, V2);
                }
            }
        }
    }
    @Override
    public boolean isRefreshable() {
        return false;
    }
    @Override
    public void refresh() {
        throw new UnsupportedOperationException();
    }
    // 14 methods


    ///////////////
    // RENDERING //
    ///////////////

    public String toStringObject() {
        return FUtil.toStringBaseIdent(this);
    }
    @Override
    public String toString() {
        return toString(40);
    }
    @Override
    public String toString(int maxChars) {
        StringBuilder sb = new StringBuilder();
        toStringAppend(sb, maxChars);
        return sb.toString();
    }
    @Override
    public void toStringAppend(StringBuilder sb, int maxChars) {
        Iterator i = entrySet().iterator();
        if(! i.hasNext()) {
            sb.append("{}");
            return;
        }
        sb.append('{');
        for (;;) {

            if(sb.length() < maxChars) {

                Entry e = (Entry) i.next();
                Object key = e.getKey();
                Object value = e.getValue();

                if(key == this) {
                    sb.append("(this Map)");
                } else {
                    if(key instanceof FMap) {
                        ((FMap) key).toStringAppend(sb, maxChars);
                    } else {
                        sb.append(key);
                    }
                }

                sb.append('=');

                if(value == this) {
                    sb.append("(this Map)");
                } else {
                    if(value instanceof FMap) {
                        ((FMap) value).toStringAppend(sb, maxChars);
                    } else {
                        sb.append(value);
                    }
                }

            } else {
                sb.append("<TRUNCATED>}").toString();
                return;
            }

            if(!i.hasNext()) {
                sb.append('}').toString();
                return;
            }
            sb.append(',').append(' ');
        }
    }
    // 4 methods


    ///////////////
    // TRANSLATE //
    ///////////////

    // Would have been nice to have just implemented
    // Map via ANonTerminal...
    @Override
    public Map<Object, Object> asJavaMap() {
        return this;
    }
    // 1 method


    /////////////
    // SORTING //
    /////////////

    public void setOrdering(String name) {
        if(orderingManagers.containsKey(name)) {
            activeOrderingManager = name;
            // return true;
        }
        // return false;
    }
    public void addOrdering(String name, OrderingManager orderingManager) {
        orderingManagers.put(name, orderingManager);
    }
    public OrderingManager getOrdering(String name) {
        // if !contains ?
        return orderingManagers.get(name);
    }
    public boolean removeOrdering(String name) {
        if(orderingManagers.containsKey(name)) {
            orderingManagers.remove(name);
            return true;
        }
        return false;
    }
    // 4 methods

    private void initAllOrderings() {
//        check();
//        orderingManagers.forEach((k, om) -> om.initialize(this));
    }
    private void updateAllOrderingsOnAdd(Object K, Object V) {
//        check();
//        orderingManagers.forEach((k, om) -> om.ntKeyAdded(K, V));
    }
    private void updateAllOrderingsOnChange(Object K, Object V) {
//        check();
//        orderingManagers.forEach((k, om) -> om.ntValueChanged(K, V));
    }
    private void clearAllOrderings() {
//        check();
//        orderingManagers.forEach((k, om) -> om.ntCleared());
    }
    private void removeFromAllOrderings(Object K) {
//        check();
//        orderingManagers.forEach((k, om) -> om.ntKeyRemoved(K));
    }
    private void check() {
        if(orderingManagers == null) {
            activeOrderingManager = FConst.SYS_INHERENT_ORDERING;
            orderingManagers = new HashMap<>();
            orderingManagers.put(activeOrderingManager, new ArrayListInsertionOrderOrderingManager());
            initAllOrderings();
        }
    }


    ///////////////////////////
    // MOVE / COPY / OVERLAY //
    ///////////////////////////

    // TODO: Update working scope if it was deleted.
    public void move(KeyPath[] Psources, KeyPath Pdest, int position, boolean preventOverwrite) throws FMapException {
        if(Psources == null || Psources.length == 0) {
            throw new KeyPathException("Invalid source key paths");
        }
        if(Pdest == null) {
            throw new KeyPathException("Invalid destination key path");
        }

        // move path1 past2
        if(Psources.length == 1) {
            NonTerminal Mdest = null;
            Object Kdest = null;

            // move path1 path2/exists/here
            if(hasPath(Pdest)) {
                Object Vdest = getByPath(Pdest);

                // move path1 path2/exists/dir
                if(Vdest instanceof NonTerminal) {
                    Mdest = (NonTerminal) Vdest;
                    ensureIsNotRoot(Psources[0], "Cannot move the root map", Type.SOURCE);
                    Kdest = Psources[0].last();
                }
            }

            // move path1 path2/does/not/exist
            // move path1 path2/exists/but/file
            if(Mdest == null) {
                KeyPath PdestCopy = new KeyPath(Pdest);  // So as not to modify input key path objects
                Kdest = PdestCopy.removeLast();
                ensureKeyPathExists(PdestCopy, Type.DESTINATION);  // [OPTION] not needed if -r option used
                Object Vdest = getByPath(PdestCopy);
                Mdest = ensureIsNonTerminal(Vdest,
                    "Destination path must refer to a non-terminal value.",
                    Type.DESTINATION);
            }

            FMapCompositeException compEx = new FMapCompositeException(
                "An error occurred with the move operation.");

            try {

                // Check the source key path
                KeyPath Psource = Psources[0];
                ensureIsNotRoot(Psource, "Cannot move the root map", Type.SOURCE);
                ensureKeyPathExists(Psource, Type.SOURCE);
                ensureKeyPathHierarchy(Psource, Pdest);

                // Copy and find parent map
                KeyPath PsourceCopy = new KeyPath(Psource);           // So as not to modify input key path objects
                Object KsourceLast = PsourceCopy.removeLast();
                NonTerminal MsourceParent = (NonTerminal) getByPath(PsourceCopy);

                // Perform overwrite check
                if(Mdest.has(Kdest) && preventOverwrite) {
                    throw new MoveOverwriteException(
                        "This would overwrite the key '" + Kdest + "' in the destination map.",
                        Kdest);
                }

                // Remove from source parent map and place into the target map
                Object Vremoved = MsourceParent.removeByKey(KsourceLast);
                Mdest.put(Kdest, Vremoved);

            } catch(Exception e) {
                compEx.addException(e);
            }

            if(compEx.getExceptionCount() != 0) {
                throw compEx;
            }

        } else {
            ensureKeyPathExists(Pdest, Type.DESTINATION);

            // Get and check destination value
            Object Vdest = getByPath(Pdest);
            NonTerminal Mdest = ensureIsNonTerminal(Vdest,
                "Destination path must refer to a non-terminal value.",
                Type.DESTINATION);

            FMapCompositeException compEx = new FMapCompositeException(
                "An error occurred with the move operation.");

            for(KeyPath Psource : Psources) {
                try {

                    // Check the source key path
                    ensureIsNotRoot(Psource, "Cannot move the root map", Type.SOURCE);
                    ensureKeyPathExists(Psource, Type.SOURCE);
                    ensureKeyPathHierarchy(Psource, Pdest);

                    // Copy and find parent map
                    KeyPath PsourceCopy = new KeyPath(Psource);  // So as not to modify input key path objects
                    Object KsourceLast = PsourceCopy.removeLast();
                    NonTerminal MsourceParent = (NonTerminal) getByPath(PsourceCopy);

                    // Perform overwrite check
                    if(Mdest.has(KsourceLast) && preventOverwrite) {
                        throw new MoveOverwriteException(
                            "This would overwrite the key '" + KsourceLast + "' in the destination map.",
                            KsourceLast);
                    }

                    // Remove from source parent map and place into the target map
                    Object Vremoved = MsourceParent.removeByKey(KsourceLast);
                    Mdest.put(KsourceLast, Vremoved);

                } catch(Exception e) {
                    compEx.addException(e);
                }
            }

            if(compEx.getExceptionCount() != 0) {
                throw compEx;
            }
        }
    }
    public void copy(KeyPath[] psources, KeyPath pdest, int position) {
        throw new UnsupportedOperationException("Copy operation not yet supported.");
    }
    @Override
    public NonTerminal overlayByKeyPath(KeyPath P) {
        NonTerminal M = this;
        for(Object S : P) {
            NonTerminal Mchild;
            if(M.has(S)) {
                Object V = M.get(S);
                if(!FUtil.isNonTerminal(V)) {
                    throw new FMapException("Cannot overlay key path due to terminal value in path.");
                }
                Mchild = (NonTerminal) V;
            } else {
                M.put(S, Mchild = new FMap());
            }
            M = Mchild;
        }
        return M;
    }
    // 3 methods


    ///////////////////
    // AMap SPECIFIC //
    ///////////////////

    @Override
    public Object remove(Object K) {
        if(has(K)) {
            Object V = super.remove(K);
            removeFromAllOrderings(K);
            fireKeyRemovedNotifier(K, V);
            if(size() == 0) {
                fireMapClearedNotifier();  // For consistency
            }
            return V;
        }
        return super.remove(K);      // Shouldn't do anything (a no-op).
    }
    private String getNextAvailableKeyInternal(String prefix) {
        if(prefix == null) {
            prefix = "key";
        }
        return StringUtil.getNextNumberedString(keySet(), prefix, false);
    }

    // Type-Specific

    public NonTerminal getNT(Object key) {
        return (NonTerminal) get(key);
    }
    public FMap getM(Object key) {
        return (FMap) get(key);
    }
    public FList getL(Object key) {
        return (FList) get(key);
    }
    public FMap getM(Object key, boolean emptyIfNull) {
        FMap M = (FMap) get(key);
        if(M == null && emptyIfNull) {
            M = A();
        }
        return M;
    }

    private Object valueOverride(Object K, Object V) {
        if(vov != null &&
                (vov.containsKey(null) /*real limited "criteria" to specify all keys */ ||
                 vov.containsKey(K))) {
            ValueOverrider vo = vov.containsKey(null) ?
                (ValueOverrider) vov.get(null) :
                (ValueOverrider) vov.get(K);
            V = vo.override(K, V);
        }
        return V;
    }

    // Wonder if there are any real world uses of this
    // operation, or if it is just trivial API shorthand?
    public void putEach(Object V) {
        for(Object K : keySet()) {
            put(K, V);
        }
    }


    ////////////
    // ENSURE //
    ////////////

    // Are these ANonTerminal material?  Not sure yet...

    // Duplicate in Command

    protected void ensureKeyPathExists(KeyPath P, Type type) throws KeyPathExistenceException {
        if(!hasPath(P)) {
            throw new KeyPathExistenceException("Key path does not exist: " + P, type);
        }
    }
    protected NonTerminal ensureIsNonTerminal(Object V, Type type) throws KeyPathValueTypeException {
        return ensureIsNonTerminal(V, "Value is not a non-terminal", type);
    }
    protected NonTerminal ensureIsNonTerminal(Object V, String message, Type type) throws KeyPathValueTypeException {
        if(!(V instanceof NonTerminal)) {
            throw new KeyPathValueTypeException(message, type);
        }
        return (NonTerminal) V;
    }
//    protected void ensureIsNotRoot(KeyPath P, Type type) throws KeyPathException {
//        ensureIsNotRoot(P, "Key path cannot refer to the root", type);
//    }
    protected void ensureIsNotRoot(KeyPath P, String message, Type type) throws KeyPathException {
        if(P.isEmpty()) {
            throw new KeyPathException(message, type);
        }
    }
    private void ensureKeyPathHierarchy(KeyPath Psource, KeyPath Pdest) {
        if(Psource.isAncestor(Pdest)) {
            throw new KeyPathHierarchyException("Source key path cannot be an ancestor of the destination key path");
        }
    }


    /////////////////
    // MOVE & COPY //
    /////////////////

    public FMap copy() {
        return copy(null);
    }
    public FMap copy(FMap copyParams) {  // What are the copy params?
        FMap Mdest = A();
        Mdest.overlayWith(this);
        return Mdest;
    }


    ////////////////
    // FLATTENING //
    ////////////////

    public NonTerminal flatten() {
        Stack<Object> path = new Stack<>();
        FMap paths = A();
        flatten(this, path, paths);
        return paths;
    }

    private static void flatten(NonTerminal M, Stack<Object> path, FMap kvs) {
        // would be nice to have this syntax:
        //  for(this) { $k, $v now available }
        for(Object K : M.keySet()) {
            Object V = M.get(K);
            if(FUtil.isNonTerminal(V) && !FUtil.isSemiTerminal(V) && !FUtil.isSysMetaKey(K)) {
                path.push(K);
                flatten((NonTerminal) V, path, kvs);
                path.pop();
            } else {
                KeyPath newPath = KP();
                for(Object segment : path) {
                    newPath.add(segment);
                }
                newPath.add(K);
                kvs.put(newPath, V);
            }
        }
    }


    /////////////
    // OVERLAY //
    /////////////

    // TODO: Are there other param objects like 'ignoreNewKeys'?
    // copy params seemed to think there'd be more.  And now
    // merge is going to have one as well - MergeStrat.

    public void overlayOnto(FMap M) {
        overlayOnto(M, false);
    }
    public void overlayOnto(FMap M, boolean ignoreNewKeys) {
        M.overlayWith(this, ignoreNewKeys);
    }
    public void overlayWith(FMap M) {
        overlayWith(M, false);
    }
    // Note this has similarities with putByPath, but it's doing it in bulk.
    public void overlayWith(FMap Msource, boolean ignoreNewKeys) {
        FMap Mdest = this;

        for(Object K : Msource.K()) {

            // If destination map does not contain the source
            // map's key, and we are ignoring new keys, then
            // just skip this key.
            if(!Mdest.has(K) && ignoreNewKeys) {
                continue;
            }

            // Get the source map's value for this key.
            Object Vsource = Msource.get(K);
            Object Vreplace;

            // If the value is a simple terminal, then we will just
            // want to copy that object reference over to the
            // destination map without any further considerations.
            // These are all considered immutable as far as the AMap
            // is concerned.
            if(FUtil.isTerminal(Vsource)) {
                Vreplace = Vsource;

            // Else if the value is a non-terminal, we need to not
            // just copy the object reference for the non-terminal,
            // but rather recursively construct any necessary
            // descendant non-terminals.
            } else {
                FMap MVsource = (FMap) Vsource;     // Just handles maps for now. TODO
                Object Vdest = Mdest.get(K);

                // If the destination map's value for this key is
                // already a non-terminal, then reuse that object.
                if(FUtil.isNonTerminal(Vdest) /* && !obliterateChildMaps */) {
                    FMap Mexisting = (FMap) Vdest;
                    Mexisting.overlayWith(MVsource, ignoreNewKeys);
                    Vreplace = Mexisting;

                // Otherwise, create a brand new non-terminal object,
                // fill it, and set that into the destination map.
                } else {
                    FMap Mnew = A();
                    Mnew.overlayWith(MVsource);
                    Vreplace = Mnew;
                }
            }

            // Put the chosen value into the destination map.
            Mdest.put(K, Vreplace);
        }
    }

    // Convenience

    public FMap overlaidOnto(FMap M) {
        return overlaidOnto(M, false);   // No changes to this object.
    }
    public FMap overlaidOnto(FMap M, boolean ignoreNewKeys) {
        overlayOnto(M, ignoreNewKeys);
        return this;                     // No changes to this object.
    }
    public FMap overlaidWith(FMap M) {
        return overlaidWith(M, false);
    }
    public FMap overlaidWith(FMap M, boolean ignoreNewKeys) {
        overlayWith(M, ignoreNewKeys);
        return this;
    }

    // Static

    public static FMap overlayOnto(FMap Mtop, FMap Mbot) {
        return overlayOnto(Mtop, Mbot, false);
    }
    public static FMap overlayOnto(FMap Mtop, FMap Mbot, boolean ignoreNewKeys) {
        return overlayWith(Mbot, Mtop, ignoreNewKeys);  // Flip parameters
    }
    public static FMap overlayWith(FMap Mbot, FMap Mtop) {
        return overlayWith(Mbot, Mtop, false);
    }
    public static FMap overlayWith(FMap Mbot, FMap Mtop, boolean ignoreNewKeys) {
        FMap Mcopy = Mbot.copy();                       // Also uses overlayWith
        Mcopy.overlayWith(Mtop, ignoreNewKeys);
        return Mcopy;
    }


    ///////////
    // MERGE //
    ///////////

    public static void merge(FMap Mleft, FMap Mright, MergeStrategy strat) {
        // TODO ? Some variant of overlay?
    }

    public interface MergeStrategy {
        Object resolveConflict(KeyPath Pleft, KeyPath Pright);
    }


    ////////////
    // SUBSET //
    ////////////

    public FMap subset(KeySpec Q) {
        // {A => 1, B => 2, C =>3, D => {X => 7, Y => 8}}
        // Q is <A | D.Y>
        // Returns {A => 1, D => {Y => 8}}
        // {"sim.type.what" => 1, "sim.type.why" => 2, "model.beta" => 3}
        // Q is <sim.*>
        // Returns {"sim.type.what" => 1, "sim.type.why" => 2}
        // {["sim", "type", "what"] => 1, ["sim", "type", "why"] => 2, ["model", "beta"] => 3}
        // Q is ?
        return null;
    }
    // A VERY limited subset op as an example, also do we need separate instances for submaps?
    public FMap subset(Object Stop) {
        FMap subset = new FMap();
        for(Object K : K()) {
            if(K instanceof Collection<?>) {
                Collection<?> Kpath = (Collection<?>) K;
                for(Object S : Kpath) {
                    if(S.equals(Stop)) {
                        subset.put(K/*actually this should prolly be a copy of K*/, get(K));
                    }
                    break;
                }
            }
        }
        return subset;
    }


    //////////
    // MISC //
    //////////

    public Object putSysValue(Object V) {
        return put(FConst.SYS_VALUE_KEY, V);
    }
    public Object getSysValue() {
        if(containsKey(FConst.SYS_VALUE_KEY)) {
            return get(FConst.SYS_VALUE_KEY);
        }
        return null;  // [OPTION] throw exception
    }
//    private void search(RTreeNode nParent, List<RTreeNode> found, String text) {
//        if(nParent.getUserObject() != null && nParent != getRoot()) {
//            if(nParent.getObject().toString().toUpperCase().contains(text)) {
//                found.add(nParent);
//            }
//        }
//        for(RTreeNode nChild : nParent.getTChildren()) {
//            search(nChild, found, text);
//        }
//    }
    public void applyPathValues(FMap PtoVmap) {
        for(Object K : PtoVmap.K()) {
            Object V = PtoVmap.get(K);
            KeyPath P = (KeyPath) K;
            putByPath(P, V);
        }
    }
    public FMap appliedPathValues(FMap PtoVmap) {
        applyPathValues(PtoVmap);
        return this;
    }

    public static FMap applyPathValues(FMap Mbot, FMap PtoVmap) {
        for(Object K : PtoVmap.K()) {
            Object V = PtoVmap.get(K);
            KeyPath P = (KeyPath) K;
            Mbot.putByPath(P, V);
        }
        return Mbot;
    }

    public void sliceKeyPathKeys() {
        for(Object K : K().toArray(new Object[0])) {
            Object V = get(K);
            KeyPath P = (KeyPath) K;
            remove(P);
            P.remove(0);
            put(P, V);
        }
    }


    /////////////////////////////////////
    // NonTerminal{Generator|Provider} //
    /////////////////////////////////////

    @Override
    public NonTerminal extract() {
        return new FMap(this); // not recursive
    }
    @Override
    public NonTerminal provideNonTerminal() {
        return this;
    }

//    public AMap whatever() {
//        RTreeNode nRoot = new RTreeNode();
//        Map<RTreeNode, RTreeNode> oldNewMap = new HashMap<RTreeNode, RTreeNode>();
//        for(RTreeNode nFound : found) {
//            RTreeNode[] path = nFound.getTPathSegments();
//            RTreeNode nParent = nRoot;
//
//            for(RTreeNode nSegment : path) {
//                if(nSegment == origModel.getRoot() && nParent == nRoot) {
//                    // do nothing
//
//                } else if(oldNewMap.containsKey(nSegment)) {
//                    nParent = oldNewMap.get(nSegment);
//
//                } else {
//                    Object uSegment = nSegment.getUserObject();
//                    RTreeNode nNew = new RTreeNode(uSegment);
//                    nParent.add(nNew);
//                    nParent = nNew;
//                    oldNewMap.put(nSegment, nNew);
//                }
//            }
//        }
//    }

    // TEMPORARY?
    private Object forceString(Object K) {
        if(K == null) {
            return null;
        }
        if(K instanceof String) {
            return K;
        }
        return K;//.toString();
    }

    protected Object readResolve() {
        mapChangedListeners = new ArrayList<>();
        mapBatchChangedListeners = new ArrayList<>();
        mapClearedListeners = new ArrayList<>();
        keyAddedListeners = new ArrayList<>();
        keyRemovedListeners = new ArrayList<>();
        keyChangedListeners = new ArrayList<>();
        valueChangedListeners = new ArrayList<>();
        return this;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        FMap X = A();
        X.put("Z", 23);
        X.put("G", 40);
        X.put("E", 17);
        X.put("A", 90);   // Needs at least trivial sorting capability - with ExtensionPoint-able sorters/comparators

        System.out.println(X);

        for(Entry<Object, Object> KV : X.entrySet()) {

        }

        if(true) {
            return;
        }

        FMap M = A(), A, B, C, E;
        M.put("A", A = A());
        M.put("B", B = A());
        A.put("C", C = A());
        A.put("D", 12);
        B.put("E", E = A());
        B.put("F", "HI");
        C.put("W", 'W');
        C.put("X", "xx");
        E.put("Y", 42.0);
        E.put("Z", "SEE?");

        NonTerminal N = M.flatten();
        FMapRenderer R = new StandardAMapRenderer();
        System.out.println(R.renderValue(N));
        if(true) {
            return;
        }

        M.overlayByKeyPath(new KeyPath(new Object[] {"A", "B", "C"}));
        M.overlayByKeyPath(new KeyPath(new Object[] {"A", "B", "D"}));
        M.overlayByKeyPath(new KeyPath(new Object[] {"A", "B", "C", "E", "F"}));
        M.overlayByKeyPath(new KeyPath(new Object[] {"A", "B", "C"}));
        System.out.println(M.toString());
    }
    public static void x() {

//        AMap Mx = A(
//            "fname", "Darkwing",
//            "lname", "Duck",
//            "daughter", A(
//                "name", "Goslin"
//            ),
//            "acceleration", 123
//        );
//
//        Mx.addSorter("beta", new Comparator<KeyValue>() {
//            @Override
//            public int compare(KeyValue o1, KeyValue o2) {
//                return o1.K.toString().compareTo(o2.K.toString());
//            }
//        });
//
//        Mx.setSorter("beta");
//        System.out.println(Mx.keySet());
//        Mx.remove("fname");
////        Mx.setSorter(null);
//        System.out.println(Mx.keySet());

//        for(Object K : Mx.keySet()) {
//            System.out.println(K + " = " + Mx.get(K));
//        }

//        for(Object V : Mx.values()) {
//            System.out.println("V="+V);
////        }
//
//        if(true) {
//            return;
//        }

//        AMapRenderer renderer = new StandardAMapRenderer();
//        StandardKeyPathRenderer.setDefaultSyntax(KeyPathSyntaxLibrary.getSyntax("Unix File System Path"));
        FMap M = A(
            "fname", "Darkwing",
            "lname", "Duck",
            "daughter", A(
                "name", "Goslin"
            )
        );
        System.out.println("ALL MAP SYNTAXES ::");
        for(String K : FMapSyntaxLibrary.getSyntaxes().keySet()) {
            FMapSyntax syntax = FMapSyntaxLibrary.getSyntaxes().get(K);
            StandardAMapRenderer renderer = new StandardAMapRenderer(syntax);
            String rendering = renderer.renderValue(M);
            System.out.println("SYNTAX :: " + K);
            System.out.println(rendering);
            System.out.println(StringUtil.replicateChar('=', 100));
        }
        FMapSyntax jsonSyntax = FMapSyntaxLibrary.getSyntaxes().get("JSON");
        StandardAMapRenderer renderer = new StandardAMapRenderer(jsonSyntax);
        String s1 = renderer.renderValue(M);

        jsonSyntax = FMapSyntaxLibrary.getSyntaxes().get("Finio");
        renderer = new StandardAMapRenderer(jsonSyntax);
        String s2 = renderer.renderValue(M);

        jsonSyntax = FMapSyntaxLibrary.getSyntaxes().get("XML");
        renderer = new StandardAMapRenderer(jsonSyntax);
        String s3 = renderer.renderValue(M);

        p(Util.sideBySide(s1, s2, s3));  /*multi-line utilities have their own \n */
//        AMap flat = M.flatten();
//        System.out.println(flat);
//        System.out.println(render(flat));

        FMap myMap = A();

        myMap.addMapChangedListener(new MapChangedListener() {
            public void mapChanged(MapChangedEvent e) {
                System.out.println(e);
            }
        });
        myMap.addKeyAddedListener(new KeyAddedListener() {
            public void keyAdded(KeyAddedEvent e) {
                System.out.println(e);
            }
        });
        myMap.addValueChangedListener(new ValueChangedListener() {
            public void valueChanged(ValueChangedEvent e) {
                System.out.println(e);
            }
        });
        myMap.addKeyRemovedListener(new KeyRemovedListener() {
            public void keyRemoved(KeyRemovedEvent e) {
                System.out.println(e);
            }
        });

        myMap.put("one", 1);
        myMap.put("two", 2);
        myMap.put("three", 3);
        FMap myMap2 = A();
        myMap2.put("four", 4);
        myMap2.put("five", 5);
        myMap2.put("six", 6);
        myMap.putAll((Map) myMap2);
        myMap.put("two", new File("some/path/to/file.txt"));

        myMap.remove("five");
    }
    private static void p(Object o) {
        p(null, o);
    }
    private static void p(String printKey, Object o) {
        System.out.print((printKey != null ? printKey + " :: " : "") + o);
    }
    private static void pl(Object o) {
        pl(null, o);
    }
    private static void pl(String printKey, Object o) {
        System.out.println((printKey != null ? printKey + " :: " : "") + o);
    }
}
