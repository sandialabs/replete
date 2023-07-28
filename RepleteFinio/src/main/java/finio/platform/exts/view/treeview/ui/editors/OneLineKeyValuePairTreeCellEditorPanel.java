package finio.platform.exts.view.treeview.ui.editors;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import finio.platform.exts.editor.UnknownNativeObjectEditorPanel;
import finio.platform.exts.view.treeview.ui.FNode;
import finio.platform.exts.view.treeview.ui.FTree;
import finio.platform.exts.view.treeview.ui.nodes.NodeFTree;
import finio.platform.exts.view.treeview.ui.nodes.NodeTerminal;
import finio.plugins.extpoints.JavaObjectEditor;
import finio.plugins.extpoints.JavaObjectEditorPanel;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.collections.Pair;
import replete.ui.button.IconButton;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;

public class OneLineKeyValuePairTreeCellEditorPanel extends TreeValueEditorPanel {


    ////////////
    // FIELDS //
    ////////////

    private JPanel pnlKContainer;
    private JPanel pnlVContainer;
    private JavaObjectEditor editorK;
    private JavaObjectEditor editorV;
    private JavaObjectEditorPanel pnlKEditor;
    private JavaObjectEditorPanel pnlVEditor;

    private JLabel lblEquals;

    private JPanel pnlAcceptCancelButtons;
    private JPanel pnlAcceptButton;
    private JPanel pnlCancelButton;
    private boolean performReverseSelection = true;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public OneLineKeyValuePairTreeCellEditorPanel(final AppContext ac,
                                            FTree tree,
                                            ActionListener acceptListener,
                                            ActionListener cancelListener,
                                            KeyListener keyListener, FontMetrics fontMetrics) {
        super(ac, tree, acceptListener, cancelListener, keyListener, fontMetrics);

        IconButton btnAccept;
        IconButton btnCancel;
        //final IconButton btnOptions;
        JLabel lblEarmarkK;
        JLabel lblEarmarkV;

        Lay.FLtg(this, "L",
            Lay.lb(CommonConcepts.EDIT, "eb=3r"),
            pnlKContainer = Lay.p(),
            lblEarmarkK = Lay.lb(FinioImageModel.MORE_OPTIONS, "cursor=hand,eb=1l"),
            lblEquals = Lay.lb(" = ", "bold,size=14"),
            pnlVContainer = Lay.p(),
            lblEarmarkV = Lay.lb(FinioImageModel.MORE_OPTIONS, "cursor=hand,eb=1l"),
            pnlAcceptCancelButtons = Lay.FL("L", "nogap"),
//            Lay.p(btnOptions = new IconButton(CommonConcepts.OPTIONS, "Options"), "eb=2l"),
            "eb=3,augb=mb(1,black),nogap"
        );

        showKeyEditorPanels(lblEarmarkK);
        showValueEditorPanels(lblEarmarkV);

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

        /*btnOptions.toImageOnly();
        btnOptions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                JCheckBoxMenuItem mnuBoolean = new JCheckBoxMenuItem("Boolean");
                JCheckBoxMenuItem mnuInteger = new JCheckBoxMenuItem("Integer");
                JCheckBoxMenuItem mnuLong = new JCheckBoxMenuItem("Long");
                JCheckBoxMenuItem mnuFloat = new JCheckBoxMenuItem("Float");
                JCheckBoxMenuItem mnuDouble = new JCheckBoxMenuItem("Double");
                JCheckBoxMenuItem mnuBinary = new JCheckBoxMenuItem("Binary");

                // Use Large Text Editor, Use Hex Editor (Binary)
                JMenuItem mnuNull = new MMenuItem(
                    "<html>Set To <b><i>" + NodeFTree.NULL_TEXT + "</i></b></html>",
                    ImageLib.get(CommonConcepts.NULL));
                JMenu mnuTest = new MMenu("Terminal Value Java Data Type");
                mnuTest.add(mnuBoolean);
                mnuTest.add(mnuInteger);
                mnuTest.add(mnuLong);
                mnuTest.add(mnuFloat);
                mnuTest.add(mnuDouble);
                mnuTest.add(mnuBinary);

                mnuNull.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ac.notImpl("Set To " + NodeFTree.NULL_TEXT);
                    }
                });

                JPopupMenu mnuOptions = new JPopupMenu();
                mnuOptions.add(mnuNull);
                mnuOptions.add(mnuTest);
//                mnuOptions.add(new MCheckBoxMenuItem("Edit String With Text Area"));
                mnuOptions.show(btnOptions, e.getX(), e.getY());

                 if(V instanceof Boolean) {
                    mnuBoolean.setSelected(true);
                } else if(V instanceof Byte) {
                    mnuByte.setSelected(true);
                } else if(V instanceof Short) {
                    mnuShort.setSelected(true);
                } else if(V instanceof Integer) {
                    mnuInteger.setSelected(true);
                } else if(V instanceof Long) {
                    mnuLong.setSelected(true);
                } else if(V instanceof Float) {
                    mnuFloat.setSelected(true);
                } else if(V instanceof Double) {
                    mnuDouble.setSelected(true);
                } else if(V instanceof Character) {
                    mnuCharacter.setSelected(true);
                } else if(V instanceof String) {
                    mnuString.setSelected(true);
                }
            }
        });*/
    }

    private void showKeyEditorPanels(JLabel lblEarmark) {
        lblEarmark.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showTypeMessage(editorK.getName(), K);
            }
        });
    }
    private void showValueEditorPanels(JLabel lblEarmark) {
        lblEarmark.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showTypeMessage(editorV.getName(), V);
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

        // Set the font of the equals sign label.
        if(lblEquals != null) {
            Font equalsFont = font.deriveFont(Font.BOLD);
            if(font.getSize() == 12) {
                equalsFont = equalsFont.deriveFont(14.0F);
            }
            lblEquals.setFont(equalsFont);
        }

        // Set the fonts of the value subcomponents.
        if(pnlVContainer != null) {
            pnlVContainer.setFont(font);
        }
        if(pnlVEditor != null) {
            pnlVEditor.setFont(font);
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

        Pair<JavaObjectEditor, JavaObjectEditorPanel> pairV =
            findAndAddObjectEditorPanel(pnlVContainer, V);
        editorV = pairV.getValue1();
        pnlVEditor = pairV.getValue2();
        pnlVEditor.addChildFocusListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                pnlKEditor.setFocusable(true);
            }
        });

        // Decide whether or not to show the accept button.
        pnlAcceptCancelButtons.remove(pnlAcceptButton);
        pnlAcceptCancelButtons.remove(pnlCancelButton);
        if(pnlKEditor.allowsEdit() || pnlVEditor.allowsEdit()) {
            pnlAcceptCancelButtons.add(pnlAcceptButton);
        }
        pnlAcceptCancelButtons.add(pnlCancelButton);
        pnlAcceptCancelButtons.updateUI();

        if(tree.isShiftForEditValue() && !(pnlVEditor instanceof UnknownNativeObjectEditorPanel)) {
            pnlKEditor.setFocusable(false);
        }

        pnlVEditor.addChildFocusListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if(performReverseSelection) {
                    pnlVEditor.selectAll(true);
                }
                performReverseSelection = false;
            }
        });

        pnlKEditor.addHitRightListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                pnlVEditor.focus();
                performReverseSelection = true;
            }
        });
        pnlVEditor.addHitLeftListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                pnlKEditor.focus();
            }
        });

        updateUI();
    }

    // What about enumerated values, combo box?
    // What about expandable values?
    @Override
    public NodeFTree getNewUserObject(FNode nCurrent) {
        Object Knew = chooseNewObject(pnlKEditor, K);
        Object Vnew = chooseNewObject(pnlVEditor, V);

        NodeTerminal uObj = nCurrent.getObject();

        return
            uObj.copy()
                .setK(Knew)
                .setV(Vnew)
        ;
    }

    @Override
    public boolean changesValue() {
        return pnlVEditor.allowsEdit();
    }
}
