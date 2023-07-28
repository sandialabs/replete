package replete.ui.tree.state;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import replete.ui.tree.NodeBase;
import replete.ui.tree.RTree;
import replete.ui.tree.RTreeNode;


/**
 * This JTree allows for the saving and restoring of
 * visual state, i.e. the expanded and selected state
 * of every node in the tree.  It restores based
 * on the toString of the path for every node.  This
 * works as long as the tree does not have multiple
 * nodes with path toString's.  User objects in the
 * tree should be overriding toString anyway, as that
 * is the text that the JTree displays for each node.
 * An enhanced version of this class would use the
 * equals methods of the user objects to determine
 * if a given node should be expanded or selected.
 * However this forces the user objects to override
 * equals, a small downside.
 *
 * @author Derek Trumbo
 */

// NOTE: Code duplication with VisualStateSavingNoFireTree.

public class VisualStateSavingTree extends RTree {


    ////////////
    // FIELDS //
    ////////////

    // Could be an option to initialize here or in saveState.
    private Map<String, Boolean> expanded = new LinkedHashMap<>();
    private Map<String, Boolean> selected = new LinkedHashMap<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public VisualStateSavingTree() {
    }
    public VisualStateSavingTree(NodeBase root) {
        super(root);
    }
    public VisualStateSavingTree(Object[] value) {
        super(value);
    }
    public VisualStateSavingTree(Vector<?> value) {
        super(value);
    }
    public VisualStateSavingTree(Hashtable<?, ?> value) {
        super(value);
    }
    public VisualStateSavingTree(TreeNode root) {
        super(root);
    }
    public VisualStateSavingTree(TreeModel newModel) {
        super(newModel);
    }
    public VisualStateSavingTree(TreeNode root, boolean asksAllowsChildren) {
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
        if(expanded != null) {
            RTreeNode nRoot = (RTreeNode) getModel().getRoot();
            restoreState(nRoot);
        }
    }
    private void restoreState(RTreeNode node) {
        for(int i = 0; i < node.getChildCount(); i++) {
            restoreState((RTreeNode) node.getChildAt(i));
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
            addSelectionPath(path);
        } else {
            removeSelectionPath(path);
        }
    }
}
