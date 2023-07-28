package replete.scrutinize.wrappers.ui;

import java.awt.Dimension;

import replete.scrutinize.core.BaseSc;

public class DimensionSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return Dimension.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "width;field",
            "height;field"
        };
    }
}
