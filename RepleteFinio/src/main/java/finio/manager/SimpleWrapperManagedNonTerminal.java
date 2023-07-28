package finio.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import finio.core.FUtil;
import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.core.OrderingManager;
import finio.core.errors.FMapException;
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
import finio.core.managed.ManagedNonTerminal;
import finio.plugins.extpoints.NonTerminalManager;

public abstract class SimpleWrapperManagedNonTerminal implements ManagedNonTerminal {

    // Not unloadable/reloadable, always loaded.  Not refreshable.  No parameters.


    ////////////
    // FIELDS //
    ////////////

    protected NonTerminalManager manager;
    protected boolean suppressAllEvents = false;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public SimpleWrapperManagedNonTerminal(NonTerminalManager manager) {
        this.manager = manager;
        initSimple();
        subscribe();
    }


    //////////////
    // ABSTRACT //
    //////////////

    protected abstract void initSimple();
    protected abstract NonTerminal getM();


    ///////////////
    // LISTENERS //
    ///////////////

    protected MapBatchChangedListener mapBatchChangedListener = new MapBatchChangedListener() {
        public void mapBatchChanged(MapBatchChangedEvent e) {
            if(isLoaded() && !suppressAllEvents) {
                fireMapBatchChangedNotifierWrapper(e);
            }
        }
    };
    protected MapClearedListener mapClearedListener = new MapClearedListener() {
        public void mapCleared(MapClearedEvent e) {
            if(isLoaded() && !suppressAllEvents) {
                fireMapClearedNotifierWrapper(e);
            }
        }
    };
    protected KeyAddedListener keyAddedListener = new KeyAddedListener() {
        public void keyAdded(KeyAddedEvent e) {
            if(isLoaded() && !suppressAllEvents) {
                fireKeyAddedNotifierWrapper(e);
            }
        }
    };
    protected KeyRemovedListener keyRemovedListener = new KeyRemovedListener() {
        public void keyRemoved(KeyRemovedEvent e) {
            if(isLoaded() && !suppressAllEvents) {
                fireKeyRemovedNotifierWrapper(e);
            }
        }
    };
    protected KeyChangedListener keyChangedListener = new KeyChangedListener() {
        public void keyChanged(KeyChangedEvent e) {
            if(isLoaded() && !suppressAllEvents) {
                fireKeyChangedNotifierWrapper(e);
            }
        }
    };
    protected ValueChangedListener valueChangedListener = new ValueChangedListener() {
        public void valueChanged(ValueChangedEvent e) {
            if(isLoaded() && !suppressAllEvents) {
                fireValueChangedNotifierWrapper(e);
            }
        }
    };

    protected void subscribe() {
//        Mreal.addMapChangedListener(new MapChangedListener() {
//            public void mapChanged(MapChangedEvent e) {
//                if(loaded) {
//                    fireMapChangedNotifierWrapper(e);
//                }
//            }
//        });
        getM().addMapBatchChangedListener(mapBatchChangedListener);
        getM().addMapClearedListener(mapClearedListener);
        getM().addKeyAddedListener(keyAddedListener);
        getM().addKeyRemovedListener(keyRemovedListener);
        getM().addKeyChangedListener(keyChangedListener);
        getM().addValueChangedListener(valueChangedListener);
    }
    protected void unsubscribe() {
        getM().removeMapBatchChangedListener(mapBatchChangedListener);
        getM().removeMapClearedListener(mapClearedListener);
        getM().removeKeyAddedListener(keyAddedListener);
        getM().removeKeyRemovedListener(keyRemovedListener);
        getM().removeKeyChangedListener(keyChangedListener);
        getM().removeValueChangedListener(valueChangedListener);
    }


    /////////
    // MAP //
    /////////

    @Override
    public boolean isEmpty() {
        return getM().isEmpty();
    }
    @Override
    public int size() {
        return getM().size();
    }
    @Override
    public int sizeNoSysMeta() {
        return getM().sizeNoSysMeta();
    }
    @Override
    public int Z() {
        return getM().Z();
    }
    @Override
    public Set<Object> getKeys() {
        return getM().getKeys();
    }
    @Override
    public Collection<Object> getValues() {
        return getM().getValues();
    }
    @Override
    public Set<Object> K() {
        return getM().K();
    }
    @Override
    public Collection<Object> V() {
        return getM().V();
    }
    @Override
    public Set<Object> keySet() {
        return getM().keySet();
    }
    @Override
    public Collection<Object> values() {
        return getM().values();
    }
    @Override
    public void clear() {
        getM().clear();
    }
    // [NT Core: 10 methods]


    /////////
    // GET //
    /////////

    @Override
    public Object get(Object K) {
        return getM().get(K);
    }
    @Override
    public Object getByKey(Object K) {
        return getM().getByKey(K);
    }
    @Override
    public Object getByIndex(int I) {
        return getM().getByIndex(I);
    }
    @Override
    public Object getKeyByIndex(int I) {
        return getM().getKeyByIndex(I);
    }
    @Override
    public Object getByPath(KeyPath P) {
        return getM().getByPath(P);
    }
    @Override
    public Object getByPathNoCopy(KeyPath P) {
        return getM().getByPathNoCopy(P);
    }
    @Override
    public Object getValid(Object K, Class type) {
        return getM().getValid(K, type);
    }
    @Override
    public Object getAndSet(Object K, Object Vdefault) {
        return getM().getAndSet(K, Vdefault);
    }
    @Override
    public Object getAndSetValid(Object K, Object Vdefault, Class type) {
        return getM().getAndSetValid(K, Vdefault, type);
    }
    // [NT Get: 9 methods]


    /////////
    // PUT //
    /////////

    @Override
    public Object put(Object K, Object V) {
        return getM().put(K, V);
    }
    @Override
    public Object putByKey(Object K, Object V) {
        return getM().putByKey(K, V);
    }
    @Override
    public Object putByIndex(int I, Object V) {
        return getM().putByIndex(I, V);
    }
    @Override
    public Object putByPath(KeyPath P, Object V) {
        return getM().putByPath(P, V);
    }
    @Override
    public Object putByPathNoCopy(KeyPath P, Object V) {
        return getM().putByPathNoCopy(P, V);
    }
    @Override
    public void putAll(Map<? extends Object, ? extends Object> m) {
        getM().putAll(m);
    }
    @Override
    public void putAll(NonTerminal Mnew) {
        getM().putAll(Mnew);
    }
    // [NT Put: 7 methods]


    ////////////
    // REMOVE //
    ////////////

    @Override
    public Object removeByKey(Object K) {
        return getM().removeByKey(K);
    }
    @Override
    public boolean removeValue(Object V) {
        return getM().removeValue(V);
    }
    @Override
    public Object removeByPath(KeyPath P) {
        return getM().removeByPath(P);
    }
    @Override
    public Object removeByPathNoCopy(KeyPath P) {
        return getM().removeByPathNoCopy(P);
    }
    // [NT Remove: 4 methods]


    //////////////
    // CONTAINS //
    //////////////

    @Override
    public boolean has(Object K) {
        return getM().has(K);
    }
    @Override
    public boolean hasKey(Object K) {
        return getM().hasKey(K);
    }
    @Override
    public boolean hasValue(Object V) {
        return getM().hasValue(V);
    }
    @Override
    public boolean hasPath(KeyPath P) {
        return getM().hasPath(P);
    }
    @Override
    public boolean hasPathNoCopy(KeyPath P) {
        return getM().hasPathNoCopy(P);
    }
    @Override
    public boolean containsKey(Object K) {
        return getM().containsKey(K);
    }
    @Override
    public boolean containsValue(Object V) {
        return getM().containsValue(V);
    }
    // [NT Put: 7 methods]


    ////////////
    // EVENTS //
    ////////////

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
        if(mapChangedListeners.size() != 0) {
            MapChangedEvent event = new MapChangedEvent(this, cause);
            for(MapChangedListener listener : mapChangedListeners) {
                listener.mapChanged(event);
            }
        }
    }
//    private void fireMapChangedNotifierWrapper(MapChangedEvent realEvent) {
//        // TODO???????????????????????
//    }

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
        if(mapBatchChangedListeners.size() != 0) {
            MapBatchChangedEvent event = new MapBatchChangedEvent(this);
            for(MapBatchChangedListener listener : mapBatchChangedListeners) {
                listener.mapBatchChanged(event);
            }
        }
    }
    private void fireMapBatchChangedNotifierWrapper(MapBatchChangedEvent realEvent) {
        fireMapBatchChangedNotifier();
    }

    protected transient List<MapClearedListener> mapClearedListeners = new ArrayList<>();
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
        for(MapClearedListener listener : mapClearedListeners) {
            listener.mapCleared(event);
        }
        fireMapChangedNotifier(event);
    }
    private void fireMapClearedNotifierWrapper(MapClearedEvent realEvent) {
        fireMapClearedNotifier();
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
    private void fireKeyAddedNotifier(Object K, Object V) {
        KeyAddedEvent event = new KeyAddedEvent(this, K, V);
        for(KeyAddedListener listener : keyAddedListeners) {
            listener.keyAdded(event);
        }
        fireMapChangedNotifier(event);
    }
    private void fireKeyAddedNotifierWrapper(KeyAddedEvent realEvent) {
        fireKeyAddedNotifier(realEvent.getK(), realEvent.getV());
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
    private void fireKeyRemovedNotifier(Object K, Object V) {
        KeyRemovedEvent event = new KeyRemovedEvent(this, K, V);
        for(KeyRemovedListener listener : keyRemovedListeners) {
            listener.keyRemoved(event);
        }
        fireMapChangedNotifier(event);
    }
    private void fireKeyRemovedNotifierWrapper(KeyRemovedEvent realEvent) {
        fireKeyRemovedNotifier(realEvent.getK(), realEvent.getV());
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
    private void fireKeyChangedNotifier(Object Kold, Object Knew) {
        KeyChangedEvent event = new KeyChangedEvent(this, Kold, Knew);
        for(KeyChangedListener listener : keyChangedListeners) {
            listener.keyChanged(event);
        }
        fireMapChangedNotifier(event);
    }
    private void fireKeyChangedNotifierWrapper(KeyChangedEvent realEvent) {
        fireKeyChangedNotifier(realEvent.getOldK(), realEvent.getNewK());
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
    private void fireValueChangedNotifier(Object K, Object Vprev, Object V) {
        ValueChangedEvent event = new ValueChangedEvent(this, K, Vprev, V);
        for(ValueChangedListener listener : valueChangedListeners) {
            listener.valueChanged(event);
        }
        fireMapChangedNotifier(event);
    }
    private void fireValueChangedNotifierWrapper(ValueChangedEvent realEvent) {
        fireValueChangedNotifier(realEvent.getK(), realEvent.getOldV(), realEvent.getNewV());
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
    public void notifyBatchUpdate() {
        fireMapBatchChangedNotifier();
    }
    // 3 methods


    /////////////////////////////
    // CHANGE ALLOWED/PROBABLE //
    /////////////////////////////

    @Override
    public boolean canAdd(Object K, Object V) {
        return getM().canAdd(K, V);
    }
    @Override
    public boolean canChangeValue(Object K, Object V) {
        return getM().canChangeValue(K, V);
    }
    @Override
    public boolean canSetValue(Object K, Object V) {
        return getM().canSetValue(K, V);
    }
    @Override
    public boolean canChangeKey(Object K1, Object K2) {
        return getM().canChangeKey(K1, K2);
    }
    @Override
    public boolean canRemove(Object K) {
        return getM().canRemove(K);
    }
    @Override
    public boolean canClearMap() {
        return getM().canClearMap();
    }
    @Override
    public boolean wouldAdd(Object K, Object V) {
        return getM().wouldAdd(K, V);
    }
    @Override
    public boolean wouldChangeValue(Object K, Object V) {
        return getM().wouldChangeValue(K, V);
    }
    @Override
    public boolean wouldSetValue(Object K, Object V) {
        return getM().wouldSetValue(K, V);
    }
    @Override
    public boolean wouldChangeKey(Object K1, Object K2) {
        return getM().wouldChangeKey(K1, K2);
    }
    @Override
    public boolean wouldRemove(Object K) {
        return getM().wouldRemove(K);
    }
    @Override
    public boolean wouldClearMap() {
        return getM().wouldClearMap();
    }
    // 12 methods


    //////////
    // MISC //
    //////////

    @Override
    public NonTerminal getSysMeta() {
        return getM().getSysMeta();
    }
    @Override
    public Object getSysMeta(Object K) {
        return getM().getSysMeta(K);
    }
    @Override
    public Object putSysMeta(Object K, Object V) {
        return getM().putSysMeta(K, V);
    }
    @Override
    public void compress() {
        getM().compress();
    }
    @Override
    public Set<Entry<Object, Object>> entrySet() {
        return getM().entrySet();
    }
    @Override
    public Object getNextAvailableKey() {
        return getM().getNextAvailableKey();
    }
    @Override
    public Object getNextAvailableKey(String prefix) {
        return getM().getNextAvailableKey(prefix);
    }
    @Override
    public void changeKey(Object Kcur, Object Knew) {
        getM().changeKey(Kcur, Knew);
    }
    @Override
    public int sizeAll() {
        return getM().sizeAll();
    }
    @Override
    public void createAlternatesMap() {
        getM().createAlternatesMap();
    }
    @Override
    public void createAlternatesMap(Object K) {
        getM().createAlternatesMap(K);
    }
    @Override
    public void describe(Object K) {
        getM().describe(K);
    }
    @Override
    public void promote(Object K) {
        getM().promote(K);
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
        return getM().flatten();
    }
    // 15 methods


    ///////////////
    // RENDERING //
    ///////////////

    // This cannot just return super.toStringObject().
    @Override
    public String toStringObject() {
        return FUtil.toStringBaseIdent(this);
    }
    @Override
    public String toString() {
        return getM().toString();
    }
    @Override
    public String toString(int maxChars) {
        return getM().toString();
    }
    @Override
    public void toStringAppend(StringBuilder buffer, int maxChars) {
        getM().toStringAppend(buffer, maxChars);
    }
    // 4 methods


    ///////////////
    // TRANSLATE //
    ///////////////

    @Override
    public Map<Object, Object> asJavaMap() {
        return getM().asJavaMap();
    }
    // 1 method


    /////////////
    // SORTING //
    /////////////

    @Override
    public void setOrdering(String name) {
        getM().setOrdering(name);
    }
    @Override
    public void addOrdering(String name, OrderingManager orderingManager) {
        getM().addOrdering(name, orderingManager);
    }
    @Override
    public OrderingManager getOrdering(String name) {
        return getM().getOrdering(name);
    }
    @Override
    public boolean removeOrdering(String name) {
        return getM().removeOrdering(name);
    }
    // 4 methods


    ///////////////////////////
    // MOVE / COPY / OVERLAY //
    ///////////////////////////

    @Override
    public void move(KeyPath[] Psources, KeyPath Pdest, int position, boolean preventOverwrite)
                                                                                               throws FMapException {
        getM().move(Psources, Pdest, position, preventOverwrite);
    }
    @Override
    public void copy(KeyPath[] psources, KeyPath pdest, int position) {
        getM().copy(psources, pdest, position);
    }
    @Override
    public NonTerminal overlayByKeyPath(KeyPath P) {
        return getM().overlayByKeyPath(P);
    }
    // 3 methods


    ////////////////
    // MANAGEMENT //
    ////////////////

    @Override
    public ManagedParameters getParams() {
        return null;
    }
    @Override
    public void setParams(ManagedParameters params) {

    }
    @Override
    public void load() {
    }
    @Override
    public void unload() {
    }
    @Override
    public boolean isLoaded() {
        return true;
    }
    @Override
    public NonTerminalManager getManager() {
        return manager;
    }
    // 6 methods
}
