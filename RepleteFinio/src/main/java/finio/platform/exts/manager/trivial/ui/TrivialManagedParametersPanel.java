package finio.platform.exts.manager.trivial.ui;

import javax.swing.JTextField;

import finio.manager.ManagedParameters;
import finio.platform.exts.manager.trivial.TrivialManagedParameters;
import finio.ui.manager.ManagedParametersPanel;
import replete.ui.form.RFormPanel;
import replete.ui.lay.Lay;

public class TrivialManagedParametersPanel extends ManagedParametersPanel {


    ////////////
    // FIELDS //
    ////////////

    private JTextField txtParam1;
    private JTextField txtParam2;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public TrivialManagedParametersPanel() {
        Lay.BLtg(this,
            "C", new TrivialFormPanel()
        );
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public ManagedParameters getParameters() {
        return new TrivialManagedParameters()
            .setParam1(txtParam1.getText())
            .setParam2(txtParam2.getText())
        ;
    }
    @Override
    public void setParameters(ManagedParameters params) {
        TrivialManagedParameters params2 = (TrivialManagedParameters) params;
        txtParam1.setText(params2.getParam1());
        txtParam2.setText(params2.getParam2());
    }
    @Override
    public String getValidationMessage() {
        return null;
    }


    /////////////////
    // INNER CLASS //
    /////////////////

    private class TrivialFormPanel extends RFormPanel {
        public TrivialFormPanel() {
            init();
        }
        @Override
        protected void addFields() {
            addField("Main", "Param 1", txtParam1 = Lay.tx("", "selectall"), 40, false);
            addField("Main", "Param 2", txtParam2 = Lay.tx("", "selectall"), 40, false);
        }

        @Override
        protected boolean showCancelButton() {
            return false;
        }
        @Override
        protected boolean showSaveButton() {
            return false;
        }
    }
}
