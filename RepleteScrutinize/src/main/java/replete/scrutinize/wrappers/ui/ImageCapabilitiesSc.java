package replete.scrutinize.wrappers.ui;

import java.awt.ImageCapabilities;

import replete.scrutinize.core.BaseSc;

public class ImageCapabilitiesSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return ImageCapabilities.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "accelerated",
            "trueVolatile"
        };
    }
}
