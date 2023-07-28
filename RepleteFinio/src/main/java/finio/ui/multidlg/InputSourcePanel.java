package finio.ui.multidlg;

import javax.swing.ImageIcon;
import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.ui.panels.RPanel;

public abstract class InputSourcePanel extends RPanel {


    //////////////
    // ABSTRACT //
    //////////////

    protected abstract void postActivate();
    protected abstract InputBundle[] getDataBundles();
    protected abstract void cleanUp();
    public abstract String getTitle();
    public abstract ImageIcon geIcon();


    ///////////////
    // NOTIFIERS //
    ///////////////

    private ChangeNotifier acceptNotifier = new ChangeNotifier(this);
    public void addAcceptListener(ChangeListener listener) {
        acceptNotifier.addListener(listener);
    }
    protected void fireAcceptNotifier() {
        acceptNotifier.fireStateChanged();
    }

    private ChangeNotifier cancelNotifier = new ChangeNotifier(this);
    public void addCancelListener(ChangeListener listener) {
        cancelNotifier.addListener(listener);
    }
    protected void fireCancelNotifier() {
        cancelNotifier.fireStateChanged();
    }
}
