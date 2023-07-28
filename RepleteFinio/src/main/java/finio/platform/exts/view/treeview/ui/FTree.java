package finio.platform.exts.view.treeview.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.core.errors.FMapCompositeException;
import finio.core.errors.MoveOverwriteException;
import finio.platform.exts.view.treeview.ui.editors.EditCompletionEvent;
import finio.platform.exts.view.treeview.ui.editors.EditCompletionListener;
import finio.platform.exts.view.treeview.ui.editors.FTreeCellEditor;
import finio.platform.exts.view.treeview.ui.nodes.NodeFTree;
import finio.platform.exts.view.treeview.ui.renderers.FTreeNodeBaseTreeRenderer;
import finio.ui.FContextMenuCreator;
import finio.ui.FontConstants;
import finio.ui.actions.FActionMap;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import finio.ui.worlds.WorldContext;
import replete.event.ChangeNotifier;
import replete.event.ExtChangeNotifier;
import replete.event.rnotif.RChangeEvent;
import replete.event.rnotif.RChangeListener;
import replete.text.StringUtil;
import replete.threads.SwingTimerManager;
import replete.ui.GuiUtil;
import replete.ui.images.concepts.ImageLib;
import replete.ui.tree.RTree;
import replete.ui.tree.RTreeNode;
import replete.ui.tree.RTreePath;
import replete.ui.uiaction.UIAction;
import replete.ui.uiaction.UIActionKeyPressedListener;
import replete.ui.windows.Dialogs;

public class FTree extends RTree {


    ////////////
    // FIELDS //
    ////////////

    private AppContext ac;
    private WorldContext wc;
    private FNode nRoot;
    private FTreeOptionsModel optModel;

    private FTreeCellEditor cellEditor;
    private NodeFTree uHoverPrev;
    private NodeFTree uWorkingScopePrev;
    private FActionMap actionMap;
    private boolean shift;
    private Set<FNode> anchorNodes = new HashSet<>();
    private FTreePanel pnlView;

    // Painting
    private Font graphicsFont = null;
    protected boolean paintSelectionOrder = true;   // Make toggleable in interface and persisted in AppState.
    private Map<FNode, EditedNodeInfo> editedNodes;
    private boolean bulkSelection = false;
    public void setBulkSelection(boolean bulkSelection) {
        this.bulkSelection = bulkSelection;
        if(!bulkSelection) {
            updateFromTreeSelection();
        }
    }

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FTree(final AppContext ac, WorldContext wc,
                 final FActionMap actionMap, FTreeOptionsModel optM, FTreePanel pnlView) {
        super(new FNode());          // Use blank node initially

//        InputMap inputMap = getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
//        KeyStroke[] keyStrokes = inputMap.allKeys();
//        for ( KeyStroke keyStroke : keyStrokes ) {
//          Object actionCommand = inputMap.get( keyStroke );
//          System.out.println( "keyStroke = " + keyStroke );
//          System.out.println( "actionCommand = " + actionCommand );
//        }

        this.ac = ac;
        this.wc = wc;
        this.actionMap = actionMap;
        this.pnlView = pnlView;

        setToggleClickCount(0);
//        setSelectionModel(new XDefaultSelectionModel());

        // Good or not? Does fix situation where getCellEditorValue()
        // called without stopCellEditing() being called first...
        setInvokesStopCellEditing(false);   // Need a consistent feel...
        setExpandsSelectedPaths(true);

        // Configure the cell editor for the tree.
        cellEditor = new FTreeCellEditor(ac, this, getFontMetrics(getFont()));
        cellEditor.addEditCompletionListener(new EditCompletionListener() {
            public void stateChanged(EditCompletionEvent e) {
                fireEditCompletionNotifier(e);
            }
        });
        cellEditor.addStartEditListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                fireStartEditNotifier();
            }
        });
        cellEditor.addCancelEditListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                fireCancelEditNotifier();
            }
        });
//        cellEditor.addCellEditorListener(new CellEditorListener() {
//            public void editingStopped(ChangeEvent e) {
//            }
//            public void editingCanceled(ChangeEvent e) {
//            }
//        });
        cellEditor.addDownListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                RTreePath pSel = getSelPath();
                RTreeNode nSel = pSel.getLast();
                RTreeNode nNextSel = nSel.getRNextSibling();

                if(isExpanded(pSel) && nSel.getCount() != 0) {
                    nNextSel = nSel.getRChildAt(0);

                } else if(nSel.getRNextSibling() != null) {
                    nNextSel = nSel.getRNextSibling();

                } else {
                    RTreeNode nSearch = nSel.getRParent();
                    while(nSearch != null) {
                        if(nSearch.getRNextSibling() != null) {
                            nNextSel = nSearch.getRNextSibling();
                            break;
                        }
                        nSearch = nSearch.getRParent();
                    }
//                    // NPE here
//                    if(nSearch.getUserObject() instanceof NodeRealm) {
//                        if(isExpanded(nSearch.getTPath()) && nSearch.getCount() != 0) {
//                            nSearch = nSearch.getTFirstChild();
//                        }
//                    }
//                    System.out.println("NextSEL="+nNextSel);
                }

                if(nNextSel != null) {
                    cancelEditing();
                    setSelectionPath(nNextSel.getRPath());
                    startEditingAtPath(getSelectionPath());
                }
            }
        });

        cellEditor.addUpListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                RTreePath pSel = getSelPath();
                RTreeNode nSel = pSel.getLast();
                RTreeNode nNextSel = nSel.getRPreviousSibling();

                if(nNextSel == null) {
                    RTreeNode nParent = nSel.getRParent();
//                    if(!(nParent.getObject() instanceof NodeRealm)) {
                        nNextSel = nParent;
//                    } else {
//                        RTreeNode nRealm = nParent;
//                        nRealm = nRealm.getTPreviousSibling();
//                        while(nRealm != null) {
//                            RTreeNode nDeep = getDeepestLastOpenChild(nRealm);
//                            if(nDeep != nRealm)  {
//                                nNextSel = nDeep;
//                                break;
//                            }
//                            nRealm = nRealm.getTPreviousSibling();
//                        }
//                    }
                } else {
                    nNextSel = getDeepestLastOpenChild(nNextSel);
                }

                if(nNextSel != null) {
                    cancelEditing();
                    setSelectionPath(nNextSel.getRPath());
                    startEditingAtPath(getSelectionPath());
                }
            }
        });

        pnlView.addWorkingScopeListener(wsListener);

        setEditable(true);
        setCellEditor(cellEditor);

        optModel = optM;
        optModel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Dialogs.showMessage(ac.getWindow(), "OPTCHANGE!");
                // TODO: recurse through nodes and toggle @@a-meta maps.
            }
        });

        enableMultiSelectContextMenu();
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = getRowForLocation(e.getX(), e.getY());
                NodeFTree uHover = null;
                if(uHoverPrev != null) {
                    uHoverPrev.setHover(false);
                }
                if(row != -1) {
                    RTreePath pHover = new RTreePath(getPathForRow(row));
                    uHover = pHover.getLast().getObject();
                    uHover.setHover(true);
                }
                uHoverPrev = uHover;
                repaint();
            }
        });

        addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                if(!bulkSelection) {       // Could also disconnect listeners
                    updateFromTreeSelection();
                }
            }
        });

        addKeyListener(new UIActionKeyPressedListener(actionMap));

        setMouseDragSelection(true);
        setDragEnabled(true);
        setDropMode(DropMode.ON_OR_INSERT);
        setTransferHandler(new TreeTransferHandler());

        Timer T = SwingTimerManager.create(50, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!editedNodes.isEmpty()) {
                    List<FNode> remove = new ArrayList<>();
                    synchronized(editedNodes) {
                        for(FNode node : editedNodes.keySet()) {
                            EditedNodeInfo info = editedNodes.get(node);
                            info.countdown--;
                            if(info.countdown == 0) {
                                remove.add(node);
                            }
                        }
                        for(FNode r : remove) {
                            editedNodes.remove(r);
                        }
                    }
                    repaint();
                }
            }
        });
        T.start();

        /*setUI(new BasicTreeUI() {
            @Override
            protected boolean shouldPaintExpandControl(TreePath path, int row,
                                                       boolean isExpanded, boolean hasBeenExpanded,
                                                       boolean isLeaf) {
                TreeNode node = (TreeNode) path.getLastPathComponent();
                if(node.getChildCount() == 0) {
                    return false;
                }
                return true;
            }
            @Override
            public Icon getCollapsedIcon() {
                return ImageLib.get(CommonConcepts.OPEN);// super.getCollapsedIcon();
            }
            @Override
            public Icon getExpandedIcon() {
                return super.getExpandedIcon();
            }
        });
        System.out.println(getUI().getClass());*/
    }

    private RChangeListener wsListener = new RChangeListener() {
        public void handle(RChangeEvent e) {
            updateWorkingScope();
        }
    };

    private void updateWorkingScope() {
        KeyPath scope = pnlView.getWorkingScope();
        NodeFTree uWorkingScope = null;
        if(scope == null) {
            uWorkingScope = nRoot.getObject();
        } else {
            uWorkingScope = (NodeFTree) nRoot.getObject();
            RTreeNode nCur = nRoot;
            for(Object seg : scope) {
                boolean found = false;
                for(RTreeNode nChild : nCur) {
                    uWorkingScope = nChild.getObject();
                    if(uWorkingScope.getK().equals(seg)) {
                        nCur = nChild;
                        expand(nCur);
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    uWorkingScope = null;
                }
            }
        }

        if(uWorkingScopePrev != null) {
            uWorkingScopePrev.setWorkingScope(false);
        }
        if(uWorkingScope != null) {
            uWorkingScope.setWorkingScope(true);
        }
        uWorkingScopePrev = uWorkingScope;

        // TODO: use treemodel.nodechange on UI thread instead of blanket updateUI method

        updateUI();
    }

    @Override
    protected TreeCellRenderer createRenderer() {
        return new FTreeNodeBaseTreeRenderer(editedNodes = new HashMap<>());
    }

    @Override
    protected JPopupMenu createPopupMenu(MouseEvent e) {
        return new FContextMenuCreator(ac, actionMap).createPopupMenu(e);
    }


    ///////////
    // PAINT //
    ///////////

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(paintSelectionOrder) {
            TreePath[] paths = getSelectionPaths();
            if(paths != null && paths.length != 0) {
                Graphics2D g2 = (Graphics2D) g;

                if(graphicsFont == null) {
                    graphicsFont = g.getFont().deriveFont(Font.BOLD);
                }
                g.setFont(graphicsFont);

                int w = 20;
                int h = 20;
                int t = 0;

                for(TreePath path : paths) {
                    Rectangle r = getPathBounds(path);
                    if(r == null) {
                        continue;
                    }

                    int x, y;
                    if(r.x == 0) {
                        x = r.x + r.width - 2/*- w / 2*/;
                        y = r.y/* - h / 2*/ - 3;
                    } else {
                        x = r.x /*+ r.width*/ - w / 2;
                        y = r.y - h / 2;
                    }

                    boolean isTarget = t == paths.length - 1;
                    if(isTarget) {
                        g.setColor(Color.red);
                    } else {
                        int del = -55;
                        g.setColor(GuiUtil.deriveColor(Color.red, del, del, del));
                    }
                    g.fillOval(x, y, w, h);

                    g.setColor(Color.yellow);
                    g.drawOval(x, y, w, h);

                    if(isTarget) {
                        int xh = x + w / 2;
                        int yh = y + h / 2;
                        g.drawLine(xh, y, xh, y + 3);
                        g.drawLine(x, yh, x + 3, yh);
                        g.drawLine(xh, y + h, xh, y + h - 3);
                        g.drawLine(x + w, yh, x + w - 3, yh);
                    }

                    g.setColor(Color.white);
                    String str = (t + 1) + "";
                    int one = (str.length() != 1 && !str.startsWith("1")) ? 1 : 0;
                    g.drawString(str, x + w / 2 - GuiUtil.stringWidth(g2, str) / 2 + one, y + GuiUtil.stringHeight(g2) - 1);

                    t++;
                }
            }
        }

        if(editedNodes.size() != 0) {
            int w = 24;
            int h = 24;
            for(EditedNodeInfo info : editedNodes.values()) {
                Rectangle r = getPathBounds(info.path);
                if(r == null) {
                    continue;
                }

                int x, y;
                if(r.x == 0) {
                    x = r.x + r.width - 2 - w / 2;   // Can't happen really...
                    y = r.y - 3;
                } else {
                    x = r.x + r.width - w / 2 + 10;
                    y = r.y - 5/* - h / 2*/;
                }

                int countdown = info.countdown;
                float opacity = countdown / (float) FTreeConstant.EDITED_COUNTDOWN;
                ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                g.drawImage(ImageLib.getImg(FinioImageModel.CHANGED_CHECK), x, y, w, h, null);
            }
        }
    }

    public class EditedNodeInfo {
        public int countdown;
        TreePath path;
        public EditedNodeInfo(TreePath path, int countdown) {
            this.path = path;
            this.countdown = countdown;
        }
    }

    public void addEditedNode(FNode node, TreePath path) {
        synchronized(editedNodes) {
            editedNodes.put(node, new EditedNodeInfo(path, FTreeConstant.EDITED_COUNTDOWN));
        }
        repaint();
    }
    public void removeEditedNode(FNode node) {
        synchronized(editedNodes) {
            editedNodes.remove(node);
        }
        repaint();
    }

//  ImageIcon icon = ImageLib.get(CommonConcepts.CANCEL);
//  @Override
  @Override
  public void paint(Graphics g) {

//      if(dragContext != null && dragContext.start != null && dragContext.end != null) {
//          int minX = Math.min(dragContext.start.x, dragContext.end.x);
//          int minY = Math.min(dragContext.start.y, dragContext.end.y);
//          int width = Math.abs(dragContext.start.x - dragContext.end.x) + 1;
//          int height = Math.abs(dragContext.start.y - dragContext.end.y) + 1;
//          g.setColor(dragContextFill);
//          g.fillRect(minX, minY, width, height);
////          g.setColor(dragContextBorder);
////          g.drawRect(minX, minY, width, height);
//      }

//     // tile it
//     Dimension d = this.getSize();
//     for(int i=0; i<d.width; i+=icon.getIconWidth()) {
//        for(int j=0; j<d.height; j+=icon.getIconHeight()) {
//           g.drawImage(icon.getImage(), i, j, null, null);
//        }
//     }
//     Paint p = new RadialGradientPaint(200F, 200F,
//         getWidth(), new float[] {0F, 1.0F}, new Color[] {Color.white, Lay.clr("245")});
//     Graphics2D g2 = (Graphics2D) g;
//     g2.setPaint(p);
//     g.fillRect(0, 0, getWidth(), getHeight());
//     ImageIcon i = new ImageIcon(User.getDesktop("planets.jpg").getAbsolutePath());
//     g.drawImage(i.getImage(), 0, 0, getWidth(), getHeight(), null);

     super.paint(g);
  }


    //////////
    // MISC //
    //////////

    void setRoot(FNode nRoot) {
        this.nRoot = nRoot;
        setModel(nRoot);
        updateWorkingScope();
    }

    public boolean isPaintSelectionOrder() {
        return paintSelectionOrder;
    }
    public void setPaintSelectionOrder(boolean paintSelectionOrder) {
        this.paintSelectionOrder = paintSelectionOrder;
        repaint();
    }

    // Node type conversion methods

    public FNode getFRoot() {
        return (FNode) getRoot();
    }

    public NodeFTree[] getFSelectionObjects() {
        Object[] sel = getSelObjects();
        if(sel == null) {
            return null;
        }
        NodeFTree[] u = new NodeFTree[sel.length];
        for(int s = 0; s < sel.length; s++) {
            u[s] = (NodeFTree) sel[s];
        }
        return u;
    }

    public FNode[] getASelectionNodes() {
        TreePath[] paths = super.getSelectionPaths();
        if(paths == null) {
            return null;
        }
        FNode[] nodes = new FNode[paths.length];
        int p = 0;
        for(TreePath path : paths) {
            nodes[p++] = (FNode) path.getLastPathComponent();
        }
        return nodes;
    }

    // Pause / Anchor

    public boolean isAnchorValid() {
        UIAction a = actionMap.getAction("toggle-anchor");
        return a.getValidator() == null || a.getValidator().isValid("toggle-anchor");
    }
//    public boolean isPauseValid() {
//        UIAction a = actionMap.getAction("toggle-pause");
//        return a.getValidator() == null || a.getValidator().isValid("toggle-pause");
//    }

    public boolean shouldUnanchor() {
        if(getFSelectionObjects() != null) {
            for(NodeFTree uSel : getFSelectionObjects()) {
                if(!uSel.isAnchor()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    public boolean shouldUnpause() {
        if(getASelectionNodes() != null) {
            for(FNode nSel : getASelectionNodes()) {
                if(!nSel.isPaused()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    // Font

    public void increaseFont() {
        changeFont(FontConstants.INC_DEC_AMOUNT);
    }

    public void decreaseFont() {
        changeFont(-FontConstants.INC_DEC_AMOUNT);
    }

    private void changeFont(int delta) {
        float cur = getFont().getSize();
        cur += delta;
        if(cur < FontConstants.MIN_SIZE) {
            cur = FontConstants.MIN_SIZE;
        }
        Font newFont = getFont().deriveFont(cur);
        setFont(newFont);
        cellEditor.setTreeFont(newFont, getFontMetrics(newFont));
    }

    // Other

    public KeyPath convertTreePathToKeyPath(TreePath Ptree) {
        KeyPath P = new KeyPath();
        boolean first = true;
        for(Object S : Ptree.getPath()) {
            if(first) {
                first = false;
                continue;
            }
            FNode nSel = (FNode) S;
            NodeFTree uSel = nSel.getObject();
            P.add(uSel.getK());
        }
        return P;
    }
    public void selectRealms(NonTerminal... Mrealms) {
        clearSelection();
        for(RTreeNode nRealm : getRoot()) {
            FNode nRealmA = (FNode) nRealm;
            boolean found = false;
            for(NonTerminal Mrealm : Mrealms) {
                if(nRealmA.getV() == Mrealm) {
                    found = true;
                    break;
                }
            }
            if(found) {
                getSelectionModel().addSelectionPath(nRealm.getRPath());
            }
        }
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    private ExtChangeNotifier<EditCompletionListener> editCompletionNotifier = new ExtChangeNotifier<>();
    public void addEditCompletionListener(EditCompletionListener listener) {
        editCompletionNotifier.addListener(listener);
    }
    private void fireEditCompletionNotifier(EditCompletionEvent event) {
        editCompletionNotifier.fireStateChanged(event);
    }

    private ChangeNotifier startEditNotifier = new ChangeNotifier(this);
    public void addStartEditListener(ChangeListener listener) {
        startEditNotifier.addListener(listener);
    }
    private void fireStartEditNotifier() {
        startEditNotifier.fireStateChanged();
    }

    private ChangeNotifier cancelEditNotifier = new ChangeNotifier(this);
    public void addCancelEditListener(ChangeListener listener) {
        cancelEditNotifier.addListener(listener);
    }
    private void fireCancelEditNotifier() {
        cancelEditNotifier.fireStateChanged();
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    // Drag & Drop

    // http://stackoverflow.com/questions/4588109/drag-and-drop-nodes-in-jtree
    private class TreeTransferHandler extends TransferHandler {
        private DataFlavor[] flavors = new DataFlavor[1];

        public TreeTransferHandler() {
            try {
                String mimeType =
                    DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" +
                    TreePath[].class.getName() + "\"";
                flavors[0] = new DataFlavor(mimeType);
            } catch(ClassNotFoundException e) {
                System.out.println("ClassNotFound: " + e.getMessage());
            }
        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            if(!support.isDrop()) {
                return false;
            }
            support.setShowDropLocation(true);
            if(!support.isDataFlavorSupported(flavors[0])) {
                return false;
            }

            JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
            JTree tree = (JTree) support.getComponent();

            // Do not allow a drop on the drag source selections.
            int dropRow = tree.getRowForPath(dl.getPath());
            int[] selRows = tree.getSelectionRows();
            for(int i = 0; i < selRows.length; i++) {
                if(selRows[i] == dropRow) {
                    return false;
                }
            }

            return true;
        }

        @Override
        protected Transferable createTransferable(JComponent cmp) {
            FTree tree = (FTree) cmp;
            if(tree.isSelection()) {
                return new NodesTransferable(getSelectionPaths());
            }
            return null;
        }

        @Override
        public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE | LINK;
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            if(!canImport(support)) {
                return false;
            }

            // Extract transfer data.
            TreePath[] paths = null;
            try {
                Transferable t = support.getTransferable();
                paths = (TreePath[]) t.getTransferData(flavors[0]);
            } catch(UnsupportedFlavorException ufe) {
                System.out.println("UnsupportedFlavor: " + ufe.getMessage());
            } catch(java.io.IOException ioe) {
                System.out.println("I/O error: " + ioe.getMessage());
            }

            // Get drop location info.
            JTree.DropLocation dl =
                    (JTree.DropLocation)support.getDropLocation();
            int childIndex = dl.getChildIndex();
            TreePath pDest = dl.getPath();
            KeyPath[] Psrc = new KeyPath[paths.length];
            for(int p = 0; p < paths.length; p++) {
                Psrc[p] = convertTreePathToKeyPath(paths[p]);
            }
            KeyPath Pdest = convertTreePathToKeyPath(pDest);
            executeDrop(Psrc, Pdest, childIndex, support.getDropAction());
            return true;
        }

        @Override
        public String toString() {
            return getClass().getName();
        }

        public class NodesTransferable implements Transferable {
            TreePath[] paths;
            public NodesTransferable(TreePath[] paths) {
                this.paths = paths;
            }
            public Object getTransferData(DataFlavor flavor)
                                     throws UnsupportedFlavorException {
                if(!isDataFlavorSupported(flavor)) {
                    throw new UnsupportedFlavorException(flavor);
                }
                return paths;
            }
            public DataFlavor[] getTransferDataFlavors() {
                return flavors;
            }
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return flavors[0].equals(flavor);
            }
        }

        public void executeDrop(KeyPath[] Psources, KeyPath Pdest, int position, int type) {
//            String res;
//            if(type == COPY) {
//                res = "Copying";
//            } else if(type == MOVE) {
//                res = "Moving";
//            } else {
//                res = "Linking";
//            }
//            res += ":\n";
//            for(KeyPath P : Psources) {
//                res += "   " + P + "\n";
//            }
//            res += "To:\n";
//            res += "   " + Pdest + "\n";
//            res += "@ position = " + position;
//            uiController.sendToConsole(res);

            switch(type) {
                case MOVE:


                    // TODO: This is not designed perfectly yet....
                    // Let's say there are 3 items moved into a map.
                    // 2 of them are overwriting, and 1 is new/not ow'ing
                    // Then the 1 move will succeed and the 2 will prompt
                    // for user interaction.  If use says continue,
                    // then the move operation will attempt to re-move
                    // the 1 that was already moved... causing an exception... need to fix!

                    try {
                        wc.getW().move(Psources, Pdest, position, true);   // Prevent overwrite
                    } catch(Exception e) {
                        if(e instanceof FMapCompositeException) {
                            List<Object> owKeys = new ArrayList<Object>();
                            for(Exception e2 : ((FMapCompositeException) e).getExceptions()) {
                                if(e2 instanceof MoveOverwriteException) {
                                    owKeys.add(((MoveOverwriteException) e2).getKey());
                                }
                            }
                            if(owKeys.size() != 0) {
                                String k = "";
                                for(Object owKey : owKeys) {
                                    k += "        " + owKey + "\n";
                                }
                                String s = StringUtil.s(owKeys.size());
                                if(Dialogs.showConfirm(ac.getWindow(),
                                        "Do you wish to overwrite the following existing key" +
                                            s + " in the destination map?\n\n" + k,
                                        "Overwrite Existing Key" + s + "?", true)) {
                                    try {
                                        wc.getW().move(Psources, Pdest, position, false);   // Overwrite
                                    } catch(Exception e3) {
                                        Dialogs.showDetails(
                                            ac.getWindow(),
                                            "An error has occurred with the move operation.",
                                            "Drag & Drop Error", e3);
                                    }
                                }
                                return;
                            }
                        }

                        Dialogs.showDetails(
                            ac.getWindow(), "An error has occurred with the move operation.",
                            "Drag & Drop Error", e);
                    }
                    break;

                case COPY:
                    try {
                        wc.getW().copy(Psources, Pdest, position);
                    } catch(Exception e) {
                        Dialogs.showDetails(
                            ac.getWindow(), "An error has occurred with the copy operation.",
                            "Drag & Drop Error", e);
                    }
                    break;
                case LINK:
                    Dialogs.showWarning(ac.getWindow(), "The link action is not currently supported.");
                    break;
            }
        }
    }

//    public TPath highestPath(final TPath[] newSelPaths) {
//        int minRow = Integer.MAX_VALUE;
//        TPath minPath = null;
//        for(TPath selPath : newSelPaths) {
//            int row = getRowForPath(selPath);
//            if(row < minRow) {
//                minRow = row;
//                minPath = selPath;
//            }
//        }
//        return minPath;
//    }

    public RTreePath highestPath(List<RTreePath> newSelPaths) {
        int minRow = Integer.MAX_VALUE;
        RTreePath minPath = null;
        for(RTreePath selPath : newSelPaths) {
            int row = getRowForPath(selPath);
            if(row < minRow) {
                minRow = row;
                minPath = selPath;
            }
        }
        return minPath;
    }

    public Set<FNode> getAnchorNodes() {
        return anchorNodes;
    }

    public void setShiftForEditValue(boolean shift) {
        this.shift = shift;
    }
    public boolean isShiftForEditValue() {
        return shift;
    }

    private void updateFromTreeSelection() {
        if(uHoverPrev != null) {
            uHoverPrev.setHover(false);
            uHoverPrev = null;
            repaint();
        }
        actionMap.validate();
    }

    /*@Override
    public boolean stopEditing() {
        System.out.println("aldfkjslaf");
        boolean val = super.stopEditing();
        RTreeNode nSel = getTSelectionNode();
        NodeMap uMap = nSel.getObject();
        ANonTerminal M = (ANonTerminal) uMap.getV();
        StandardAMapRenderer renderer = new StandardAMapRenderer();
        controller.sendToConsole(renderer.render(M));
        return val;
    }*/

//    private class XDefaultSelectionModel extends DefaultTreeSelectionModel {
//        Constructor<?> c;
//        public XDefaultSelectionModel() {
//            try {
//                Class<?> x = Class.forName("javax.swing.tree.PathPlaceHolder");
//                x.
//                for(Constructor y : x.getConstructors()) {
//                    System.out.println(y);
//                }
//                System.out.println(x);
//                c = x.getConstructor(new Class<?>[] {TreePath.class, boolean.class});
//                System.out.println(c);
//            } catch(ClassNotFoundException e) {
//                e.printStackTrace();
//            } catch(NoSuchMethodException e) {
//                e.printStackTrace();
//            } catch(SecurityException e) {
//                e.printStackTrace();
//            }
//
//        }
//        @Override
//        public void addSelectionPaths(TreePath[] paths) {
//            int       newPathLength = ((paths == null) ? 0 : paths.length);
//
//            if(newPathLength > 0) {
//                if(selectionMode == TreeSelectionModel.SINGLE_TREE_SELECTION) {
//                    setSelectionPaths(paths);
//                }
//                else if(selectionMode == TreeSelectionModel.
//                        CONTIGUOUS_TREE_SELECTION && !canPathsBeAdded(paths)) {
//                    if(arePathsContiguous(paths)) {
//                        setSelectionPaths(paths);
//                    }
//                    else {
//                        TreePath[]          newPaths = new TreePath[1];
//
//                        newPaths[0] = paths[0];
//                        setSelectionPaths(newPaths);
//                    }
//                }
//                else {
//                    int               counter, validCount;
//                    int               oldCount;
//                    TreePath          beginLeadPath = leadPath;
//                    Vector  cPaths = null;
//
//                    if(selection == null) {
//                        oldCount = 0;
//                    } else {
//                        oldCount = selection.length;
//                    }
//                    /* Determine the paths that aren't currently in the
//                       selection. */
//                    Hashtable<TreePath, Boolean> lastPaths = (Hashtable<TreePath, Boolean>) ReflectionUtil.get("lastPaths", this);
//                    Hashtable<TreePath, Boolean> uniquePaths = (Hashtable<TreePath, Boolean>) ReflectionUtil.get("uniquePaths", this);
//                    lastPaths.clear();
//                    for(counter = 0, validCount = 0; counter < newPathLength;
//                        counter++) {
//                        if(paths[counter] != null) {
//                            if (uniquePaths.get(paths[counter]) == null) {
//                                validCount++;
//                                if(cPaths == null) {
//                                    cPaths = new Vector();
//                                }
//                                Object o = null;
//                                try {
//                                    o = c.newInstance(paths[counter], true);
//                                } catch(Exception e) {
//                                    e.printStackTrace();
//                                }
//                                cPaths.addElement(o);
//                                uniquePaths.put(paths[counter], Boolean.TRUE);
//                                lastPaths.put(paths[counter], Boolean.TRUE);
//                            }
//                            leadPath = paths[counter];
//                        }
//                    }
//
//                    if(leadPath == null) {
//                        leadPath = beginLeadPath;
//                    }
//
//                    if(validCount > 0) {
//                        TreePath         newSelection[] = new TreePath[oldCount +
//                                                                      validCount];
//
//                        /* And build the new selection. */
//                        if(oldCount > 0) {
//                            System.arraycopy(selection, 0, newSelection, 0,
//                                             oldCount);
//                        }
//                        if(validCount != paths.length) {
//                            /* Some of the paths in paths are already in
//                               the selection. */
//                            Enumeration<TreePath> newPaths = lastPaths.keys();
//
//                            counter = oldCount;
//                            while (newPaths.hasMoreElements()) {
//                                newSelection[counter++] = newPaths.nextElement();
//                            }
//                        }
//                        else {
//                            System.arraycopy(paths, 0, newSelection, oldCount,
//                                             validCount);
//                        }
//
//                        selection = newSelection;
//
//                        insureUniqueness();
//
//                        updateLeadIndex();
//
//                        resetRowSelection();
//
//                        notifyPathChange(cPaths, beginLeadPath);
//                    } else {
//                        leadPath = beginLeadPath;
//                    }
//                    lastPaths.clear();
//                }
//            }
//        }
//    }
}
