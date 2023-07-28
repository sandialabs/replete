package finio.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import finio.core.syntax.FMapSyntax;
import finio.core.syntax.FMapSyntaxLibrary;
import finio.ui.images.FinioImageModel;
import replete.ui.button.IconButton;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeDialog;

public class SyntaxSelectionDialog extends EscapeDialog {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    public static int SELECT = 0;
    private static int CANCEL = 1;

    // Other

    private JComboBox cboSyntax;
    private JButton btnDelete;
    private SyntaxConfigPanel pnlConfig;
    private int result = CANCEL;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public SyntaxSelectionDialog(JFrame owner) {
        super(owner, "Select Syntax", true);
        setIcon(FinioImageModel.SYNTAX);

        DefaultComboBoxModel<SynGlob> mdl = new DefaultComboBoxModel<>();
        for(String sName : FMapSyntaxLibrary.getSyntaxes().keySet()) {
            FMapSyntax syntax = FMapSyntaxLibrary.getSyntaxes().get(sName);
            mdl.addElement(new SynGlob(sName, syntax));
        }

        final JButton btnSelect, btnCancel, btnAdd, btnCopy;

        Lay.BLtg(this,
            "N", Lay.FL("L",
                Lay.lb("Syntax:"), cboSyntax = Lay.cb(mdl),
                btnAdd = new IconButton(CommonConcepts.ADD, "Add Syntax", 2),
                btnCopy = new IconButton(CommonConcepts.COPY, "Copy Syntax", 2),
                btnDelete = new IconButton(CommonConcepts.DELETE, "Delete Syntax", 2)
            ),
            "C", pnlConfig = new SyntaxConfigPanel(),
            "S", Lay.FL("R",
                btnSelect = Lay.btn("&Select", CommonConcepts.ACCEPT),
                btnCancel = Lay.btn("&Cancel", CommonConcepts.CANCEL)
            )
        );

        btnSelect.addActionListener(e -> {
            result = SELECT;
            close();
        });

        btnCancel.addActionListener(e -> close());

        cboSyntax.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateConfig();
            }
        });

        updateConfig();

        pack();
        Lay.hn(this, "center");
        setDefaultButton(btnSelect);
    }

    private void updateConfig() {
        SynGlob g = (SynGlob) cboSyntax.getSelectedItem();
        pnlConfig.setSyntax(g.syntax, g.syntax.isUserDefined());
        btnDelete.setEnabled(g.syntax.isUserDefined());
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getResult() {
        return result;
    }

    // Accessors (Computed)

    public FMapSyntax getSyntax() {
        return pnlConfig.getSyntax();
    }


    /////////////////
    // INNER CLASS //
    /////////////////

    private class SynGlob {
        private String name;
        private FMapSyntax syntax;
        public SynGlob(String name, FMapSyntax syntax) {
            this.name = name;
            this.syntax = syntax;
        }
        @Override
        public String toString() {
            return name + (syntax.isUserDefined() ? " (user-defined)" : " (built-in)");
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        SyntaxSelectionDialog dlg = new SyntaxSelectionDialog(null);
        dlg.pack();
        Lay.hn(dlg, "center,visible");
        if(dlg.getResult() == SyntaxSelectionDialog.SELECT) {
            System.out.println(dlg.getSyntax());
        }
    }
}
