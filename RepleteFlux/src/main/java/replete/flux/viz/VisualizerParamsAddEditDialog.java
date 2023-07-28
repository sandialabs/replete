package replete.flux.viz;

import java.awt.Window;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import replete.plugins.Generator;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.lay.Lay;
import replete.ui.validation.ValidationCheckDialog;
import replete.ui.validation.ValidationContext;
import replete.ui.windows.escape.EscapeDialog;

// TODO: This class was copied to DatasetManagerParamsAddEditDialog.
// Maybe one day these classes could be merged.

public class VisualizerParamsAddEditDialog extends EscapeDialog {


    ///////////
    // ENUMS //
    ///////////

    public enum Action {
        ADD("Add View",        CommonConcepts.ADD,     "Enter the initial configuration for this visualizer.", "Crea&te", CommonConcepts.ADD),
        EDIT("Configure View", CommonConcepts.OPTIONS, "Change the configuration for this visualizer.",        "Upda&te", CommonConcepts.CHANGE),
        CLONE("Clone View",    CommonConcepts.CLONE,   "Modify the configuration for the cloned visualizer.",  "Clon&e",  CommonConcepts.CLONE);
        // ^Same as ADD, but has different UI text

        private String dialogTitle;
        private ImageModelConcept dialogConcept;
        private String descText;
        private String acceptText;
        private ImageModelConcept acceptButtonConcept;

        private Action(String dialogTitle, ImageModelConcept dialogConcept, String descText,
                       String acceptText, ImageModelConcept acceptButtonConcept) {
            this.dialogTitle = dialogTitle;
            this.dialogConcept = dialogConcept;
            this.descText = descText;
            this.acceptText = acceptText;
            this.acceptButtonConcept = acceptButtonConcept;
        }

        public String getDialogTitle() {
            return dialogTitle;
        }
        public ImageModelConcept getDialogConcept() {
            return dialogConcept;
        }
        public String getDescText() {
            return descText;
        }
        public String getAcceptText() {
            return acceptText;
        }
        public ImageModelConcept getAcceptButtonConcept() {
            return acceptButtonConcept;
        }
    }


    ////////////
    // FIELDS //
    ////////////

    public static final int ACCEPT = 0;
    public static final int CANCEL = 1;

    private int result = CANCEL;
    private VisualizerParamsPanel pnlParams;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public VisualizerParamsAddEditDialog(Window parent, VisualizerParams params, String typeName, Action action) {
        super(parent, action.getDialogTitle() + ": " + typeName, true);
        setIcon(action.getDialogConcept());

        VisualizerGenerator generator = Generator.lookup(params);
        pnlParams = generator.createParamsPanel();
        pnlParams.set(params);

        JButton btnAccept;
        Lay.BLtg(this,
            "N", Lay.FL("L", Lay.lb(action.getDescText()), "bg=white,mb=[1b,black]"),
            "C", pnlParams,
            "S", Lay.FL("R",
                btnAccept = Lay.btn(action.getAcceptText(), action.getAcceptButtonConcept(), (ActionListener) e -> {
                    ValidationContext vContext = pnlParams.validateInput();
                    if(vContext.hasMessage()) {
                        ValidationCheckDialog dlg =
                            new ValidationCheckDialog(
                                this, vContext, false, "Input", null);
                        dlg.setCloseButtonText("&Return && Review");
                        dlg.setVisible(true);
                        if(dlg.getResult() != ValidationCheckDialog.CONTINUE) {
                            return;
                        }
                    }
                    result = ACCEPT;
                    close();
                }),
                Lay.btn("&Cancel", CommonConcepts.CANCEL, "closer"),
                "bg=100,mb=[1t,black]"
            ),
            "size=600,center"
        );

        setDefaultButton(btnAccept);
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getResult() {
        return result;
    }
    public VisualizerParams getParams() {
        return pnlParams.get();
    }
}
