package finio.ui.actions.validation;

import finio.core.FUtil;
import finio.ui.app.AppContext;
import finio.ui.view.SelectionContext;


public class NonTerminalLikeValueActionValidator extends AActionValidator {


    ////////////
    // FIELDS //
    ////////////

    public NonTerminalLikeValueActionValidator(AppContext ac) {
        super(ac);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected boolean accept(AppContext ac, SelectionContext C) {
        if(super.accept(ac, C)) {
            Object V = C.getV();
            return FUtil.isNonTerminalLike(V);
        }
        return false;
    }
}
