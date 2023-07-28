package finio.ui.view;

import javax.swing.ImageIcon;
import javax.swing.event.ChangeListener;

import finio.core.KeyPath;
import finio.platform.exts.view.treeview.ui.FNode;
import finio.plugins.extpoints.View;
import finio.ui.app.AppContext;
import finio.ui.fpanel.FPanel;
import finio.ui.worlds.WorldContext;
import replete.event.ChangeNotifier;
import replete.event.rnotif.RChangeListener;
import replete.event.rnotif.RChangeNotifier;

public abstract class ViewPanel extends FPanel {


    ////////////
    // FIELDS //
    ////////////

    protected AppContext ac;
    protected WorldContext wc;
    protected Object K;           // Not that important but gives a little more context surrounding the V.
    protected Object V;           // This is the actual value being visualized by this view panel.
    protected View view;          // This is the plug-in that created this view panel (not sure if needed?)
    private KeyPath workingScope = KeyPath.KP();


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ViewPanel(AppContext ac, WorldContext wc, Object K, Object V, View view) {
        this.ac = ac;
        this.wc = wc;
        this.K = K;
        this.V = V;
        this.view = view;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getViewTypeName() {
        return view.getName();
    }
    public ImageIcon getViewTypeIcon() {
        return view.getIcon();
    }
    public View getView() {
        return view;
    }
    public KeyPath getWorkingScope() {
        return workingScope;
    }
    public Object getK() {
        return K;
    }
    public Object getV() {
        return V;
    }

    // Mutators

    public boolean setWorkingScope(KeyPath P) {
//        if(V.hasPath(P)) {
            workingScope = P;
            fireWorkingScopeNotifier();
//        }
        return true;
    }


    ///////////////
    // SELECTION //
    ///////////////

    public abstract SelectionContext[] getSelectedValues(int reverseDepth);
    public SelectionContext getSelectedValue() {
        return getSelectedValues(1)[0];
    }
    public SelectionContext getSelectedValue(int reverseDepth) {
        return getSelectedValues(reverseDepth)[0];
    }
    public SelectionContext[] getSelectionValues() {
        return getSelectedValues(1);
    }
    public void clearSelection() {}
    public void addSelection(SelectRequest selectionRequest) {}
    public void beginBulkSelection() {}
    public void endBulkSelection() {}


    ///////////////
    // EXPANSION //
    ///////////////

    public void addExpand(ExpandRequest expandRequest) {}
    public void expand(FNode node) {}
    public void expandToLevel(FNode node, int level) {}
    // public void addCollapse(CollapseRequest collapseRequest) {}


    /////////////
    // EDITING //
    /////////////

    public void startEditing() {}
    public void cancelEditing() {}
    public void setShiftForEditValue(boolean editShift) {}
    public SelectionContext[] getEditContextContexts() {return null;}


    //////////
    // MISC //
    //////////

    public void init() {}
    public void increaseFont() {}
    public void decreaseFont() {}
    public void refresh() {}


    ///////////////
    // NOTIFIERS //
    ///////////////

    public abstract void addSelectedListener(ChangeListener listner);
    public abstract void removeSelectedListener(ChangeListener listener);
    public abstract void addAnyActionListener(ChangeListener listener);

    private RChangeNotifier workingScopeNotifier = new RChangeNotifier();
    public void addWorkingScopeListener(RChangeListener listener) {
        workingScopeNotifier.addListener(listener);
    }
    public void removeWorkingScopeListener(RChangeListener listener) {
        workingScopeNotifier.removeListener(listener);
    }
    protected void fireWorkingScopeNotifier() {
        workingScopeNotifier.fire();
    }

    private ChangeNotifier selectedValuesNotifier = new ChangeNotifier(this);
    public void addSelectedValuesListener(ChangeListener listener) {
        selectedValuesNotifier.addListener(listener);
    }
    public void removeSelectedValuesListener(ChangeListener listener) {
        selectedValuesNotifier.removeListener(listener);
    }
    protected void fireSelectedValuesNotifier() {
        selectedValuesNotifier.fireStateChanged();
    }

    private ChangeNotifier editContextChangedNotifier = new ChangeNotifier(this);
    public void addEditContextChangedListener(ChangeListener listener) {
        editContextChangedNotifier.addListener(listener);
    }
    public void removeEditContextChangedListener(ChangeListener listener) {
        editContextChangedNotifier.removeListener(listener);
    }
    protected void fireEditContextChangedNotifier() {
        editContextChangedNotifier.fireStateChanged();
    }
}
