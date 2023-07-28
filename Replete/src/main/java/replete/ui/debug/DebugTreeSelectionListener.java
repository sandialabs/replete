package replete.ui.debug;

import java.util.Arrays;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Derek Trumbo
 */

public class DebugTreeSelectionListener extends DebugListener implements TreeSelectionListener {

    public DebugTreeSelectionListener() {}
    public DebugTreeSelectionListener(String evNames) {
        super(evNames);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        debug("valueChanged", e);
    }

    private void debug(String evName, TreeSelectionEvent e) {
        if(!acceptEvent(evName)) {
            return;
        }

        JTree tree = (JTree) e.getSource();
        String debugStr = "== TreeSelectionListener ==\n";
        debugStr += "T.getAnchorSelectionPath=" + tree.getAnchorSelectionPath() + "\n";
        debugStr += "T.getLastSelectedPathComponent=" + tree.getLastSelectedPathComponent() + "\n";
        if (tree.getLastSelectedPathComponent() != null) {
            DefaultMutableTreeNode lastSelPathComp = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            debugStr += "T.LSPC.getUserObject="+ lastSelPathComp.getUserObject() + "\n";
            debugStr += "T.LSPC.getUserObjectPath=" + Arrays.toString(lastSelPathComp.getUserObjectPath()) + "\n";
        }
        debugStr += "T.getLeadSelectionPath=" + tree.getLeadSelectionPath() + "\n";
        debugStr += "T.getLeadSelectionRow=" + tree.getLeadSelectionRow() + "\n";
        debugStr += "T.getMaxSelectionRow=" + tree.getMaxSelectionRow() + "\n";
        debugStr += "T.getMinSelectionRow=" + tree.getMinSelectionRow() + "\n";
        debugStr += "T.getSelectionCount=" + tree.getSelectionCount() + "\n";
        debugStr += "T.getSelectionPath=" + tree.getSelectionPath() + "\n";     // path to the first selected node
        debugStr += "T.getSelectionPaths=" + Arrays.toString(tree.getSelectionPaths()) + "\n";  // order in which the user selected the objects
        debugStr += "T.getSelectionRows=" + Arrays.toString(tree.getSelectionRows()) + "\n";    // order in which the user selected the objects
        debugStr += "T.isSelectionEmpty=" + tree.isSelectionEmpty() + "\n";
        debugStr += "E.getNewLeadSelectionPath=" + e.getNewLeadSelectionPath() + "\n";
        debugStr += "E.getOldLeadSelectionPath=" + e.getOldLeadSelectionPath() + "\n";
        debugStr += "E.getPath=" + e.getPath() + "\n";
        debugStr += "E.getPaths=" + Arrays.toString(e.getPaths()) + "\n";
        debugStr += "E.isAddedPath=" + e.isAddedPath() + "\n";
        debugStr += "===========================\n";

        output(debugStr);
    }
}
