package replete.scrutinize.wrappers.ui;

import javax.swing.UIManager;

import replete.scrutinize.core.BaseSc;

public class UIManagerSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return UIManager.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "auxiliaryLookAndFeels",
            "crossPlatformLookAndFeelClassName",
            "defaults",
            "installedLookAndFeels",
            "lookAndFeel",
            "multiLookAndFeel",
            "lookAndFeelDefaults",
            "systemLookAndFeelClassName"
        };
    }
}
