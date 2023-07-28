package replete.scrutinize.wrappers.ui;

import java.awt.GraphicsEnvironment;

import replete.scrutinize.core.BaseSc;

public class GraphicsEnvironmentSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return GraphicsEnvironment.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "allFonts",
            "availableFontFamilyNames",
            "localGraphicsEnvironment",
            "defaultScreenDevice:Default Screen Device",
            "centerPoint:Center Point",
            "maximumWindowBounds:Maximum Window Bounds",
            "screenDevices:Screen Devices",
            "headlessInstance",
            "headless"
        };
    }
}
