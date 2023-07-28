package replete.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This notifier keeps a map of lists of listeners.  The map
 * maps priority numbers to a list of listeners.  Each integer
 * priority number can have its own list of listeners.
 * Listeners are added to a specific priority list.  Listeners
 * can exist in multiple priority lists, but only one listener
 * is allowed to exist within a single priority list.  The
 * global integers FIRST, MIDDLE, and LAST are provided for
 * convenience but any integer can be used when adding and
 * removing listeners from the lists.  This notifier can be
 * used also in a one-priority-list environment, with special
 * add and remove methods operating solely on the MIDDLE list.
 * When the notifier is fired, the lists associated with
 * the smaller priority numbers are notified before those
 * associated with higher priority numbers.  The listeners
 * within a single list are notified in the order in which they
 * exist in the list (usually the order in which they were
 * added).   The idea is that the listeners that exist together
 * within a single priority list can be called in any order
 * with respect to each other, but that the order in which
 * the lists are notified is important.  This idea prevents
 * the need to have to reposition listeners within a single
 * priority list.
 *
 * This implements the Observer design pattern.
 * PriorityChangeNotifier is the ConcreteObservable class,
 * ChangeListener is the Observer interface, and the
 * Objects saved in list are the ConcreteObservers.
 *
 * @author Derek Trumbo
 */

public class PriorityChangeNotifier {

    ////////////
    // FIELDS //
    ////////////

    protected ChangeEvent event;

    public static final int FIRST = 0;
    public static final int MIDDLE = 50;
    public static final int LAST = 100;

    protected static Map<Integer, List<ChangeListener>> listeners =
        new TreeMap<Integer, List<ChangeListener>>();

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PriorityChangeNotifier(Object source) {
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

    public void addListener(ChangeListener listener) {
        addListener(listener, MIDDLE);
    }

    public void addListener(ChangeListener listener, int priority) {
        List<ChangeListener> list = listeners.get(priority);
        if(list == null) {
            list = new ArrayList<ChangeListener>();
            listeners.put(priority, list);
        }
        if(!list.contains(listener)) {
            list.add(listener);
        }
    }

    public void removeListener(ChangeListener listener) {
        removeListener(listener, MIDDLE);
    }
    public void removeListener(ChangeListener ls, int priority) {
        List<ChangeListener> list = listeners.get(priority);
        if(list != null) {
            list.remove(ls);
            if(list.size() == 0) {
                listeners.remove(priority);
            }
        }
    }

    public void removeAll() {
        listeners.clear();
    }

    public List<ChangeListener> getListeners(int priority) {
        return listeners.get(priority);
    }

    public void fireStateChanged() {
        for(int priority : listeners.keySet()) {
            for(ChangeListener listener : listeners.get(priority)) {
                listener.stateChanged(event);
            }
        }
    }

    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        PriorityChangeNotifier n = new PriorityChangeNotifier("Test");
        n.addListener(new Listener("MID 1"));
        n.addListener(new Listener("MID 2"));
        n.addListener(new Listener("MID 3"));
        n.addListener(new Listener("FIRST 1"), PriorityChangeNotifier.FIRST);
        n.addListener(new Listener("FIRST 2"), PriorityChangeNotifier.FIRST);
        n.addListener(new Listener("FIRST 3"), PriorityChangeNotifier.FIRST);
        n.addListener(new Listener("LAST 1"), PriorityChangeNotifier.LAST);
        n.addListener(new Listener("LAST 2"), PriorityChangeNotifier.LAST);
        n.addListener(new Listener("LAST 3"), PriorityChangeNotifier.LAST);
        n.addListener(new Listener("MID 4"), PriorityChangeNotifier.MIDDLE);
        n.addListener(new Listener("NEG"), -10);
        n.addListener(new Listener("LARGE"), 5000);
        n.fireStateChanged();
        System.out.println();
        List<ChangeListener> midListeners = n.getListeners(50);
        for(ChangeListener listener : midListeners) {
            System.out.println(listener);
        }
    }

    protected static class Listener implements ChangeListener {
        protected String name;
        public Listener(String n) {
            name = n;
        }
        public void stateChanged(ChangeEvent e) {
            System.out.println(this);
        }
        @Override
        public String toString() {
            return "Listener: " + name;
        }
    }
}
