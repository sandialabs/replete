package replete.flux.viz.raindrops;

import replete.flux.viz.VisualizerParamsPanel;
import replete.ui.form.RFormPanel;
import replete.ui.lay.Lay;
import replete.ui.text.validating.IntContextValidator;
import replete.ui.text.validating.ValidatingTextField;
import replete.ui.text.validating.Validator;
import replete.ui.validation.ValidationContext;

public class RaindropsVisualizerParamsPanel extends VisualizerParamsPanel<RaindropsVisualizerParams> {


    ////////////
    // FIELDS //
    ////////////

    private ValidatingTextField txtValue;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RaindropsVisualizerParamsPanel() {
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
    public RaindropsVisualizerParams get() {
        return new RaindropsVisualizerParams()
            .setTrackedId(lastSetBean.getTrackedId())      // Very important - tracks the PersistentController
            .setValue(txtValue.getInteger())
        ;
    }

    // Mutators

    @Override
    public void set(RaindropsVisualizerParams params) {
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
