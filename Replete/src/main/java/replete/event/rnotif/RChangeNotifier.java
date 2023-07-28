package replete.event.rnotif;

import java.util.ArrayList;
import java.util.List;

public class RChangeNotifier {

    // TODO: It's possible we can have more options for when a remove
    // listener is called during a fire -- whether or not the fire should
    // be called on the listener that's going to be removed.


    ////////////
    // FIELDS //
    ////////////

    private Object source;
    private List<RChangeListenerConfig> listeners = new ArrayList<>();
    private RFireContext fireContext;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RChangeNotifier() {
    }
    public RChangeNotifier(Object source) {
        this.source = source;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    // Mutators

    public void addListener(RChangeListener listener) {
        addListener(new RChangeListenerConfig(listener));
    }
    public void addListener(RChangeListenerConfig listener) {
        if(fireContext != null) {
            fireContext.add(listener);
        } else {
            if(!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
    }
    public void removeListener(RChangeListener listener) {
        removeListener(new RChangeListenerConfig(listener));
    }
    public void removeListener(RChangeListenerConfig listener) {
        if(fireContext != null) {
            fireContext.remove(listener);
        } else {
            if(listeners.contains(listener)) {
                listeners.remove(listener);
            }
        }
    }

    public synchronized void fire() {
        if(fireContext != null) {
            throw new IllegalStateException("already have a fire context");
        }

        RChangeEvent event = new RChangeEvent(this, source);
        List<RChangeListenerConfig> listenerListCopy = new ArrayList<>(listeners);

        fireContext = new RFireContext(event, listenerListCopy);
        fireContext.fire();

        if(fireContext.getConfigToAdd() != null) {
            for(RChangeListenerConfig config : fireContext.getConfigToAdd()) {
                listeners.add(config);
            }
        }

        if(fireContext.getConfigToRemove() != null) {
            for(RChangeListenerConfig config : fireContext.getConfigToRemove()) {
                listeners.remove(config);
            }
        }

        fireContext = null;
    }
}
