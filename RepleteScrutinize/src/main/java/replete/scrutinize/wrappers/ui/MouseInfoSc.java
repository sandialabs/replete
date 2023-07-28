package replete.scrutinize.wrappers.ui;

import java.awt.MouseInfo;

import replete.scrutinize.core.BaseSc;


public class MouseInfoSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return MouseInfo.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "pointerInfo",
            "numberOfButtons"
        };
    }
}
