package replete.event;

import java.util.ArrayList;
import java.util.List;

/**
 * This notifier keeps a list of ChangeListeners, and notifies them when its
 * fireStateChanged method is called.
 *
 * This implements the Observer design pattern. ChangeNotifier is the
 * ConcreteObservable class, ChangeListener is the Observer interface, and the
 * Objects saved in list are the ConcreteObservers.
 *
 * @author Derek Trumbo
 */

public class ExtChangeNotifier<T extends ExtChangeListener> {

    ////////////
    // FIELDS //
    ////////////

    /** List of listeners desiring notification of changes. */
    protected List<T> listeners = new ArrayList<T>();

    /////////////
    // METHODS //
    /////////////

    /**
     * Register a listener to be notified of changes.
     * Does nothing if the listener already exists
     * in the list.
     * @param ls the listener to add
     */
    public void addListener(T ls) {
        synchronized (listeners) {
            if(!listeners.contains(ls)) {
                listeners.add(ls);
            }
        }
    }

    /**
     * Register a listener to be notified of changes.
     * Place the listener in the list at the
     * specified index.  Does nothing if the listener
     * already exists in the list.  Remove the
     * listener beforehand to reposition it.
     *
     * @param ls the listener to add
     * @param index
     *    the location in the list to add the listener. Values 0 or less put the listener at
     *    the beginning of the list. Values greater to or equal to one less than the number
     *    of listeners in the list will be placed at the end of the list.
     */
    public void addListener(T ls, int index) {
        synchronized (listeners) {
            if(!listeners.contains(ls)) {
                if(index >= listeners.size()) {
                    index = listeners.size() - 1;
                }
                if(index < 0) {
                    index = 0;
                }
                listeners.add(index, ls);
            }
        }
    }

    /**
     * Cease notifying a listener of changes.
     *
     * @param ls the listener to stop notifying
     */
    public void removeListener(T ls) {
        synchronized (listeners) {
            listeners.remove(ls);
        }
    }

    public void removeAll() {
        synchronized (listeners) {
            listeners.clear();
        }
    }

    /**
     * Get a modifiable list of all registered listeners.
     * This list can be rearranged to put certain listeners
     * in specific locations in the list, knowing that
     * listeners are notified starting with listener index 0.
     * This can also be useful for debugging - knowing what
     * listeners are currently registered.
     */
    public List<T> getListeners() {
        List<T> copy;
        synchronized (listeners) {
            copy = new ArrayList<>(listeners);
        }
        return copy;
    }

    /**
     * Notify all listeners that are registered for notification.
     */
    public void fireStateChanged(Object event) {
        // Defensive copy - could be configurable, but this shouldn't
        // hurt any existing uses of the ExtChangeNotifier.
        for(T listener : getListeners()) {
            listener.stateChanged(event);
        }
    }
}
