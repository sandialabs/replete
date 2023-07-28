package finio.ui.manager;

import javax.swing.JFrame;

import finio.manager.ManagedParameters;
import replete.ui.button.RButton;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeDialog;

public class ManagedParametersDialog extends EscapeDialog {


    ////////////
    // FIELDS //
    ////////////

    public static final int SET = 0;
    public static final int CANCEL = 1;

    private int result = CANCEL;
    protected ManagedParameters params;

    private RButton btnSet;
    private RButton btnCancel;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ManagedParametersDialog(JFrame owner, ManagedParameters params, final ManagedParametersPanel pnlParams) {
        super(owner, "Managed Parameters", true);
        this.params = params;

        Lay.BLtg(this,
            "C", pnlParams,
            "S", Lay.FL("R",
                btnSet = Lay.btn("&Set", CommonConcepts.ACCEPT),
                btnCancel = Lay.btn("&Cancel", CommonConcepts.CANCEL)
            ),
            "dimw=400,size=500,pack,center"
        );

        setDefaultButton(btnSet);

        btnSet.addActionListener(e -> {
            this.params = pnlParams.getParameters();
            result = SET;
            close();
        });
        btnCancel.addActionListener(e -> close());

        pnlParams.setParameters(params);
    }


    ////////////
    // RESULT //
    ////////////

    public int getResult() {
        return result;
    }
    public ManagedParameters getParams() {
        return params;
    }
    // shared 'options' modified in place
}
