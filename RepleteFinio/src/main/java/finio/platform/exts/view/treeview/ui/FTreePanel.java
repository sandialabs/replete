package finio.platform.exts.view.treeview.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import finio.core.FConst;
import finio.core.FUtil;
import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.platform.exts.view.treeview.ui.editors.EditCompletionEvent;
import finio.platform.exts.view.treeview.ui.editors.EditCompletionListener;
import finio.platform.exts.view.treeview.ui.nodes.NodeExpandable;
import finio.platform.exts.view.treeview.ui.nodes.NodeFTree;
import finio.platform.exts.view.treeview.ui.nodes.NodeNonTerminal;
import finio.platform.exts.view.treeview.ui.nodes.NodeWorld;
import finio.plugins.extpoints.View;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectRequest;
import finio.ui.view.SelectionContext;
import finio.ui.view.ViewPanel;
import finio.ui.worlds.WorldContext;
import replete.collections.ArrayUtil;
import replete.event.ChangeNotifier;
import replete.event.rnotif.RChangeEvent;
import replete.event.rnotif.RChangeListener;
import replete.ui.GuiUtil;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;
import replete.ui.text.RTextPane;
import replete.ui.tree.RTreeNode;
import replete.ui.tree.RTreePath;
import replete.ui.windows.Dialogs;


public class FTreePanel extends ViewPanel {


    ////////////
    // FIELDS //
    ////////////

    // UI
    private FTree tree;
    private FNode nRoot;
    private JPanel pnlNodeInfoParent;
    private JPanel pnlNodeInfo;

    // Node Info
    private JLabel lblNumSelected;
    private JLabel lblDepth;
    private JLabel lblTreeUOType;
    private JLabel lblTreeUOType2;
    private JLabel lblKeyType;
    private JLabel lblKeyTS;
    private JLabel lblValueHash;
    private JLabel lblValueType;
    private RTextPane txtValueTS;

    // Node Info Data
    private Set<String> kt;
    private Set<String> kts;
    private Set<String> vh;
    private Set<String> vt;
    private Set<String> vts;
    private Set<String> tuo;
    private Set<String> tuo2;
    private Set<String> dp;

    private JPanel pnlWorkingScope;
    private JLabel lblWorkingScope;
    private NonTerminal Mcontext;
    private SelectionContext<TreePanelSelectionContextSegment>[] editContextSelContexts;
    private boolean bulkSelection = false;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FTreePanel(final AppContext ac, WorldContext wc, Object K, Object V, View view) {
        this(ac, wc, K, V, view, Integer.MAX_VALUE, true);
    }
    public FTreePanel(final AppContext ac, WorldContext wc, Object K, Object V, View view, int maxInitialNodeDepth, boolean useWorldRealmNodes) {
        super(ac, wc, K, V, view);
        Mcontext = (NonTerminal) V;

        final FTreeOptionsModel optModel = new FTreeOptionsModel();

        NodeFTree uRoot;
        if(useWorldRealmNodes) {
            if(wc.getW() == Mcontext) {
                uRoot = new NodeWorld(Mcontext);
            } else {
                boolean realm = wc.getW().has(K) && wc.getW().get(K) == Mcontext;
                uRoot = new NodeNonTerminal(K, Mcontext, realm);
            }
        } else {
            uRoot = new NodeNonTerminal(K, Mcontext, false);
        }

        tree = new FTree(ac, wc, ac.getActionMap(), optModel, this);
        tree.addEditCompletionListener(new EditCompletionListener() {
            public void stateChanged(EditCompletionEvent e) {
                if(!checkEditCompletionEvent(e)) {
                    e.cancel();
                }
            }
        });
        tree.addStartEditListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setEditContextSelContexts(getSelectedValues(Integer.MAX_VALUE));
            }
        });
        tree.addCancelEditListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                for(SelectionContext<TreePanelSelectionContextSegment> C : editContextSelContexts) {
                    TreePanelSelectionContextSegment S = C.getSegment(0);
                    FNode nCurrent = S.getNode();
                    NodeFTree n = nCurrent.get();
                    n.setEditing(false);
                }
                setEditContextSelContexts(null);
            }
        });
        boolean meta = ac.getConfig().isShowNodeMeta();
        nRoot = new FNode(uRoot, maxInitialNodeDepth, 0, tree, meta);
        tree.setRoot(nRoot);

        pnlWorkingScope = Lay.BL(
            "W", Lay.lb("Working Scope: ", FinioImageModel.WORKING_SCOPE, "fg=992F2C,font=Verdana,bold"),
            "C", lblWorkingScope = Lay.lb("", "font=Verdana,fg=E24741,bold"),
            "bg=FFF7F7,eb=1"
        );

        JScrollPane scr;
        Lay.BLtg(this,
            "N", pnlWorkingScope,

            //"N", new TitlePanel("Tree", ImageLib.get(FinioImageModel.TREE_VIEW)),

            "C", pnlNodeInfoParent = Lay.BL(
                "C", scr = Lay.sp(tree, "opaque=false"),
                "opaque=false"
            )
        );
        scr.getViewport().setOpaque(false);

        pnlNodeInfo = Lay.BL(
            "N", Lay.FL(Lay.lb("<html><u>Node Information</u></html>", "font=Verdana")),
            "C", new NodeInfoForm(),
            "prefw=250,bg=EDFCFF,chtransp,mb=[1,316B2B]"
        );

        // So tree can be transparent but still initially readable
        // and can show off FPanel features.  Ideally, the tree
        // could also have user-configurable settings and could
        // themselves turn off the opacity in the UI.  Then
        // we wouldn't need this trick.
        Lay.hn(this, "bg=white");
        Lay.hn(tree, "opaque=false");

        addWorkingScopeListener(wsListener);

        ac.getActionMap().addAnyActionListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                fireAnyActionNotifier();
            }
        });

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                if(!bulkSelection) {              // Could also disconnect listeners
                    updateFromTreeSelection();
                }
            }
        });

        // Options

//        optModel.setShowSysMetaMaps(!optModel.isShowSysMetaMaps());
//        updateSelected();
//        optModel.addChangeListener(new ChangeListener() {
//            public void stateChanged(ChangeEvent e) {
//                btnSysMeta.setSelected(optModel.isShowSysMetaMaps());
//            }
//        });

        tree.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                fireSelectedNotifier();
            }
        });

        updateWorkingScopeErrorIcon();
    }

//    private class AcceptEditResult {
//        SelectionContext selContext;
//    }

    public void setRootVisible(boolean visible) {
        tree.setRootVisible(visible);
    }

    public void setShowWorkingScope(boolean show) {
        if(show) {
            if(pnlWorkingScope.getParent() == null) {
                add(pnlWorkingScope, BorderLayout.NORTH);
                updateUI();
            }
        } else {
            if(pnlWorkingScope.getParent() != null) {
                remove(pnlWorkingScope);
                updateUI();
            }
        }
    }

    protected boolean checkEditCompletionEvent(EditCompletionEvent e) {
//        FNode nCurrent = e.getnCurrent();
//        FNode nCurrentParent = e.getnCurrentParent();
        Object Kcurrent0 = e.getKcurrent();
        Object Vcurrent0 = e.getVcurrent();
        NodeFTree uNew = e.getuNew();
        boolean changesValue = e.isChangesValue();

        boolean keyChanged = !FUtil.equals(uNew.getK(), Kcurrent0);
        boolean valueChanged = !FUtil.equals(uNew.getV(), Vcurrent0);

        if(!keyChanged && !valueChanged) {
            e.setNoChange(true);
            return false;
        }

//        AcceptEditResult[] results = new AcceptEditResult[editContextSelContexts.length];

        int r = 0;
        for(SelectionContext<TreePanelSelectionContextSegment> C : editContextSelContexts) {
            TreePanelSelectionContextSegment S = C.getSegment(0);
            TreePanelSelectionContextSegment Sparent = C.getSegment(1);
            FNode nCurrent = S.getNode();
            FNode nCurrentParent = Sparent.getNode();
            boolean foundEditNode = nCurrent == e.getnCurrent();

            Object Kcurrent = nCurrent.getK();
            Object Vcurrent = nCurrent.getV();

            // Data type checking for parent (this error condition should never happen).
            NodeFTree uParent = nCurrentParent.getObject();
            Object V = uParent.getV();
            if(!(V instanceof NonTerminal)) {
                Dialogs.showWarning(ac.getWindow(), "The parent is not a map!");
                return false;
            }
            NonTerminal Mparent = (NonTerminal) V;

//            NodeFTree uNew;
//            try {
//                uNew = pnlEditor.getNewUserObject(nCurrent);
//            } catch(UnexpectedValueType ex) {
//                String t = e.getClazz().getSimpleName();
//                String n = StringUtil.n(t);
//                Dialogs.showWarning(uiController.getWindow(),
//                    "Value entered expected to be a" + n + " [" + t + "].  " +
//                    "Did you mean to convert to a string?", "Unexpected Value Type");
//                return false;
//            }

            // Key uniqueness checking.
            Object Knew;
            Object Vnew = uNew.getV();

            if(keyChanged) {
                Knew = uNew.getK();   // with replacement
                if(FUtil.isStringOrChar(Knew)) {
                    String s = "" + Knew;
                    if(s.contains(FConst.SYS_KV_SPECIAL)) {
                        s = s.replaceAll(Pattern.quote(FConst.SYS_KV_SPECIAL), cleanRepl((String) Kcurrent));  // Matcher.quoteReplacement??
                        Knew = s;
                        if(foundEditNode) {
                            e.setNewKey((String) Knew);
                        }
                    }
                }
            } else {
                Knew = Kcurrent;
            }

            if(valueChanged) {
                Vnew = uNew.getV();
                if(FUtil.isStringOrChar(Vnew)) {
                    String s = "" + Vnew;
                    if(s.contains(FConst.SYS_KV_SPECIAL)) {
                        s = s.replaceAll(Pattern.quote(FConst.SYS_KV_SPECIAL), cleanRepl((String) Vcurrent));
                        Vnew = s;
                        if(foundEditNode) {
                            e.setNewValue((String) Vnew);
                        }
                    }
                }
            } else {
                Vnew = Vcurrent;
            }

            if(keyChanged && !FUtil.equals(Knew, Kcurrent) && Mparent.containsKey(Knew)) {
                Dialogs.showWarning(ac.getWindow(),
                    "This key already exists in this map.", "Could not save change.");
                return false;
            }

            if(keyChanged && !FUtil.equals(Knew, Kcurrent)) {
                Mparent.changeKey(Kcurrent, Knew);
            }

            if(changesValue && valueChanged) {
                Mparent.put(Knew, Vnew);         // TODO: This is getting called even with you edit a key.
            }

            // TODO: Need to check if value actually changed or not, only set if changed.
            // TODO: Need to perform @@ replacement on value
            // TODO: Need smarter @@ replacement... upper/lower/regex/slicing/conversion
            // TODO: Need separate pass for validation and actuation.
            // TODO: Never leave nodes "editing" if editing is stopped -- need to have this in editing canceled as well!

            NodeFTree n = nCurrent.get();
            n.setEditing(false);

            tree.addEditedNode(nCurrent, S.getPath());

//            results[r++] = new AcceptEditResult();
        }
//
//        for(AcceptEditResult result : results) {
//
//        }

        setEditContextSelContexts(null);

        return true;// todo what to return?
    }

    private String cleanRepl(String repl) {
        return
            repl.replaceAll("\\\\", "\\\\\\\\")
                .replaceAll("\\$", "\\\\\\$");
    }

    private RChangeListener wsListener = new RChangeListener() {
        public void handle(RChangeEvent e) {
            updateWorkingScopeErrorIcon();
        }
    };
    public void updateWorkingScopeErrorIcon() {
        KeyPath scope = getWorkingScope();
        String path = scope.toUnixString();
        lblWorkingScope.setText(path);
    }


    //////////
    // MISC //
    //////////

    private void setEditContextSelContexts(SelectionContext<TreePanelSelectionContextSegment>[] edit) {
        editContextSelContexts = edit;
        fireEditContextChangedNotifier();
    }

    public FTree getTree() {
        return tree;
    }

//    public void updateWorkingScopeErrorIcon() {
//        KeyPath scope = uiController.getWorld().getWorkingScope();
//
//        // Weird location for this, but it's here...
//        String path = scope.toUnixString();
//        String countStatus =
//            "(E " +
//                ANode.getConstructedNodes() + "/" + NodeATree.getConstructedNodes() +
//            ", C " +
//                (nRoot.getCountAll() + 1) + "/" + (uiController.getWorld().sizeAll() + 1) +
//            ", L " +
//                AMap.mcAdded + "/" + AMap.mcRemoved +
//            ")";
//        }
//    }

    private void updateNodeInfoFromSelected() {
        boolean enabled = ac.getConfig().isNodeInfoEnabled();
        if(!enabled || tree.getSelectionCount() == 0) {
            pnlNodeInfoParent.remove(pnlNodeInfo);
            pnlNodeInfoParent.updateUI();

        } else {
            Lay.BLtg(pnlNodeInfoParent, "E", pnlNodeInfo);
            pnlNodeInfoParent.updateUI();
            lblNumSelected.setText(tree.getSelectionCount() + "");

            setNodeInfoLabelComputing(lblDepth);
            setNodeInfoLabelComputing(lblTreeUOType);
            setNodeInfoLabelComputing(lblTreeUOType2);
            setNodeInfoLabelComputing(lblKeyType);
            setNodeInfoLabelComputing(lblKeyTS);
            setNodeInfoLabelComputing(lblValueHash);
            setNodeInfoLabelComputing(lblValueType);
            setNodeInfoLabelComputing(txtValueTS);

            // TODO: How to not draw the ... until just a few seconds after this
            // tread takes too long (so as not to have to draw them at all
            // in the case when the thread finishes really fast).
            // TODO: How to kill previous threads if a new request comes in
            Thread t = new Thread("Selected Tree Node Updater") {
                @Override
                public void run() {
                    kt = new LinkedHashSet<>();
                    kts = new LinkedHashSet<>();
                    vh = new LinkedHashSet<>();
                    vt = new LinkedHashSet<>();
                    vts = new LinkedHashSet<>();
                    tuo = new LinkedHashSet<>();
                    tuo2 = new LinkedHashSet<>();
                    dp = new LinkedHashSet<>();
                    if(tree.getSelectionCount() == 0) {
                        // Witnessed this happening
                        return;
                    }
                    for(TreePath path : tree.getSelectionPaths()) {
                        FNode nSel = (FNode) path.getLastPathComponent();
                        dp.add("" + nSel.getANodeDepth());
                        NodeFTree uSel = nSel.getObject();
                        if(path.getPathCount() == 1) {
                            kt.add("<html><i>(N/A)</i></html>");
                            kts.add("<html><i>(N/A)</i></html>");
                        } else {
                            kt.add(wrapNullType(nSel.getK()));
                            kts.add(wrapNullString(nSel.getK()));
                        }
                        vh.add(nSel.getV() == null ? "N/A" : nSel.getV().hashCode() + "");
                        vt.add(wrapNullType(nSel.getV()));
                        vts.add(wrapNullString(nSel.getV()));
                        tuo.add(uSel.getClass().getSimpleName());
                        String props =
                            "A" + (uSel.isAnchor()?"+":"-") +
                            "  H" + (uSel.isHover()?"+":"-") +
                            "  E" + (uSel.isEditing()?"+":"-") +
                            "  WS" + (uSel.isWorkingScope()?"+":"-");
                        if(uSel instanceof NodeExpandable) {
                            props += "  P" + (((NodeExpandable) uSel).isPaused()?"+":"-");
                            props += "  S" + (nSel.getSimplified() != null ? "+" : "-");
                        }
                        tuo2.add(props);
                    }

                    GuiUtil.safe(new Runnable() {
                        @Override
                        public void run() {
                            setNodeInfoLabel(lblDepth, dp);
                            setNodeInfoLabel(lblTreeUOType, tuo);
                            setNodeInfoLabel(lblTreeUOType2, tuo2);
                            setNodeInfoLabel(lblKeyType, kt);
                            setNodeInfoLabel(lblKeyTS, kts);
                            setNodeInfoLabel(lblValueHash, vh);
                            setNodeInfoLabel(lblValueType, vt);
                            setNodeInfoLabel(txtValueTS, vts);
                        }
                    });
                }
            };
            t.start();
        }
    }
    private void setNodeInfoLabelComputing(RTextPane txt) {
        txt.setText("...");
        txt.setFont(txt.getFont().deriveFont(Font.ITALIC));
    }
    private void setNodeInfoLabelComputing(JLabel lbl) {
        lbl.setText("<html><i>...</i></html>");
    }
    private void setNodeInfoLabel(JLabel lbl, Set<String> values) {
        if(values.size() == 1) {
//            java.util.ConcurrentModificationException
//            at java.util.LinkedHashMap$LinkedHashIterator.nextEntry(LinkedHashMap.java:394)
//            at java.util.LinkedHashMap$KeyIterator.next(LinkedHashMap.java:405)
//            at java.util.AbstractCollection.finishToArray(AbstractCollection.java:230)
//            at java.util.AbstractCollection.toArray(AbstractCollection.java:198)
//            at finio.platform.exts.view.treeview.ui.FTreePanel.setNodeInfoLabel(FTreePanel.java:474)
            lbl.setText("<html>" + values.toArray(new String[0])[0] + "</html>");
        } else {
            lbl.setText("<html><i>(multiple)</i></html>");
        }
    }
    private void setNodeInfoLabel(RTextPane txt, Set<String> values) {
        if(values.size() == 1) {
            txt.setText(values.toArray(new String[0])[0]);
            txt.setFont(txt.getFont().deriveFont(Font.PLAIN));
        } else {
            txt.setText("(multiple)");
            txt.setFont(txt.getFont().deriveFont(Font.ITALIC));
        }
        txt.setCaretPosition(0);
    }
    private String wrapNullType(Object KorV) {
        String result;
        if(KorV == null) {
            result = "<i>" + FUtil.NULL_TEXT + "</i>";
        } else {
            result = KorV.getClass().getSimpleName();
        }
        return result;
    }
    private String wrapNullString(Object KorV) {
        String result;
        if(KorV == null) {
            result = "<i>" + FUtil.NULL_TEXT + "</i>";
        } else if(KorV instanceof NonTerminal) {
            try {
                result = ((NonTerminal) KorV).toString(10000);
            } catch(Exception e) {
                result = "<ERROR>";  // Fix this
            }
        } else {
            result = KorV.toString();
        }
        return result;
    }
    public KeyPath[] getSelectedKeyPaths() {
        List<KeyPath> Ps = new ArrayList<>();
        if(tree.getSelectionCount() != 0) {
            for(TreePath Ptree : tree.getSelectionPaths()) {
                Ps.add(tree.convertTreePathToKeyPath(Ptree));
            }
        }
        return Ps.toArray(new KeyPath[0]);
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    private ChangeNotifier anyActionNotifier = new ChangeNotifier(this);
    @Override
    public void addAnyActionListener(ChangeListener listener) {
        anyActionNotifier.addListener(listener);
    }
    private void fireAnyActionNotifier() {
        anyActionNotifier.fireStateChanged();
    }

    private ChangeNotifier selectedNotifier = new ChangeNotifier(this);
    @Override
    public void addSelectedListener(ChangeListener listner) {
        selectedNotifier.addListener(listner);
    }
    @Override
    public void removeSelectedListener(ChangeListener listener) {
        selectedNotifier.removeListener(listener);
    }
    private void fireSelectedNotifier() {
        selectedNotifier.fireStateChanged();
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void focus() {
        tree.requestFocusInWindow();
    }


    //////////
    // MISC //
    //////////

    @Override
    public void init() {
        tree.expandToLevel(1);
    }
    @Override
    public void increaseFont() {
        tree.increaseFont();
    }
    @Override
    public void decreaseFont() {
        tree.decreaseFont();
    }
    @Override
    public void refresh() {
        tree.updateUI();
        tree.getModel().reload();
        tree.expandToLevel(1);
    }


    ///////////////
    // SELECTION //
    ///////////////

    @Override
    public SelectionContext[] getSelectedValues(int reverseDepth) {
        RTreePath[] paths = tree.getSelPaths();
        if(paths == null) {
            return new SelectionContext[0];
        }
        SelectionContext[] Cs = new SelectionContext[paths.length];
        int c = 0;
        for(RTreePath pSel : paths) {
            FNode nSel = (FNode) pSel.getLast();
            SelectionContext C =
                new SelectionContext();
            int d = 0;
            while(nSel != null && d < reverseDepth) {
                Object K = nSel.getK();
                Object V = nSel.getV();
                TreePanelSelectionContextSegment segment =
                    new TreePanelSelectionContextSegment(K, V, nSel, pSel);
                C.addSegment(segment);
                nSel = (FNode) nSel.getRParent();
                if(nSel != null) {
                    pSel = nSel.getRPath();   // Still disappointed in TreePath impl
                }
                d++;
            }
            Cs[c++] = C;
        }
        return Cs;
    }
    @Override
    public void clearSelection() {
        tree.clearSelection();
    }
    @Override
    public void addSelection(SelectRequest selectRequest) {
        if(selectRequest.getAction() == SelectAction.ROOT) {
            tree.getSelectionModel().addSelectionPath(nRoot.getRPath());

        } else {

            TreePanelSelectionContextSegment S =
                (TreePanelSelectionContextSegment)
                    selectRequest.getContext().getSegment(0);
            RTreePath pSel = S.getPath();
            FNode nParent;

            switch(selectRequest.getAction()) {
                case SELF:
                    tree.getSelectionModel().addSelectionPath(pSel);
                    break;

                case CHILD:
                    FNode nSel = S.getNode();
                    FNode nNew = nSel.getChildByKey(selectRequest.getArgs()[0]);
                    if(nNew != null) {    // One or more ancestor nodes of nNew might be paused
                        TreePath newPath = pSel.pathByAddingChild(nNew);
                        tree.getSelectionModel().addSelectionPath(newPath);
                    }
                    break;

                case CHILDREN:
                    nSel = S.getNode();
                    // Can be made even faster?
                    TreePath[] paths = new TreePath[nSel.getCount()];
                    for(int i = 0; i < nSel.getCount(); i++) {
                        FNode nChild = (FNode) nSel.getChildAt(i);
                        RTreePath path = nChild.getRPath();
                        paths[i] = path;
                    }
                    tree.getSelectionModel().addSelectionPaths(paths);
                    break;

                case SIBLING:
                    if(selectRequest.getContext().getSegmentCount() > 1) {
                        TreePanelSelectionContextSegment Sparent =
                            (TreePanelSelectionContextSegment)
                                selectRequest.getContext().getSegment(1);
                        RTreePath pParent = Sparent.getPath();
                        Object Ksib = ArrayUtil.isBlank(selectRequest.getArgs()) ? null :
                            selectRequest.getArgs()[0];
                        for(RTreeNode child : Sparent.getNode()) {
                            FNode fChild = (FNode) child;
                            if(Ksib == null || FUtil.equals(Ksib, fChild.getK())) {
                                tree.getSelectionModel().addSelectionPath(
                                    pParent.pathByAddingChild(child));
                            }
                        }
                    }
                    break;

                case PARENT:
                    if(selectRequest.getContext().getSegmentCount() > 1) {
                        TreePanelSelectionContextSegment Sparent =
                            (TreePanelSelectionContextSegment)
                                selectRequest.getContext().getSegment(1);
                        RTreePath pParent = Sparent.getPath();
                        tree.getSelectionModel().addSelectionPath(pParent);
                    }
                    break;

                case GRANDPARENT:
//                    Object[] dataG = selectRequest.getContext().getData(2);
//                    FNode nGrandparent = (FNode) dataG[1];
//                    nNew = nGrandparent.getChildByKey(selectRequest.getArgs()[0]);
//                    tree.getSelectionModel().addSelectionPath(nNew.getTPath());
                    break;

                default:
                    // Won't happen
                    break;
            }
        }
    }
    @Override
    public void beginBulkSelection() {
        bulkSelection = true;
        tree.setBulkSelection(bulkSelection);
    }
    @Override
    public void endBulkSelection() {
        bulkSelection = false;
        tree.setBulkSelection(bulkSelection);
        updateFromTreeSelection();
    }
    private void updateFromTreeSelection() {
        updateNodeInfoFromSelected();
        fireSelectedValuesNotifier();
    }


    ///////////////
    // EXPANSION //
    ///////////////

    @Override
    public void expand(FNode node) {
        tree.expand(node);
    }
    @Override
    public void expandToLevel(FNode node, int level) {
        tree.expandToLevel(node, level);
    }
    @Override
    public void setShiftForEditValue(boolean editShift) {
        tree.setShiftForEditValue(editShift);
    }
    @Override
    public void addExpand(ExpandRequest expandRequest) {
        if(expandRequest.getAction() == SelectAction.ROOT) {
            exp(expandRequest, nRoot.getRPath(), nRoot);

        } else {
            TreePanelSelectionContextSegment S =
                (TreePanelSelectionContextSegment)
                    expandRequest.getContext().getSegment(0);
            RTreePath pSel = S.getPath();
            FNode nSel = S.getNode();

            switch(expandRequest.getAction()) {
                case SELF:
                    exp(expandRequest, pSel, nSel);
                    break;

                case CHILD:
                    FNode nNew = nSel.getChildByKey(expandRequest.getArgs()[0]);
                    exp(expandRequest, new RTreePath(pSel.pathByAddingChild(nNew)), nNew);
                    break;

                case CHILDREN:
                    break;

                case SIBLING:
                    TreePanelSelectionContextSegment Sparent =
                        (TreePanelSelectionContextSegment)
                            expandRequest.getContext().getSegment(1);
                    FNode nParent = Sparent.getNode();
                    nNew = nParent.getChildByKey(expandRequest.getArgs()[0]);
                    exp(expandRequest, nNew.getRPath(), nNew);
                    break;

                case PARENT:
                    if(true) {
                        throw new RuntimeException("not impl...");
                    }
                    break;

                case GRANDPARENT:
                    break;

                default:
                    // Won't happen
                    break;
            }
        }
    }

    private void exp(ExpandRequest expandRequest, RTreePath p, FNode n) {
        if(expandRequest.isAll()) {
            tree.initExpandAllLimiter();
            boolean all = tree.expandAll(p);
            if(!all) {
                Dialogs.showWarning(tree,
                    "Cannot expand more than 10,000 nodes at a time.",
                    "Expand Error");
            }

        } else {
            if(expandRequest.getLevel() == -1) {
                tree.expand(p);
            } else {
                tree.expandToLevel(n, expandRequest.getLevel());
            }
        }
    }


    /////////////
    // EDITING //
    /////////////

    @Override
    public void startEditing() {

        // Set all selected nodes' user objects to an editing
        // state and choose the path at which the user will
        // perform the editing for the entire edit context.
        RTreePath Pstart = tree.getSelPath();
        RTreePath Plead = tree.getLeadSelectionRPath();

        for(RTreePath P : tree.getSelPaths()) {
            NodeFTree u = P.get();
            u.setEditing(true);

            if(P.equals(Plead)) {
                Pstart = Plead;
            }
        }

        tree.doEdit(Pstart);
    }
    @Override
    public void cancelEditing() {
        tree.cancelEditing();
    }
    @Override
    public SelectionContext[] getEditContextContexts() {
        return editContextSelContexts;
    }


    /////////////////
    // INNER CLASS //
    /////////////////

    private class NodeInfoForm extends RPanel {
        public NodeInfoForm() {
            Lay.BLtg(this, "N",
                Lay.BxL(
                    Lay.BL(
                        "W", Lay.BL("N", Lay.lb("<html><b># Selected:</b></html>", "fg=316B2B,prefw=105,font=Verdana")),
                        "C", lblNumSelected = Lay.lb()
                    ),
                    Lay.BL(
                        "W", Lay.BL("N", Lay.lb("<html><b>Depth:</b></html>", "fg=316B2B,prefw=105,font=Verdana")),
                        "C", lblDepth = Lay.lb()
                    ),
                    Lay.BL(
                        "W", Lay.BL("N", Lay.lb("<html><b>Type:</b></html>", "fg=316B2B,prefw=105,font=Verdana")),
                        "C", lblTreeUOType = Lay.lb()
                    ),
                    Lay.BL(
                        "W", Lay.BL("N", Lay.lb("<html><b>Properties:</b></html>", "fg=316B2B,prefw=105,font=Verdana")),
                        "C", lblTreeUOType2 = Lay.lb()
                    ),
                    Lay.BL(
                        "W", Lay.BL("N", Lay.lb("<html><b>Key Type:</b></html>", "fg=316B2B,prefw=105,font=Verdana")),
                        "C", lblKeyType = Lay.lb()
                    ),
                    Lay.BL(
                        "W", Lay.BL("N", Lay.lb("<html><b>Key String:</b></html>", "fg=316B2B,prefw=105,font=Verdana")),
                        "C", lblKeyTS = Lay.lb()
                    ),
                    Lay.BL(
                        "W", Lay.BL("N", Lay.lb("<html><b>Value Hash:</b></html>", "fg=316B2B,prefw=105,font=Verdana")),
                        "C", lblValueHash = Lay.lb()
                    ),
                    Lay.BL(
                        "W", Lay.BL("N", Lay.lb("<html><b>Value Type:</b></html>", "fg=316B2B,prefw=105,font=Verdana")),
                        "C", lblValueType = Lay.lb()
                    ),
                    Lay.BL(
                        "W", Lay.lb("<html><b>Value String:</b></html>", "fg=316B2B,prefw=105,font=Verdana"),
                        "C", Lay.p()
                    )
                ),
                "C", Lay.sp(txtValueTS = Lay.txp("", "editable=false")),
                "eb=5"
            );

            lblTreeUOType2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lblTreeUOType2.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    Set<String> msgs = new LinkedHashSet<>();
                    for(TreePath path : tree.getSelectionPaths()) {
                        FNode nSel = (FNode) path.getLastPathComponent();
                        NodeFTree uSel = nSel.getObject();
                        String msg =
                            "A" + (uSel.isAnchor()?"+":"-") + " = Node " + isIsNot(uSel.isAnchor()) + " Anchored\n" +
                            "H" + (uSel.isHover()?"+":"-") + " = Node " + isIsNot(uSel.isHover()) + " Highlighted\n" +
                            "E" + (uSel.isEditing()?"+":"-") + " = Node " + isIsNot(uSel.isEditing()) + " Being Edited\n" +
                            "WS" + (uSel.isWorkingScope()?"+":"-") + " = Node " + isIsNot(uSel.isWorkingScope()) + " The Working Scope";
                        if(uSel instanceof NodeExpandable) {
                            boolean psd = ((NodeExpandable) uSel).isPaused();
                            msg += "\nP" + (psd?"+":"-") + " = Node " + isIsNot(psd) + " Paused";
                            boolean simp = nSel.getSimplified() != null;
                            msg += "\nS" + (simp?"+":"-") + " = Node " + isIsNot(psd) + " Simplified";
                        }
                        msgs.add(msg);
                    }

                    String message;
                    if(msgs.size() == 1) {
                        message = msgs.toArray(new String[0])[0];
                    } else {
                        message = "(multiple)";
                    }

                    Dialogs.showMessage(ac.getWindow(), message,
                        "Node Properties");
                }
            });
        }

        private String isIsNot(boolean b) {
            return b ? "Is" : "Is Not";
        }
    }
}
