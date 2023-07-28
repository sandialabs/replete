package finio.ui.actions.validation;

import finio.ui.app.AppContext;
import finio.ui.view.SelectionContext;

public class NonNullValueActionValidator extends AActionValidator {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public NonNullValueActionValidator(AppContext ac) {
        super(ac);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected boolean accept(AppContext ac, SelectionContext C) {
        if(super.accept(ac, C)) {
            Object V = C.getV();
            return V != null;
        }
        return false;
    }
}
