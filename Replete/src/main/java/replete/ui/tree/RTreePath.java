package replete.ui.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.TreePath;

public class RTreePath extends TreePath implements Iterable<RTreeNode> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RTreePath() {
        super();
        init();
    }
    public RTreePath(Object singlePath) {
        super(singlePath);
        init();
    }
    public RTreePath(Object[] path, int length) {
        super(path, length);
        init();
    }
    public RTreePath(Object[] path) {
        super(path);
        init();
    }
    public RTreePath(TreePath parent, Object lastElement) {
        super(parent, lastElement);
        init();
    }
    public RTreePath(TreePath copy) {
        super(copy.getPath());
        init();
    }
    private void init() {
        tpathcount++;
    }
    public static int tpathcount = 0;


    ///////////////
    // ACCESSORS //
    ///////////////

    public RTreeNode getLast() {
        return (RTreeNode) getLastPathComponent();
    }
    public <T extends NodeBase> T getObject() {
        return (T) getLast().getUserObject();
    }
    public <T extends NodeBase> T get() {
        return getObject();
    }
    @Override
    public Iterator<RTreeNode> iterator() {
        return new SegmentIterator(null);
    }
    public int size() {
        return getPathCount();
    }
    public boolean type(Class<? extends NodeBase> clazz) {
        NodeBase n = get();
        if(n == null) {
            return false;
        }
        return clazz.isAssignableFrom(n.getClass());
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class SegmentIterator implements Iterator<RTreeNode> {
        private Class<? extends NodeBase> filter;
        private int position;
        private List<RTreeNode> validNodes = new ArrayList<>();
        public SegmentIterator(Class<? extends NodeBase> f) {
            filter = f;
            calculateValidNodes();
            if(validNodes.size() == 0) {
                position = -1;
            } else {
                position = 0;
            }
        }
        private void calculateValidNodes() {
            for(int i = 0; i < getPathCount(); i++) {
                RTreeNode nChild = (RTreeNode) getPathComponent(i);
                if(filter == null || filter.isAssignableFrom(nChild.getObject().getClass())) {
                    validNodes.add(nChild);
                }
            }
        }
        public boolean hasNext() {
            return position != -1 && position < validNodes.size();
        }
        public RTreeNode next() {
            if(position == -1) {
                return null;
            }
            return validNodes.get(position++);
        }
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove a segment using this iterator.");
        }
    }
}
