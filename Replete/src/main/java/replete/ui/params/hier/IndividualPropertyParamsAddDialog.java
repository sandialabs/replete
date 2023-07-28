package replete.ui.params.hier;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

import replete.params.hier.PropertyParams;
import replete.params.hier.PropertySetSpecification;
import replete.params.hier.PropertySlot;
import replete.plugins.Generator;
import replete.plugins.ui.GeneratorWrapper;
import replete.ui.combo.RComboBox;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeDialog;

public class IndividualPropertyParamsAddDialog extends EscapeDialog {


    ////////////
    // FIELDS //
    ////////////

    public static final int SAVE = 0;
    public static final int CANCEL = 1;
    private int result = CANCEL;
    private PropertyParamsPanel pnlParams;
    private RComboBox<LocalWrapper> cboGenerators;
    private JLabel lblDesc;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public IndividualPropertyParamsAddDialog(Window parent, PropertySetSpecification spec,
                                           Map<String, PropertyParams> usedSlots, String groupLabel) {
        super(parent, "Add Property for Group: " + groupLabel, true);
        setIcon(CommonConcepts.ADD);

        DefaultComboBoxModel<LocalWrapper> mdlGenerators = new DefaultComboBoxModel<>();
        for(String key : spec.getKeys()) {
            if(usedSlots.containsKey(key)) {
                continue;
            }
            PropertySlot slot = spec.getSlot(key);
            PropertyGenerator generator = Generator.lookup(slot.getParamsClass());
            GeneratorWrapper gWrapper = new GeneratorWrapper<>(generator);
            LocalWrapper wrapper = new LocalWrapper(slot, gWrapper);
            mdlGenerators.addElement(wrapper);
        }

        Lay.BLtg(this,
            "N", Lay.GL(2, 1,
                Lay.FL("L",
                    Lay.lb("Slot:"),
                    cboGenerators = Lay.cb(mdlGenerators, new LocalWrapperIconRenderer()),
                    "mb=[1b,20]"
                ),
                lblDesc = Lay.lb("", "eb=5lr")
            ),
            "C", pnlParams,
            "S", Lay.FL("R",
                Lay.btn("&Add", CommonConcepts.ACCEPT,
                    (ActionListener) e -> {
                        if(checkValidationPass()) {
                            result = SAVE;
                            close();
                        }
                    }
                ),
                Lay.btn("&Cancel", CommonConcepts.CANCEL, "closer"),
                "bg=100,mb=[1t,black]"
            ),
            "db=Add,size=[600,200],center"
        );

        updateParamsPanel();

        cboGenerators.addActionListener(e -> updateParamsPanel());
    }

    private void updateParamsPanel() {
        LocalWrapper wrapper = cboGenerators.getSelected();
        GeneratorWrapper gWrapper = wrapper.wrapper;
        PropertyGenerator generator = (PropertyGenerator) gWrapper.getGenerator();
        PropertyParams params = generator.createParams();
        PropertyParamsPanel pnlParamsNew = generator.createParamsPanel();
        pnlParamsNew.set(params);

        if(pnlParams != null) {
            remove(pnlParams);
        }
        add(pnlParamsNew, BorderLayout.CENTER);
        updateUI();

        pnlParams = pnlParamsNew;

        String desc = PropertyUtil.getDescription(wrapper.slot, gWrapper.getGenerator());
        Icon icon = PropertyUtil.getIcon(wrapper.slot, gWrapper.getGenerator());

        lblDesc.setIcon(icon);
        lblDesc.setText("<html><u>" + wrapper.slot.getName() + ":</u> " + desc + "</html>");
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getResult() {
        return result;
    }
    public PropertyParams getParams() {
        return pnlParams.get();
    }
    public String getKey() {
        LocalWrapper wrapper = cboGenerators.getSelected();
        return wrapper.slot.getKey();
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class LocalWrapper {
        PropertySlot slot;
        GeneratorWrapper wrapper;

        public LocalWrapper(PropertySlot slot, GeneratorWrapper wrapper) {
            this.slot = slot;
            this.wrapper = wrapper;
        }

        @Override
        public String toString() {
            return slot.getName();
        }
    }

    public class LocalWrapperIconRenderer extends DefaultListCellRenderer {   // Useful for combo boxes
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
            LocalWrapper wrapper = (LocalWrapper) value;
            GeneratorWrapper gWrapper = wrapper.wrapper;
            Icon icon = PropertyUtil.getIcon(wrapper.slot, gWrapper.getGenerator());
            lbl.setIcon(icon);
            Lay.hn(lbl, "eb=3l");
            return lbl;
        }
    }
}
