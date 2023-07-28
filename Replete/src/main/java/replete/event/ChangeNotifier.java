package replete.event;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import replete.ui.GuiUtil;

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

public class ChangeNotifier {


    ////////////
    // FIELDS //
    ////////////

    /** Event that will tell listeners where a notification came from. */
    protected ChangeEvent event;

    /** List of listeners desiring notification of changes. */
    protected List<ListenerGlob> listeners = new ArrayList<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    /**
     * Create a new ChangeNotifier object.
     *
     * @param source the object that will be included as the source in the
     *            ChangeEvent sent to all listeners.
     */
    public ChangeNotifier(Object source) {
        event = new ChangeEvent(source);
    }


    /////////////
    // METHODS //
    /////////////

    public Object getSource() {
        return event.getSource();
    }
    public void setSource(Object source) {
        event = new ChangeEvent(source);
    }
    public List<ListenerGlob> getListeners() {
        return listeners;
    }
    public List<ChangeListener> getListeners2() {
        List<ChangeListener> l = new ArrayList<>();
        for(ListenerGlob l2 : listeners) {
            l.add(l2.listener);
        }
        return l;
    }

    /**
     * Register a listener to be notified of changes.
     * Does nothing if the listener already exists
     * in the list.
     * @param listener the listener to add
     */
    public void addListener(ChangeListener listener) {
        addListener(listener, false, true);
    }
    public void addListener(ChangeListener listener, boolean useEDT, boolean edtSync) {
        addListener(listener, listeners.size(), useEDT, edtSync);
    }

    /**
     * Register a listener to be notified of changes.
     * Place the listener in the list at the
     * specified index.  Does nothing if the listener
     * already exists in the list.  Remove the
     * listener beforehand to reposition it.
     *
     * @param listener the listener to add
     * @param index
     *    the location in the list to add the listener. Values 0 or less put the listener at
     *    the beginning of the list. Values greater to or equal to one less than the number
     *    of listeners in the list will be placed at the end of the list.
     */
    public void addListener(ChangeListener listener, int index) {
        addListener(listener, index, false);
    }
    public void addListener(ChangeListener listener, int index, boolean useEDT) {
        addListener(listener, index, useEDT, true);
    }
    public void addListener(ChangeListener listener, int index, boolean useEDT, boolean edtSync) {
        if(!listeners.contains(new ListenerGlob(listener))) {
            if(index >= listeners.size()) {
                index = listeners.size() - 1;
            }
            if(index < 0) {
                index = 0;
            }
            ListenerGlob glob = new ListenerGlob(listener, useEDT, edtSync);
            listeners.add(index, glob);
        }
    }

    /**
     * Cease notifying a listener of changes.
     *
     * @param ls the listener to stop notifying
     */
    public void removeListener(ChangeListener ls) {
        listeners.remove(new ListenerGlob(ls));
    }

    public void removeAll() {
        listeners.clear();
    }

    /**
     * Get a modifiable list of all registered listeners.
     * This list can be rearranged to put certain listeners
     * in specific locations in the list, knowing that
     * listeners are notified starting with listener index 0.
     * This can also be useful for debugging - knowing what
     * listeners are currently registered.
     */
//    public List<ChangeListener> getListeners() {
//        return listeners;
//    }

    /**
     * Notify all listeners that are registered for notification.
     */
    public void fireStateChanged() {
        for(ListenerGlob glob : listeners) {
            final ListenerGlob finalGlob = glob;
            if(glob.useEDT) {
                Runnable runnable = new Runnable() {
                    public void run() {
                        finalGlob.listener.stateChanged(event);
                    }
                };
                if(glob.edtSync) {
                    GuiUtil.safeSync(runnable);
                } else {
                    GuiUtil.safe(runnable);
                }
            } else {
                finalGlob.listener.stateChanged(event);
            }
        }
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class ListenerGlob {
        public boolean useEDT;
        public boolean edtSync;
        public ChangeListener listener;
        public ListenerGlob(ChangeListener ls) {
            this(ls, false, false);
        }
        public ListenerGlob(ChangeListener ls, boolean useEDT, boolean edtSync) {
            listener = ls;
            this.useEDT = useEDT;
            this.edtSync = edtSync;
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((listener == null) ? 0 : listener.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if(this == obj) {
                return true;
            }
            if(obj == null) {
                return false;
            }
            if(getClass() != obj.getClass()) {
                return false;
            }
            ListenerGlob other = (ListenerGlob) obj;
            if(listener == null) {
                if(other.listener != null) {
                    return false;
                }
            } else if(!listener.equals(other.listener)) {
                return false;
            }
            return true;
        }
    }
}
