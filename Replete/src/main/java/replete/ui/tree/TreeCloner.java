package replete.ui.tree;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Convenience method for performing a "deep copy" on
 * the data structure that generally backs a JTree.
 * The user object stored in the tree nodes must
 * be cloneable and the clone method must be public.
 *
 * @author Derek Trumbo
 */

public class TreeCloner {

    // Perform a deep copy an entire tree of DefaultMutableTreeNode structure.
    // The baseUserObjectClass must be Cloneable.
    // Throws an exception if the invocation of the clone method fails.
    public static DefaultMutableTreeNode deepTreeCopy(DefaultMutableTreeNode oldRoot)
        throws TreeClonerException {

        // Map from existing tree nodes to the new nodes cloned from them.
        Map<DefaultMutableTreeNode, DefaultMutableTreeNode> oldToNew = new HashMap<>(100);
        Enumeration<Object> enumerator = oldRoot.preorderEnumeration();
        DefaultMutableTreeNode newRoot = null;
        while(enumerator.hasMoreElements()) {

            // Get the next existing node and clone it.
            DefaultMutableTreeNode oldNode = (DefaultMutableTreeNode) enumerator.nextElement();
            DefaultMutableTreeNode newNode = (DefaultMutableTreeNode) oldNode.clone();

            // Get the node's user object, clone it, and set it to the new node.
            Object oldUserObject = oldNode.getUserObject();
            try {
                Method cloneMethod = oldNode.getUserObject().getClass().getMethod("clone");
                newNode.setUserObject(cloneMethod.invoke(oldUserObject));
            } catch(Exception e) {
                throw new TreeClonerException(e);
            }

            // If this is the first node in the loop, save it as the root.
            if(newRoot == null) {
                newRoot = newNode;
            }

            // Add the pair of nodes to the map.
            oldToNew.put(oldNode, newNode);

            // If this existing node has a parent, then the new node
            // needs to be added to that new parent node's children.
            // Because of the pre-order iteration, the parents will
            // have been cloned before their children, and in the
            // correct order.
            DefaultMutableTreeNode oldNodesParent = (DefaultMutableTreeNode) oldNode.getParent();
            if(oldNodesParent != null) {
                DefaultMutableTreeNode newNodesParent = oldToNew.get(oldNodesParent);
                newNodesParent.add(newNode);
            }
        }

        return newRoot;
    }

    //////////
    // TEST //
    //////////

    protected static class MyObj implements Cloneable {
        String text;
        public MyObj(String t) {
            text = t;
        }
        @Override
        public Object clone() {
            return new MyObj(text);
        }
        @Override
        public String toString() {
            return text;
        }
    }

    public static void main(String[] args) {
        DefaultMutableTreeNode node1 = new DefaultMutableTreeNode(new MyObj("Obj1"));
        DefaultMutableTreeNode node2 = new DefaultMutableTreeNode(new MyObj("Obj2"));
        DefaultMutableTreeNode node3 = new DefaultMutableTreeNode(new MyObj("Obj3"));
        DefaultMutableTreeNode node4 = new DefaultMutableTreeNode(new MyObj("Obj4"));

        node1.add(node2);
        node1.add(node3);
        node3.add(node4);

        System.out.println(node1.getUserObject() + " - " + node1.getUserObject().hashCode());
        System.out.println(node2.getUserObject() + " - " + node2.getUserObject().hashCode());
        System.out.println(node3.getUserObject() + " - " + node3.getUserObject().hashCode());
        System.out.println(node4.getUserObject() + " - " + node4.getUserObject().hashCode());
        System.out.println();

        try {
            DefaultMutableTreeNode copy1 = deepTreeCopy(node1);
            DefaultMutableTreeNode copy2 = (DefaultMutableTreeNode) copy1.getChildAt(0);
            DefaultMutableTreeNode copy3 = (DefaultMutableTreeNode) copy1.getChildAt(1);
            DefaultMutableTreeNode copy4 = (DefaultMutableTreeNode) copy3.getChildAt(0);

            System.out.println(copy1.getUserObject() + " - " + copy1.getUserObject().hashCode());
            System.out.println(copy2.getUserObject() + " - " + copy2.getUserObject().hashCode());
            System.out.println(copy3.getUserObject() + " - " + copy3.getUserObject().hashCode());
            System.out.println(copy4.getUserObject() + " - " + copy4.getUserObject().hashCode());

        } catch(TreeClonerException e) {
            e.printStackTrace();
        }
    }
}
