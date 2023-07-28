package replete.plugins;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionListener;

import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeDialog;

public class SystemUserDescriptorEditDialog extends EscapeDialog {


    ////////////
    // FIELDS //
    ////////////

    public static final int ACCEPT = 0;
    public static final int CANCEL = 1;

    private int result = CANCEL;

    private HumanDescriptorPanel pnlSystemDescriptor;
    private HumanDescriptorPanel pnlUserDescriptor;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public SystemUserDescriptorEditDialog(Window parent, String title, String description,
                                          HumanDescriptor systemDescriptor,
                                          HumanDescriptor userDescriptor,
                                          String userDescNameHelp,
                                          String userDescDescHelp) {
        this(parent, title, description,
            systemDescriptor, userDescriptor,
            userDescNameHelp, userDescDescHelp,
            "Upda&te", CommonConcepts.CHANGE, "", "Update", null, 500);
    }
    public SystemUserDescriptorEditDialog(Window parent, String title, String description,
                                          HumanDescriptor systemDescriptor,
                                          HumanDescriptor userDescriptor,
                                          String userDescNameHelp,
                                          String userDescDescHelp,
                                          String acceptButtonText,
                                          ImageModelConcept acceptButtonIcon,
                                          String acceptButtonHints,
                                          String dbName, Component southComponent,
                                          int winHeight) {
        super(parent, title, true);
        setIcon(CommonConcepts.TEXT_INPUT);

        Lay.BLtg(this,
            "N", Lay.FL("L", Lay.lb(description), "bg=white,mb=[1b,black]"),
            "C", Lay.BL(
                "N", Lay.BL(
                    "C", pnlSystemDescriptor = new HumanDescriptorPanel(
                        120, "Type Name", "Type Description",
                        "<html><i>(system-defined)</i></html>",
                        "<html><i>(system-defined)</i></html>",
                        true
                    ),
                    "eb=7b,augb=mb(1b,black)"
                ),
                "C", pnlUserDescriptor = new HumanDescriptorPanel(
                    120,
                    userDescNameHelp,
                    userDescDescHelp
                ),
                "S", southComponent,
                "eb=5b"
            ),
            "S", Lay.FL("R",
                Lay.btn(acceptButtonText, acceptButtonIcon, acceptButtonHints, (ActionListener) e -> {
                    result = ACCEPT;
                    close();
                }),
                Lay.btn("&Cancel", CommonConcepts.CANCEL, "closer"),
                "bg=100,mb=[1t,black]"
            ),
            "db=" + dbName + ",size=[700," + winHeight + "],center"
        );

        pnlSystemDescriptor.set(systemDescriptor);
        pnlUserDescriptor.set(userDescriptor);
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getResult() {
        return result;
    }
    public HumanDescriptor getUserDescriptor() {
        return pnlUserDescriptor.get();
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        HumanDescriptor systemDescriptor = new HumanDescriptor("A", "B");
        HumanDescriptor userDescriptor = new HumanDescriptor("X", "Y");
        SystemUserDescriptorEditDialog dlg =
            new SystemUserDescriptorEditDialog(null,
                "Title", "Description",
                systemDescriptor, userDescriptor,
                "<html><i>(optional - a short label describing this view's current configuration and results)</i></html>",
                "<html><i>(optional - a longer description of this view's current configuration and results)</i></html>"
            );
        dlg.setVisible(true);
        System.out.println(dlg.getResult() + "/" + dlg.getUserDescriptor());
    }
}
