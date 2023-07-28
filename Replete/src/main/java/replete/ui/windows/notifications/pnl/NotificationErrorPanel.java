package replete.ui.windows.notifications.pnl;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import replete.ui.GuiUtil;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.windows.Dialogs;
import replete.ui.windows.ExceptionDetails;
import replete.ui.windows.notifications.NotificationImageModel;
import replete.ui.windows.notifications.msg.NotificationError;

public class NotificationErrorPanel extends NotificationCommonPanel {


    ///////////
    // FIELD //
    ///////////

    private static final ImageIcon DEFAULT_ICON = ImageLib.get(CommonConcepts.ERROR);
    private NotificationError notif;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NotificationErrorPanel(NotificationError notif) {
        this.notif = notif;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getClickCount() >= 2 && notif.getClickAction() != null) {
                    notif.getClickAction().clicked(notif);
                }
            }
        });

        List<JButton> buttons = new ArrayList<>();

        if(notif.getClickAction() != null) {
            buttons.add(
                Lay.btn(
                    NotificationImageModel.GOTO, "icon,ttt=Go-To-Error",
                    (ActionListener) e -> notif.getClickAction().clicked(notif)
                )
            );
        }

        if(notif.getError() != null) {
            ActionListener listener = e -> {
                Dialogs.showDetails(GuiUtil.win(NotificationErrorPanel.this),
                    new ExceptionDetails()
                        .setMessage(notif.getTitle())
                        .setTitle("Error")
                        .setError(notif.getError())
                        .setInitiallyOpen(true));
            };
            buttons.add(
                Lay.btn(
                    NotificationImageModel.TRACE, "icon,ttt=Show-Details",
                    listener
                )
            );
        }

        buttons.add(
            Lay.btn(
                CommonConcepts.CLOSE, "icon,ttt=Remove-Error",
                (ActionListener) e -> fireRemoveNotifier()
            )
        );

        Lay.BLtg(this,
            "C", Lay.lb(notif.getTitle(), notif.getIcon() == null ? DEFAULT_ICON : notif.getIcon()),
            "E", Lay.GBL(
                Lay.GL(1, buttons.size(),
                    buttons
                )
            ),
            "eb=5lr"
        );
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public NotificationError getNotification() {
        return notif;
    }
}
