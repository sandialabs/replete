package replete.ui.windows.notifications;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTree;

import replete.ui.lay.Lay;

public class TestNotificationDialog extends NotificationDialog {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TestNotificationDialog(JFrame parent, String title, boolean modal) {
        super(parent, title, modal);
        JButton btnToggleGp, btnToggleStatus;
        Lay.BLtg(this,
            "C", Lay.BL(
                "N", Lay.FL(
                    btnToggleGp = new JButton("Toggle Notification Area"),
                    btnToggleStatus = new JButton("Toggle Status")
                ),
                "W", Lay.sp(new JTree()),
                "C", Lay.sp(new JTable())
            ),
            "size=[600,600],center=2"
        );
        btnToggleGp.addActionListener(e -> setShowNotificationArea(!isShowNotificationArea()));
        btnToggleStatus.addActionListener(e -> setShowStatusBar(!isShowStatusBar()));
    }
}
