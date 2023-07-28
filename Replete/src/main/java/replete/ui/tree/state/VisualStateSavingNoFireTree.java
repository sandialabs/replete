package replete.ui.tree.state;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import replete.ui.nofire.NoFireTree;
import replete.ui.tree.NodeBase;
import replete.ui.tree.RTreeNode;


// NOTE: Code duplication with VisualStateSavingTree.

public class VisualStateSavingNoFireTree extends NoFireTree {


    ////////////
    // FIELDS //
    ////////////

    // Could be an option to initialize here or in saveState.
    private Map<String, Boolean> expanded = new LinkedHashMap<>();
    private Map<String, Boolean> selected = new LinkedHashMap<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public VisualStateSavingNoFireTree() {
    }
    public VisualStateSavingNoFireTree(NodeBase root) {
        super(root);
    }
    public VisualStateSavingNoFireTree(Object[] value) {
        super(value);
    }
    public VisualStateSavingNoFireTree(Vector<?> value) {
        super(value);
    }
    public VisualStateSavingNoFireTree(Hashtable<?, ?> value) {
        super(value);
    }
    public VisualStateSavingNoFireTree(TreeNode root) {
        super(root);
    }
    public VisualStateSavingNoFireTree(TreeModel newModel) {
        super(newModel);
    }
    public VisualStateSavingNoFireTree(TreeNode root, boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
    }


    //////////
    // SAVE //
    //////////

    public void saveState() {
        RTreeNode nRoot = (RTreeNode) getModel().getRoot();
        expanded.clear();
        selected.clear();
        saveState(nRoot);
    }
    private void saveState(RTreeNode node) {
        TreePath path = new TreePath(node.getPath());
        expanded.put(path.toString(), isExpanded(path));
        selected.put(path.toString(), isPathSelected(path));
        for(int i = 0; i < node.getChildCount(); i++) {
            saveState((RTreeNode) node.getChildAt(i));
        }
    }


    /////////////
    // RESTORE //
    /////////////

    public void restoreState() {
        if(expanded != null || selected != null) {
            if(selected != null) {
                clearSelection();
            }
            RTreeNode nRoot = (RTreeNode) getModel().getRoot();
            restoreState(nRoot);
        }
    }
    private void restoreState(RTreeNode node) {
        for(int i = 0; i < node.getChildCount(); i++) {
            restoreState((RTreeNode) node.getChildAt(i));
        }

        TreePath path = new TreePath(node.getPath());

        // Post-order traversal required because collapsePath
        // will EXPAND parent nodes!!
        if(expanded != null) {
            if(expanded.get(path.toString()) != null && expanded.get(path.toString())) {
                expandPath(path);
            } else {
                collapsePath(path);
            }
        }

        if(selected != null) {
            if(selected.get(path.toString()) != null && selected.get(path.toString())) {
                addSelectionPath(path);
            } else {
                removeSelectionPath(path);
            }
        }
    }
    public void restoreStateNoFire() {
        if(expanded != null) {
            RTreeNode nRoot = (RTreeNode) getModel().getRoot();
            restoreStateNoFire(nRoot);
        }
    }
    private void restoreStateNoFire(RTreeNode node) {
        for(int i = 0; i < node.getChildCount(); i++) {
            restoreStateNoFire((RTreeNode) node.getChildAt(i));
        }

        // Post-order traversal required because collapsePath
        // will EXPAND parent nodes!!
        TreePath path = new TreePath(node.getPath());
        if(expanded.get(path.toString()) != null && expanded.get(path.toString())) {
            expandPath(path);
        } else {
            collapsePath(path);
        }

        if(selected.get(path.toString()) != null && selected.get(path.toString())) {
            addSelectionPathNoFire(path);
        } else {
            removeSelectionPathNoFire(path);
        }
    }

    public static VisualStateSavingNoFireTree empty() {
        VisualStateSavingNoFireTree tre = new VisualStateSavingNoFireTree(new RTreeNode());
        tre.setRootVisible(false);
        return tre;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public Map<String, Boolean> getExpanded() {
        return expanded;
    }
    public Map<String, Boolean> getSelected() {
        return selected;
    }
}
