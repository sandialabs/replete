package replete.ui.params.hier.test;

import javax.swing.ImageIcon;

import replete.text.StringUtil;
import replete.ui.params.hier.PropertyGenerator;
import replete.ui.params.hier.PropertyParamsPanel;

public class TestPropertyGenerator extends PropertyGenerator<TestPropertyParams> {


    ///////////
    // FIELD //
    ///////////

    public static final String ARTF_ID_KEYWORDS = "keywords";


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "Test Property";
    }

    @Override
    public String getDescription() {
        return StringUtil.createMissingText("Description");
    }

    @Override
    public ImageIcon getIcon() {
        return null; //ImageLib.get("keywords.gif");
    }

    @Override
    public Class<?>[] getCoordinatedClasses() {
        return new Class[] {
            TestPropertyParams.class,
            TestPropertyParamsPanel.class
        };
    }

    @Override
    public TestPropertyParams createParams() {
        return new TestPropertyParams();
    }

    @Override
    public PropertyParamsPanel<TestPropertyParams> createParamsPanel(Object... args) {
        return new TestPropertyParamsPanel();
    }
}
