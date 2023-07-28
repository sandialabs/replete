package finio.platform.exts.view.treeview.ui.editors;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTree;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreeCellEditor;

import finio.platform.exts.view.treeview.ui.FNode;
import finio.platform.exts.view.treeview.ui.FTree;
import finio.platform.exts.view.treeview.ui.nodes.NodeFTree;
import finio.platform.exts.view.treeview.ui.nodes.NodeNonTerminal;
import finio.platform.exts.view.treeview.ui.nodes.NodeTerminal;
import finio.ui.app.AppContext;
import replete.event.ChangeNotifier;
import replete.event.ExtChangeNotifier;
import replete.ui.tree.RTreeNode;
import replete.ui.tree.RTreePath;

public class FTreeCellEditor extends AbstractCellEditor implements TreeCellEditor {


    ////////////
    // FIELDS //
    ////////////

    private AppContext ac;
    private FTree tree;
    private TreeValueEditorPanel pnlEditor;

    // Editor sub-panels
    private OneLineKeyTreeCellEditorPanel pnlNonTerminal;
    private OneLineKeyValuePairTreeCellEditorPanel pnlTerminalKV;

    private FNode nCurrent;
    private FNode nCurrentParent;
    private Object Kcurrent;
    private Object Vcurrent;

    private String overrideNewUserObjectKey;
    private String overrideNewUserObjectValue;

    private boolean noChange = false;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FTreeCellEditor(AppContext ac, FTree tree, FontMetrics fontMetrics) {
        this.ac = ac;
        this.tree = tree;

        pnlNonTerminal =
            new OneLineKeyTreeCellEditorPanel(
                ac, tree, acceptListener, cancelListener, keyListener, fontMetrics);

        // New, experimental
        pnlTerminalKV =
            new OneLineKeyValuePairTreeCellEditorPanel(
                ac, tree, acceptListener, cancelListener, keyListener, fontMetrics);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Object getCellEditorValue() {
        NodeFTree uNew = pnlEditor.getNewUserObject(nCurrent);
        if(overrideNewUserObjectKey != null) {
            uNew.setK(overrideNewUserObjectKey);
        }
        if(overrideNewUserObjectValue != null) {
            uNew.setV(overrideNewUserObjectValue);
        }
        return uNew;
    }

    @Override
    public void cancelCellEditing() {
        super.cancelCellEditing();
        fireCancelEditNotifier();
    }

    // Tell the UI components that contain this cell editor that a
    // change is desired.  Those components have the proper context
    // to validate and effect the change.
    @Override
    public boolean stopCellEditing() {

        NodeFTree uNew = pnlEditor.getNewUserObject(nCurrent);

        EditCompletionEvent event = new EditCompletionEvent()
            .setnCurrent(nCurrent)
            .setnCurrentParent(nCurrentParent)
            .setuNew(uNew)
            .setKcurrent(Kcurrent)
            .setVcurrent(Vcurrent)
            .setChangesValue(pnlEditor.changesValue());

        fireEditCompletionNotifier(event);

        if(event.isNoChange()) {
            noChange = true;
            return false;
        }

        if(event.isCanceled()) {
            return false;
        }

        // For $$ replacement situations
        // TODO: Value as well?
        overrideNewUserObjectKey = event.getkNew();
        overrideNewUserObjectValue = event.getvNew();

        return super.stopCellEditing();
    }

    @Override
    public boolean isCellEditable(EventObject ev) {
        RTreePath pClicked = null;

        if(ev == null) {
            pClicked = new RTreePath(tree.getSelectionPath());

        } else if(ev instanceof MouseEvent) {
            MouseEvent mev = (MouseEvent) ev;
            if(mev.getClickCount() > 1) {
                pClicked = tree.getRPathForLocation(mev.getX(), mev.getY());
            }
        }

        if(pClicked != null) {
            RTreeNode nLast = pClicked.getLast();
            return
                nLast.getObject() instanceof NodeNonTerminal ||
                nLast.getObject() instanceof NodeTerminal;
        }

        return false;
    }

    @Override
    public Component getTreeCellEditorComponent(
            JTree tree, Object value, boolean isSelected,
            boolean expanded, boolean leaf, int row) {
        fireStartEditNotifier();

        FNode nEdit = (FNode) value;
        NodeFTree uEdit = (NodeFTree) nEdit.getObject();
        uEdit.setEditing(true);                             // Even if covered by other code, needed for double-click for now.

        nCurrent = nEdit;
        nCurrentParent = (FNode) nEdit.getRParent();
        Kcurrent = uEdit.getK();
        Vcurrent = uEdit.getV();

        if(uEdit instanceof NodeNonTerminal) {
            pnlNonTerminal.setKeyValue(Kcurrent, Vcurrent);
            pnlEditor = pnlNonTerminal;

        } else if(uEdit instanceof NodeTerminal) {
            NodeTerminal uTerm = (NodeTerminal) uEdit;
            pnlTerminalKV.setKeyValue(Kcurrent, Vcurrent);
            pnlEditor = pnlTerminalKV;

//            if(AUtil.isOtherJavaObject(uTerm.getV())) {
//                pnlTerminalRO.setKeyValue(Kcurrent, Vcurrent);
//                pnlEditor = pnlTerminalRO;
//            } else {
//                pnlTerminal.setKeyValue(Kcurrent, Vcurrent);
//                pnlEditor = pnlTerminal;
//            }
        }

        return pnlEditor;
    }

    private ActionListener acceptListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            doStop();
        }
    };
    private ActionListener cancelListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            cancelCellEditing();
        }
    };

    private KeyListener keyListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                fireDownNotifier();
            } else if(e.getKeyCode() == KeyEvent.VK_UP) {
                fireUpNotifier();
            } else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                doStop();
            }
        }
    };

    private void doStop() {
        noChange = false;
        stopCellEditing();
        if(noChange) {
            cancelCellEditing();
        }
    }

    public void setTreeFont(Font font, FontMetrics metrics) {
        pnlNonTerminal.setTreeFont(font);
        pnlNonTerminal.setFontMetrics(metrics);
        pnlTerminalKV.setTreeFont(font);
        pnlTerminalKV.setFontMetrics(metrics);
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

    protected ChangeNotifier upNotifier = new ChangeNotifier(this);
    public void addUpListener(ChangeListener listener) {
        upNotifier.addListener(listener);
    }
    protected void fireUpNotifier() {
        upNotifier.fireStateChanged();
    }

    protected ChangeNotifier downNotifier = new ChangeNotifier(this);
    public void addDownListener(ChangeListener listener) {
        downNotifier.addListener(listener);
    }
    protected void fireDownNotifier() {
        downNotifier.fireStateChanged();
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
}
