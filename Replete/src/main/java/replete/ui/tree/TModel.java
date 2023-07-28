package replete.ui.tree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import replete.util.ReflectionUtil;


public class TModel extends DefaultTreeModel {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    // Standard

    public TModel(RTreeNode root) {
        super(root);
    }
    public TModel(RTreeNode root, boolean asks) {
        super(root, asks);
    }

    // Copy

    public TModel(DefaultTreeModel source) {
        super(null);
        root = (TreeNode) source.getRoot();
        if(root == null) {
            root = new RTreeNode();
        }
        asksAllowsChildren = source.asksAllowsChildren();
        listenerList = ReflectionUtil.get(source, "listenerList");
    }


    ////////////
    // APPEND //
    ////////////

    public <T extends MutableTreeNode> T append(T nNewChild) {
        return append(getTRoot(), nNewChild);
    }
    public <T extends MutableTreeNode> T append(MutableTreeNode nParent, T nNewChild) {
        insertNodeInto(nNewChild, nParent, nParent.getChildCount());
        return nNewChild;
    }
    public RTreeNode append(Object uNewChild) {
        RTreeNode nNewChild = new RTreeNode(uNewChild);
        append(getTRoot(), nNewChild);
        return nNewChild;
    }
    public RTreeNode append(MutableTreeNode nParent, Object uNewChild) {
        RTreeNode nNewChild = new RTreeNode(uNewChild);
        insertNodeInto(nNewChild, nParent, nParent.getChildCount());
        return nNewChild;
    }


    ///////////////
    // ACCCESSOR //
    ///////////////

    public RTreeNode getTRoot() {
        return (RTreeNode) root;
    }


    ////////////
    // REMOVE //
    ////////////

    public void remove(MutableTreeNode nNode) {
        removeNodeFromParent(nNode);
    }
    public void remove(TreeNode nNode, Class<?> userObjectType) {
        for(int i = nNode.getChildCount() - 1; i >= 0; i--) {
            DefaultMutableTreeNode nChild =
                (DefaultMutableTreeNode) nNode.getChildAt(i);
            Object uObj = nChild.getUserObject();
            if(userObjectType.isAssignableFrom(uObj.getClass())) {
                remove(nChild);
            }
        }
    }
    public void removeAll(TreeNode nParent) {
        for(int i = nParent.getChildCount() - 1; i >= 0; i--) {
            MutableTreeNode nChild = (MutableTreeNode) nParent.getChildAt(i);
            remove(nChild);
        }
    }
}
