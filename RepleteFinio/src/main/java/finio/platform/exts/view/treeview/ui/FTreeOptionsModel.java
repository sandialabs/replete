package finio.platform.exts.view.treeview.ui;

import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;

public class FTreeOptionsModel {


    ////////////
    // FIELDS //
    ////////////

    private boolean showSysMetaMaps = true;


    //////////////
    // NOTIFIER //
    //////////////

    private ChangeNotifier changeNotifier = new ChangeNotifier(this);
    public void addChangeListener(ChangeListener listener) {
        changeNotifier.addListener(listener);
    }
    private void fireChangeNotifier() {
        changeNotifier.fireStateChanged();
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isShowSysMetaMaps() {
        return showSysMetaMaps;
    }

    // Mutators

    public void setShowSysMetaMaps(boolean show) {
        showSysMetaMaps = show;
        fireChangeNotifier();
    }
}
