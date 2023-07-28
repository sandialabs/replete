package finio.ui.actions.validation;

import finio.ui.app.AppContext;
import finio.ui.view.SelectionContext;

public class SpecificTypeValueActionValidator extends AActionValidator {


    ///////////
    // FIELD //
    ///////////

    private Class[] valueTypes;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public SpecificTypeValueActionValidator(AppContext ac, Class... valueTypes) {
        super(ac);
        this.valueTypes = valueTypes;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected boolean accept(AppContext ac, SelectionContext C) {
        if(super.accept(ac, C)) {
            Object V = C.getV();
            for(Class valueType : valueTypes) {
                if(V != null && valueType.isAssignableFrom(V.getClass())) {
                    return true;
                }
            }
        }
        return false;
    }
}
