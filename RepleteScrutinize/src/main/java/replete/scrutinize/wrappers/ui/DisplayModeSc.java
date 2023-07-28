package replete.scrutinize.wrappers.ui;

import java.awt.DisplayMode;

import replete.scrutinize.core.BaseSc;


public class DisplayModeSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return DisplayMode.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "bitDepth",
            "height",
            "refreshRate",
            "width"
        };
    }
}
