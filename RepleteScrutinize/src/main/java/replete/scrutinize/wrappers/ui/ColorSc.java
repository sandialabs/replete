package replete.scrutinize.wrappers.ui;

import java.awt.Color;

import replete.scrutinize.core.BaseSc;

public class ColorSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return Color.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "getRGB",
            "getTransparency",
            "getRed",
            "getGreen",
            "getBlue",
            "getAlpha",
        };
    }
}