package finio.core;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import finio.core.errors.FMapException;
import finio.core.events.KeyAddedListener;
import finio.core.events.KeyChangedListener;
import finio.core.events.KeyRemovedListener;
import finio.core.events.MapBatchChangedListener;
import finio.core.events.MapChangedListener;
import finio.core.events.MapClearedListener;
import finio.core.events.ValueChangedListener;

// We have an issue where a Java ArrayList subclass cannot implement
// Map<Object, Object> due to a signature conflict with
//  * boolean List.remove(Object)
//  * Object Map.remove(Object)
// This is quite disappointing.  So we'll have to instead have a
// method in this interface that generates a Map object backed
// by this non-terminal.
// Derek Next: pick up with FMap verification

public interface NonTerminal {


    /////////
    // MAP //
    /////////

    public boolean isEmpty();                // Primary empty method
    public int size();                       // Primary size method
    public int sizeNoSysMeta();              // Secondary size method ignores presence of FConst.SYS_META_KEY
    public int Z();                          // Shorthand -- always returns size()
    public Set<Object> getKeys();            // Primary keys method
    public Collection<Object> getValues();   // Primary values method
    public Set<Object> K();                  // Shorthand -- always returns getKeys()
    public Collection<Object> V();           // Shorthand -- always returns getValues()
    public Set<Object> keySet();             // For Map interface -- always returns getKeys()
    public Collection<Object> values();      // For Map interface -- always returns getValues()
    public void clear();                     // Primary clear method
    //public Set<Entry<Object, Object>> E();
    //public Set<IndexedEntry> IE();  IndexedEntry is [Integer,Object,Object]
//    public Set<Entry<Object, Object>> entrySet();        // For Map interface -- can be complicated to implement, move to "Map" section some day
    // [NT Core: 11 methods]


    /////////
    // GET //
    /////////

    // public Object get(int I);                                       // This method untouched for lists
    public Object get(Object K);                                       // Primary get method
    public Object getByKey(Object K);                                  // For thoroughness -- always returns get()
    //TODO: public Object getKey(Object V);                            // Future
    public Object getByIndex(int I);                                   // Ambiguously defined return for invalid indices
    public Object getKeyByIndex(int I);                                // Ambiguously defined return for invalid indices
    public Object getByPath(KeyPath P);                                // Primary get by path method
    public Object getByPathNoCopy(KeyPath P);                          // Optimization method
    public Object getValid(Object K, Class T);                         // Convenience method
    public Object getAndSet(Object K, Object Vdefault);                // Convenience method
    public Object getAndSetValid(Object K, Object Vdefault, Class T);  // Convenience method
    // TODO: public Object[] getBySpec(Spec Q);                        // Future
    // TODO: public Object getWithRead(Object K);                      // Future: Bypasses ManagedValueManagers
    // [NT Get: 9 methods]


    /////////
    // PUT //
    /////////

    public Object put(Object K, Object V);                                // Primary put method
    public Object putByKey(Object K, Object V);                           // For thoroughness -- always returns put()
    public Object putByIndex(int I, Object V);
    public Object putByPath(KeyPath P, Object V);                         // Primary put by path method
    public Object putByPathNoCopy(KeyPath P, Object V);                   // Optimization method
    public void putAll(Map<? extends Object, ? extends Object> m);        // For Map interface
    public void putAll(NonTerminal M);
    // TODO: public Object[] putBySpec(Spec Q);                           // Future
    // [NT Put: 7 methods]


    ////////////
    // REMOVE //
    ////////////

    // public Object remove(int I);                 // This method untouched for lists
    // public Object|boolean remove(Object K|V);    // Not possible due to conflicting APIs
    public Object removeByKey(Object K);            // Need explicitly separate methods
    public boolean removeValue(Object V);           // Had to decide on a return type
    public Object removeByPath(KeyPath P);          // Primary remove by path method
    public Object removeByPathNoCopy(KeyPath P);    // Optimization method
    // TODO: public Object[] removeBySpec(Spec Q);  // Future
    // [NT Remove: 4 methods]


    //////////////
    // CONTAINS //
    //////////////

    public boolean has(Object K);                       // Primary has method
    public boolean hasKey(Object K);                    // For thoroughness -- always returns has()
    public boolean hasValue(Object V);
    public boolean hasPath(KeyPath P);                  // Primary has path method
    public boolean hasPathNoCopy(KeyPath P);            // Optimization method
    public boolean containsKey(Object K);               // For Map interface -- always returns hasKey()
    public boolean containsValue(Object V);             // For Map interface -- always returns hasValue()
    // public boolean contains(Object V);               // This method untouched for lists (not listmaps)
    // public boolean containsAll(Collection<?> Vs);    // This method untouched for lists (not listmaps)
    // public int indexOf(Object V);                    // In FList & FListMap, hopefully FMap too
    // public int lastIndexOf(Object V);                // In FList & FListMap, Hopefully FMap too
    // Could potentially have a containsAllKeys(...) if found to be useful
    // [NT Contains: 7 methods]


    ////////////
    // EVENTS //
    ////////////

    // TODO: We must analyze how these listeners map to a list-based map.
    // How to support post-addition or post-removal key clean up.
    public void addMapChangedListener(MapChangedListener listener);
    public void addMapBatchChangedListener(MapBatchChangedListener listener);
    public void addMapClearedListener(MapClearedListener listener);
    public void addKeyAddedListener(KeyAddedListener listener);
    public void addKeyRemovedListener(KeyRemovedListener listener);
    public void addKeyChangedListener(KeyChangedListener listener);
    public void addValueChangedListener(ValueChangedListener listener);
    public void removeMapChangedListener(MapChangedListener listener);
    public void removeMapBatchChangedListener(MapBatchChangedListener listener);
    public void removeMapClearedListener(MapClearedListener listener);
    public void removeKeyAddedListener(KeyAddedListener listener);
    public void removeKeyRemovedListener(KeyRemovedListener listener);
    public void removeKeyChangedListener(KeyChangedListener listener);
    public void removeValueChangedListener(ValueChangedListener listener);
    public MapChangedListener[] getMapChangedListeners();
    public MapBatchChangedListener[] getMapBatchChangedListeners();
    public MapClearedListener[] getMapClearedListeners();
    public KeyAddedListener[] getKeyAddedListeners();
    public KeyRemovedListener[] getKeyRemovedListeners();
    public KeyChangedListener[] getKeyChangedListeners();
    public ValueChangedListener[] getValueChangedListeners();
    // [NT Events: 7 x 3 = 21 methods]

    // Other possible future notifiers:
    //  - Sort Changed
    //  - Hierarchy
    //  - Recursive Change (same as batch change?)


    /////////////////////////
    // EVENTS SUPPLEMENTAL //
    /////////////////////////

    public boolean isSuppressAllEvents();
    public void setSuppressAllEvents(boolean suppress);
    public void notifyBatchUpdate();
    // 3 methods


    /////////////////////////////
    // CHANGE ALLOWED/PROBABLE //
    /////////////////////////////

    public boolean canAdd(Object K, Object V);
    public boolean canChangeValue(Object K, Object V);
    public boolean canSetValue(Object K, Object V);       // IMPL => return canAdd(K, V) || canChangeValue(K, V);
    public boolean canChangeKey(Object K1, Object K2);
    public boolean canRemove(Object K);
    public boolean canClearMap();
    public boolean wouldAdd(Object K, Object V);          // IMPL => return canAdd(K, V) && !has(K);
    public boolean wouldChangeValue(Object K, Object V);  // IMPL => return canChangeValue(K, V) && has(K);
    public boolean wouldSetValue(Object K, Object V);     // IMPL => return wouldAdd(K, V) || wouldChangeValue(K, V);
    public boolean wouldChangeKey(Object K1, Object K2);  // IMPL => return canChangeKey(K1, K2) && has(K1) && !has(K2);
    public boolean wouldRemove(Object K);                 // IMPL => return canRemove(K) && has(K);
    public boolean wouldClearMap();                       // IMPL => return canClearMap();
    // 12 methods


    //////////
    // MISC //
    //////////

    // Technically most of these could have associated can/would methods...
    public NonTerminal getSysMeta();
    public Object getSysMeta(Object K);                  // Shorthand for research purposes
    public Object putSysMeta(Object K, Object V);        // Shorthand for research purposes
    public void compress();                              // Optional to implement
    public Set<Entry<Object, Object>> entrySet();        // For Map interface -- can be complicated to implement, move to "Map" section some day
    public Object getNextAvailableKey();
    public Object getNextAvailableKey(String prefix);
    public void changeKey(Object Kcur, Object Knew);
    public int sizeAll();
    public void createAlternatesMap();
    public void createAlternatesMap(Object K);
    public void describe(Object K);
    public void promote(Object K);
    public boolean isRefreshable();                      // Technically could save for a RefreshableNonTerminal sub-interface
    public void refresh();
    public NonTerminal flatten();
    // 15 methods


    ///////////////
    // RENDERING //
    ///////////////

    public String toStringObject();
    public String toString();
    public String toString(int maxChars);
    public void toStringAppend(StringBuilder buffer, int maxChars);
    // 4 methods


    ///////////////
    // TRANSLATE //
    ///////////////

    // Would have been nice to have just implemented
    // Map via ANonTerminal.  However, asJavaMap will
    // either return a shell delegating object which
    // delegates all method calls to this underlying
    // object, and no copy is made, or it will return
    // this object itself.
    public Map<Object, Object> asJavaMap();              // toJavaMap would imply a copy of some sort
    // 1 method


    /////////////
    // SORTING //
    /////////////

    public void setOrdering(String name);
    public void addOrdering(String name, OrderingManager orderingManager);
    public OrderingManager getOrdering(String name);
    public boolean removeOrdering(String name);
    // 4 methods

    // Always need a default sort method that every non-terminal starts out with.
    // Cannot have a non-terminal without a determined order over which those
    // keys will be iterated from the very beginning.
    // Need a "currently selected" sort using which the methods getByIndex(int I),
    // getKeyByIndex(int I), and putByIndex(int I, Object V) (and future ones
    // like hasIndex(int I) and moveEntry(int Isrc, int Idst), moveEntry(Object K, int Idst)
    // It's the "Active/Current" ordering/sort map.  Getting an iterator from the
    // map will always use the "active" ordering.  Adding an element to the non-terminal
    // has to allow each ordering to decide where in its order the new element goes.
    // "Moving" an entry has nothing to do with the other registered orderings.
    // Technically you could have ALL the index methods also take a specific ordering
    // to work off of:
    //    getByIndex(String orderingName, int I)
    //    getKeyByIndex(String orderingName, int I)
    //    putByIndex(String orderingName, int I, Object V)
    //    moveEntry(String orderingName, int Isrc, int Idst)
    //    keyIterator(String orderingName)
    //    entryIterator(String orderingName)
    //    etc.
    // But this is probably somewhat overkill.  However, it is not to say that the
    // all the orderings aren't available under the hood ready for people to modify
    // at will.  The ordering should take into consideration aspects of the value
    // as much as aspects of the key.  Obviously there will need to have much
    // consideration paid to synchronization.  One potential implementation:
    // Map<String, List<KeyValue>> orderings = new HashMap<>();

//    public FMap() {
//        init();
//    }
//    private void init() {
//        List<KeyValue> defaultOrdering = new ArrayList<>();
//        orderings.put(FConst.DEFAULT_ORDER"DefaultInsertionOrder", defaultOrdering);
//
//    }




    ///////////////////////////
    // MOVE / COPY / OVERLAY //
    ///////////////////////////

    public void move(KeyPath[] Psources, KeyPath Pdest, int position, boolean preventOverwrite) throws FMapException;
    public void copy(KeyPath[] psources, KeyPath pdest, int position);
    public NonTerminal overlayByKeyPath(KeyPath P);
    // 3 methods

//    public List<Object> getRegisteredIncomingReferences();
//    public void registerIncomingReference(Object holder);
//    public void unregisterIncomingReference(Object holder);
}
