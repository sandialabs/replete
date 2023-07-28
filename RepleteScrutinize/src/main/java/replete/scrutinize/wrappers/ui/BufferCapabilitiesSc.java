package replete.scrutinize.wrappers.ui;

import java.awt.BufferCapabilities;

import replete.scrutinize.core.BaseSc;

public class BufferCapabilitiesSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return BufferCapabilities.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "backBufferCapabilities",
            "flipContents",
            "frontBufferCapabilities",
            "fullScreenRequired",
            "multiBufferAvailable",
            "pageFlipping",
        };
    }
}
