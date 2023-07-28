package replete.ui.tree.cb;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import replete.ui.tree.RTree;
import replete.ui.windows.escape.EscapeFrame;



// Initial concept based off of
//    http://www.java2s.com/Code/Java/Swing-JFC/CheckBoxNodeTreeSample.htm
// But now looks quite different as many changes were
// made for usability and flexibility along with some
// bug fixes.

// Still to be done:
//   1.  Figure out how the space bar can be used to change
//       selected cells.
//   2.  Allow insets around the nodes like CheckBoxList.
//       This will involve placing the check box in a panel.
//   3.  Investigate whether or not we can get a behavior
//       similar to CheckBoxList - single click selects,
//       double click changes check box.
//   4.  Is there not supposed to be a focus border in Aqua L&F?
//   5.  Give up on idea that double click still expands/
//       contracts a node with children?
//   6.  Complete all necessary getters and setters, don't
//       just expose the checked map.
//   7.  Investigate inconsistency with being able to multi
//       select with the keyboard but not the mouse.
//   8.  Reinitialize map on setModel, etc.?

/**
 * @author Derek Trumbo
 */

public class RCheckBoxTree extends RTree {

    ////////////
    // FIELDS //
    ////////////

    private static final long serialVersionUID = 1L;

    public Map<DefaultMutableTreeNode, Boolean> checkedMap =
        new HashMap<DefaultMutableTreeNode, Boolean>();

    protected CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
    protected CheckBoxNodeEditor editor = new CheckBoxNodeEditor();

    protected boolean nonLeafCheckboxes;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RCheckBoxTree() {
        init();
    }
    public RCheckBoxTree(Hashtable<?,?> value) {
        super(value);
        init();
    }
    public RCheckBoxTree(Object[] value) {
        super(value);
        init();
    }
    public RCheckBoxTree(TreeModel newModel) {
        super(newModel);
        init();
    }
    public RCheckBoxTree(TreeNode root) {
        super(root);
        init();
    }
    public RCheckBoxTree(TreeNode root, boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
        init();
    }
    public RCheckBoxTree(Vector<?> value) {
        super(value);
        init();
    }

    ////////////////////
    // INITIALIZATION //
    ////////////////////

    protected void init() {
        setCellRenderer(renderer);
        setCellEditor(editor);
        setEditable(true);
        updateRenderers();
        //initCheckedFromModel();
        addCheckBoxListeners();
    }

    protected void addCheckBoxListeners() {

        // Allow the check boxes to be changed via the space key.
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(!RCheckBoxTree.this.isEnabled()) {
                    return;
                }
                if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                    /*int index = getSelectedIndex();
                    if(index != -1) {
                        checked[index] = !checked[index];
                        repaint();
                        fireListCheckedEvent(index);
                    }*/
                    System.out.println("space pressed");
                }
            }
        });

        addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {

                // getForeground and getBackground could theoretically
                // return null if the tree has not yet been added to
                // any container and isForegroundSet or isBackgroundSet
                // is false (which supposedly could vary from UI to UI),
                // which indicates that the component should pull those
                // colors from its parent.  So to cover all our bases
                // we should update the renderer each time our parent
                // changes.
                if(evt.getPropertyName().equals("ancestor")) {
                    updateRenderers();
                }
            }
        });
    }

    //////////////////////////////
    // AUTOMATIC CHECK BEHAVIOR //
    //////////////////////////////

    // Could also have been implemented as a single integer
    // with bit-mask flags like CheckBoxTree.AUTO_CHECK_UNION = 2
    // etc.  It wasn't done this way just because since there
    // are only two dimensions for auto check behavior, the
    // enum was a little cleaner.  If this behavior is not
    // good enough for a given problem, custom tree check
    // listeners can be added to fulfill the need.
    public enum AutoCheckBehavior {
        NONE,
        CHILD_INTERSECTION_CASCADE,
        CHILD_UNION_CASCADE,
        CHILD_INTERSECTION_NO_CASCADE,
        CHILD_UNION_NO_CASCADE
    }

    protected AutoCheckBehavior autoCheck = AutoCheckBehavior.NONE;
    public AutoCheckBehavior getAutoCheckBehavior() {
        return autoCheck;
    }
    public void setAutoCheckBehavior(AutoCheckBehavior behavior) {
        autoCheck = behavior;
    }

    // Change code for CHILD_INTERSECTION_* and CHILD_UNION_*.
    private void doAutoCheck(DefaultMutableTreeNode checkedNode, boolean chk, boolean initialCall) {

        // Whether or not to cascade check changes to children nodes.
        boolean doCascade =
            autoCheck == AutoCheckBehavior.CHILD_UNION_CASCADE ||
            autoCheck == AutoCheckBehavior.CHILD_INTERSECTION_CASCADE;

        // If cascading is on then change all the children underneath
        // (if any) to the same value that this node has.
        if(doCascade) {
            for(int a = 0; a < checkedNode.getChildCount(); a++) {
                checkIfDifferent((DefaultMutableTreeNode) checkedNode.getChildAt(a), chk);
                if(checkedNode.getChildAt(a).getChildCount() != 0) {
                    doAutoCheck((DefaultMutableTreeNode) checkedNode.getChildAt(a), chk, false);
                }
            }
        }

        // The ancestors should only be changed by the original node that
        // was changed, not any children changed due to cascading.
        // Also, don't change any ancestors unless non-leaf checkboxes
        // is enabled.
        if(!initialCall || !nonLeafCheckboxes) {
            return;
        }

        // Whether or not intersection is desired.
        boolean doIntersection =
            autoCheck == AutoCheckBehavior.CHILD_INTERSECTION_CASCADE ||
            autoCheck == AutoCheckBehavior.CHILD_INTERSECTION_NO_CASCADE;

        // Set all the ancestor nodes of this node appropriately
        // depending on auto check behavior.
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) checkedNode.getParent();
        while(parent != null) {
            boolean parentChecked = doIntersection;
            for(int a = 0; a < parent.getChildCount(); a++) {
                DefaultMutableTreeNode sibling = (DefaultMutableTreeNode) parent.getChildAt(a);
                boolean siblingChecked = checkedMap.get(sibling) != null && checkedMap.get(sibling);
                if(doIntersection) {
                    if(!siblingChecked) {
                        parentChecked = false;
                        break;
                    }
                } else {
                    if(siblingChecked) {
                        parentChecked = true;
                        break;
                    }
                }
            }
            checkIfDifferent(parent, parentChecked);
            parent = (DefaultMutableTreeNode) parent.getParent();
        }
    }

    protected void checkIfDifferent(DefaultMutableTreeNode node, boolean newChk) {
        boolean curChk = checkedMap.get(node) != null && checkedMap.get(node);
        if(curChk != newChk) {
            checkedMap.put(node, newChk);
            fireTreeCheckedEvent(new TreePath(node.getPath()), newChk);
        }
    }

    //////////////////////
    // PROPERTY CHANGES //
    //////////////////////

    // Property changes are used as a small optimization so that
    // these values are not needed to be called each time a
    // cell is rendered.

    @Override
    public void setForeground(Color bg) {
        super.setForeground(bg);
        updateRenderers();
    }
    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        updateRenderers();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        updateRenderers();
    }
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        updateRenderers();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        updateRenderers();
    }

    protected void updateRenderers() {
        if(renderer != null) {
            renderer.updateRendererUI(this);
        }
        if(editor != null) {
            editor.updateRendererUI(this);
        }
    }

    ///////////////
    // LISTENERS //
    ///////////////

    public TreeCheckedListener[] getTreeCheckedListeners() {
        return listeners.toArray(new TreeCheckedListener[0]);
    }
    protected List<TreeCheckedListener> listeners = new ArrayList<TreeCheckedListener>();
    public void addTreeCheckedListener(TreeCheckedListener listener) {
        listeners.add(listener);
    }
    public void removeTreeCheckedListener(TreeCheckedListener listener) {
        listeners.remove(listener);
    }
    protected void fireTreeCheckedEvent(TreePath path, boolean checked) {
        TreeCheckedEvent e = new TreeCheckedEvent(this, path, checked);
        for(TreeCheckedListener listener : listeners) {
            listener.valueChanged(e);
        }
    }

    /////////////////////
    // GET/SET METHODS //
    /////////////////////

    // These methods are analogous to the "selection" methods.

    /*
     void
    addSelectionPath(TreePath path)
              Adds the node identified by the specified TreePath to the current selection.
     void
    addSelectionPaths(TreePath[] paths)
              Adds each path in the array of paths to the current selection.
     void
    addSelectionRow(int row)
              Adds the path at the specified row to the current selection.
     void
    addSelectionRows(int[] rows)
              Adds the paths at each of the specified rows to the current selection.
    */

    public void clearChecked() {
        checkedMap = new HashMap<DefaultMutableTreeNode, Boolean>();
        repaint();
    }

    public int getMaxCheckedRow() {
        return 0;
    }

    public int getMinCheckedRow() {
        return 0;
    }

    public int getCheckedCount() {
        int numChecked = 0;
        for(DefaultMutableTreeNode n : checkedMap.keySet()) {
            if(checkedMap.get(n)) {
                numChecked++;
            }
        }
        return numChecked;
    }

    public TreePath getCheckedPath() {
        return null;
    }

    public TreePath[] getCheckedPaths() {

        return null;
    }

    public int[] getCheckedRows() {
        return null;
    }

    public boolean isPathChecked(TreePath path) {
        return false;
    }

    public boolean isRowChecked(int row) {
        return false;
    }

    public boolean isCheckedEmpty() {
        return getCheckedCount() == 0;
    }

    /*
     void
    removeSelectionInterval(int index0, int index1)
              Removes the nodes between index0 and index1, inclusive, from the selection.
     void
    removeSelectionPath(TreePath path)
              Removes the node identified by the specified path from the current selection.
     void
    removeSelectionPaths(TreePath[] paths)
              Removes the nodes identified by the specified paths from the current selection.
     void
    removeSelectionRow(int row)
              Removes the row at the index row from the current selection.
     void
    removeSelectionRows(int[] rows)
              Removes the rows that are selected at each of the specified rows.

     void
    setSelectionInterval(int index0, int index1)
              Selects the nodes between index0 and index1, inclusive.
     void
    setSelectionPath(TreePath path)
              Selects the node identified by the specified path.
     void
    setSelectionPaths(TreePath[] paths)
              Selects the nodes identified by the specified array of paths.
     void
    setSelectionRow(int row)
              Selects the node at the specified row in the display.
     void
    setSelectionRows(int[] rows)
              Selects the nodes corresponding to each of the specified rows in the display.
    */

    public boolean isNonLeafCheckboxes() {
        return nonLeafCheckboxes;
    }
    public void setNonLeafCheckboxes(boolean checkboxes) {
        nonLeafCheckboxes = checkboxes;
    }

    public void setCheckNodeInsets(Insets insets) {
        renderer.setInsets(insets);
        editor.editorRenderer.setInsets(insets);
        updateUI();     // Must be called instead of repaint.
    }
    public Insets getCheckNodeInsets() {
        return renderer.getInsets();
    }

    //////////////
    // RENDERER //
    //////////////

    protected class RendererAwareCheckBox extends JCheckBox {
        protected CheckBoxNodeRenderer renderer;
        public CheckBoxNodeRenderer getRenderer() {
            return renderer;
        }
        public void setRenderer(CheckBoxNodeRenderer r) {
            renderer = r;
        }
    }

    protected class CheckBoxNodeRenderer implements TreeCellRenderer {
        protected Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
        protected Border defaultFocusBorder = UIManager.getBorder("List.focusCellHighlightBorder");
        protected Color treeSelFg = UIManager.getColor("Tree.selectionForeground");
        protected Color treeSelBg = UIManager.getColor("Tree.selectionBackground");

        protected DefaultTreeCellRenderer nonLeafRenderer = new DefaultTreeCellRenderer();
        protected RendererAwareCheckBox chk;
        protected JPanel pnl;

        protected Insets insets;
        public Insets getInsets() {
            return insets;
        }
        public void setInsets(Insets newInsets) {
            insets = newInsets;
            chk.setBorder(BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right));
        }

        public RendererAwareCheckBox getCheckBox() {
            return chk;
        }

        // Keeping these things locally and updating them
        // only when the tree properties change is a small
        // optimization so that they don't need to be called
        // every time a cell needs to be repainted.
        protected Color treeFg;
        protected Color treeBg;
        protected boolean treeEnabled;
        protected Font treeFont;

        public CheckBoxNodeRenderer() {
            chk = new RendererAwareCheckBox();
            chk.setOpaque(false);
            chk.setRenderer(this);
            chk.setFocusPainted(false);   // Needed for when renderer used for editor.
            chk.setBorderPainted(false);  // Panel will handle the border.

            pnl = new JPanel(new BorderLayout());
            pnl.add(chk, BorderLayout.CENTER);

            setInsets(new Insets(0, 0, 0, 0));
        }

        public void updateRendererUI(JTree tree) {
            treeFg      = tree.getForeground();
            treeBg      = tree.getBackground();
            treeEnabled = tree.isEnabled();
            treeFont    = tree.getFont();

            // getForeground and getBackground could theoretically
            // return null if the tree has not yet been added to
            // any container and isForegroundSet or isBackgroundSet
            // is false (which supposedly could vary from UI to UI),
            // which indicates that the component should pull those
            // colors from its parent.
            if(treeFg == null) {
                treeFg = UIManager.getColor("Tree.textForeground");
            }
            if(treeBg == null) {
                treeBg = UIManager.getColor("Tree.textBackground");
            }

            chk.setEnabled(treeEnabled);
            chk.setFont(treeFont);
            nonLeafRenderer.setEnabled(treeEnabled);
            nonLeafRenderer.setFont(treeFont);
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
                                                      boolean expanded, boolean leaf, int row,
                                                      boolean hasFocus) {
            Component ret;

            if(leaf || nonLeafCheckboxes) {

                String stringValue = tree.convertValueToText(value, selected,
                    expanded, leaf, row, false);
                chk.setText(stringValue);

                // The checkbox is rendered selected if there exists
                // a selected value in the checked map for this node
                // and that value is true.
                chk.setSelected(false);
                Boolean isChecked = checkedMap.get(value);
                if(isChecked != null) {
                    chk.setSelected(isChecked);
                }

                chk.setForeground(selected ? treeSelFg : treeFg);
                pnl.setBorder(hasFocus ? defaultFocusBorder : noFocusBorder);
                pnl.setBackground(selected ? treeSelBg : treeBg);

                ret = pnl;

            } else {

                ret = nonLeafRenderer.getTreeCellRendererComponent(tree,
                    value, selected, expanded, leaf, row, hasFocus);
                DefaultTreeCellRenderer l = (DefaultTreeCellRenderer) ret;

                if(!selected) {
                    l.setOpaque(true);
                } else {
                    l.setOpaque(false);
                }

                ret.setForeground(selected ? treeSelFg : treeFg);
                ret.setBackground(selected ? treeSelBg : treeBg);
            }

            return ret;
        }

        protected DefaultMutableTreeNode editObject;
    }

    ////////////
    // EDITOR //
    ////////////

    protected class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {

        // The editor must use its own instance of the renderer,
        // cannot use the enclosing class's instance.
        protected CheckBoxNodeRenderer editorRenderer = new CheckBoxNodeRenderer();

        public void updateRendererUI(JTree tree) {
            editorRenderer.updateRendererUI(tree);
        }

        public Object getCellEditorValue() {
            checkedMap.put(editorRenderer.editObject, editorRenderer.chk.isSelected());
            return editorRenderer.editObject.getUserObject();
        }

        @Override
        public boolean isCellEditable(EventObject event) {
            boolean cellEditable = false;
            if(event instanceof MouseEvent) {
                MouseEvent mouseEvent = (MouseEvent) event;
                TreePath path = getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
                if(path != null) {
                    Object node = path.getLastPathComponent();
                    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
                    cellEditable = treeNode.isLeaf() || nonLeafCheckboxes;
                }
            }
            return cellEditable;
        }

        public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected,
                                                    boolean expanded, boolean leaf, int row) {

            // We don't want to hear about the events that would
            // occur during the initialization of the check box
            // for this node.
            editorRenderer.getCheckBox().removeItemListener(itemListener);

            JPanel p = (JPanel)
                editorRenderer.getTreeCellRendererComponent(
                    tree, value, true, expanded, leaf, row, true);

            editorRenderer.editObject = (DefaultMutableTreeNode) value;

            // Add the item listener back to hear about changes
            // that the user makes.
            editorRenderer.getCheckBox().addItemListener(itemListener);

            return p;
        }

        protected ItemListener itemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                stopCellEditing();
                boolean checked = (itemEvent.getStateChange() == ItemEvent.SELECTED);
                fireTreeCheckedEvent(new TreePath(editorRenderer.editObject.getPath()),
                    checked);

                // Fire off auto checks if desired.
                if(autoCheck != AutoCheckBehavior.NONE) {
                    doAutoCheck(editorRenderer.editObject, checked, true);
                    repaint();
                }
            }
        };
    }

    //////////
    // TEST //
    //////////

    public static void main(String[] args) {

        final RCheckBoxTree treCheckboxes = new RCheckBoxTree(new JTree().getModel());
        final RCheckBoxTree treCheckboxes2 = new RCheckBoxTree(new JTree().getModel());
        final JTree treDefault = new JTree();

        treCheckboxes2.setNonLeafCheckboxes(true);
        treDefault.setEditable(true);

        JPanel pnl = new JPanel(new GridLayout(1, 3));
        pnl.add(new JScrollPane(treCheckboxes));
        pnl.add(new JScrollPane(treCheckboxes2));
        pnl.add(new JScrollPane(treDefault));

        JButton btnDo = new JButton("Do Stuff");
        btnDo.setMnemonic('D');
        btnDo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                treCheckboxes.setForeground(Color.red);
                treCheckboxes2.setForeground(Color.red);
                treDefault.setForeground(Color.red);

                treCheckboxes.setBackground(Color.green);
                treCheckboxes2.setBackground(Color.green);
                treDefault.setBackground(Color.green);

                treCheckboxes.setFont(new Font("Courier New", 0, 16));
                treCheckboxes2.setFont(new Font("Courier New", 0, 16));
                treDefault.setFont(new Font("Courier New", 0, 16));

                treCheckboxes.setCheckNodeInsets(new Insets(2, 2, 2, 2));
                treCheckboxes2.setCheckNodeInsets(new Insets(5, 5, 5, 5));

                treCheckboxes.updateUI();
                treCheckboxes2.updateUI();
                treDefault.updateUI();
            }
        });
        JPanel pnlButtons = new JPanel();
        pnlButtons.add(btnDo);

        treCheckboxes.setAutoCheckBehavior(AutoCheckBehavior.CHILD_UNION_CASCADE);
        treCheckboxes.addTreeCheckedListener(new TreeCheckedListener() {
            public void valueChanged(TreeCheckedEvent e) {
                System.out.println("Tree1Checked: " + e.getPath() + "/" + e.isChecked());
                System.out.println("  map -> " + treCheckboxes.checkedMap);
            }
        });
        treCheckboxes2.setAutoCheckBehavior(AutoCheckBehavior.CHILD_INTERSECTION_CASCADE);
        treCheckboxes2.addTreeCheckedListener(new TreeCheckedListener() {
            public void valueChanged(TreeCheckedEvent e) {
                System.out.println("Tree2Checked: " + e.getPath() + "/" + e.isChecked());
                System.out.println("  map -> " + treCheckboxes2.checkedMap);
            }
        });
        treDefault.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                System.out.println(e.getPath() + "/"+ e.isAddedPath());
                System.out.println("  "+Arrays.toString(e.getPaths()));

            }
        });

        JFrame frame = new EscapeFrame();
        frame.setTitle("CheckBoxTree Demo");
        frame.setLayout(new BorderLayout());
        frame.add(pnl, BorderLayout.CENTER);
        frame.add(pnlButtons, BorderLayout.SOUTH);
        frame.setSize(800, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
