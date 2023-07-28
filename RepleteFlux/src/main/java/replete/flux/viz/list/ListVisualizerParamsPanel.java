package replete.flux.viz.list;

import replete.flux.viz.VisualizerParamsPanel;
import replete.ui.form.RFormPanel;
import replete.ui.lay.Lay;
import replete.ui.text.validating.IntContextValidator;
import replete.ui.text.validating.ValidatingTextField;
import replete.ui.text.validating.Validator;
import replete.ui.validation.ValidationContext;

public class ListVisualizerParamsPanel extends VisualizerParamsPanel<ListVisualizerParams> {


    ////////////
    // FIELDS //
    ////////////

    private ValidatingTextField txtValue;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ListVisualizerParamsPanel() {
        super();

        Lay.BLtg(this,
            "C", new FormPanel()
        );
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors (Computed)

    @Override
    public ListVisualizerParams get() {
        return new ListVisualizerParams()
            .setTrackedId(lastSetBean.getTrackedId())      // Very important - tracks the PersistentController
            .setValue(txtValue.getInteger())
        ;
    }

    // Mutators

    @Override
    public void set(ListVisualizerParams params) {
        super.set(params);                                 // Very important - saves the tracked ID via lastSetBean
        txtValue.setValidText("" + params.getValue());
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void validateInput(ValidationContext context) {
        context.check("Value", txtValue);
    }


    /////////////////
    // INNER CLASS //
    /////////////////

    private class FormPanel extends RFormPanel {
        public FormPanel() {
            init();
        }

        @Override
        public void addFields() {
            Validator validator = new IntContextValidator();

            addField("Value", txtValue = Lay.tx("", validator, "selectall,validating,prefh=25"));

//            boolean editable = context.getAction() != JobParamsPanelAction.VIEW;
//            txtMaxTopUrls.setEditable(editable);
        }
        @Override
        protected boolean showSaveButton() {
            return false;
        }
        @Override
        protected boolean showCancelButton() {
            return false;
        }
    }
}
