package replete.ui.params.hier;

import java.awt.Window;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import replete.params.hier.PropertyGroup;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeDialog;

public class GroupEditDialog<T> extends EscapeDialog {


    ////////////
    // FIELDS //
    ////////////

    public static final int SAVE = 0;
    public static final int CANCEL = 1;

    private int result = CANCEL;
    private GroupParamsPanel<T> pnlGroup;
    private CriteriaBeanPanel<T, ?> pnlCriteria;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public GroupEditDialog(Window parent, PropertyGroup<T> group, boolean addMode, CriteriaBeanPanel<T, ?> pnlCriteria) {
        super(parent, (addMode ? "Add" : "Edit") + " Group", true);
        this.pnlCriteria = pnlCriteria;
        setIcon(addMode ? CommonConcepts.ADD : CommonConcepts.EDIT);

        String acceptText = addMode ? "&Add" : "Se&t";

        JButton btnSet;
        Lay.BLtg(this,
            "C", pnlGroup = new GroupParamsPanel(pnlCriteria),
            "S", Lay.FL("R",
                btnSet = Lay.btn(acceptText, CommonConcepts.ACCEPT,
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
            "size=[575,300],center"
        );

        setDefaultButton(btnSet);        // Not using "db=" hint since button text not fixed
        pnlGroup.set(group);
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getResult() {
        return result;
    }
    public PropertyGroup<T> getGroup() {
        return pnlGroup.get();
    }
}
