package replete.ui.nofire;

import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListModel;

public class NoFireList<T> extends JList<T> {
    private boolean suppressFire = false;

    // This is only to be used when you change the model
    // and the event that the list would get from the model
    // might change the selection in the list.
    // You should use the *NoFire methods in all other
    // cases.
    public void setNoFireEnabled(boolean noFire) {
        suppressFire = noFire;
    }

    public NoFireList() {}
    public NoFireList(ListModel<T> arg0) {
        super(arg0);
    }
    public NoFireList(T[] arg0) {
        super(arg0);
    }
    public NoFireList(Vector<T> arg0) {
        super(arg0);
    }

    public void addSelectionIntervalNoFire(int anchor, int lead) {
        suppressFire = true;
        super.addSelectionInterval(anchor, lead);
        suppressFire = false;
    }
    public void clearSelectionNoFire() {
        suppressFire = true;
        super.clearSelection();
        suppressFire = false;
    }
    public void removeSelectionIntervalNoFire(int anchor, int lead) {
        suppressFire = true;
        super.removeSelectionInterval(anchor, lead);
        suppressFire = false;
    }
    public void setSelectionIntervalNoFire(int anchor, int lead) {
        suppressFire = true;
        super.setSelectionInterval(anchor, lead);
        suppressFire = false;
    }
    public void setSelectedIndexNoFire(int index) {
        suppressFire = true;
        super.setSelectedIndex(index);
        suppressFire = false;
    }
    public void setSelectedIndicesNoFire(int[] indices) {
        suppressFire = true;
        super.setSelectedIndices(indices);
        suppressFire = false;
    }

    @Override
    protected void fireSelectionValueChanged(int firstIndex, int lastIndex, boolean isAdjusting) {
        if(!suppressFire) {
            super.fireSelectionValueChanged(firstIndex, lastIndex, isAdjusting);
        }
    }
}
