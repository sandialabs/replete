package replete.ui.help;

import java.awt.Component;

import replete.ui.help.model.HelpDataModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.windows.notifications.NotificationFrame;

public class HelpFrame extends NotificationFrame {

    private static HelpFrame instance;

    public static HelpFrame getInstanceAndShow(Component parent, HelpDataModel dataModel) {
        if(instance == null) {
            instance = new HelpFrame(dataModel);
        }
        instance.setLocationRelativeTo(parent);
        if(!instance.isVisible()) {
            instance.setVisible(true);
        }
        instance.requestFocus();
        instance.toFront();
        return instance;
    }

    public HelpFrame(HelpDataModel dataModel) {
        super("Help", CommonConcepts.HELP);

        HelpUiController uiController = new HelpUiController();

        Lay.BLtg(this,
            "C", new HelpPanel(uiController, dataModel),
            "S", Lay.FL("R", Lay.btn("&Close", "closer")),
            "size=800"
        );
    }
}
