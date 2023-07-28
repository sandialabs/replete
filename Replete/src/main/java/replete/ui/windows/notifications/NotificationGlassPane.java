
package replete.ui.windows.notifications;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.ui.button.RButton;
import replete.ui.lay.Lay;

//http://weblogs.java.net/blog/alexfromsun/archive/2006/09/a_wellbehaved_g.html

public class NotificationGlassPane extends JPanel {


    ////////////
    // FIELDS //
    ////////////

    private NotificationTabbedPane pnlNotifications;
    private JPanel pnlButtons;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NotificationGlassPane(NotificationModel notifModel) {

        RButton btnClose = Lay.btn(NotificationImageModel.COLLAPSE, "icon,ttt=Collapse");
        btnClose.addActionListener(e -> fireCollapseClickedNotifier());

        pnlButtons = Lay.p(btnClose, "mb=[1tlr,7A8A99],bg=FFFFBC");
        pnlNotifications = new NotificationTabbedPane(notifModel);

        Lay.ALtg(this,
            pnlButtons,
            pnlNotifications,
            "opaque=false"
        );
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public NotificationTabbedPane getNotificationPanel() {
        return pnlNotifications;
    }
    public JPanel getButtonPanel() {
        return pnlButtons;
    }


    //////////
    // MISC //
    //////////

    public void updateTimedInfo() {
        pnlNotifications.updateTimedInfo();
    }


    ////////////////////////
    // MOUSE EVENT FIXING //
    ////////////////////////

    // This allows both the cursors from below and the cursors
    // for the help notes panel to be shown properly.

    @Override
    public boolean contains(int x, int y) {
        if(isVisible()) {
            if(x >= pnlNotifications.getX() && x <= pnlNotifications.getX() + pnlNotifications.getWidth() &&
                    y >= pnlNotifications.getY() && y <= pnlNotifications.getY() + pnlNotifications.getHeight() ) {
                return true;
            }
            return false;
        }
        return super.contains(x, y);
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    private ChangeNotifier collapseClickedNotifier = new ChangeNotifier(this);
    public void addCollapseClickedListener(ChangeListener listener) {
        collapseClickedNotifier.addListener(listener);
    }
    private void fireCollapseClickedNotifier() {
        collapseClickedNotifier.fireStateChanged();
    }
}
