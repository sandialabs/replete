package finio.platform.exts.view.tabs.ui;

import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import finio.core.FImages;
import finio.core.FUtil;
import finio.core.NonTerminal;
import finio.core.events.KeyAddedEvent;
import finio.core.events.KeyAddedListener;
import finio.core.events.KeyChangedEvent;
import finio.core.events.KeyChangedListener;
import finio.core.events.KeyRemovedEvent;
import finio.core.events.KeyRemovedListener;
import finio.core.events.MapBatchChangedEvent;
import finio.core.events.MapBatchChangedListener;
import finio.core.events.MapClearedEvent;
import finio.core.events.MapClearedListener;
import finio.core.events.ValueChangedEvent;
import finio.core.events.ValueChangedListener;
import finio.platform.exts.view.treeview.ui.FNode;
import finio.plugins.extpoints.View;
import finio.ui.actions.FActionMap;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectRequest;
import finio.ui.view.SelectionContext;
import finio.ui.view.ViewPanel;
import finio.ui.worlds.WorldContext;
import replete.event.ChangeNotifier;
import replete.text.StringUtil;
import replete.ui.lay.Lay;
import replete.ui.menu.RMenu;
import replete.ui.tabbed.HeaderContextMenuEvent;
import replete.ui.tabbed.HeaderContextMenuListener;
import replete.ui.tabbed.RTabbedPane;
import replete.ui.uiaction.ActionValidator;
import replete.ui.uiaction.HoverEvent;
import replete.ui.uiaction.HoverListener;
import replete.ui.uiaction.UIAction;
import replete.ui.uiaction.UIActionPopupMenu;


public class FTabbedPanel extends ViewPanel {


    ////////////
    // FIELDS //
    ////////////

    // UI
    private RTabbedPane tabs;

    // Core
    private FActionMap actionMap;
    private NonTerminal Mcontext;

    // Selection

//    private SelectionContext<TreePanelSelectionContextSegment>[] editContextSelContexts;
    private boolean bulkSelection = false;

    private ANodeMapBatchChangedListener mapBatchChangedListener;
    private ANodeMapClearedListener mapClearedListener;
    private ANodeKeyAddedListener keyAddedListener;
    private ANodeKeyRemovedListener keyRemovedListener;
    private ANodeKeyChangedListener keyChangedListener;
    private ANodeValueChangedListener valueChangedListener;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FTabbedPanel(final AppContext ac, WorldContext wc, Object K, Object V, View view) {
        super(ac, wc, K, V, view);
        Mcontext = (NonTerminal) V;
        actionMap = ac.getActionMap();

        Lay.BLtg(this,
            "C", tabs = Lay.TBL("borders")         // "default closable" not applicable here
        );

        tabs.addChangeListener(tabChangeListener);
        tabs.addHeaderContextMenuListener(headerContextMenuListener);

        subscribe();
        initialize();
//        updateWorkingScopeErrorIcon();
    }


    ///////////////
    // LISTENERS //
    ///////////////

    private ChangeListener tabChangeListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
//            int index = tabs.getSelectedIndex();
//            Lay.hn(TabbedViewsPanel.this, "bg=" + (index == -1 ? "238" : "100"));
//            ViewContainerPanel pnlViewCont = (index == -1) ? null :
//                (ViewContainerPanel) tabs.getHeaderPanelAt(index).getAdditionalInfo();
//            fireViewSelectionNotifier(pnlViewCont, index);
            System.out.println(tabs.getSelectedIndex());
        }
    };
    private HeaderContextMenuListener headerContextMenuListener = new HeaderContextMenuListener() {
        public void stateChanged(HeaderContextMenuEvent e) {
//            JPopupMenu mnuPopup = new UIActionPopupMenu(actionMap);
//            actionMap.cleanUp();
            JPopupMenu mnuPopup = createPopupMenu();
            mnuPopup.show(e.getComponent(), e.getX(), e.getY());
        }
    };

    protected JPopupMenu createPopupMenu() {
        SelectionContext[] Cs = ac.getSelectedValues();
        final JLabel lblInfo = Lay.lb(
            getSelectedText(Cs, "Selected         "),
            FinioImageModel.SELECTED,
            "eb=6l"
        );
        final JLabel lblInfo2 = Lay.lb("", FinioImageModel.ACTIONABLE, "eb=6l");

        lblInfo2.setForeground(Lay.clr("006806"));
        lblInfo2.setText("... Actionable");

        JPanel pnlInfo = Lay.GL(
            2, 1,
            lblInfo, lblInfo2,
            "eb=20r"
        );

        final UIActionPopupMenu mnuPopup = new UIActionPopupMenu(actionMap, pnlInfo);
        mnuPopup.addHoverListener(createHoverListener(lblInfo, lblInfo2));

//        if(isSelection()) {
//            JMenuItem mnuAnchor = actionMap.getPopupMenuComponent("toggle-anchor");
//            if(mnuAnchor != null) {
//                mnuAnchor.setText(shouldUnanchor() ? "Un&anchor" : "&Anchor");
//            }
//            JMenuItem mnuPause = actionMap.getPopupMenuComponent("toggle-pause");
//            if(mnuPause != null) {
//                mnuPause.setText(shouldUnpause() ? "Un&pause" : "&Pause");
//            }
//        }
        return mnuPopup;
    }

    private HoverListener createHoverListener(final JLabel lblInfo, final JLabel lblInfo2) {
        return new HoverListener() {
            public void stateChanged(HoverEvent e) {
                UIAction action = actionMap.getAction(e.getId());

                if(e.getType().equals("exit")) {
                    return;

                } else if(e.getComponent() instanceof RMenu) {
                    lblInfo2.setText("... Actionable");
                    return;
                }

                ActionValidator validator = action.getValidator();
                SelectionContext[] Cs;
                if(validator == null) {
                    Cs = ac.getSelectedValues();
                    lblInfo2.setText(getSelectedText(Cs, "Actionable"));

                } else {
                    validator.isValid(e.getId());
                    if(validator instanceof AActionValidator) {
                        List<SelectionContext> sel = ((AActionValidator) validator).getLastValidResults();
                        if(sel == null) {
                            lblInfo2.setText("Any Actionable");

                        } else {
                            Cs = sel.toArray(new SelectionContext[0]);
                            lblInfo2.setText(getSelectedText(Cs, "Actionable"));
                        }

                    } else {
                        Cs = new SelectionContext[0];
                        lblInfo2.setText("???2");
                    }
                }
            }
        };
    }

    private String getSelectedText(SelectionContext[] Cs, String modifier) {
        int nt = 0;
        int t = 0;
        for(SelectionContext C : Cs) {
            if(FUtil.isTerminal(C.getV())) {
                t++;
            } else {
                nt++;
            }
        }

        String msg = "";
        if(nt != 0) {
            msg += nt + " Non-Terminal" + StringUtil.s(nt);
        }
        if(t != 0) {
            if(!msg.isEmpty()) {
                msg += ", ";
            }
            msg += t + " Terminal" + StringUtil.s(t);
        }
        if(nt == 0 && t == 0) {
            msg = "0";
        }
        return msg + " " + modifier;
    }

    private void initialize() {
        for(Object K : Mcontext.K()) {
            Object V = Mcontext.get(K);
            addTabFromPair(K, V);
        }
    }

    private void addTabFromPair(Object K, Object V) {
        Icon icon;
        if(FUtil.isNonTerminal(V)) {
            icon = FImages.createIconForNonTerminal(K, V, false, false, false, false);
        } else {
            icon = FImages.createIconForTerminal(K, V, false);
        }
        tabs.addTab(K.toString(), icon, Lay.GBL(Lay.lb(FUtil.toDiagnosticString(V))), null, K);
    }

    private void subscribe() {
        NonTerminal M = Mcontext;

        if(mapClearedListener != null) {
            throw new RuntimeException(
                FNode.class.getSimpleName() + ": Attempting to subscribe to the non-terminal value again.");
        }

        //M.addMapChangedListener(mapChangedListener = new ANodeMapChangedListener());   Not needed right now
        M.addMapBatchChangedListener(mapBatchChangedListener = new ANodeMapBatchChangedListener());
        M.addMapClearedListener(mapClearedListener = new ANodeMapClearedListener());
        M.addKeyAddedListener(keyAddedListener = new ANodeKeyAddedListener());
        M.addKeyRemovedListener(keyRemovedListener = new ANodeKeyRemovedListener());
        M.addKeyChangedListener(keyChangedListener = new ANodeKeyChangedListener());
        M.addValueChangedListener(valueChangedListener = new ANodeValueChangedListener());
    }

//    private boolean checkEditCompletionEvent(EditCompletionEvent e) {
////        FNode nCurrent = e.getnCurrent();
////        FNode nCurrentParent = e.getnCurrentParent();
//        Object Kcurrent0 = e.getKcurrent();
//        Object Vcurrent0 = e.getVcurrent();
//        NodeFTree uNew = e.getuNew();
//        boolean changesValue = e.isChangesValue();
//
//        boolean keyChanged = !AUtil.equals(uNew.getK(), Kcurrent0);
//        boolean valueChanged = !AUtil.equals(uNew.getV(), Vcurrent0);
//
//        if(!keyChanged && !valueChanged) {
//            e.setNoChange(true);
//            return false;
//        }
//
////        AcceptEditResult[] results = new AcceptEditResult[editContextSelContexts.length];
//
//        int r = 0;
//        for(SelectionContext<TreePanelSelectionContextSegment> C : editContextSelContexts) {
//            TreePanelSelectionContextSegment S = C.getSegment(0);
//            TreePanelSelectionContextSegment Sparent = C.getSegment(1);
//            FNode nCurrent = S.getNode();
//            FNode nCurrentParent = Sparent.getNode();
//            boolean foundEditNode = nCurrent == e.getnCurrent();
//
//            Object Kcurrent = nCurrent.getK();
//            Object Vcurrent = nCurrent.getV();
//
//            // Data type checking for parent (this error condition should never happen).
//            NodeFTree uParent = nCurrentParent.getObject();
//            Object V = uParent.getV();
//            if(!(V instanceof NonTerminal)) {
//                Dialogs.showWarning(ac.getWindow(), "The parent is not a map!");
//                return false;
//            }
//            NonTerminal Mparent = (NonTerminal) V;
//
////            NodeFTree uNew;
////            try {
////                uNew = pnlEditor.getNewUserObject(nCurrent);
////            } catch(UnexpectedValueType ex) {
////                String t = e.getClazz().getSimpleName();
////                String n = StringUtil.n(t);
////                Dialogs.showWarning(uiController.getWindow(),
////                    "Value entered expected to be a" + n + " [" + t + "].  " +
////                    "Did you mean to convert to a string?", "Unexpected Value Type");
////                return false;
////            }
//
//            // Key uniqueness checking.
//            Object Knew;
//            Object Vnew = uNew.getV();
//
//            if(keyChanged) {
//                Knew = uNew.getK();   // with replacement
//                if(AUtil.isString(Knew)) {
//                    String s = "" + Knew;
//                    if(s.contains(AConst.SYS_KV_SPECIAL)) {
//                        s = s.replaceAll(Pattern.quote(AConst.SYS_KV_SPECIAL), cleanRepl((String) Kcurrent));
//                        Knew = s;
//                        if(foundEditNode) {
//                            e.setNewKey((String) Knew);
//                        }
//                    }
//                }
//            } else {
//                Knew = Kcurrent;
//            }
//
//            if(valueChanged) {
//                Vnew = uNew.getV();
//                if(AUtil.isString(Vnew)) {
//                    String s = "" + Vnew;
//                    if(s.contains(AConst.SYS_KV_SPECIAL)) {
//                        s = s.replaceAll(Pattern.quote(AConst.SYS_KV_SPECIAL), cleanRepl((String) Vcurrent));
//                        Vnew = s;
//                        if(foundEditNode) {
//                            e.setNewValue((String) Vnew);
//                        }
//                    }
//                }
//            } else {
//                Vnew = Vcurrent;
//            }
//
//            if(keyChanged && !AUtil.equals(Knew, Kcurrent) && Mparent.containsKey(Knew)) {
//                Dialogs.showWarning(ac.getWindow(),
//                    "This key already exists in this map.", "Could not save change.");
//                return false;
//            }
//
//            if(keyChanged && !AUtil.equals(Knew, Kcurrent)) {
//                Mparent.changeKey(Kcurrent, Knew);
//            }
//
//            if(changesValue && valueChanged) {
//                Mparent.put(Knew, Vnew);         // TODO: This is getting called even with you edit a key.
//            }
//
//            // TODO: Need to check if value actually changed or not, only set if changed.
//            // TODO: Need to perform @@ replacement on value
//            // TODO: Need smarter @@ replacement... upper/lower/regex/slicing/conversion
//            // TODO: Need separate pass for validation and actuation.
//            // TODO: Never leave nodes "editing" if editing is stopped -- need to have this in editing canceled as well!
//
//            NodeFTree n = nCurrent.get();
//            n.setEditing(false);
//
//            tree.addEditedNode(nCurrent, S.getPath());
//
////            results[r++] = new AcceptEditResult();
//        }
////
////        for(AcceptEditResult result : results) {
////
////        }
//
//        setEditContextSelContexts(null);
//
//        return true;// todo what to return?
//    }

//    private String cleanRepl(String repl) {
//        return
//            repl.replaceAll("\\\\", "\\\\\\\\")
//                .replaceAll("\\$", "\\\\\\$");
//    }

//    private RChangeListener wsListener = new RChangeListener() {
//        public void handle(RChangeEvent e) {
//            updateWorkingScopeErrorIcon();
//        }
//    };


    //////////
    // MISC //
    //////////

//    private void setEditContextSelContexts(SelectionContext<TreePanelSelectionContextSegment>[] edit) {
//        editContextSelContexts = edit;
//        fireEditContextChangedNotifier();
//    }

    public RTabbedPane getTabs() {
        return tabs;
    }

//    public KeyPath[] getSelectedKeyPaths() {
//        List<KeyPath> Ps = new ArrayList<>();
//        if(tree.getSelectionCount() != 0) {
//            for(TreePath Ptree : tree.getSelectionPaths()) {
//                Ps.add(tree.convertTreePathToKeyPath(Ptree));
//            }
//        }
//        return Ps.toArray(new KeyPath[0]);
//    }


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
        tabs.focus();
    }


    //////////
    // MISC //
    //////////

//    @Override
//    public void init() {
//        tree.expandToLevel(1);
//    }
//    @Override
//    public void increaseFont() {
//        tree.increaseFont();
//    }
//    @Override
//    public void decreaseFont() {
//        tree.decreaseFont();
//    }
//    @Override
//    public void refresh() {
//        tree.updateUI();
//        tree.getModel().reload();
//        tree.expandToLevel(1);
//    }


    ///////////////
    // SELECTION //
    ///////////////

    @Override
    public SelectionContext[] getSelectedValues(int reverseDepth) {
        if(tabs.getTabCount() == 0) {
            return new SelectionContext[0];
        }
        SelectionContext[] Cs = new SelectionContext[1];
        Object K = tabs.getKeyAt(tabs.getSelectedIndex());
        SelectionContext C = new SelectionContext();
        TabbedPanelSelectionContextSegment segment =
            new TabbedPanelSelectionContextSegment(K, Mcontext.get(K), tabs.getSelectedIndex());
        C.addSegment(segment);
        TabbedPanelSelectionContextSegment segmentParent =
            new TabbedPanelSelectionContextSegment(null, Mcontext, -1);
        C.addSegment(segmentParent);
        Cs[0] = C;
        return Cs;
    }

    @Override
    public void addSelection(SelectRequest selectRequest) {
//        if(selectRequest.getAction() == SelectAction.ROOT) {
//            tree.getSelectionModel().addSelectionPath(nRoot.getTPath());
//
//        } else {
//
//            TreePanelSelectionContextSegment S =
//                (TreePanelSelectionContextSegment)
//                    selectRequest.getContext().getSegment(0);
//            TPath pSel = S.getPath();
//            FNode nParent;
//
//            switch(selectRequest.getAction()) {
//                case SELF:
//                    tree.getSelectionModel().addSelectionPath(pSel);
//                    break;
//
//                case CHILD:
//                    FNode nSel = S.getNode();
//                    FNode nNew = nSel.getChildByKey(selectRequest.getArgs()[0]);
//                    if(nNew != null) {    // One or more ancestor nodes of nNew might be paused
//                        TreePath newPath = pSel.pathByAddingChild(nNew);
//                        tree.getSelectionModel().addSelectionPath(newPath);
//                    }
//                    break;
//
//                case CHILDREN:
//                    nSel = S.getNode();
//                    // Can be made even faster?
//                    TreePath[] paths = new TreePath[nSel.getCount()];
//                    for(int i = 0; i < nSel.getCount(); i++) {
//                        FNode nChild = (FNode) nSel.getChildAt(i);
//                        TPath path = nChild.getTPath();
//                        paths[i] = path;
//                    }
//                    tree.getSelectionModel().addSelectionPaths(paths);
//                    break;
//
//                case SIBLING:
//                    if(selectRequest.getContext().getSegmentCount() > 1) {
//                        TreePanelSelectionContextSegment Sparent =
//                            (TreePanelSelectionContextSegment)
//                                selectRequest.getContext().getSegment(1);
//                        TPath pParent = Sparent.getPath();
//                        Object Ksib = ArrayUtil.isBlank(selectRequest.getArgs()) ? null :
//                            selectRequest.getArgs()[0];
//                        for(RTreeNode child : Sparent.getNode()) {
//                            FNode fChild = (FNode) child;
//                            if(Ksib == null || AUtil.equals(Ksib, fChild.getK())) {
//                                tree.getSelectionModel().addSelectionPath(
//                                    pParent.pathByAddingChild(child));
//                            }
//                        }
//                    }
//                    break;
//
//                case PARENT:
//                    if(selectRequest.getContext().getSegmentCount() > 1) {
//                        TreePanelSelectionContextSegment Sparent =
//                            (TreePanelSelectionContextSegment)
//                                selectRequest.getContext().getSegment(1);
//                        TPath pParent = Sparent.getPath();
//                        tree.getSelectionModel().addSelectionPath(pParent);
//                    }
//                    break;
//
//                case GRANDPARENT:
////                    Object[] dataG = selectRequest.getContext().getData(2);
////                    FNode nGrandparent = (FNode) dataG[1];
////                    nNew = nGrandparent.getChildByKey(selectRequest.getArgs()[0]);
////                    tree.getSelectionModel().addSelectionPath(nNew.getTPath());
//                    break;
//
//                default:
//                    // Won't happen
//                    break;
//            }
//        }
    }
    @Override
    public void beginBulkSelection() {
//        bulkSelection = true;
//        tree.setBulkSelection(bulkSelection);
    }
    @Override
    public void endBulkSelection() {
//        bulkSelection = false;
//        tree.setBulkSelection(bulkSelection);
//        updateFromTreeSelection();
    }
    private void updateFromTreeSelection() {
//        updateNodeInfoFromSelected();
//        fireSelectedValuesNotifier();
    }


    ///////////////
    // EXPANSION //
    ///////////////

    @Override
    public void expand(FNode node) {
//        tree.expand(node);
    }
    @Override
    public void expandToLevel(FNode node, int level) {
//        tree.expandToLevel(node, level);
    }
    @Override
    public void setShiftForEditValue(boolean editShift) {
//        tree.setShiftForEditValue(editShift);
    }
    @Override
    public void addExpand(ExpandRequest expandRequest) {
//        if(expandRequest.getAction() == SelectAction.ROOT) {
//            exp(expandRequest, nRoot.getTPath(), nRoot);
//
//        } else {
//            TreePanelSelectionContextSegment S =
//                (TreePanelSelectionContextSegment)
//                    expandRequest.getContext().getSegment(0);
//            TPath pSel = S.getPath();
//            FNode nSel = S.getNode();
//
//            switch(expandRequest.getAction()) {
//                case SELF:
//                    exp(expandRequest, pSel, nSel);
//                    break;
//
//                case CHILD:
//                    FNode nNew = nSel.getChildByKey(expandRequest.getArgs()[0]);
//                    exp(expandRequest, new TPath(pSel.pathByAddingChild(nNew)), nNew);
//                    break;
//
//                case CHILDREN:
//                    break;
//
//                case SIBLING:
//                    TreePanelSelectionContextSegment Sparent =
//                        (TreePanelSelectionContextSegment)
//                            expandRequest.getContext().getSegment(1);
//                    FNode nParent = Sparent.getNode();
//                    nNew = nParent.getChildByKey(expandRequest.getArgs()[0]);
//                    exp(expandRequest, nNew.getTPath(), nNew);
//                    break;
//
//                case PARENT:
//                    if(true) {
//                        throw new RuntimeException("not impl...");
//                    }
//                    break;
//
//                case GRANDPARENT:
//                    break;
//
//                default:
//                    // Won't happen
//                    break;
//            }
//        }
    }

//    private void exp(ExpandRequest expandRequest, TPath p, FNode n) {
//        if(expandRequest.isAll()) {
//            tree.initExpandAllLimiter();
//            boolean all = tree.expandAll(p);
//            if(!all) {
//                Dialogs.showWarning(tree,
//                    "Cannot expand more than 10,000 nodes at a time.",
//                    "Expand Error");
//            }
//
//        } else {
//            if(expandRequest.getLevel() == -1) {
//                tree.expand(p);
//            } else {
//                tree.expandToLevel(n, expandRequest.getLevel());
//            }
//        }
//    }


    /////////////
    // EDITING //
    /////////////

    @Override
    public void startEditing() {

        // Set all selected nodes' user objects to an editing
        // state and choose the path at which the user will
        // perform the editing for the entire edit context.
//        TPath Pstart = tree.getTSelectionPath();
//        TPath Plead = tree.getTLeadSelectionPath();
//
//        for(TPath P : tree.getTSelectionPaths()) {
//            NodeFTree u = P.get();
//            u.setEditing(true);
//
//            if(P.equals(Plead)) {
//                Pstart = Plead;
//            }
//        }
//
//        tree.doEdit(Pstart);
    }
    @Override
    public void cancelEditing() {
//        tree.cancelEditing();
    }
    @Override
    public SelectionContext[] getEditContextContexts() {
        return null;//editContextSelContexts;
    }

    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class ANodeMapBatchChangedListener implements MapBatchChangedListener {
        public void mapBatchChanged(MapBatchChangedEvent e) {
        }
    }

    private class ANodeMapClearedListener implements MapClearedListener {
        public void mapCleared(MapClearedEvent e) {
            tabs.removeAll();
        }
    }

    // Done
    private class ANodeKeyAddedListener implements KeyAddedListener {
        public void keyAdded(final KeyAddedEvent e) {
            addTabFromPair(e.getK(), e.getV());
        }
    }

    // Done
    private class ANodeKeyRemovedListener implements KeyRemovedListener {
        public void keyRemoved(KeyRemovedEvent e) {
            int index = tabs.indexOfTabByKey(e.getK());
            tabs.remove(index);
        }
    }

    // Done
    private class ANodeKeyChangedListener implements KeyChangedListener {
        public void keyChanged(KeyChangedEvent e) {
        }
    }

    private class ANodeValueChangedListener implements ValueChangedListener {
        public void valueChanged(ValueChangedEvent e) {
        }
    }
}
