package replete.scrutinize.wrappers.ui;

import javax.swing.LookAndFeel;

import replete.scrutinize.core.BaseSc;

public class LookAndFeelSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return LookAndFeel.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "getName",
            "getID",
            "getDescription",
            "getSupportsWindowDecorations",
            "isNativeLookAndFeel",
            "isSupportedLookAndFeel",
            "getDefaults"
        };
    }
}
