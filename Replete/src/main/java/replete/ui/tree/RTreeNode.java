package replete.ui.tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import replete.text.StringUtil;

// The RTreeNode extends on DefaultMutableTreeNode.  Not
// only is it much easier to type, it provides several
// convenience methods for simpler tree code.

public class RTreeNode extends DefaultMutableTreeNode implements Iterable<RTreeNode> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RTreeNode() {
    }
    public RTreeNode(Object userObject) {
        super(userObject);
    }
    public RTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }


    //////////
    // MISC //
    //////////

    public boolean moveUp() {
        return moveUp(null);
    }
    public boolean moveUp(Class<? extends NodeBase> uGroup) {
        if(parent == null) {
            return false;
        }
        int index = parent.getIndex(this);
        if(index == 0) {
            return false;
        }
        RTreeNode nParent = (RTreeNode) parent;
        if(uGroup != null && !nParent.getRChildBefore(this).type(uGroup)) {
            return false;
        }
        nParent.swap(index, index - 1);
        return true;
    }

    public boolean moveDown() {
        return moveDown(null);
    }
    public boolean moveDown(Class<? extends NodeBase> uGroup) {
        if(parent == null) {
            return false;
        }
        int index = parent.getIndex(this);
        if(index == parent.getChildCount() - 1) {
            return false;
        }
        RTreeNode nParent = (RTreeNode) parent;
        if(uGroup != null && !nParent.getRChildAfter(this).type(uGroup)) {
            return false;
        }
        nParent.swap(index, index + 1);
        return true;
    }

    public void swap(int index0, int index1) {
        if(index0 < 0 || index0 >= getChildCount()) {
            throw new IllegalArgumentException("Invalid swap indices");
        }
        if(index1 < 0 || index1 >= getChildCount()) {
            throw new IllegalArgumentException("Invalid swap indices");
        }
        TreeNode child0 = getChildAt(index0);
        TreeNode child1 = getChildAt(index1);

        children.setElementAt(child1, index0);
        children.setElementAt(child0, index1);
    }

    // Didn't exist in base class.  Implementation modified version of removeAt.
    public void setChildAt(MutableTreeNode newChild, int childIndex) {
        if(!allowsChildren) {
            throw new IllegalStateException("node does not allow children");
        } else if(newChild == null) {
            throw new IllegalArgumentException("new child is null");
        } else if(isNodeAncestor(newChild)) {
            throw new IllegalArgumentException("new child is an ancestor");
        } else if(childIndex < 0 || childIndex >= getChildCount()) {
            throw new IllegalArgumentException("invalid set index");
        }

        MutableTreeNode oldNode = (MutableTreeNode) getChildAt(childIndex);
        if(newChild == oldNode) {
            return;     // Already there!
        }

        MutableTreeNode oldParent = (MutableTreeNode) newChild.getParent();
        if(oldParent != null) {
            oldParent.remove(newChild);
        }
        newChild.setParent(this);

        if(children == null) {
            children = new Vector();    // Not necessary in this method
        }
        children.setElementAt(newChild, childIndex);

        oldNode.setParent(null);
    }

    public boolean type(Class... classes) {
        Object uObject = getObject();
        if(uObject == null) {
            return false;
        }
        for(Class clazz : classes) {
            if(clazz.isAssignableFrom(uObject.getClass())) {
                return true;
            }
        }
        return false;
    }
    public boolean ptype(Class... classes) {
        RTreeNode p = getRParent();
        if(p == null) {
            return false;
        }
        Object uObject = p.getObject();
        if(uObject == null) {
            return false;
        }
        for(Class clazz : classes) {
            if(clazz.isAssignableFrom(uObject.getClass())) {
                return true;
            }
        }
        return false;
    }

    public RTreeNode getParent(Class<? extends NodeBase> findParent) {
        RTreeNode cur = this;
        while(cur != null &&
                (cur.getUserObject() == null || !findParent.isAssignableFrom(cur.getUserObject().getClass()))) {
            cur = (RTreeNode) cur.getParent();
        }
        return cur;
    }

    public <T extends NodeBase> T getParentObject(Class<T> findParent) {
        RTreeNode nParent = getParent(findParent);
        if(nParent != null) {
            return (T) nParent.getUserObject();
        }
        return null;
    }

    public RTreeNode getRChild(String text) {
        for(int i = 0; i < getChildCount(); i++) {
            RTreeNode child = getRChildAt(i);
            if(text.equals(child.getUserObject().toString())) {
                return child;
            }
        }
        return null;
    }

    public <T extends NodeBase> RTreeNode add(T uObject) {
        RTreeNode nNew = new RTreeNode(uObject);
        add(nNew);
        return nNew;
    }

    public <T extends NodeBase> RTreeNode insert(T uObject, int index) {
        RTreeNode nNew = new RTreeNode(uObject);
        insert(nNew, index);
        return nNew;
    }

    public void sortChildren(Comparator<RTreeNode> comparator) {
        List<RTreeNode> childNodes = new ArrayList<>();
        for(RTreeNode childNode : getRChildren()) {
            childNodes.add(childNode);
        }
        childNodes.sort(comparator);
        removeAllChildren();
        childNodes.stream().forEach(c -> add(c));
    }

    public RTreeNode getRChildAt(int index) {
        return (RTreeNode) getChildAt(index);
    }

    public RTreeNode getRParent() {
        return (RTreeNode) getParent();
    }
    public TreeNode[] getPathSegments() {
        return super.getPath();
    }
    public RTreeNode[] getRPathSegments() {
        TreeNode[] path = getPath();
        RTreeNode[] tpath = new RTreeNode[path.length];
        System.arraycopy(path, 0, tpath, 0, path.length);
        return tpath;
    }
    public RTreePath getRPath() {
        return new RTreePath(getRPathSegments());
    }

    public <T extends NodeBase> T getObjectAt(int index) {
        return (T) ((DefaultMutableTreeNode) getChildAt(index)).getUserObject();
    }

    public <T extends NodeBase> T getObject() {
        return (T) getUserObject();
    }
    public <T extends NodeBase> T get() {
        return (T) getUserObject();
    }


    public RTreeNode getRChild(RTreeNode nFind) {
        for(RTreeNode nChild : getRChildren()) {
            if(nChild == nFind) {
                return nChild;
            }
        }
        return null;
    }
    public boolean contains(RTreeNode nFind) {
        for(RTreeNode nChild : getRChildren()) {
            if(nChild == nFind) {
                return true;
            }
        }
        return false;
    }
    public boolean hasChildren() {
        return getCount() != 0;
    }
    public int getCount() {
        return getChildCount();
    }
    public int getCountAll() {
        int sz = getCount();
        for(RTreeNode nChild : this) {
            sz += nChild.getCountAll();
        }
        return sz;
    }
    public Iterable<RTreeNode> getRChildren() {
        return getRChildren(null);
    }
    public Iterable<RTreeNode> getRChildren(final Class<? extends NodeBase> filter) {
        return new Iterable<RTreeNode>() {
            @Override
            public Iterator<RTreeNode> iterator() {
                return new ChildIterator(RTreeNode.this, filter, null);
            }
        };
    }
    @Override
    public Iterator<RTreeNode> iterator() {
        return new ChildIterator(this, null, null);
    }
    public RTreeNode getRChildAfter(RTreeNode n) {
        return (RTreeNode) getChildAfter(n);
    }
    public RTreeNode getRChildBefore(RTreeNode n) {
        return (RTreeNode) getChildBefore(n);
    }
    public RTreeNode getRNextSibling() {
        return (RTreeNode) super.getNextSibling();
    }
    public RTreeNode getRPreviousSibling() {
        return (RTreeNode) super.getPreviousSibling();
    }
    public RTreeNode getRFirstChild() {
        return (RTreeNode) super.getFirstChild();
    }
    public RTreeNode getRLastChild() {
        return (RTreeNode) super.getLastChild();
    }
    public Iterable<RTreeNode> getRSiblings() {
        return getRSiblings(null);
    }
    public Iterable<RTreeNode> getRSiblings(final Class<? extends NodeBase> filter) {
        return new Iterable<RTreeNode>() {
            @Override
            public Iterator<RTreeNode> iterator() {
                return new ChildIterator(getRParent(), filter, RTreeNode.this);
            }
        };
    }
    public void print() {
        print(this, 0);
    }
    private void print(RTreeNode nParent, int level) {
        String sp = StringUtil.spaces(4 * level);
        String ts = nParent.toString();
        String ex = getUserObject() != null ? " (" + nParent.getUserObject().getClass().getSimpleName() + ")" : "";
        System.out.println(sp + ts + ex);
        for(RTreeNode nChild : nParent.getRChildren()) {
            print(nChild, level + 1);
        }
    }

    private class ChildIterator implements Iterator<RTreeNode> {
        private Class<? extends NodeBase> filter;
        private int position;
        private List<RTreeNode> validNodes = new ArrayList<>();
        public ChildIterator(RTreeNode nContext, Class<? extends NodeBase> f, RTreeNode nSkip) {
            filter = f;
            calculateValidNodes(nContext, nSkip);
            if(validNodes.size() == 0) {
                position = -1;
            } else {
                position = 0;
            }
        }
        private void calculateValidNodes(RTreeNode context, RTreeNode nSkip) {
            for(int i = 0; i < context.getChildCount(); i++) {
                RTreeNode nChild = context.getRChildAt(i);
                if(nSkip == null || nChild != nSkip) {
                    if(filter == null || filter.isAssignableFrom(nChild.getObject().getClass())) {
                        validNodes.add(nChild);
                    }
                }
            }
        }
        @Override
        public boolean hasNext() {
            return position != -1 && position < validNodes.size();
        }
        @Override
        public RTreeNode next() {
            if(position == -1) {
                return null;
            }
            return validNodes.get(position++);
        }
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove a child using this iterator.");
        }
    }


    public boolean exists(RTreePath path) {
        return exists(path, 1);
    }
    private boolean exists(RTreePath path, int l) {
        if(l == path.getPathCount()) {
            return true;
        }
        RTreeNode S = (RTreeNode) path.getPath()[l];
        RTreeNode nChild = getRChild(S);
        if(nChild == null) {
            return false;
        }
        if(!nChild.exists(path, l + 1)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String u = getUserObject() == null ? "null" : getUserObject().getClass().getSimpleName() + "<" + getUserObject() + ">";
        return getClass().getSimpleName() + "[" + u + "] " + getChildCount() + " child" + (getChildCount()==1?"":"ren");
    }
    public int indexBefore(Class<? extends NodeBase> userObjectClass) {
        int i = 0;
        while(i < getCount() && !(userObjectClass.isAssignableFrom(getObjectAt(i).getClass()))) {
            i++;
        }
        return i;
    }

    public void removeAll(Class<?> userObjectClass) {
        for(int i = getCount() - 1; i >= 0; i--) {
            DefaultMutableTreeNode nChild = (DefaultMutableTreeNode) getChildAt(i);
            Object uObj = nChild.getUserObject();
            if(uObj != null && userObjectClass.isAssignableFrom(uObj.getClass())) {
                remove(i);
            }
        }
    }
}
