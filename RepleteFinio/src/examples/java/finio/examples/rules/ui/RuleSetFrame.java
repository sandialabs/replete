package finio.examples.rules.ui;


import javax.swing.JButton;
import javax.swing.JPanel;

import finio.examples.rules.modelx.XRuleSetHierarchy;
import finio.ui.images.FinioImageModel;
import replete.ui.GuiUtil;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeFrame;

public class RuleSetFrame extends EscapeFrame {


    ////////////
    // FIELDS //
    ////////////

    private JPanel pnlInner;
    private JButton btnClose;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public RuleSetFrame(XRuleSetHierarchy xhier) {
        super("Finio Example: Rule Set Hierarchy");

        GuiUtil.enableTabsHighlighted();
        Lay.BLtg(this,
            "C", Lay.TBL(
                "Manual",
                    CommonConcepts.MANUAL,
                    pnlInner = new PdfParametersPanel(xhier),
                "Finio",
                    FinioImageModel.FINIO_LOGO,
                    pnlInner = new FinioPdfParametersPanel(xhier)
            ),
            "S", Lay.FL("R",
                btnClose = Lay.btn("&Close", CommonConcepts.CANCEL),
                "bg=100,mb=[1t,black]"
            ),
            "size=700,loc=[50,50],bg=020087"
        );

        btnClose.addActionListener(e -> close());
    }
}
