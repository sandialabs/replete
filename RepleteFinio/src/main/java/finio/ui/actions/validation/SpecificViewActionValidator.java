package finio.ui.actions.validation;

import finio.ui.app.AppContext;
import finio.ui.view.ViewPanel;
import finio.ui.world.WorldPanel;
import finio.ui.worlds.WorldContext;

public class SpecificViewActionValidator extends AActionValidator {


    ///////////
    // FIELD //
    ///////////

    private Class<? extends ViewPanel> pnlViewClass;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public SpecificViewActionValidator(AppContext ac, Class<? extends ViewPanel> pnlViewClass) {
        super(ac);
        this.pnlViewClass = pnlViewClass;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected boolean acceptView(AppContext ac) {
        WorldContext wc = ac.getSelectedWorld();
        if(wc != null) {
            WorldPanel pnlWorld = wc.getWorldPanel();
            if(pnlWorld != null) {
                ViewPanel pnlView = pnlWorld.getSelectedView();
                if(pnlView != null && pnlViewClass.isAssignableFrom(pnlView.getClass())) {
                    return true;
                }
            }
        }
        return false;
    }
}
