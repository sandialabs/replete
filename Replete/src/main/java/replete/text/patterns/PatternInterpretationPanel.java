package replete.text.patterns;

import java.awt.event.ActionListener;

import replete.ui.BeanPanel;
import replete.ui.button.RCheckBox;
import replete.ui.combo.RComboBox;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;
import replete.ui.text.validating.NonblankStringContextValidator;
import replete.ui.text.validating.ValidatingTextField;
import replete.ui.validation.ValidationContext;
import replete.ui.windows.Dialogs;
import replete.ui.windows.escape.EscapeFrame;

public class PatternInterpretationPanel extends BeanPanel<PatternInterpretation> {


    ////////////
    // FIELDS //
    ////////////

    private RComboBox<PatternInterpretationType> cboPatternInterpretationType;
    private RCheckBox chkCaseSensitive;
    private RCheckBox chkWholeMatch;
    private RPanel pnlHierDelim;
    private ValidatingTextField txtHierDelim;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PatternInterpretationPanel() {

        Lay.FLtg(this, "L",
            cboPatternInterpretationType = Lay.cb(PatternInterpretationType.values()),
            Lay.hs(5),
            chkCaseSensitive = Lay.chk("Case Sensitive?"),
            Lay.hs(5),
            chkWholeMatch = Lay.chk("Whole Match?"),
            pnlHierDelim = Lay.FL("L",
                Lay.hs(5),
                Lay.lb("Delim:"),
                Lay.hs(5),
                txtHierDelim = Lay.tx("", new NonblankStringContextValidator(), 3, "selectall,prefh=25"),
                "nogap,!visible"
            ),
            "nogap"
        );

        cboPatternInterpretationType.setSelected(PatternInterpretationType.LITERAL);
        cboPatternInterpretationType.addItemListener(e -> {
            PatternInterpretationType type = cboPatternInterpretationType.getSelected();
            boolean hier =
                type == PatternInterpretationType.HIER_LEFT_TO_RIGHT ||
                type == PatternInterpretationType.HIER_RIGHT_TO_LEFT;
            pnlHierDelim.setVisible(hier);
        });
    }

    @Override
    public PatternInterpretation get() {
        PatternInterpretationType type = cboPatternInterpretationType.getSelected();
        String delim =
            type == PatternInterpretationType.HIER_LEFT_TO_RIGHT ||
                type == PatternInterpretationType.HIER_RIGHT_TO_LEFT ?
            txtHierDelim.getText() :
            null
        ;

        return new PatternInterpretation()
            .setType(type)
            .setCaseSensitive(chkCaseSensitive.isSelected())
            .setWholeMatch(chkWholeMatch.isSelected())
            .setHierarchicalDelim(delim)
        ;
    }

    // Mutator

    @Override
    public void set(PatternInterpretation interp) {

        // This special method bypasses the read-only locking
        // mechanism on the RComboBox.  JComboBox seems to
        // be one of the harder ones to cleanly disable
        // on read-only.
        cboPatternInterpretationType.setSelectedItemForce(interp.getType());
        chkCaseSensitive.setSelected(interp.isCaseSensitive());
        chkWholeMatch.setSelected(interp.isWholeMatch());
        txtHierDelim.setText(interp.getHierarchicalDelim());

        PatternInterpretationType type = interp.getType();
        boolean hier =
            type == PatternInterpretationType.HIER_LEFT_TO_RIGHT ||
            type == PatternInterpretationType.HIER_RIGHT_TO_LEFT;
        if(hier && txtHierDelim.isBlank()) {
            txtHierDelim.setText(PatternInterpretation.DEFAULT_HIER_DELIM);
        }
    }

    @Override
    public void validateInput(ValidationContext context) {
        PatternInterpretationType type = cboPatternInterpretationType.getSelected();
        boolean hier =
            type == PatternInterpretationType.HIER_LEFT_TO_RIGHT ||
            type == PatternInterpretationType.HIER_RIGHT_TO_LEFT;
        if(hier) {
            context.check("Hierarchical Delimiter", txtHierDelim);
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        PatternInterpretationPanel pnl;
        EscapeFrame fra = Lay.fr();
        Lay.BLtg(fra,
            "N", Lay.FL("L",
                pnl = new PatternInterpretationPanel()
            ),
            "C", Lay.hn(new PatternInterpretationInfoPanel(), "eb=5"),
            "S", Lay.FL("R", Lay.btn("Show", (ActionListener) e -> {
                if(pnl.checkValidationPass()) {
                    Dialogs.showMessage(null, "[" + pnl.get() + "]");
                }
            })),
            "db=Show,size=[600,200],center,visible"
        );

        PatternInterpretation interp = new PatternInterpretation()
            .setType(PatternInterpretationType.LITERAL)
            .setCaseSensitive(true)
            .setWholeMatch(false)
//            .setHierarchicalDelim(";")
        ;
        pnl.set(interp);
    }
}
