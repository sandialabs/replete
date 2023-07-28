package replete.ui.params.hier;

import replete.params.hier.Criteria;
import replete.params.hier.PropertyGroup;
import replete.text.StringUtil;
import replete.ui.BeanPanel;
import replete.ui.form2.FieldDescriptor;
import replete.ui.form2.NewRFormPanel;
import replete.ui.lay.Lay;
import replete.ui.text.RTextField;
import replete.ui.validation.ValidationContext;


public class GroupParamsPanel<T> extends BeanPanel<PropertyGroup<T>> {


    ////////////
    // FIELDS //
    ////////////

    private RTextField txtLabel;
    private CriteriaBeanPanel pnlCriteria;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public GroupParamsPanel(CriteriaBeanPanel<T, ?> pnlCriteria) {
        this.pnlCriteria = pnlCriteria;

        Lay.BLtg(this,
            "N", new InnerFormPanel(),
            "C", Lay.p(pnlCriteria, "eb=30l")
        );
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors (Computed)

    @Override
    public PropertyGroup get() {
        String lbl = txtLabel.getText();
        lbl = StringUtil.isBlank(lbl) ? null : lbl;
        return new PropertyGroup<>()
            .setLabel(lbl)
            .setCriteria((Criteria) pnlCriteria.get())
            .setProperties(lastSetBean.getProperties())
            .setChildren(lastSetBean.getChildren())
        ;
    }

    // Mutators

    @Override
    public void set(PropertyGroup<T> params) {
        super.set(params);
        txtLabel.setText(params.getLabel());
        pnlCriteria.set(params.getCriteria());
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void validateInput(ValidationContext context) {
        context.check("Condition", pnlCriteria);
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class InnerFormPanel extends NewRFormPanel {
        public InnerFormPanel() {
            init();
        }

        @Override
        public void addFields() {

            // Label
            txtLabel = Lay.tx("", "selectall");
            addField(
                new FieldDescriptor()
                    .setCaption("Label")
                    .setComponent(txtLabel)
                    .setFill(true)
            );

            // Condition
            addField(
                new FieldDescriptor()
                    .setCaption("Condition")
                    .setComponent(Lay.lb("<html><i>(use the panel below to identify which elements belong to this group)</i></html>"))
            );
        }
    }
}
