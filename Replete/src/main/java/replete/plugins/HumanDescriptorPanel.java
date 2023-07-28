
package replete.plugins;

import java.awt.KeyboardFocusManager;

import replete.text.StringUtil;
import replete.ui.BeanPanel;
import replete.ui.form.RFormPanel;
import replete.ui.lay.Lay;
import replete.ui.text.RLabel;
import replete.ui.text.RTextField;
import replete.ui.text.RTextPane;

public class HumanDescriptorPanel extends BeanPanel<HumanDescriptor> {


    ////////////
    // FIELDS //
    ////////////

    private boolean readOnly;
    private RLabel lblName;
    private RLabel lblDescription;
    private RTextField txtName;
    private RTextPane txtDescription;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public HumanDescriptorPanel(int labelColWidth, String nameHelp, String descHelp) {
        this(labelColWidth, "Name", "Description", nameHelp, descHelp, false);
    }
    public HumanDescriptorPanel(int labelColWidth, String nameLabel, String descLabel,
                                String nameHelp, String descHelp) {
        this(labelColWidth, nameLabel, descLabel, nameHelp, descHelp, false);
    }
    public HumanDescriptorPanel(int labelColWidth, String nameLabel, String descLabel,
                                String nameHelp, String descHelp, boolean readOnly) {
        this.readOnly = readOnly;

        Lay.BLtg(this,
            "C", new FormPanel(labelColWidth, nameLabel, descLabel, nameHelp, descHelp)
        );
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    @Override
    public HumanDescriptor get() {
        if(readOnly) {
            return lastSetBean;
        }
        return new HumanDescriptor()
            .setName(StringUtil.forceBlankNull(txtName.getTrimmed()))
            .setDescription(StringUtil.forceBlankNull(txtDescription.getTrimmed()))
        ;
    }

    // Mutators

    @Override
    public void set(HumanDescriptor bean) {
        if(StringUtil.isBlank(bean.getName())) {
            lblName.setText("(No Name)");
        } else {
            lblName.setText(bean.getName());
        }
        if(StringUtil.isBlank(bean.getDescription())) {
            lblDescription.setText("(No Description)");
        } else {
            lblDescription.setText(
                "<html>" + StringUtil.markupMissingText(bean.getDescription()) + "</html>"
            );
        }

        txtName.setText(bean.getName());
        txtDescription.setText(bean.getDescription());
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class FormPanel extends RFormPanel {
        private String nameLabel;
        private String descLabel;
        private String nameHelp;
        private String descHelp;

        public FormPanel(int labelColWidth, String nameLabel, String descLabel,
                         String nameHelp, String descHelp) {
            super(labelColWidth);
            this.nameLabel = nameLabel;
            this.descLabel = descLabel;
            this.nameHelp = nameHelp;
            this.descHelp = descHelp;
            init();
        }

        @Override
        public void addFields() {
            lblName = Lay.lb();           // Either the labels or text fields get used
            lblDescription = Lay.lb();

            txtName = Lay.tx("", 25, "selectall,prefh=25");
            txtDescription = Lay.txp("", "selectall");
            txtDescription.setAllowHorizScroll(false);
            txtDescription.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
            txtDescription.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);

            if(readOnly) {
                addField(nameLabel, lblName, nameHelp);
                addField("Main", descLabel, lblDescription, descHelp);
            } else {
                addField(nameLabel, txtName, nameHelp);
                addField("Main", descLabel, Lay.sp(txtDescription), 10, true, descHelp);
            }
            addField("Icon", Lay.lb("<html><i>(not implemented)</i></html>"));
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


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        HumanDescriptorPanel pnl = new HumanDescriptorPanel(
            120, "Type Name", "Type Description",
            "<html><i>(system-defined)</i></html>",
            "<html><i>(system-defined)</i></html>",
            true
        );
        pnl.set(new HumanDescriptor("AAAAAA", StringUtil.createMissingText("Description")));
        Lay.BLtg(Lay.fr("aaa"), "C", pnl, "size=500,center,visible");
    }
}
