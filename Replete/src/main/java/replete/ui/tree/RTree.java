package replete.ui.tree;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.function.Function;

import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import replete.errors.UnexpectedEnumValueUnicornException;
import replete.event.ChangeNotifier;
import replete.ui.SelectionStateCreationMethod;
import replete.ui.SelectionStateSavable;
import replete.ui.drag.MouseDragHelped;
import replete.ui.drag.MouseDragHelper;
import replete.ui.drag.RectangleIterator;
import replete.ui.lay.Lay;
import replete.ui.panels.SelectionState;
import replete.ui.windows.escape.EscapeFrame;
import replete.util.ReflectionUtil;


// TODO: more desires for a better JTree:
// 2.  when nodes are added, their isCollapsible
//     method should be inspected to decide initial
//     expanded state.

public class RTree extends JTree implements MouseDragHelped, SelectionStateSavable {


    ///////////
    // ENUMS //
    ///////////

    public enum TreeSelectionStateIdentityMethod implements SelectionStateCreationMethod {
        PATH_TO_STRING,                // (Default) Uses selected paths' toString as key
        NODE_TO_STRING,                // Uses selected nodes' toString as key
        USER_OBJECT_HASH_CODE,         // Uses selected nodes' user objects' hash codes as key
        USER_OBJECT_WRAPPED_HASH_CODE, // Uses selected nodes' user objects' wrapped objects' hash code as key
        CUSTOM                         // Function<TreePath, Object> as 2nd argument = customIdentityFunction
    }


    ////////////
    // FIELDS //
    ////////////

    private static int expandAllLimiterCount;
    private MouseDragHelper helper = new MouseDragHelper(this);
    private boolean rootCollapsible = true;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    // New //

    public RTree(NodeBase uRoot) {
        RTreeNode nRoot = new RTreeNode(uRoot);
        setModel(nRoot);
        init();
    }

    // Standard //

    public RTree() {
        super();
        init();
    }
    public RTree(Object[] value) {
        super(value);
        init();
        ReflectionUtil.invoke(this, "expandRoot");
    }
    public RTree(Vector<?> value) {
        super(value);
        init();
        ReflectionUtil.invoke(this, "expandRoot");
    }
    public RTree(Hashtable<?, ?> value) {
        super(value);
        init();
        ReflectionUtil.invoke(this, "expandRoot");
    }
    public RTree(TreeNode root) {
        super(root);
        init();
    }
    public RTree(TreeModel newModel) {
        super(newModel);
        init();
    }
    public RTree(TreeNode root, boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
        init();
    }

    private void init() {
        setMouseDragSelection(true);
        setModel(new TModel(getModel()));
        setCellRenderer(createRenderer());
        addTreeWillExpandListener(treeWillExpandListener);

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getModel().getRoot();
        Object uObj = root.getUserObject();
        if(uObj instanceof NodeBase) {
            NodeBase uBase = (NodeBase) uObj;
            if(!uBase.isCollapsible()) {
                expandPath(new TreePath(root));
            }
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean empty = (getPathForLocation(e.getX(), e.getY()) == null);
                if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    if(empty) {
                        fireDoubleClickEmptyNotifier();
                    } else {
                        fireDoubleClickNotifier();
                    }
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_DELETE) {
                    fireDeleteKeyNotifier();
                } else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    fireEnterKeyNotifier();
                } else if(e.getKeyCode() == KeyEvent.VK_F5) {
                    updateUI();
                }
            }
        });
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isRootCollapsible() {
        return rootCollapsible;
    }

    // Mutators

    public RTree setRootCollapsible(boolean rootCollapsible) {
        this.rootCollapsible = rootCollapsible;
        return this;
    }

    public static RTree empty() {
        RTree tre = new RTree(new RTreeNode());
        tre.setRootVisible(false);
        return tre;
    }

    public void clear() {
        setModel(new RTreeNode());
        setRootVisible(false);
    }

    protected TreeCellRenderer createRenderer() {
        return new NodeBaseTreeRenderer();
    }

    /* Unfortunately there is a lot of code that
     * does not access the tree's "TreeModel" at all
     * but rather accesses nRoot node tree directly.
     * TODO: Would be nice if you could instrument a
     * RTreeNode tree to make calls to the TreeModel for
     * the tree they are in.  When only RTreeNode objects
     * are edited, then only a repaint will look through
     * the model and repaint the entire thing (usually
     * caused through updateUI at this point).
     */
    /*private TreeModelListener expandListener = new TreeModelListener() {
        public void treeStructureChanged(TreeModelEvent e) {
            System.out.println(e.getPath().getClass());
            for(Object o : e.getPath()) {
                System.out.println(o.getClass());
            }
        }
        public void treeNodesRemoved(TreeModelEvent e) {
            System.out.println(e.getPath().getClass());
            for(Object o : e.getPath()) {
                System.out.println(o.getClass());
            }
        }
        public void treeNodesInserted(TreeModelEvent e) {
            System.out.println(e.getPath().getClass());
            for(Object o : e.getPath()) {
                System.out.println(o.getClass());
            }
        }
        public void treeNodesChanged(TreeModelEvent e) {
            System.out.println(e.getPath().getClass());
            for(Object o : e.getPath()) {
                System.out.println(o.getClass());
            }
        }
    };*/


    ///////////////
    // NOTIFIERS //
    ///////////////

    protected ChangeNotifier doubleClickEmptyNotifier = new ChangeNotifier(this);
    public void addDoubleClickEmptyListener(ChangeListener listener) {
        doubleClickEmptyNotifier.addListener(listener);
    }
    protected void fireDoubleClickEmptyNotifier() {
        doubleClickEmptyNotifier.fireStateChanged();
    }

    protected ChangeNotifier doubleClickNotifier = new ChangeNotifier(this);
    public void addDoubleClickListener(ChangeListener listener) {
        doubleClickNotifier.addListener(listener);
    }
    protected void fireDoubleClickNotifier() {
        doubleClickNotifier.fireStateChanged();
    }

    protected ChangeNotifier deleteKeyNotifier = new ChangeNotifier(this);
    public void addDeleteKeyListener(ChangeListener listener) {
        deleteKeyNotifier.addListener(listener);
    }
    protected void fireDeleteKeyNotifier() {
        deleteKeyNotifier.fireStateChanged();
    }

    protected ChangeNotifier enterKeyNotifier = new ChangeNotifier(this);
    public void addEnterKeyListener(ChangeListener listener) {
        enterKeyNotifier.addListener(listener);
    }
    protected void fireEnterKeyNotifier() {
        enterKeyNotifier.fireStateChanged();
    }


    //////////
    // MISC //
    //////////

    public void doEdit() {
        doEdit(getSelectionPath());
    }
    public void doEdit(TreePath path) {
        startEditingAtPath(path);
    }

    public void update() {
        ((TModel) getModel()).reload();
        updateUI();
    }

    public void addSelectionListener(TreeSelectionListener listener) {
        getSelectionModel().addTreeSelectionListener(listener);
    }

    // Convenience methods

    public void setModel(RTreeNode nRoot) {
        setModel(new TModel(nRoot, false));
    }

    public TModel getTModel() {
        return (TModel) super.getModel();
    }
    @Override
    public DefaultTreeModel getModel() {
        return (DefaultTreeModel) super.getModel();
    }
    public RTreeNode getRoot() {
        return (RTreeNode) super.getModel().getRoot();
    }
    public NodeBase getRootObject() {
        return (NodeBase) ((RTreeNode) super.getModel().getRoot()).getUserObject();
    }
    public void append(MutableTreeNode nNew) {
        getTModel().append(nNew);
    }
    public void append(MutableTreeNode nParent, MutableTreeNode nChild) {
        getTModel().append(nParent, nChild);
    }
    public RTreePath getTPathForLocation(int x, int y) {
        return tpathOrNull(super.getPathForLocation(x, y));
    }
    public RTreePath getTPathForRow(int row) {
        return tpathOrNull(super.getPathForRow(row));
    }
    public RTreePath tpathOrNull(TreePath path) {
        return path == null ? null : new RTreePath(path);
    }
    public RTreePath[] getTSelectionPaths() {
        TreePath[] paths = super.getSelectionPaths();
        if(paths == null) {
            return null;
        }
        RTreePath[] paths2 = new RTreePath[paths.length];
        int p = 0;
        for(TreePath path : paths) {
            paths2[p++] = new RTreePath(path);
        }
        return paths2;
    }
    public RTreePath getTLeadSelectionPath() {
        return tpathOrNull(getLeadSelectionPath());
    }
    public RTreePath getTSelectionPath() {
        return tpathOrNull(super.getSelectionPath());
    }
    public RTreeNode getTSelectionNode() {
        RTreePath path = getTSelectionPath();
        if(path != null) {
            return path.getLast();
        }
        return null;
    }
    public RTreeNode[] getTSelectionNodes() {
        TreePath[] paths = super.getSelectionPaths();
        if(paths == null) {
            return null;
        }
        RTreeNode[] nodes = new RTreeNode[paths.length];
        int p = 0;
        for(TreePath path : paths) {
            nodes[p++] = (RTreeNode) path.getLastPathComponent();
        }
        return nodes;
    }
    public <T extends NodeBase> T getTSelectionObject() {
        RTreeNode n = getTSelectionNode();
        if(n != null) {
            return n.getObject();  // (NodeBase)
        }
        return null;
    }
    public Object[] getTSelectionObjects() {
        TreePath[] paths = super.getSelectionPaths();
        if(paths == null) {
            return null;
        }
        Object[] objs = new Object[paths.length];
        int p = 0;
        for(TreePath path : paths) {
            objs[p++] = ((RTreeNode) path.getLastPathComponent()).getUserObject();
        }
        return objs;
    }
    public <T extends NodeBase> RTreePath getTSelectionPath(Class<T> uClass) {
        TreePath p0 = super.getSelectionPath();
        if(p0 != null) {
            RTreePath p = new RTreePath(p0);
            RTreeNode n = p.getLast();
            T u = n.getObject();  // (NodeBase)
            if(uClass.isAssignableFrom(u.getClass())) {
                return p;
            }
        }
        return null;
    }
    public <T extends NodeBase> RTreeNode getTSelectionNode(Class<T> uClass) {
        TreePath p0 = super.getSelectionPath();
        if(p0 != null) {
            RTreePath p = new RTreePath(p0);
            RTreeNode n = p.getLast();
            T u = n.getObject();  // (NodeBase)
            if(uClass.isAssignableFrom(u.getClass())) {
                return n;
            }
        }
        return null;
    }
    public <T extends NodeBase> T getTSelectionObject(Class<T> uClass) {
        TreePath p0 = super.getSelectionPath();
        if(p0 != null) {
            RTreePath p = new RTreePath(p0);
            RTreeNode n = p.getLast();
            T u = n.getObject();  // (NodeBase)
            if(uClass.isAssignableFrom(u.getClass())) {
                return u;
            }
        }
        return null;
    }
    public <T extends NodeBase> RTreeNode getTSelectionNodeClimb(Class<T> uStopClass) {
        return getTSelectionNodeClimb(uStopClass, false);
    }
    public <T extends NodeBase> RTreeNode getTSelectionNodeClimb(Class<T> uStopClass, boolean allowRoot) {
        TreePath p0 = super.getSelectionPath();
        if(p0 != null) {
            RTreePath p = new RTreePath(p0);
            RTreeNode n = p.getLast();
            return getContextRoot(uStopClass, n, allowRoot);
        }
        return null;
    }

    // uStopClass == null && allowRoot returns root in all cases.
    public <T extends NodeBase> RTreeNode getContextRoot(Class<T> uStopClass, RTreeNode nSel, boolean allowRoot) {
        RTreeNode chosenNode = null;
        RTreeNode n = nSel;
        RTreeNode p = null;
        while(n != null) {
            Class<?> uClass = n.getUserObject().getClass();
            if(uStopClass != null && uStopClass.isAssignableFrom(uClass)) {
                chosenNode = n;
                break;
            }
            p = n;
            n = n.getRParent();
        }
        if(chosenNode == null && allowRoot) {
            chosenNode = p;
        }
        return chosenNode;
    }


    //TODO selection paths
    public void remove(MutableTreeNode nNode) {
        DefaultMutableTreeNode sib = ((DefaultMutableTreeNode) nNode).getNextSibling();
        if(sib == null) {
            sib = ((DefaultMutableTreeNode) nNode).getPreviousSibling();
        }
        if(sib == null) {
            sib = (DefaultMutableTreeNode) nNode.getParent();
        }
        Object sel = getSelectionPath().getLastPathComponent();
        getTModel().remove(nNode);
        if(sel == nNode) {
            select(sib);   // TODO: Always expand and scroll as well?
        }
        updateUI();
    }

    public void select(TreeNode nNode) {
        if(nNode.getChildCount() != 0) {
            /* If there's flicker by jumping twice,
             * you can use getPathBounds to calculate the desired
             * visible rectangle, taking into consideration
             * the child's rectangle and then just making
             * a single call to scrollRectToVisible.
             * Rectangle childBounds = getPathBounds(getPath(nNode));
             */
            TreeNode nLastChild = nNode.getChildAt(nNode.getChildCount() - 1);
            TreePath path = getPath(nLastChild);
            setSelectionPath(path);
            scrollPathToVisible(path);
        }
        TreePath path = getPath(nNode);
        expandPath(path);
        setSelectionPath(path);
        scrollPathToVisible(path);
        updateUI();
    }

    public RTreeNode getSelNode() {
        TreePath selPath = getSelectionPath();
        if(selPath == null) {
            return null;
        }
        return (RTreeNode) selPath.getLastPathComponent();
    }

    public void setSelNode(RTreeNode n) {
        setSelectionPath(n.getRPath());
    }

    public void moveUp(RTreeNode n) {
        n.moveUp();
        updateUI();
    }

    public void moveDown(RTreeNode n) {
        n.moveDown();
        updateUI();
    }

    // Expand

    public void expand(RTreeNode nNode) {
        expandPath(getPath(nNode));
    }
    public void expand(TreePath pPath) {
        expandPath(pPath);
    }

    // Expand All

    public void initExpandAllLimiter() {
        expandAllLimiterCount = 0;
    }
    public boolean expandAll(RTreeNode nNode) {
        return expandAll(getPath(nNode));
    }
    public boolean expandAll(RTreePath pPath) {
        removeTreeWillExpandListener(treeWillExpandListener);
        boolean all = expandAllRecursive(pPath);
        addTreeWillExpandListener(treeWillExpandListener);
        return all;
    }

    // Not the most efficient, constructing new paths each time.  Would
    // be nice if recursion could keep track of path!
    // NOTE: Technically from a JTree/DefaultMutableTreeNodeSense
    // leaf nodes can be expanded or collapsed as needed as well,
    // but we'll try to save the user some cycles by not expanding
    // those nodes without children.
    // NOTE: there seem to still be some bugs somewhere with this,
    // with the SimpleTree potentially still allowing mass expand alls.
    private boolean expandAllRecursive(RTreePath pPath) {
        RTreeNode nLast = pPath.getLast();
        if(nLast.getCount() != 0) {
            if(expandAllLimiterCount >= 10000) {
                return false;
            }
            expandAllLimiterCount++;
            for(int c = 0; c < nLast.getChildCount(); c++) {
                if(!expandAllRecursive(nLast.getRChildAt(c).getRPath())) {
                    return false;
                }
            }
            expandPath(pPath);
        }
        return true;
    }

    @Override
    public void addTreeSelectionListener(TreeSelectionListener tsl) {
        super.addTreeSelectionListener(tsl);
    }
    public void expandToLevel(int level) {
        expandToLevel(getRoot(), level);
    }
    public void expandToLevel(RTreeNode nStart, int level) {
        expandToLevel(nStart, 0, level);
    }
    private void expandToLevel(RTreeNode nParent, int curLevel, int maxLevel) {
        if(curLevel <= maxLevel) {
            expand(nParent);
            for(RTreeNode nChild : nParent) {
                expandToLevel(nChild, curLevel + 1, maxLevel);
            }
        }
    }
    public void expandSelected() {
        for(RTreeNode nSel : getTSelectionNodes()) {
            expand(nSel);
        }
    }
    public void expandAllSelected() {
        for(RTreeNode nSel : getTSelectionNodes()) {
            expandAll(nSel);
        }
    }

    public void collapse(RTreeNode nNode) {
        collapsePath(getPath(nNode));
    }
    public void collapse(RTreePath pPath) {
        collapsePath(pPath);
    }
    public void collapseAll(RTreeNode nNode) {
        collapseAll(getPath(nNode));
    }
    public void collapseAll(RTreePath pPath) {
        RTreeNode nLast = pPath.getLast();
        if(nLast.getCount() != 0) {
            for(int c = 0; c < nLast.getChildCount(); c++) {
                collapseAll(nLast.getRChildAt(c).getRPath());  // Not the most efficient, constructing new paths each time.  Would be nice if recursion could keep track of path!
            }
            collapsePath(pPath);
        }
    }
    public void collapseSelected() {
        for(RTreeNode nSel : getTSelectionNodes()) {
            collapse(nSel);
        }
    }
    public void collapseAllSelected() {
        for(RTreeNode nSel : getTSelectionNodes()) {
            collapseAll(nSel);
        }
    }

    public <T extends NodeBase> T getSelObject() {
        RTreeNode nSel = getSelNode();
        if(nSel == null) {
            return null;
        }
        return (T) nSel.getUserObject();
    }

    public boolean type(Class<? extends NodeBase> clazz) {
        NodeBase n = getSelObject();
        if(n == null) {
            return false;
        }
        return clazz.isAssignableFrom(n.getClass());
    }

    public RTreePath getPath(TreeNode node) {
        List<Object> nodesToParent = new ArrayList<>();
        Object p = node;
        while(p != null) {
            nodesToParent.add(0, p);
            p = ((TreeNode)p).getParent();
        }
        return new RTreePath(nodesToParent.toArray(new Object[0]));
    }

    public void collapseAll() {
        for(int i = 0; i < getRowCount(); i++) {
            collapseRow(i);
        }
    }

    public void expandAll() {
        for(int i = 0; i < getRowCount(); i++) {
            expandRow(i);
        }
    }

    public boolean isSelection() {
        return getSelectionCount() != 0;
    }

    public RTreeNode getDeepestLastOpenChild(RTreeNode nNode) {
        while(isExpanded(nNode.getRPath()) && nNode.getCount() != 0) {
            nNode = nNode.getRLastChild();
        }
        return nNode;
    }

/*
 *  THINK ABOUT THIS SOME DAY... what is it and is it needed?  Refer to N2A - wait, has it come to this!!??
 *
    public RTreeNode getContextRoot(Class<?>... classes) {
    }
    public RTreeNode getContextRoot(Class<?> stopAtClass) {
        RTreeNode chosenNode = null;
        TreePath path = getSelectionPath();
        if(path != null) {
            return getContextRoot(stopAtClass, (RTreeNode) path.getLastPathComponent());
        } else if(addToClass == null) {
            return getContextRoot(stopAtClass, null);
        }
        return chosenNode;
    }
    private RTreeNode getContextRoot(Class<?> stopAtClass, RTreeNode nSel) {
        RTreeNode chosenNode = null;
        RTreeNode n = nSel;
        while(n != null) {
            Class<?> uClass = n.getUserObject().getClass();
            if(stopAtClass != null) {
                if(stopAtClass.isAssignableFrom(uClass)) {
                    chosenNode = n;
                    break;
                }
            } else if(addToClass != null && addToClass.isAssignableFrom(uClass)) {
                chosenNode = n;
                break;
            }
            n = (RTreeNode) n.getParent();
        }
        if(addToClass == null && stopAtClass == null) {
            chosenNode = (RTreeNode) model.getRoot();
        }
        return chosenNode;
    }
*/

    protected void showMultiSelectEnabledContextMenu(MouseEvent e) {
        if(e.isPopupTrigger()) {
            TreePath clickedPath = getPathForLocation(e.getX(), e.getY());
            boolean found = false;
            if(getSelectionCount() != 0) {
                for(TreePath selPath : getSelectionPaths()) {
                    if(selPath.equals(clickedPath)) {
                        found = true;
                        break;
                    }
                }
            }
            if(!found) {
                if(e.isControlDown()) {
                    getSelectionModel().addSelectionPath(clickedPath);
                } else {
                    getSelectionModel().setSelectionPath(clickedPath);
                }
            }
            JPopupMenu mnuPopup = createPopupMenu(e);
            if(mnuPopup != null) {
                mnuPopup.show(this, e.getX(), e.getY());
            }
        }
    }

    protected JPopupMenu createPopupMenu(MouseEvent e) {
        return null;
    }

    public void enableMultiSelectContextMenu() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                showMultiSelectEnabledContextMenu(e);
            }
            @Override
            public void mousePressed(MouseEvent e) {
                focus();
                showMultiSelectEnabledContextMenu(e);
            }
        });
    }

    public void focus() {
        requestFocusInWindow();
    }

    private TreeWillExpandListener treeWillExpandListener = new TreeWillExpandListener() {
        public void treeWillCollapse(TreeExpansionEvent e) throws ExpandVetoException {
            Object obj = e.getPath().getLastPathComponent();
            if(obj instanceof RTreeNode) {
                RTreeNode nObj = (RTreeNode) obj;
                Object uObj = nObj.getUserObject();
                if(uObj instanceof NodeBase) {
                    NodeBase uBase = (NodeBase) nObj.getUserObject();
                    if(!uBase.isCollapsible()) {
                        throw new ExpandVetoException(e);
                    }
                }
            }

            if(obj == RTree.super.getModel().getRoot() && !rootCollapsible) {
                throw new ExpandVetoException(e);
            }
        }
        public void treeWillExpand(TreeExpansionEvent arg0) throws ExpandVetoException {}
    };


    ///////////
    // PAINT //
    ///////////

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        helper.paint(g);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // Because figuring out how to save and restore selection state
    // in a JTree is a very non-trivial process, we use the argument-
    // based version of this method to allow client code to pass in
    // a "selection state gathering method" which tells the tree
    // how to determine "node identity".  This is because client code
    // knows more about what's in the tree than the tree itself really.
    // It knows what properties of the nodes can be reliably used
    // to determine this identity.
    @Override
    public SelectionState getSelectionState(Object... args) {
        TreeSelectionStateIdentityMethod method =
            getDefaultArg(args, TreeSelectionStateIdentityMethod.PATH_TO_STRING);   // Classic but inflexible

        // Method-specific arguments
        Function<TreePath, Object> customIdentityFunction =
            getDefaultArg(args, Function.class, 1);

        Set<Object> expanded = new LinkedHashSet<>();
        Set<Object> selected = new LinkedHashSet<>();

        getSelectionState(getRoot(), expanded, selected, method, customIdentityFunction);

        return new SelectionState(
            "expanded",  expanded,
            "selected",  selected,
            "method",    method,
            "ifunction", customIdentityFunction
        );
    }

    private void getSelectionState(RTreeNode node,
                                   Set<Object> expanded,
                                   Set<Object> selected,
                                   TreeSelectionStateIdentityMethod method,
                                   Function<TreePath, Object> customIdentityFunction) {
        TreePath path = new TreePath(node.getPath());

        Object key = makeKey(node, path, method, customIdentityFunction);

        if(isExpanded(path)) {
            expanded.add(key);
        }
        if(isPathSelected(path)) {
            selected.add(key);
        }

        for(int i = 0; i < node.getChildCount(); i++) {
            getSelectionState(node.getRChildAt(i), expanded, selected, method, customIdentityFunction);
        }
    }

    @Override
    public void setSelectionState(SelectionState state) {
        Set<Object> expanded = state.getGx("expanded");
        Set<Object> selected = state.getGx("selected");
        TreeSelectionStateIdentityMethod method = state.getGx("method");
        Function<TreePath, Object> customIdentityFunction = state.getGx("ifunction");
        setSelectionState(getRoot(), expanded, selected, method, customIdentityFunction);
    }

    private void setSelectionState(RTreeNode node,
                                   Set<Object> expanded, Set<Object> selected,
                                   TreeSelectionStateIdentityMethod method,
                                   // These are the method-specific params -->
                                   Function<TreePath, Object> customIdentityFunction) {

        // Post-order traversal required because collapsePath will EXPAND parent nodes!!
        for(int i = 0; i < node.getChildCount(); i++) {
            setSelectionState(node.getRChildAt(i), expanded, selected, method, customIdentityFunction);
        }

        TreePath path = new TreePath(node.getPath());

        Object key = makeKey(node, path, method, customIdentityFunction);

        if(expanded.contains(key)) {
            expandPath(path);
        } else {
            collapsePath(path);
        }

        if(selected.contains(key)) {
            addSelectionPath(path);
        } else {
            removeSelectionPath(path);
        }
    }

    private Object makeKey(RTreeNode node, TreePath path,
                           TreeSelectionStateIdentityMethod method,
                           // These are the method-specific params -->
                           Function<TreePath, Object> customIdentityFunction) {

        if(method == TreeSelectionStateIdentityMethod.PATH_TO_STRING) {
            return path.toString();

        } else if(method == TreeSelectionStateIdentityMethod.NODE_TO_STRING) {
            return node.toString();

        } else if(method == TreeSelectionStateIdentityMethod.USER_OBJECT_HASH_CODE) {
            return node.getUserObject() == null ? 0 : node.getUserObject().hashCode();

        } else if(method == TreeSelectionStateIdentityMethod.USER_OBJECT_WRAPPED_HASH_CODE) {
            return
                node.get() == null ? 0 :
                    node.get().getWrappedObject() == null ? 0 :
                        node.get().getWrappedObject().hashCode();

        } else if(method == TreeSelectionStateIdentityMethod.CUSTOM) {
            return customIdentityFunction.apply(path);

        } else {
            throw new UnexpectedEnumValueUnicornException(method);
        }
    }

    @Override
    public void setModel(TreeModel newModel) {
        super.setModel(newModel);

        // TODO: This solution only respect's the root's isCollapsible call,
        // and should eventually be turned into a recursive method that
        // traverses all nodes that exist in the new model to make sure all
        // nodes that are not collapsible are expanded.  But at least this
        // is a start.  All nodes' isCollapsible is still respected in the
        // treeWillExpandListener during user interaction.  I guess we would
        // technically need to do these checks *any* time the model adds a
        // node (see NodeBase.isCollapsible).
        if(getModel().getRoot() instanceof RTreeNode) {
            if(((RTreeNode)getModel().getRoot()).getUserObject() instanceof NodeBase) {
                if(!getRootObject().isCollapsible()) {
                    ReflectionUtil.invoke(this, "expandRoot");
                }
            }
        }
    }


    //////////
    // TEST //
    //////////

    public static void mainx(String[] args) {
        EscapeFrame win = new EscapeFrame("Hello");
        RTreeNode nRoot = new RTreeNode(new NodeTest("Root"));
        final RTree tre = new RTree(nRoot);
        nRoot.add(new RTreeNode(new NodeTest("what")));
        nRoot.add(new RTreeNode(new NodeTest("why")));
        nRoot.add(new RTreeNode(new NodeTest("where")));
        nRoot.add(new RTreeNode(new NodeTest("when")));
        tre.setMouseDragSelection(true);

        JButton btn = new JButton("Add");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Object obj = tre.getSelectionPath().getLastPathComponent();
                MutableTreeNode nSel = (MutableTreeNode) obj;
                tre.append(nSel, new RTreeNode(new NodeTest("What")));
            }
        });

        JButton btn2 = new JButton("Remove");
        btn2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Object obj = tre.getSelectionPath().getLastPathComponent();
                MutableTreeNode nSel = (MutableTreeNode) obj;
                tre.remove(nSel);
            }
        });

        Lay.BLtg(win,
            "C", Lay.sp(tre),
            "S", Lay.FL("R", btn, btn2)
        );

        win.setSize(400, 400);
        win.setLocationRelativeTo(null);
        win.setVisible(true);
    }

    private static class NodeTest extends NodeBase {
        public String s;
        public NodeTest(String s) {
            this.s = s;
        }
        @Override
        public String toString() {
            return s;
        }
    }


    ////////////////////////////
    // DRAG (LASSO) SELECTION //
    ////////////////////////////

    @Override
    public void setMouseDragSelection(boolean enabled) {
        helper.setMouseDragSelection(enabled);
    }
    @Override
    public boolean hasSelection(MouseEvent e) {
        TreePath p = getPathForLocation(e.getPoint().x, e.getPoint().y);
        return p != null;
    }
    @Override
    public RectangleIterator getRectangleIterator(int x, int y) {
        return new RTreeRowIterator(x, y);
    }

    @Override
    public void updateCleanUp() {
        setLeadSelectionPath(null);
    }
    private class RTreeRowIterator extends RectangleIterator {
        private int row;
        private int cur;
        public RTreeRowIterator(int x, int y) {
            cur = row = Math.max(0,
                getClosestRowForLocation(x, y) - 1   // -1 fudge
            );
        }
        @Override
        public boolean hasNext() {
            return row < getRowCount();
        }
        @Override
        public Rectangle next() {
            cur = row;
            return getRowBounds(row++);
        }
        @Override
        public void addSelection() {
            TreePath P = getPathForRow(cur);
            getSelectionModel().addSelectionPath(P);
        }
        @Override
        public void removeSelection() {
            TreePath P = getPathForRow(cur);
            getSelectionModel().removeSelectionPath(P);
        }
    }
}
