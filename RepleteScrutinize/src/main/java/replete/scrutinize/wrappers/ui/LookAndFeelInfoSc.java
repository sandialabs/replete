package replete.scrutinize.wrappers.ui;

import javax.swing.UIManager.LookAndFeelInfo;

import replete.scrutinize.core.BaseSc;

public class LookAndFeelInfoSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return LookAndFeelInfo.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "name",
            "className"
        };
    }
}
