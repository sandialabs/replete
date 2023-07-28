
package replete.ui.windows.notifications;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import replete.collections.tl.TLAddListener;
import replete.collections.tl.events.TLAddEvent;
import replete.event.ChangeNotifier;
import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeDialog;
import replete.ui.windows.notifications.msg.NotificationTask;
import replete.ui.worker.RWorkerStatus;
import replete.ui.worker.events.RWorkerStatusEvent;
import replete.ui.worker.events.RWorkerStatusListener;

public class NotificationDialog extends EscapeDialog implements NotificationWindow {


    ////////////
    // FIELDS //
    ////////////

    public static final int UPDATE_INTERVAL = 1000;

    private NotificationStatusBar pnlStatus;
    private NotificationGlassPane pnlGlassPane;
    private NotificationModel notificationModel;
    private int cursorTasks = 0;

    // Calls the update function every second for clock and memory.
    protected Timer tmrUpdate;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NotificationDialog() {
        super();
        init();
    }
    public NotificationDialog(String title) {
        super(title);
        init();
    }
    public NotificationDialog(Dialog owner) {
        super(owner);
        init();
    }
    public NotificationDialog(Dialog owner, boolean modal) {
        super(owner, modal);
        init();
    }
    public NotificationDialog(Dialog owner, String title) {
        super(owner, title);
        init();
    }
    public NotificationDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
        init();
    }
    public NotificationDialog(Dialog owner, String title, boolean modal, ImageIcon icon) {
        super(owner, title, modal, icon);
        init();
    }
    public NotificationDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        init();
    }
    public NotificationDialog(Frame owner) {
        super(owner);
        init();
    }
    public NotificationDialog(Frame owner, boolean modal) {
        super(owner, modal);
        init();
    }
    public NotificationDialog(Frame owner, String title) {
        super(owner, title);
        init();
    }
    public NotificationDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        init();
    }
    public NotificationDialog(Frame owner, String title, boolean modal, ImageIcon icon) {
        super(owner, title, modal, icon);
        init();
    }
    public NotificationDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        init();
    }
    public NotificationDialog(Window owner) {
        super(owner);
        init();
    }
    public NotificationDialog(Window owner, ModalityType modalityType) {
        super(owner, modalityType);
        init();
    }
    public NotificationDialog(Window owner, String title) {
        super(owner, title);
        init();
    }
    public NotificationDialog(Window owner, String title, ModalityType modalityType) {
        super(owner, title, modalityType);
        init();
    }
    public NotificationDialog(Window owner, String title, boolean modal) {  // New
        super(owner, title, modal);
        init();
    }
    public NotificationDialog(Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
        super(owner, title, modalityType, gc);
        init();
    }

    private void init() {
        notificationModel = new NotificationModel();
        pnlGlassPane = new NotificationGlassPane(notificationModel);
        pnlGlassPane.setVisible(false);
        setGlassPane(pnlGlassPane);

        getContentPane().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                positionGlassFrame();
            }
        });

        pnlGlassPane.addCollapseClickedListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setShowNotificationArea(false);
            }
        });

        notificationModel.getTasks().addAddListener(new TLAddListener<NotificationTask>() {
            public void stateChanged(TLAddEvent<NotificationTask> e) {
                for(Object index : e.getAdded().keySet()) {
                    NotificationTask notif = e.getAdded().get(index);
                    if(notif.getAction() != null) {
                        if(notif.getAction().getStatus() != RWorkerStatus.FINISHED) {
                            if(notif.isUseWaitCursor()) {
                                incCursor();
                                notif.getAction().addStatusListener(new RWorkerStatusListener() {
                                    public void stateChanged(RWorkerStatusEvent e) {
                                        if(e.getCurrent() == RWorkerStatus.FINISHED) {
                                            decCursor();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });

        // Use util, not Swing timer so can use daemon feature to shut down
        // automatically when app ends, not hanging the exiting.
        tmrUpdate = new Timer("NotificationDialog Update", true);
        tmrUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                if(pnlStatus != null) {
                    pnlStatus.updateTimedInfo();
                }
                if(pnlGlassPane != null) {
                    pnlGlassPane.updateTimedInfo();
                }
            }
        }, 0, UPDATE_INTERVAL);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public NotificationStatusBar getStatusBar() {
        return pnlStatus;
    }
    public NotificationTabbedPane getTabbedPane() {
        return pnlGlassPane.getNotificationPanel();
    }
    public NotificationModel getNotificationModel() {
        return notificationModel;
    }
    public boolean isShowStatusBar() {
        return pnlStatus != null;
    }

    // Mutators

    public void setShowStatusBar(boolean show) {
        if(show) {
            if(pnlStatus == null) {
                pnlStatus = new NotificationStatusBar(this, notificationModel);
                Lay.BLtg(this,
                    "S", pnlStatus
                );
                positionGlassFrame();
            }
        } else {
            remove(pnlStatus);
            pnlStatus = null;
            positionGlassFrame();
        }
    }


    //////////
    // WAIT //
    //////////

    private synchronized void incCursor() {
        cursorTasks++;
        waitOn();
    }
    private synchronized void decCursor() {
        if(cursorTasks > 0) {
            cursorTasks--;
        }
        if(cursorTasks == 0) {
            waitOff();
        }
    }


    ////////////////
    // GLASS PANE //
    ////////////////

    protected void positionGlassFrame() {

        NotificationTabbedPane pnlNotification = pnlGlassPane.getNotificationPanel();

        int border = 8;
        int w = getContentPane().getSize().width - border * 2;
        int h = NotificationTabbedPane.INIT_HEIGHT;
        int x = border;
        int y = getContentPane().getLocation().y +
                getContentPane().getSize().height +
                getContentPane().getY() -
                pnlNotification.getHeight() -
                (isShowStatusBar() ? pnlStatus.getPreferredSize().height : 0);
        if(getJMenuBar() != null) {
            y -= 23;      // Not sure how to calculate dynamically.
        }

        pnlNotification.setSize(w, h);
        pnlNotification.setLocation(x, y + 1);

        JPanel pnlButtons = pnlGlassPane.getButtonPanel();
        pnlButtons.setSize(20, 20);
        pnlButtons.setLocation(border + w - 20 - 4 , y + 6);

        updateUI();
    }

    public boolean isShowNotificationArea() {
        return pnlGlassPane.isVisible();
    }
    public void setShowNotificationArea(boolean show) {
        if(show) {
            positionGlassFrame();
            pnlGlassPane.setVisible(true);
        } else {
            pnlGlassPane.setVisible(false);
        }
        fireAreaShownChangeNotifier();
    }

    @Override
    protected void escapePressed() {
        if(pnlGlassPane.isShowing()) {
            pnlGlassPane.setVisible(false);
        } else {
            super.escapePressed();
        }
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    private ChangeNotifier areaShownChangeNotifier = new ChangeNotifier(this);
    public void addAreaShownChangeListener(ChangeListener listener) {
        areaShownChangeNotifier.addListener(listener);
    }
    private void fireAreaShownChangeNotifier() {
        areaShownChangeNotifier.fireStateChanged();
    }
}
