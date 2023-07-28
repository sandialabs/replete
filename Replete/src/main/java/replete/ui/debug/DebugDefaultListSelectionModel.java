package replete.ui.debug;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

/**
 * @author Derek Trumbo
 */

public class DebugDefaultListSelectionModel extends DebugListener implements ListSelectionModel {
    private DefaultListSelectionModel model = new DefaultListSelectionModel();

    public DebugDefaultListSelectionModel() {}
    public DebugDefaultListSelectionModel(String evNames) {
        super(evNames);
    }

    @Override
    public void setValueIsAdjusting(boolean valueIsAdjusting) {
        debug("setValueIsAdjusting", "" + valueIsAdjusting);
        model.setValueIsAdjusting(valueIsAdjusting);
    }
    @Override
    public void setSelectionMode(int selectionMode) {
        debug("setSelectionMode", "" + selectionMode);
        model.setSelectionMode(selectionMode);
    }
    @Override
    public void setSelectionInterval(int index0, int index1) {
        debug("setSelectionInterval", index0 + "-" + index1);
        model.setSelectionInterval(index0, index1);
    }
    @Override
    public void setLeadSelectionIndex(int index) {
        debug("setLeadSelectionIndex", "" + index);
        model.setLeadSelectionIndex(index);
    }
    @Override
    public void setAnchorSelectionIndex(int index) {
        debug("setAnchorSelectionIndex", "" + index);
        model.setAnchorSelectionIndex(index);
    }
    @Override
    public void removeSelectionInterval(int index0, int index1) {
        debug("removeSelectionInterval", index0 + "-" + index1);
        model.removeSelectionInterval(index0, index1);
    }
    @Override
    public void removeListSelectionListener(ListSelectionListener x) {
        debug("removeListSelectionListener", "" + x);
        model.removeListSelectionListener(x);
    }
    @Override
    public void removeIndexInterval(int index0, int index1) {
        debug("removeIndexInterval", index0 + "-" + index1);
        model.removeIndexInterval(index0, index1);
    }
    @Override
    public boolean isSelectionEmpty() {
        debug("isSelectionEmpty");
        return model.isSelectionEmpty();
    }
    @Override
    public boolean isSelectedIndex(int index) {
        debug("isSelectedIndex", "" + index);
        return model.isSelectedIndex(index);
    }
    @Override
    public void insertIndexInterval(int index, int length, boolean before) {
        debug("insertIndexInterval", index + " " + length + " " + before);
        model.insertIndexInterval(index, length, before);
    }
    @Override
    public boolean getValueIsAdjusting() {
        debug("getValueIsAdjusting");
        return model.getValueIsAdjusting();
    }
    @Override
    public int getSelectionMode() {
        debug("getSelectionMode");
        return model.getSelectionMode();
    }
    @Override
    public int getMinSelectionIndex() {
        debug("getMinSelectionIndex");
        return model.getMinSelectionIndex();
    }
    @Override
    public int getMaxSelectionIndex() {
        debug("getMaxSelectionIndex");
        return model.getMaxSelectionIndex();
    }
    @Override
    public int getLeadSelectionIndex() {
        debug("getLeadSelectionIndex");
        return model.getLeadSelectionIndex();
    }
    @Override
    public int getAnchorSelectionIndex() {
        debug("getAnchorSelectionIndex");
        return model.getAnchorSelectionIndex();
    }
    @Override
    public void clearSelection() {
        debug("clearSelection");
        model.clearSelection();
    }
    @Override
    public void addSelectionInterval(int index0, int index1) {
        debug("addSelectionInterval", index0 + "-" + index1);
        model.addSelectionInterval(index0, index1);
    }
    @Override
    public void addListSelectionListener(ListSelectionListener x) {
        debug("addListSelectionListener", "" + x);
        model.addListSelectionListener(x);
    }

    private void debug(String evName) {
        debug(evName, "");
    }
    private void debug(String evName, String rest) {
        if(!acceptEvent(evName)) {
            return;
        }

        String debugStr = evName + "{" + rest + "}";
        output(debugStr);
    }
}
