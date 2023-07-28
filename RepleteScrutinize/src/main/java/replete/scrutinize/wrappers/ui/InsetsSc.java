package replete.scrutinize.wrappers.ui;

import java.awt.Insets;

import replete.scrutinize.core.BaseSc;

public class InsetsSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return Insets.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "top;field",
            "bottom;field",
            "left;field",
            "right;field",
        };
    }
}
