package replete.ui.params.hier.test;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import replete.ui.form.RFormPanel;
import replete.ui.lay.Lay;
import replete.ui.params.hier.PropertyParamsPanel;


public class TestPropertyParamsPanel extends PropertyParamsPanel<TestPropertyParams> {


    ////////////
    // FIELDS //
    ////////////

    // UI

    private JCheckBox chkEmailUrls;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TestPropertyParamsPanel() {
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
    public TestPropertyParams get() {
        return new TestPropertyParams()
            .setValue(chkEmailUrls.isSelected())
        ;
    }

    // Mutators

    @Override
    public void set(TestPropertyParams params) {
        chkEmailUrls.setSelected(params.isValue());
    }

//
//    ////////////////
//    // OVERRIDDEN //
//    ////////////////
//
//    @Override
//    public void validateInput(ValidationContext context) {
//    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class FormPanel extends RFormPanel {
        public FormPanel() {
            super(120);
            init();
        }

        @Override
        public void addFields() {
            JPanel pnlEU = Lay.FL("L",
                chkEmailUrls = Lay.chk("<html><i>(tells crawler to send URLs in alert e-mails)</i></html>"),
                "hgap=0"
            );

            addField("Main", "Test Value?",   pnlEU,    40, false);
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
