package replete.scrutinize.wrappers.ui;

import java.awt.Point;

import replete.scrutinize.core.BaseSc;

public class PointSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return Point.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "x;field",
            "y;field"
        };
    }
}
