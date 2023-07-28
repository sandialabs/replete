package finio.core.managed;

import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;

public abstract class AbstractManagedValueManager implements ManagedValueManager {

    public void set(Object V) {
    }


    //////////////
    // NOTIFIER //
    //////////////

    private ChangeNotifier changeNotifier = new ChangeNotifier(this);
    public void addChangeListener(ChangeListener listener) {
        changeNotifier.addListener(listener);
    }
    protected void fireChangeNotifier() {
        changeNotifier.fireStateChanged();
    }
}
