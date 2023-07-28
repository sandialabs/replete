package finio.platform.exts.view.treeview.ui.editors;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import finio.platform.exts.view.treeview.ui.FNode;
import finio.platform.exts.view.treeview.ui.FTree;
import finio.platform.exts.view.treeview.ui.nodes.NodeFTree;
import finio.platform.exts.view.treeview.ui.nodes.NodeNonTerminal;
import finio.plugins.extpoints.JavaObjectEditor;
import finio.plugins.extpoints.JavaObjectEditorPanel;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.collections.Pair;
import replete.ui.button.IconButton;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;

public class OneLineKeyTreeCellEditorPanel extends TreeValueEditorPanel {


    ////////////
    // FIELDS //
    ////////////

    private JPanel pnlKContainer;
    private JavaObjectEditor editorK;
    private JavaObjectEditorPanel pnlKEditor;

    private JPanel pnlAcceptCancelButtons;
    private JPanel pnlAcceptButton;
    private JPanel pnlCancelButton;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public OneLineKeyTreeCellEditorPanel(final AppContext ac,
                                       FTree tree,
                                       ActionListener acceptListener,
                                       ActionListener cancelListener,
                                       KeyListener keyListener, FontMetrics fontMetrics) {
        super(ac, tree, acceptListener, cancelListener, keyListener, fontMetrics);

        IconButton btnAccept;
        IconButton btnCancel;
        JLabel lblEarmarkK;

        Lay.FLtg(this, "L",
            Lay.lb(CommonConcepts.EDIT, "eb=3r"),
            pnlKContainer = Lay.p(),
            lblEarmarkK = Lay.lb(FinioImageModel.MORE_OPTIONS, "cursor=hand,eb=1l"),
            pnlAcceptCancelButtons = Lay.FL("L", "nogap"),
            "eb=3,augb=mb(1,black),nogap"
        );

        showKeyEditorPanels(lblEarmarkK);

        pnlAcceptButton = Lay.p(
            btnAccept = new IconButton(CommonConcepts.ACCEPT, "Accept"),
            "eb=3l"
        );
        pnlCancelButton = Lay.p(
            btnCancel = new IconButton(CommonConcepts.CANCEL, "Cancel"),
            "eb=3l"
        );

        pnlAcceptCancelButtons.add(pnlCancelButton);

        btnAccept.toImageOnly();
        btnAccept.addActionListener(acceptListener);

        btnCancel.toImageOnly();
        btnCancel.addActionListener(cancelListener);

//        btnOptions.toImageOnly();
//        btnOptions.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                JMenuItem mnuConvert = new MMenuItem("Convert To Terminal Value", ImageLib.get(FinioImageModel.TERMINAL_CONVERT));
//                JMenuItem mnuFlatten = new MMenuItem("Flatten");
//
//                mnuConvert.addActionListener(new ActionListener() {
//                    public void actionPerformed(ActionEvent e) {
//                        ac.notImpl("Convert To Terminal Value");
//                    }
//                });
//                mnuFlatten.addActionListener(new ActionListener() {
//                    public void actionPerformed(ActionEvent e) {
//                        ac.notImpl("Flatten");
//                    }
//                });
//
//                JPopupMenu mnuOptions = new JPopupMenu();
//                mnuOptions.add(mnuConvert);
//                mnuOptions.add(mnuFlatten);
//                mnuOptions.show(btnOptions, e.getX(), e.getY());
//            }
//        });
    }

    private void showKeyEditorPanels(JLabel lblEarmark) {
        lblEarmark.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showTypeMessage(editorK.getName(), K);
            }
        });
    }

    @Override
    public void setTreeFont(Font font) {

        // Set the fonts of the key subcomponents.
        if(pnlKContainer != null) {
            pnlKContainer.setFont(font);
        }
        if(pnlKEditor != null) {
            pnlKEditor.setFont(font);
        }
    }

    @Override
    public void setKeyValue(Object K, Object V) {
        this.K = K;
        this.V = V;

        Pair<JavaObjectEditor, JavaObjectEditorPanel> pairK =
            findAndAddObjectEditorPanel(pnlKContainer, K);
        editorK = pairK.getValue1();
        pnlKEditor = pairK.getValue2();

        // Decide whether or not to show the accept button.
        pnlAcceptCancelButtons.remove(pnlAcceptButton);
        pnlAcceptCancelButtons.remove(pnlCancelButton);
        if(pnlKEditor.allowsEdit()) {
            pnlAcceptCancelButtons.add(pnlAcceptButton);
        }
        pnlAcceptCancelButtons.add(pnlCancelButton);
        pnlAcceptCancelButtons.updateUI();

        // Place the key into the key text field.
//        setKeyString(K);
//        updateTextFieldWidth(txtK);

        updateUI();
    }

    @Override
    public NodeFTree getNewUserObject(FNode nCurrent) {
        Object Knew = chooseNewObject(pnlKEditor, K);

        NodeNonTerminal uObj = nCurrent.getObject();

        return
            uObj.copy()
                .setK(Knew)
                .setV(V)
        ;
    }

    @Override
    public boolean changesValue() {
        return false;
    }
}
