package replete.flux;

import java.awt.Window;
import java.awt.event.ActionListener;

import replete.flux.streams.FluxDataStreamModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.validation.ValidationCheckDialog;
import replete.ui.validation.ValidationContext;
import replete.ui.windows.escape.EscapeDialog;

public class FluxPanelParamsDialog extends EscapeDialog {


    ////////////
    // FIELDS //
    ////////////

    public static final int ACCEPT = 0;
    public static final int CANCEL = 1;

    private int result = CANCEL;
    private FluxPanelParamsPanel pnlParams;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public FluxPanelParamsDialog(Window parent, FluxPanelParams params,
                                 FluxDataStreamModel dataStreamModel, FluxPanelContext context) {
        super(parent, "Flux Configuration", true);

        Lay.BLtg(this,
            "N", Lay.FL("L", Lay.lb("stuff stuff"), "bg=white,mb=[1b,black]"),
            "C", pnlParams = new FluxPanelParamsPanel(context),
            "S", Lay.FL("R",
                Lay.btn("Se&t", CommonConcepts.ACCEPT, (ActionListener) e -> {
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
            "db=Set,size=[700,400],center"
        );

        pnlParams.set(params);
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getResult() {
        return result;
    }
    public FluxPanelParams getParams() {
        return pnlParams.get();
    }
}
