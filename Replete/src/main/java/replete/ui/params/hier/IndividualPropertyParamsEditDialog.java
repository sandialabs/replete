package replete.ui.params.hier;

import java.awt.Window;
import java.awt.event.ActionListener;

import javax.swing.Icon;

import replete.params.hier.PropertyParams;
import replete.params.hier.PropertySetSpecification;
import replete.params.hier.PropertySlot;
import replete.plugins.Generator;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeDialog;

public class IndividualPropertyParamsEditDialog extends EscapeDialog {


    ////////////
    // FIELDS //
    ////////////

    public static final int SAVE = 0;
    public static final int CANCEL = 1;
    private int result = CANCEL;
    private PropertyParamsPanel pnlParams;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public IndividualPropertyParamsEditDialog(Window parent, PropertySetSpecification spec,
                                            String key, PropertyParams params, String groupLabel) {
        super(parent, "Edit Property for Group: " + groupLabel, true);
        setIcon(CommonConcepts.EDIT);

        PropertyGenerator generator = Generator.lookup(params.getClass());
        pnlParams = generator.createParamsPanel();

        PropertySlot slot = spec.getSlot(key);
        String desc = PropertyUtil.getDescription(slot, generator);
        Icon icon = PropertyUtil.getIcon(slot, generator);

        Lay.BLtg(this,
            "N", Lay.lb("<html><u>" + slot.getName() + ":</u>" + desc + "</html>", icon, "eb=5lrt"),
            "C", pnlParams,
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
            "db=Set,size=[600,200],center"
        );

        pnlParams.set(params);
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
}
