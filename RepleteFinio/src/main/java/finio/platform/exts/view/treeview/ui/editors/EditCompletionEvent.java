package finio.platform.exts.view.treeview.ui.editors;

import finio.platform.exts.view.treeview.ui.FNode;
import finio.platform.exts.view.treeview.ui.nodes.NodeFTree;

public class EditCompletionEvent {


    ////////////
    // FIELDS //
    ////////////

    private FNode nCurrent;
    private FNode nCurrentParent;
    private NodeFTree uNew;
    private Object Kcurrent;
    private Object Vcurrent;
    private boolean changesValue;

    boolean canceled = false;
    String kNew = null;
    String vNew = null;
    boolean noChange = false;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    // Core
    
    public FNode getnCurrent() {
        return nCurrent;
    }
    public FNode getnCurrentParent() {
        return nCurrentParent;
    }
    public NodeFTree getuNew() {
        return uNew;
    }
    public Object getKcurrent() {
        return Kcurrent;
    }
    public Object getVcurrent() {
        return Vcurrent;
    }
    public boolean isChangesValue() {
        return changesValue;
    }

    // Procedural
    
    public boolean isCanceled() {
        return canceled;
    }
    public String getkNew() {
        return kNew;
    }
    public String getvNew() {
        return vNew;
    }
    public boolean isNoChange() {
        return noChange;
    }

    // Mutators (Builder)
    
    // Core

    public EditCompletionEvent setnCurrent(FNode nCurrent) {
        this.nCurrent = nCurrent;
        return this;
    }
    public EditCompletionEvent setnCurrentParent(FNode nCurrentParent) {
        this.nCurrentParent = nCurrentParent;
        return this;
    }
    public EditCompletionEvent setuNew(NodeFTree uNew) {
        this.uNew = uNew;
        return this;
    }
    public EditCompletionEvent setKcurrent(Object kcurrent) {
        Kcurrent = kcurrent;
        return this;
    }
    public EditCompletionEvent setVcurrent(Object vcurrent) {
        Vcurrent = vcurrent;
        return this;
    }
    public EditCompletionEvent setChangesValue(boolean changesValue) {
        this.changesValue = changesValue;
        return this;
    }

    // Procedural
    
    public void cancel() {
        canceled = true;
    }
    public void setNewKey(String knew) {
        kNew = knew;
    }
    public void setNewValue(String vnew) {
        vNew = vnew;
    }
    public EditCompletionEvent setNoChange(boolean noChange) {
        this.noChange = noChange;
        return this;
    }
}
