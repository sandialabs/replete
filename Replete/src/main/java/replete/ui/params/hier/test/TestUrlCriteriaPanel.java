package replete.ui.params.hier.test;

import replete.ui.lay.Lay;
import replete.ui.params.hier.CriteriaBeanPanel;
import replete.ui.text.RTextField;
import replete.ui.validation.ValidationContext;

public class TestUrlCriteriaPanel extends CriteriaBeanPanel<String, TestUrlCriteria> {


    ////////////
    // FIELDS //
    ////////////

    private RTextField txtExpression;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TestUrlCriteriaPanel() {
        Lay.BLtg(this,
            "N", Lay.BL(
                "W", Lay.lb("Expression:"),
                "C", txtExpression = Lay.tx("", "selectall")
            )
        );
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    @Override
    public TestUrlCriteria get() {
        return new TestUrlCriteria(txtExpression.getTrimmed());
    }

    // Mutators

    @Override
    public void set(TestUrlCriteria bean) {
        txtExpression.setText(bean.getExpression());
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void validateInput(ValidationContext context) {
        context.errorFor("Expression", "Must not be blank", txtExpression.isBlank());
    }
}
