package replete.ui.nofire;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import replete.ui.tree.NodeBase;
import replete.ui.tree.RTree;


public class NoFireTree extends RTree {
    private boolean suppressFire = false;

    // This is only to be used when you change the model
    // and the event that the tree would get from the model
    // might change the selection in the tree.  Note that
    // if you change the structure of your DefaultMutableTreeNode
    // model manually, no events will fire (and you'd then have
    // to call something like JTree.updateUI to have the tree
    // update itself from the changed nodes.  It is suggested
    // that you change the tree's model via JTree.getModel().
    // You should use the *NoFire methods in all other
    // cases.
    public void setNoFireEnabled(boolean noFire) {
        suppressFire = noFire;
    }

    public NoFireTree() {
    }
    public NoFireTree(NodeBase root) {
        super(root);
    }
    public NoFireTree(Object[] arg0) {
        super(arg0);
    }
    public NoFireTree(Vector<?> arg0) {
        super(arg0);
    }
    public NoFireTree(Hashtable<?, ?> arg0) {
        super(arg0);
    }
    public NoFireTree(TreeNode arg0) {
        super(arg0);
    }
    public NoFireTree(TreeModel arg0) {
        super(arg0);
    }
    public NoFireTree(TreeNode arg0, boolean arg1) {
        super(arg0, arg1);
    }

    public void addSelectionPathNoFire(TreePath path) {
        suppressFire = true;
        super.addSelectionPath(path);
        suppressFire = false;
    }
    public void addSelectionPathsNoFire(TreePath[] paths) {
        suppressFire = true;
        super.addSelectionPaths(paths);
        suppressFire = false;
    }
    public void setSelectionPathNoFire(TreePath path) {
        suppressFire = true;
        super.setSelectionPath(path);
        suppressFire = false;
    }
    public void setSelectionPathsNoFire(TreePath[] paths) {
        suppressFire = true;
        super.setSelectionPaths(paths);
        suppressFire = false;
    }
    public void setSelectionIntervalNoFire(int index0, int index1) {
        suppressFire = true;
        super.setSelectionInterval(index0, index1);
        suppressFire = false;
    }
    public void addSelectionIntervalNoFire(int index0, int index1) {
        suppressFire = true;
        super.addSelectionInterval(index0, index1);
        suppressFire = false;
    }
    public void removeSelectionIntervalNoFire(int index0, int index1) {
        suppressFire = true;
        super.removeSelectionInterval(index0, index1);
        suppressFire = false;
    }
    public void removeSelectionPathNoFire(TreePath path) {
        suppressFire = true;
        super.removeSelectionPath(path);
        suppressFire = false;
    }
    public void removeSelectionPathsNoFire(TreePath[] paths) {
        suppressFire = true;
        super.removeSelectionPaths(paths);
        suppressFire = false;
    }
    public void removeSelectionRowNoFire(int row) {
        suppressFire = true;
        super.removeSelectionRow(row);
        suppressFire = false;
    }
    public void removeSelectionRowsNoFire(int[] rows) {
        suppressFire = true;
        super.removeSelectionRows(rows);
        suppressFire = false;
    }
    public void clearSelectionNoFire() {
        suppressFire = true;
        super.clearSelection();
        suppressFire = false;
    }

    @Override
    protected void fireValueChanged(TreeSelectionEvent e) {
        if(!suppressFire) {
            super.fireValueChanged(e);
        }
    }
}
