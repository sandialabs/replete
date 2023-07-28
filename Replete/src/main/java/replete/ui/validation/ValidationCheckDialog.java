package replete.ui.validation;

import java.awt.Window;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

import replete.text.StringUtil;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeDialog;

public class ValidationCheckDialog extends EscapeDialog {


    ////////////
    // FIELDS //
    ////////////

    public static final int CONTINUE = 0;
    public static final int CLOSE = 1;

    private int result = CLOSE;
    private JLabel lblDescription;
    private ValidationContextPanel pnlContext;
    private JButton btnContinue;
    private JButton btnClose;
    private String during;
    private String implications;

    private ValidationContext context;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ValidationCheckDialog(Window window, ValidationContext context,
                                 boolean showEmptyFrames, String label, String implications) {
        super(window, "Validation Problems", true);
        this.context = context;
        setIcon(CommonConcepts.EXCEPTION);

        String lbl = StringUtil.prefixIf(label, " ");
        setTitle(lbl + getTitle());
        lbl = lbl.toLowerCase();

        during = lbl + "validation";
        this.implications = implications;

        btnContinue = Lay.btn(
            "Con&tinue", CommonConcepts.NEXT,
            (ActionListener) e -> {
                result = CONTINUE;
                closeDialog();
            }
        );

        btnContinue.setVisible(!context.hasError());

        Lay.BLtg(this,
            "N", lblDescription = Lay.lb("", "eb=5"),
            "C", Lay.p(pnlContext = new ValidationContextPanel(showEmptyFrames), "eb=5lr"),
            "S", Lay.FL("R",
                btnContinue,
                btnClose = Lay.btn("&Close", CommonConcepts.CANCEL, "closer")
            ),
            "size=[700,500],center"
        );

        updateMainLabel();

        pnlContext.set(context);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public int getResult() {
        return result;
    }

    // Mutators

    public void setCloseButtonText(String text) {
        btnClose.setText(text);
    }
    public void setRootLabel(String label) {
        pnlContext.setRootLabel(label);
    }
    public void setContinueButtonVisible(boolean visible) {
        btnContinue.setVisible(visible);
    }
    public void setDuring(String during) {
        this.during = during;
        updateMainLabel();
    }
    public void setImplications(String implications) {
        this.implications = implications;
        updateMainLabel();
    }
    public void updateMainLabel() {
        String content;

        //boolean info = context.hasInfo();   // Doesn't matter
        boolean warn = context.hasWarning();
        boolean error = context.hasError();

        String inputValidationImpl;
        if(error) {
            content = "Errors were found during " + during + ".";
            inputValidationImpl = "They must be resolved before continuing.";
        } else if(warn) {
            content = "Warnings were found during " + during + ".";
            inputValidationImpl = "You may choose to resolve them or continue anyway.";
        } else {
            content = "There were no warnings or errors found during " + during + ".";
            inputValidationImpl = null;
        }

        if(!StringUtil.isBlank(implications)) {
            content += "  " + implications;
        } else if(!StringUtil.isBlank(inputValidationImpl)) {
            content += "  " + inputValidationImpl;
        }

        lblDescription.setText("<html>" + content + "</html>");
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        ValidationContext vContext = ValidationContext.createTestContext();
        ValidationCheckDialog dlg =
            new ValidationCheckDialog(
                null, vContext, false, "Test", null);
        dlg.setVisible(true);
    }
}
