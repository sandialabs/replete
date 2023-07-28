package replete.ui.params.hier;

import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import replete.params.hier.PropertyParams;
import replete.params.hier.PropertySet;
import replete.params.hier.PropertySetSpecification;
import replete.params.hier.PropertySlot;
import replete.plugins.Generator;
import replete.ui.BeanPanel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.params.hier.images.HierParamsImageModel;
import replete.ui.validation.ValidationContext;
import replete.ui.windows.escape.EscapeDialog;

public class GroupPropertyParamsAddEditDialog extends EscapeDialog {


    ////////////
    // FIELDS //
    ////////////

    public static final int SAVE = 0;
    public static final int CANCEL = 1;
    private int result = CANCEL;
    private JPanel pnlCenter;
    private List<PropertyParamsWrapperPanel> wrapperPanels = new ArrayList<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public GroupPropertyParamsAddEditDialog(Window parent, PropertySetSpecification spec,
                                            Map<String, PropertyParams> usedSlots, String groupLabel) {
        super(parent, "Edit All Properties for Group: " + groupLabel, true);
        setIcon(HierParamsImageModel.EDIT_ALL);

        Lay.BLtg(this,
            "N", Lay.lb("<html>All applicable properties for this group can be enabled, disabled, and edited below.</html>",
                "eb=5,augb=mb(1b,20),bg=white"
            ),
            "C", pnlCenter = Lay.BxL(),
            "S", Lay.FL("R",
                Lay.btn("Se&t", CommonConcepts.ACCEPT,
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
            "db=Set,size=[800,600],center"
        );

        int r = 0;
        for(String key : spec.getKeys()) {
            PropertySlot slot = spec.getSlot(key);
            Class<? extends PropertyParams> paramsClass = slot.getParamsClass();
            PropertyGenerator generator = Generator.lookup(paramsClass);
            PropertyParams params = usedSlots.get(key);
            PropertyParamsPanel pnlParams = generator.createParamsPanel();
            if(params != null) {
                pnlParams.set(params);
            } else {
                pnlParams.set(generator.createParams());
            }
            PropertyParamsWrapperPanel pnlWrapper = new PropertyParamsWrapperPanel(
                slot, generator, pnlParams, key, params != null);
            wrapperPanels.add(pnlWrapper);
            Lay.hn(pnlWrapper, "alignx=0,eb=10");
            pnlCenter.add(pnlWrapper);
            if(r % 2 == 1) {
                Lay.hn(pnlWrapper, "bg=248");
            }
            r++;
        }

        pnlCenter.add(Box.createVerticalGlue());

//        pnlParams.set(params);
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getResult() {
        return result;
    }
    public PropertySet getSelectedProperties() {
        PropertySet properties = new PropertySet();
        for(PropertyParamsWrapperPanel pnlWrapper : wrapperPanels) {
            if(pnlWrapper.isSelected()) {
                properties.put(pnlWrapper.getKey(), pnlWrapper.get());
            }
        }
        return properties;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void validateInput(ValidationContext context) {
        for(int i = 0; i < wrapperPanels.size(); i++) {
            PropertyParamsWrapperPanel pnl = wrapperPanels.get(i);
            context.check(pnl.getSlot().getName(), pnl);
        }
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class PropertyParamsWrapperPanel<P extends PropertyParams> extends BeanPanel<P> {


        ////////////
        // FIELDS //
        ////////////

        private PropertySlot slot;
        private JCheckBox chkSelected;
        private PropertyParamsPanel<P> pnlParams;
        private String key;


        //////////////////
    // CONSTRUCTORS //
    //////////////////

        public PropertyParamsWrapperPanel(PropertySlot slot, PropertyGenerator<P> generator,
                                          PropertyParamsPanel<P> pnlParams, String key,
                                          boolean selected) {
            this.slot = slot;
            this.pnlParams = pnlParams;
            this.key = key;

            String desc = PropertyUtil.getDescription(slot, generator);
            Icon icon = PropertyUtil.getIcon(slot, generator);

            JLabel lbl;
            Lay.BLtg(this,
                "N", Lay.BL(
                    "W", chkSelected = Lay.chk("", "cursor=hand"),
                    "C", lbl = Lay.lb(
                        "<html><u>" + slot.getName() + ":</u>" + desc + "</html>",
                        icon,
                        "cursor=hand,eb=2l"
                    )
                ),
                "C", pnlParams,
                "dimh=80,opaque=true,chtransp"
            );
            lbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    chkSelected.setSelected(!chkSelected.isSelected());
                }
            });
            chkSelected.setSelected(selected);
        }


        //////////////////////////
        // ACCESSORS / MUTATORS //
        //////////////////////////

        // Accessors (Computed)

        public boolean isSelected() {
            return chkSelected.isSelected();
        }
        @Override
        public P get() {
            return pnlParams.get();
        }
        public String getKey() {
            return key;
        }
        public PropertySlot getSlot() {
            return slot;
        }

        // Mutators

        @Override
        public void set(P bean) {
            pnlParams.set(bean);
        }


        ////////////////
        // OVERRIDDEN //
        ////////////////

        @Override
        public void validateInput(ValidationContext context) {
            context.check(pnlParams);
        }
    }
}
