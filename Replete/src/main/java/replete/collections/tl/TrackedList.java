package replete.collections.tl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import replete.collections.tl.events.TLAddEvent;
import replete.collections.tl.events.TLChangeEvent;
import replete.collections.tl.events.TLClearEvent;
import replete.collections.tl.events.TLElementChangeEvent;
import replete.collections.tl.events.TLEvent;
import replete.collections.tl.events.TLMoveEvent;
import replete.collections.tl.events.TLRemoveEvent;
import replete.collections.tl.events.TLReplaceEvent;
import replete.event.ExtChangeNotifier;

public class TrackedList<T> extends ArrayList<T> {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    protected static final int INIT = 0;
    protected static final int ADD  = 1;   // Used for managing element changed listeners
    protected static final int REPL = 2;
    protected static final int REMV = 3;

    // Core

    protected boolean suppressEvents = false;
    protected boolean trackElements = false;

    // Empty list when not in use, otherwise has same size as this list.
    // [OPTION] You could implement much more complicated logic in
    // TrackedList to only keep as few elements as possible by not adding
    // additional null elements, etc.  This was removed due to code
    // readability and complexity issues.
    protected List<ElementChangeListener<T>> elementListeners;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TrackedList() {
        super();
        init();
    }
    public TrackedList(Collection<? extends T> c) {
        super(c);
        init();
    }
    public TrackedList(int initialCapacity) {
        super(initialCapacity);
        init();
    }

    private void init() {
        elementListeners = new ArrayList<ElementChangeListener<T>>();
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isSuppressEvents() {
        return suppressEvents;
    }
    public boolean isTrackElements() {
        return trackElements;
    }
    protected List<ElementChangeListener<T>> getElementListeners() {
        return elementListeners;
    }

    // Mutators

    public void setSuppressEvents(boolean suppressEvents) {
        this.suppressEvents = suppressEvents;
    }
    public void setTrackElements(boolean track) {

        // Don't do anything if we're not changing state.  The
        // code afterwards is not written to be that robust.
        if(trackElements == track) {
            return;
        }

        if(!track) {

            // Remove element listeners
            for(int i = size() - 1; i >= 0; i--) {
                removeElementChangeListenerFromElement(i, get(i), REMV);
            }

            // elementListeners Should be clear

            trackElements = false;

        } else {

            trackElements = true;

            // elementListeners Should be clear

            // Add element listeners
            for(int i = 0; i < size(); i++) {
                addElementChangeListenerToElement(i, get(i), INIT);
            }
        }
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    // Add element(s)

    private ExtChangeNotifier<TLAddListener<T>> addNotifier =
        new ExtChangeNotifier<TLAddListener<T>>();
    public void addAddListener(TLAddListener<T> listener) {
        addNotifier.addListener(listener);
    }
    public void removeAddListener(TLAddListener<T> listener) {
        // TODO: create removes
    }
    private void fireAddNotifier(TLAddEvent<T> event) {
        fire(addNotifier, event);
    }

    // Remove element(s)

    private ExtChangeNotifier<TLRemoveListener<T>> removeNotifier =
        new ExtChangeNotifier<TLRemoveListener<T>>();
    public void addRemoveListener(TLRemoveListener<T> listener) {
        removeNotifier.addListener(listener);
    }
    private void fireRemoveNotifier(TLRemoveEvent<T> event) {
        fire(removeNotifier, event);
    }

    // Move element(s) due to add or remove

    private ExtChangeNotifier<TLMoveListener<T>> moveNotifier =
        new ExtChangeNotifier<TLMoveListener<T>>();
    public void addMoveListener(TLMoveListener<T> listener) {
        moveNotifier.addListener(listener);
    }
    private void fireMoveNotifier(TLMoveEvent<T> event) {
        if(!event.getMoved().isEmpty()) {
            fire(moveNotifier, event);
        }
    }

    // Replace element (List.set)

    private ExtChangeNotifier<TLReplaceListener<T>> replaceNotifier =
        new ExtChangeNotifier<TLReplaceListener<T>>();
    public void addReplaceListener(TLReplaceListener<T> listener) {
        replaceNotifier.addListener(listener);
    }
    private void fireReplaceNotifier(TLReplaceEvent<T> event) {
        fire(replaceNotifier, event);
    }

    // Clear elements

    private ExtChangeNotifier<TLClearListener<T>> clearNotifier =
        new ExtChangeNotifier<TLClearListener<T>>();
    public void addClearListener(TLClearListener<T> listener) {
        clearNotifier.addListener(listener);
    }
    private void fireClearNotifier() {                  // Convenience
        fireClearNotifier(new TLClearEvent<T>(this));
    }
    private void fireClearNotifier(TLClearEvent<T> event) {
        if(size() == 0) {
            fire(clearNotifier, event);
        }
    }

    // Generic change

    private ExtChangeNotifier<TLChangeListener<T>> changeNotifier =
        new ExtChangeNotifier<TLChangeListener<T>>();
    public void addChangeListener(TLChangeListener<T> listener) {
        changeNotifier.addListener(listener);
    }
    private void fireChangeNotifier() {                 // Convenience
        fireChangeNotifier(new TLChangeEvent<T>(this));
    }
    private void fireChangeNotifier(TLChangeEvent<T> event) {
        fire(changeNotifier, event);
    }

    // Individual element change

    private ExtChangeNotifier<TLElementChangeListener<T>> elementChangeNotifier =
        new ExtChangeNotifier<TLElementChangeListener<T>>();
    public void addElementChangeListener(TLElementChangeListener<T> listener) {
        elementChangeNotifier.addListener(listener);
    }
    protected void fireElementChangeNotifier(TLElementChangeEvent<T> event) {
        fire(elementChangeNotifier, event);
    }

    // All events

    private ExtChangeNotifier<TLAnyEventListener<T>> anyNotifier =
        new ExtChangeNotifier<TLAnyEventListener<T>>();
    public void addAnyEventListener(TLAnyEventListener<T> listener) {
        anyNotifier.addListener(listener);
    }
    private void fireAnyEventNotifier(TLEvent<T> event) {
        if(!suppressEvents) {
            anyNotifier.fireStateChanged(event);
        }
    }

    // Helper

    private void fire(ExtChangeNotifier<?> notifier, TLEvent<T> event) {
        if(!suppressEvents) {
            notifier.fireStateChanged(event);
            fireAnyEventNotifier(event);
        }
    }

    protected void listenerCallback(ElementChangeListener<T> listener, boolean invalid, int index) {
        if(!invalid) {
            TLElementChangeEvent<T> event = new TLElementChangeEvent<T>(
                this, index, get(index));
            fireElementChangeNotifier(event);
         }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // [OPTION] Some of the following code could be slightly
    // more efficient by not preparing events or calling fire*
    // methods unless they will conceivably do something.
    // The tradeoff is the current code readability that exists.

    @Override
    public void add(int index, T element) {
        super.add(index, element);

        // Prepare add and move events
        TLAddEvent<T> addEvent = prepareAddEvent(index);
        TLMoveEvent<T> moveEvent = prepareMoveEvent(index + 1, 1);

        // Fire add, move, and change events
        fireAddNotifier(addEvent);
        fireMoveNotifier(moveEvent);
        fireChangeNotifier();
    }

    // Really just a specific form of this.add(size(), element)
    @Override
    public boolean add(T element) {
        super.add(element);
        int index = size() - 1;

        // Prepare add event
        TLAddEvent<T> addEvent = prepareAddEvent(index);

        // Fire add and change events
        fireAddNotifier(addEvent);
        fireChangeNotifier();

        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> elements) {
        int prevSize = size();
        boolean someAdded = super.addAll(elements);

        if(someAdded) {

            // Prepare add event
            TLAddEvent<T> addEvent = prepareAddEvent(prevSize, size());

            // Fire add and change events
            fireAddNotifier(addEvent);
            fireChangeNotifier();
        }

        return someAdded;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> elements) {
        int prevSize = size();
        boolean someAdded = super.addAll(index, elements);

        if(someAdded) {
            int numAdded = size() - prevSize;

            // Prepare add and move events
            TLAddEvent<T> addEvent = prepareAddEvent(index, index + numAdded);
            TLMoveEvent<T> moveEvent = prepareMoveEvent(index + numAdded, numAdded);

            // Fire add, move, and change events
            fireAddNotifier(addEvent);
            fireMoveNotifier(moveEvent);
            fireChangeNotifier();
        }

        return someAdded;
    }

    // TODO: Where we are... remove events don't properly handle element changes

    @Override
    public T remove(int index) {

        TLRemoveEvent<T> removeEvent = prepareRemoveEvent(index);

        T element = super.remove(index);

        // Update element listeners
        removeElementChangeListenerFromElement(index, element, REMV);

        // Prepare remove and move events
        TLMoveEvent<T> moveEvent = prepareMoveEvent(index, -1);

        // Fire remove, move, clear, and change events
        fireRemoveNotifier(removeEvent);
        fireMoveNotifier(moveEvent);
        fireClearNotifier();         // List had to be non-empty before method
        fireChangeNotifier();

        return element;
    }

    @Override
    public boolean remove(Object element) {

        // Remove & move event prep
        int index = indexOf(element);

        boolean someRemoved = super.remove(element);

        if(someRemoved) {

            // Remove & move event prep
            Map<Integer, T> removed = new LinkedHashMap<Integer, T>();
            removed.put(index, (T) element);
            TLRemoveEvent<T> removeEvent = new TLRemoveEvent<T>(this, removed);

            // Update element listeners
            removeElementChangeListenerFromElement(index, (T) element, REMV);

            TLMoveEvent<T> moveEvent = prepareMoveEvent(index, -1);

            // Fire remove, move, clear, and change events
            fireRemoveNotifier(removeEvent);
            fireMoveNotifier(moveEvent);
            fireClearNotifier();         // List had to be non-empty before method
            fireChangeNotifier();
        }

        return someRemoved;
    }

    @Override
    public boolean removeAll(Collection<?> c) {

        // Remove & move event prep
        Map<Integer, T> removed = new LinkedHashMap<Integer, T>();
        Map<Integer, T> moved = new LinkedHashMap<Integer, T>();
        Map<Integer, Integer> indexChanges = new LinkedHashMap<Integer, Integer>();

        Iterator<T> it = iterator();
        int index = 0;
        int numRemoved = 0;
        while(it.hasNext()) {
            T element = it.next();
            if(c.contains(element)) {
                removed.put(index, element);
                numRemoved++;
            } else if(numRemoved != 0) {
                moved.put(index - numRemoved, element);
                indexChanges.put(index - numRemoved, index);
            }
            index++;
        }

        // This assumes that the base class implementation does not delegate
        // calls to the remove method.
        int before = size();
        boolean someRemoved = super.removeAll(c);
        int after = size();

        int delta = before - after;
        if(delta != removed.size()) {
            throw new IllegalStateException("Invalid removeAll state.");
        }

        removeCleanUp(someRemoved, removed, moved, indexChanges);

        return someRemoved;
    }

    @Override
    public boolean retainAll(Collection<?> c) {

        // Remove & move event prep
        Map<Integer, T> removed = new LinkedHashMap<Integer, T>();
        Map<Integer, T> moved = new LinkedHashMap<Integer, T>();
        Map<Integer, Integer> indexChanges = new LinkedHashMap<Integer, Integer>();

        Iterator<T> e = iterator();
        int index = 0;
        int numRemoved = 0;
        while(e.hasNext()) {
            T element = e.next();
            if(!c.contains(element)) {
                removed.put(index, element);
                numRemoved++;
            } else if(numRemoved != 0) {
                moved.put(index - numRemoved, element);
                indexChanges.put(index - numRemoved, index);
            }
            index++;
        }

        // This assumes that the base class implementation does not delegate
        // calls to the remove method.
        int before = size();
        boolean someRemoved = super.retainAll(c);
        int after = size();

        int delta = before - after;
        if(delta != removed.size()) {
            throw new IllegalStateException("Invalid retainAll state.");
        }

        removeCleanUp(someRemoved, removed, moved, indexChanges);

        return someRemoved;
    }

    private void removeCleanUp(boolean someRemoved, Map<Integer, T> removed, Map<Integer, T> moved,
                               Map<Integer, Integer> indexChanges) {
        if(someRemoved) {

            // Update element listeners
            Integer[] indices = removed.keySet().toArray(new Integer[0]);
            for(int ii = indices.length - 1; ii >= 0; ii--) {
                int index = indices[ii];
                T element = removed.get(index);
                removeElementChangeListenerFromElement(index, element, REMV);
            }

            if(trackElements && elementListeners.size() != size()) {
                throw new IllegalStateException("Element listeners list is wrong size.");
            }

            // Update existing element change listeners' indices
            for(int i = indices[0]; i < size(); i++) {
                updateElementChangeListenerIndex(i);
            }

            // Fire remove, move, clear, and change events
            fireRemoveNotifier(new TLRemoveEvent<T>(this, removed));
            fireMoveNotifier(new TLMoveEvent<T>(this, moved, indexChanges));
            fireClearNotifier();         // List had to be non-empty before method
            fireChangeNotifier();
        }
    }

    @Override
    public T set(int index, T element) {
        T previous = super.set(index, element);

        // [OPTION] Fire add/remove events on set
        // Remove event
        //Map<Integer, T> removed = new LinkedHashMap<Integer, T>();
        //removed.put(index, previous);
        //fireRemoveNotifier(new TLRemoveEvent<T>(this, removed));

        // Add event
        //Map<Integer, T> added = new LinkedHashMap<Integer, T>();
        //added.put(index, element);
        //fireAddNotifier(new TLAddEvent<T>(this, added));

        TLReplaceEvent<T> replaceEvent = prepareReplaceEvent(index, previous, element);

        // Fire replace and change events
        fireReplaceNotifier(replaceEvent);
        fireChangeNotifier();

        return previous;
    }

    @Override
    public void clear() {

        // Prepare remove event
        TLRemoveEvent<T> removeEvent = prepareRemoveEvent(0, size());

        super.clear();

        // Fire remove, clear, and change events
        if(!removeEvent.getRemoved().isEmpty()) {
            fireRemoveNotifier(removeEvent);
            fireClearNotifier();
            fireChangeNotifier();
        }
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String toStringOriginal() {
        return getClass().getSimpleName() + "@" + hashCode();
        // Unfortunately the hashCode value does change as the objects
        // in the list changes, so it does not provide a good way
        // to visually identify the list.  However, it is not possible
        // to get the original Object.hashCode value without copying
        // multiple existing Java source files.
    }


    ////////////
    // HELPER //
    ////////////

    private TLAddEvent<T> prepareAddEvent(int start) {
        return prepareAddEvent(start, start + 1);
    }
    private TLAddEvent<T> prepareAddEvent(int start, int endNonIncl) {

        // Prepare add event
        Map<Integer, T> added = new LinkedHashMap<Integer, T>();
        for(int i = start; i < endNonIncl; i++) {
            T element = get(i);
            added.put(i, element);

            addElementChangeListenerToElement(i, element, ADD);
        }

        TLAddEvent<T> addEvent = new TLAddEvent<T>(this, added);
        return addEvent;
    }

    private TLRemoveEvent<T> prepareRemoveEvent(int start) {
        return prepareRemoveEvent(start, start + 1);
    }
    private TLRemoveEvent<T> prepareRemoveEvent(int start, int endNonIncl) {
        Map<Integer, T> removed = new LinkedHashMap<Integer, T>();
        for(int i = start; i < endNonIncl; i++) {
            T element = get(i);
            removed.put(i, element);
        }
        TLRemoveEvent<T> removeEvent = new TLRemoveEvent<T>(this, removed);
        return removeEvent;
    }

    private TLMoveEvent<T> prepareMoveEvent(int start, int delta) {

        // Prepare move event
        Map<Integer, T> moved = new LinkedHashMap<Integer, T>();
        Map<Integer, Integer> indexChanges = new LinkedHashMap<Integer, Integer>();
        for(int i = start; i < size(); i++) {
            moved.put(i, get(i));
            indexChanges.put(i, i - delta);

            // Update existing element change listeners' indices
            updateElementChangeListenerIndex(i);
        }

        TLMoveEvent<T> moveEvent = new TLMoveEvent<T>(this, moved, indexChanges);
        return moveEvent;
    }

    private TLReplaceEvent<T> prepareReplaceEvent(int index, T previous, T element) {
        removeElementChangeListenerFromElement(index, previous, REPL);
        addElementChangeListenerToElement(index, element, REPL);

        TLReplaceEvent<T> replaceEvent = new TLReplaceEvent<T>(this, index, previous, element);
        return replaceEvent;
    }


    ////////////////////////////
    // ELEMENT CHANGE HELPERS //
    ////////////////////////////

    protected void removeElementChangeListenerFromElement(int index, T element, int which) {
        if(trackElements) {
            if(element instanceof ChangeTrackable) {
                ElementChangeListener<T> listener = elementListeners.get(index);
                if(listener != null) {
                    if(listener.getIndex() != index) {
                        throw new IllegalStateException("Listener identifying itself as [" + listener.getIndex() + "] was found to be in position [" + index +  "]");
                    }
                    ((ChangeTrackable) element).removeChangeListener(listener);
                    listener.invalidate();
                }
            }
            if(which == REPL) {
                elementListeners.set(index, null);   // Technically unnecessary, as addECLTE does this again.
            } else {
                elementListeners.remove(index);
            }
        }
    }

    protected void addElementChangeListenerToElement(int index, T element, int which) {
        if(trackElements) {
            ElementChangeListener<T> listener = null;
            if(element instanceof ChangeTrackable) {
                listener =
                    new ElementChangeListener(this, index);
                ((ChangeTrackable) element).addChangeListener(listener);
            }

            if(which == REPL) {
                elementListeners.set(index, listener);

            } else {
                elementListeners.add(index, listener);
            }
        }
    }

    private void updateElementChangeListenerIndex(int i) {
        if(trackElements) {
            ElementChangeListener listener = elementListeners.get(i);
            if(listener != null) {
                listener.setIndex(i);
            }
        }
    }
}
