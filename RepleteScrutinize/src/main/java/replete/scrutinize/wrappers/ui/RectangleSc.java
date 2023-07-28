package replete.scrutinize.wrappers.ui;

import java.awt.Rectangle;

import replete.scrutinize.core.BaseSc;

public class RectangleSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return Rectangle.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "x;field",
            "y;field",
            "width;field",
            "height;field"
        };
    }
}
