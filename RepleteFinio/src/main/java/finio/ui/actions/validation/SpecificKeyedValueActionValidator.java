package finio.ui.actions.validation;

import finio.core.FUtil;
import finio.ui.app.AppContext;
import finio.ui.view.SelectionContext;

public class SpecificKeyedValueActionValidator extends AActionValidator {


    ///////////
    // FIELD //
    ///////////

    private Object K;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public SpecificKeyedValueActionValidator(AppContext ac, Object K) {
        super(ac);
        this.K = K;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected boolean accept(AppContext ac, SelectionContext C) {
        if(super.accept(ac, C)) {
            return FUtil.equals(C.getK(), K);
        }
        return false;
    }
}
