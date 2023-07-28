package replete.scrutinize.wrappers.ui;

import java.awt.PointerInfo;

import replete.scrutinize.core.BaseSc;

public class PointerInfoSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return PointerInfo.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "device",
            "location"
        };
    }
}
