package finio.ui;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import finio.core.syntax.FMapSyntax;
import replete.ui.lay.Lay;
import replete.ui.text.DocumentChangeListener;
import replete.ui.text.RTextField;

public class SyntaxConfigPanel extends JPanel {


    ////////////
    // FIELDS //
    ////////////

    // UI

    private JPanel pnlTokens;
    private JTextArea txtPattern;
    private JTextField txtMapOpen;
    private JTextField txtMapClose;
    private JTextField txtListOpen;
    private JTextField txtListClose;
    private JTextField txtPairOpen;
    private JTextField txtPairClose1;
    private JTextField txtPairClose2;
    private JTextField txtKeyOpen;
    private JTextField txtKeyClose;
    private JTextField txtValueOpen;
    private JTextField txtValueClose;
    private JTextField txtAssign;
    private JTextField txtAssignOpen;
    private JTextField txtAssignClose;
    private JTextField txtEscape;

    // Core

    private boolean editable;
    private FMapSyntax syntax;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public SyntaxConfigPanel() {
        String bg = "EDF5FF";

        Lay.BLtg(this,
            "N", Lay.BL(2, 1,
                "N", Lay.lb("Syntax Pattern", "bg=100,eb=5,fg=white,augb=mb(1b,black)"),
                "C", Lay.p(
                    Lay.sp(txtPattern = Lay.txa("", "size=14,font=Verdana")),
                    "eb=3,prefh=60,bg=" + bg
                )
            ),
            "C", Lay.BL(
                "N", Lay.lb("Syntax Tokens", "bg=100,eb=5,fg=white,augb=mb(1b,black)"),
                "C", pnlTokens = Lay.GL(4, 4, "bg=" + bg)
            ),
            "mb=[1bt,black]"
        );

        // Row 1
        txtMapOpen = make("Map Open");
        txtMapClose = make("Map Close");
        txtListOpen = make("List Open");
        txtListClose = make("List Close");

        // Row 2
        txtPairOpen = make("Pair Open");
        txtPairClose1 = make("Pair Close 1");
        txtPairClose2 = make("Pair Close 2");
        pnlTokens.add(Lay.p("opaque=false"));

        // Row 3
        txtKeyOpen = make("Key Open");
        txtKeyClose = make("Key Close");
        txtValueOpen = make("Value Open");
        txtValueClose = make("Value Close");

        // Row 4
        txtAssign = make("Assign");
        txtAssignOpen = make("Assign Open");
        txtAssignClose = make("Assign Close");
        txtEscape = make("Escape");
    }

    protected void changeEditable(boolean editable) {
        txtPattern.setEditable(editable);
        txtMapOpen.setEditable(editable);
        txtMapClose.setEditable(editable);
        txtListOpen.setEditable(editable);
        txtListClose.setEditable(editable);
        txtPairOpen.setEditable(editable);
        txtPairClose1.setEditable(editable);
        txtPairClose2.setEditable(editable);
        txtKeyOpen.setEditable(editable);
        txtKeyClose.setEditable(editable);
        txtValueOpen.setEditable(editable);
        txtValueClose.setEditable(editable);
        txtAssign.setEditable(editable);
        txtAssignOpen.setEditable(editable);
        txtAssignClose.setEditable(editable);
        txtEscape.setEditable(editable);
    }

    public void setSyntax(FMapSyntax syntax, boolean editable) {
        this.editable = editable;
        this.syntax = syntax;

        changeEditable(editable);

        txtPattern.setText(syntax.getPattern());
        txtMapOpen.setText(syntax.getMapOpen());
        txtMapClose.setText(syntax.getMapClose());
        txtListOpen.setText(syntax.getListOpen());
        txtListClose.setText(syntax.getListClose());
        txtPairOpen.setText(syntax.getPairOpen());
        txtPairClose1.setText(syntax.getPairClose1());
        txtPairClose2.setText(syntax.getPairClose2());
        txtKeyOpen.setText(syntax.getKeyOpen());
        txtKeyClose.setText(syntax.getKeyClose());
        txtValueOpen.setText(syntax.getValueOpen());
        txtValueClose.setText(syntax.getValueClose());
        txtAssign.setText(syntax.getAssign());
        txtAssignOpen.setText(syntax.getAssignOpen());
        txtAssignClose.setText(syntax.getAssignClose());
        txtEscape.setText(syntax.getEscape());
    }
    public FMapSyntax getSyntax() {
        if(!editable) {
            return syntax;
        }

        return new FMapSyntax()
            .setPattern(txtPattern.getText())
            .setMapOpen(txtMapOpen.getText())
            .setMapClose(txtMapClose.getText())
            .setListOpen(txtListOpen.getText())
            .setListClose(txtListClose.getText())
            .setPairOpen(txtPairOpen.getText())
            .setPairClose1(txtPairClose1.getText())
            .setPairClose2(txtPairClose2.getText())
            .setKeyOpen(txtKeyOpen.getText())
            .setKeyClose(txtKeyClose.getText())
            .setValueOpen(txtValueOpen.getText())
            .setValueClose(txtValueClose.getText())
            .setAssign(txtAssign.getText())
            .setAssignOpen(txtAssignOpen.getText())
            .setAssignClose(txtAssignClose.getText());
    }

    private JTextField make(String type) {
        final RTextField txt;
        pnlTokens.add(
            Lay.BL(
                "C", Lay.lb(type + ":", "prefw=110"),
                "E", txt = Lay.tx("",
                    "selectall,cols=3,center,size=16,bold,eb=2,augb=mb(1,black),font=Verdana"),
                "eb=5,opaque=false"
            )
        );
        txt.addChangeListener(new DocumentChangeListener() {
            @Override
            public void documentChanged(DocumentEvent e) {
                txt.setBackground(txt.getText().isEmpty() ? Color.white : Lay.clr("CEFFE7"));
            }
        });
        return txt;
    }
}
