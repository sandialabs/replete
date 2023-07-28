package replete.diff.plugin;

import java.awt.Window;
import java.awt.event.ActionListener;

import replete.diff.DifferGenerator;
import replete.diff.DifferParams;
import replete.diff.DifferParamsPanel;
import replete.plugins.Generator;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.validation.ValidationContext;
import replete.ui.windows.escape.EscapeDialog;

public class EditDifferParamsDialog extends EscapeDialog {


    ///////////
    // ENUMS //
    ///////////

    public static final int OK = 0;
    public static final int CANCEL = 1;

    private int result = CANCEL;

    private DifferParamsPanel pnlParams;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public EditDifferParamsDialog(Window parent, DifferParams params) {
        super(parent, "Edit Differ Params", true);

        DifferGenerator generator = Generator.lookup(params);
        pnlParams = generator.createParamsPanel();
        pnlParams.set(params);

        Lay.BLtg(this,
            "C", pnlParams,
            "S", Lay.FL("R",
                Lay.btn("&OK", CommonConcepts.ACCEPT, (ActionListener) e -> {
                    if(checkValidationPass()) {
                        result = OK;
                        close();
                    }
                }),
                Lay.btn("&Cancel", "closer"),
                "vgap=0,eb=5b"
            ),
            "size=400"
        );
        setLocationRelativeTo(parent);
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getResult() {
        return result;
    }
    public DifferParams getParams() {
        return pnlParams.get();
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void validateInput(ValidationContext context) {
        context.check(pnlParams);
    }
}