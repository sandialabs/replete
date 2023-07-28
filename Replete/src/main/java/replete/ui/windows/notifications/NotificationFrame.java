
package replete.ui.windows.notifications;

import java.awt.GraphicsConfiguration;
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
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeFrame;
import replete.ui.windows.notifications.msg.NotificationTask;
import replete.ui.worker.RWorkerStatus;
import replete.ui.worker.events.RWorkerStatusEvent;
import replete.ui.worker.events.RWorkerStatusListener;

public class NotificationFrame extends EscapeFrame implements NotificationWindow {


    ////////////
    // FIELDS //
    ////////////

    public static final int UPDATE_INTERVAL = 1000;

    private NotificationStatusBar pnlStatus;
    private NotificationGlassPane pnlGlassPane;
    private NotificationModel notificationModel;
    private int cursorTasks = 0;                  // TODO: investigate synchronization issues with this

    // Calls the update function every second for clock and memory.
    protected Timer tmrUpdate;
    private boolean showingStatusBar;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NotificationFrame() {
        init();
    }
    public NotificationFrame(ImageIcon icon) {
        super(icon);
        init();
    }
    public NotificationFrame(String title, ImageIcon icon) {
        super(title, icon);
        init();
    }
    public NotificationFrame(String title) {
        super(title);
        init();
    }
    public NotificationFrame(GraphicsConfiguration gc) {
        super(gc);
        init();
    }
    public NotificationFrame(ImageIcon icon, GraphicsConfiguration gc) {
        super(icon, gc);
        init();
    }
    public NotificationFrame(ImageModelConcept concept, GraphicsConfiguration gc) {
        super(concept, gc);
        init();
    }
    public NotificationFrame(ImageModelConcept concept) {
        super(concept);
        init();
    }
    public NotificationFrame(String title, GraphicsConfiguration gc) {
        super(title, gc);
        init();
    }
    public NotificationFrame(String title, ImageIcon icon, GraphicsConfiguration gc) {
        super(title, icon, gc);
        init();
    }
    public NotificationFrame(String title, ImageModelConcept concept, GraphicsConfiguration gc) {
        super(title, concept, gc);
        init();
    }
    public NotificationFrame(String title, ImageModelConcept concept) {
        super(title, concept);
        init();
    }

    private void init() {
        notificationModel = new NotificationModel();
        pnlStatus = new NotificationStatusBar(this, notificationModel);

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
        tmrUpdate = new Timer("NotificationFrame Update", true);
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
        return showingStatusBar;
    }

    // Mutators

    public void setShowStatusBar(boolean show) {
        if(show == showingStatusBar) {
            return;
        }
        if(show) {
            Lay.BLtg(this,
                "S", pnlStatus
            );
            showingStatusBar = true;
        } else {
            remove(pnlStatus);
            showingStatusBar = false;
        }
        positionGlassFrame();
    }


    //////////
    // WAIT //
    //////////

    private void incCursor() {
        cursorTasks++;
        waitOn();
    }
    private void decCursor() {
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
