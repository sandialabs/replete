
package replete.ui.windows.notifications;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import replete.collections.tl.TLAddListener;
import replete.collections.tl.TLChangeListener;
import replete.collections.tl.TLRemoveListener;
import replete.collections.tl.TrackedList;
import replete.collections.tl.events.TLAddEvent;
import replete.collections.tl.events.TLChangeEvent;
import replete.collections.tl.events.TLRemoveEvent;
import replete.progress.ProgressManager;
import replete.progress.ProgressMessage;
import replete.text.StringUtil;
import replete.threads.SwingTimerManager;
import replete.ui.MemoryUsageDialog;
import replete.ui.button.IconButton;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.lay.Lay;
import replete.ui.panels.GradientPanel;
import replete.ui.progress.ProgressBarPanel;
import replete.ui.progress.WarningProgressBar;
import replete.ui.text.RLabel;
import replete.ui.windows.notifications.msg.NotificationCommon;
import replete.ui.windows.notifications.msg.NotificationError;
import replete.ui.windows.notifications.msg.NotificationInfo;
import replete.ui.windows.notifications.msg.NotificationTask;
import replete.ui.worker.RWorker;
import replete.ui.worker.RWorkerStatus;
import replete.util.MemoryUtil;



/**
 * A status bar with a progress bar, message,
 * stop button, pause/play button, and memory
 * meter.
 *
 * @author Derek Trumbo
 */

public class NotificationStatusBar extends GradientPanel {


    ////////////
    // FIELDS //
    ////////////

    public static final int BYTES_PER_MEGABYTE = 1024 * 1024;

    protected ImageIcon stopImage     = ImageLib.get(CommonConcepts.STOP);
    protected ImageIcon pauseImage    = ImageLib.get(CommonConcepts.PAUSE);
    protected ImageIcon pauseReqImage = ImageLib.get(NotificationImageModel.PAUSE_REQUESTED);
    protected ImageIcon playImage     = ImageLib.get(CommonConcepts.PLAY);
    protected ImageIcon trashImage    = ImageLib.get(NotificationImageModel.GARBAGE_COLLECT);

    // UI components (from left to right)
    private RLabel lblExpand;
    private JLabel lblProgIcon;
    private JLabel lblErrorIcon;
    private JLabel lblInfoIcon;
    private IconButton btnStop;
    private IconButton btnPause;
    private JProgressBar pgbStatus;
    private JLabel lblStatus;
    private JLabel lblClock;
    private WarningProgressBar pgbMemory;

    // Other
    protected JDialog dlgMemoryUsage;
    protected JPanel pnlMemoryBar;
    protected long clockStart;
    protected long clockEnd;

    // External model/UI references
    private NotificationWindow parent;
    private NotificationModel model;

    // Internal notification queue state
    private NotificationQueue nQueue = new NotificationQueue();
    private NotificationWatcher currentWatcher;

    private ProgressBarPanel pnlProg;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NotificationStatusBar(final NotificationWindow parent, NotificationModel model) {
        super(new BorderLayout(0, 0));
        this.parent = parent;
        this.model = model;
        setGradientEnabled(false);

        String hints = ",cursor=hand,opaque=false";
        lblExpand    = Lay.lb(NotificationImageModel.EXPAND, "ttt=Expand" + hints);
        lblProgIcon  = Lay.lb(CommonConcepts.PROGRESS,       "ttt=Tasks,visible=false" + hints);
        lblErrorIcon = Lay.lb(CommonConcepts.ERROR,          "ttt=Errors,visible=false" + hints);
        lblInfoIcon  = Lay.lb(CommonConcepts.INFO,           "ttt=Notifications,visible=false" + hints);

        lblExpand.addMouseListener(new ToggleNotificationTabListener(ToggleNotificationTabListener.LEAVE));
        lblProgIcon.addMouseListener(new ToggleNotificationTabListener(ToggleNotificationTabListener.TASKS));
        lblErrorIcon.addMouseListener(new ToggleNotificationTabListener(ToggleNotificationTabListener.ERRORS));
        lblInfoIcon.addMouseListener(new ToggleNotificationTabListener(ToggleNotificationTabListener.INFOS));

        parent.addAreaShownChangeListener(e -> {
            boolean expanded = parent.isShowNotificationArea();
            ImageModelConcept concept =
                expanded ? NotificationImageModel.COLLAPSE : NotificationImageModel.EXPAND;
            lblExpand.setIcon(concept);
            lblExpand.setToolTipText(expanded ? "Collapse" : "Expand");
        });

        model.getTasks().addChangeListener(new TLChangeListener<NotificationTask>() {
            @Override
            public void stateChanged(TLChangeEvent<NotificationTask> e) {
                updateIconsFromModel();
            }
        });
        model.getErrors().addChangeListener(new TLChangeListener<NotificationError>() {
            @Override
            public void stateChanged(TLChangeEvent<NotificationError> e) {
                updateIconsFromModel();
            }
        });
        model.getInfos().addChangeListener(new TLChangeListener<NotificationInfo>() {
            @Override
            public void stateChanged(TLChangeEvent<NotificationInfo> e) {
                updateIconsFromModel();
            }
        });

        model.getTasks().addAddListener(new TLAddListener<NotificationTask>() {
            @Override
            public void stateChanged(TLAddEvent<NotificationTask> e) {
                long T = System.currentTimeMillis();
                for(NotificationTask notif : e.getAdded().values()) {
                    nQueue.addInstance(
                        new NotificationWatcher(
                            notif,
                            DEFAULT_TASK_PRIORITY,
                            T,
                            -1
                        )
                    );
                }
            }
        });
        model.getTasks().addRemoveListener(new TLRemoveListener<NotificationTask>() {
            @Override
            public void stateChanged(TLRemoveEvent<NotificationTask> e) {
                for(NotificationTask notif : e.getRemoved().values()) {
                    nQueue.removeInstance(notif);
                }
            }
        });

        model.getErrors().addAddListener(new TLAddListener<NotificationError>() {
            @Override
            public void stateChanged(TLAddEvent<NotificationError> e) {
                long T = System.currentTimeMillis();
                for(NotificationError notif : e.getAdded().values()) {
                    nQueue.addInstance(
                        new NotificationWatcher(
                            notif,
                            DEFAULT_ERROR_PRIORITY,
                            T,
                            5000
                        )
                    );
                }
            }
        });
        model.getErrors().addRemoveListener(new TLRemoveListener<NotificationError>() {
            @Override
            public void stateChanged(TLRemoveEvent<NotificationError> e) {
                for(NotificationError notif : e.getRemoved().values()) {
                    nQueue.removeInstance(notif);
                }
            }
        });

        model.getInfos().addAddListener(new TLAddListener<NotificationInfo>() {
            @Override
            public void stateChanged(TLAddEvent<NotificationInfo> e) {
                long T = System.currentTimeMillis();
                for(NotificationInfo notif : e.getAdded().values()) {
                    nQueue.addInstance(
                        new NotificationWatcher(
                            notif,
                            DEFAULT_INFO_PRIORITY,
                            T,
                            5000
                        )
                    );
                }
            }
        });
        model.getInfos().addRemoveListener(new TLRemoveListener<NotificationInfo>() {
            @Override
            public void stateChanged(TLRemoveEvent<NotificationInfo> e) {
                for(NotificationInfo notif : e.getRemoved().values()) {
                    nQueue.removeInstance(notif);
                }
            }
        });

        nQueue.addChangeListener(new TLChangeListener<NotificationWatcher>() {
            @Override
            public void stateChanged(TLChangeEvent<NotificationWatcher> e) {
                if(nQueue.isEmpty()) {
                    setShowPauseButton(false);
                    setShowStopButton(false);
                    setShowProgressBar(false);
                    setStatusMessage("");
                    setShowClock(false, 0, 0);
                    currentWatcher = null;
                } else {
                    if(nQueue.get(0) != currentWatcher) {
                        currentWatcher = nQueue.get(0);
                        setUiState(currentWatcher);
                    }
                }
            }
        });
        updateIconsFromModel();

        btnStop = new IconButton(stopImage, "Stop");
        btnPause = new IconButton(pauseImage, "Pause");
        btnStop.toImageOnly();
        btnPause.toImageOnly();

        // Buttons invisible at beginning
        btnStop.setVisible(false);
        btnPause.setVisible(false);

        btnStop.addActionListener(e -> {
            if(currentWatcher != null) {
                if(currentWatcher.notif instanceof NotificationTask) {
                    NotificationTask task = (NotificationTask) currentWatcher.notif;
                    RWorker action = task.getAction();
                    if(action != null) {
                        action.stopContext();
                    }
                }
            }
        });
        btnPause.addActionListener(e -> {
            if(currentWatcher != null) {
                if(currentWatcher.notif instanceof NotificationTask) {
                    NotificationTask task = (NotificationTask) currentWatcher.notif;
                    RWorker action = task.getAction();
                    if(action != null) {
                        if(action.isPaused()) {
                            action.unpause();
                        } else {
                            action.pause();
                        }
                    }
                }
            }
        });

        // Progress bar invisible at beginning.
        pgbStatus = Lay.pgb(true, "bg=white,cursor=hand");
        pnlProg = new ProgressBarPanel(pgbStatus);
        setShowProgressBar(false);

        // Set up buttons and progress bar.
        JPanel pnlProgress = Lay.GBL(
            Lay.FL(
                lblExpand,
                lblProgIcon,
                lblErrorIcon,
                lblInfoIcon,
                btnStop,
                btnPause,
                pnlProg,
                "opaque=false"
            ),
            "opaque=false"
        );
        pnlProgress.setOpaque(false);

        // Status and clock labels invisible at beginning.
        lblStatus = Lay.lb("", "cursor=hand,opaque=false");
        lblClock = Lay.lb("", "cursor=hand,opaque=false");

        lblStatus.setVisible(false);
        lblClock.setVisible(false);

        JPanel pnlLabels = Lay.BL(
            "W", lblClock,
            "C", lblStatus,
            "opaque=false"
        );

        pgbStatus.addMouseListener(new ToggleNotificationTabListener(ToggleNotificationTabListener.TASKS));
        lblClock.addMouseListener(new ToggleNotificationTabListener(ToggleNotificationTabListener.TASKS));
        lblStatus.addMouseListener(new ToggleNotificationTabListener(ToggleNotificationTabListener.DYNAMIC));

        // Set up memory bar and label.
        pgbMemory = new WarningProgressBar(90);
        pgbMemory.setValue(0);
        pgbMemory.setString("0%");
        pgbMemory.setStringPainted(true);
        Lay.hn(pgbMemory, "cursor=hand,bg=white");
        pgbMemory.addMouseListener(new MouseAdapter() {
           @Override
           public void mouseClicked(MouseEvent e) {
               if(dlgMemoryUsage == null || !dlgMemoryUsage.isDisplayable()) {
                   Component rootParent = SwingUtilities.getRoot(NotificationStatusBar.this);
                   if(rootParent instanceof JFrame) {
                       dlgMemoryUsage = new MemoryUsageDialog((JFrame) rootParent);
                   } else {
                       dlgMemoryUsage = new MemoryUsageDialog((JDialog) rootParent);
                   }
                   dlgMemoryUsage.setVisible(true);
               } else if(dlgMemoryUsage != null) {
                   dlgMemoryUsage.requestFocus();
               }
           }
        });

        IconButton btnGC = new IconButton(trashImage, "Garbage Collect");
        btnGC.addActionListener(e -> MemoryUtil.attemptToReclaim());

        pnlMemoryBar = new JPanel();
        pnlMemoryBar.add(new JLabel("Memory:"));
        pnlMemoryBar.add(pgbMemory);
        pnlMemoryBar.add(btnGC);
        pnlMemoryBar.setOpaque(false);

        // Set up status bar.
        add(pnlProgress, BorderLayout.WEST);
        add(pnlLabels, BorderLayout.CENTER);
        add(pnlMemoryBar, BorderLayout.EAST);

        // Make sure memory bar gets text in it right away so
        // status bar isn't shorter than it should be for a
        // brief moment before first update.
        updateMemory();

        ProgressManager.addProgressListener(e -> {
            ProgressMessage m = (ProgressMessage) e.getSource();
            setProgressBar(m.calculatePercentDone());
            setShowProgressBar(true);
            setStatusMessage(m.renderTextualMessage());
        });
        ProgressManager.addClearListener(e -> {
            setShowProgressBar(false);
            setStatusMessage(" ");
        });
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if(lblProgIcon != null) {
            lblProgIcon.setForeground(fg);
            lblErrorIcon.setForeground(fg);
            lblInfoIcon.setForeground(fg);
            lblStatus.setForeground(fg);
            lblClock.setForeground(fg);
        }
    }


    //////////
    // MISC //
    //////////

    protected void updateIconsFromModel() {
        lblProgIcon.setVisible(!model.getTasks().isEmpty());
        lblProgIcon.setText(model.getTasks().size() + "");
        lblErrorIcon.setVisible(!model.getErrors().isEmpty());
        lblErrorIcon.setText(model.getErrors().size() + "");
        lblInfoIcon.setVisible(!model.getInfos().isEmpty());
        lblInfoIcon.setText(model.getInfos().size() + "");
    }

    private void setUiState(NotificationWatcher watcher) {
        if(watcher == currentWatcher) {
            lblStatus.setForeground(getForeground());

            if(currentWatcher.notif instanceof NotificationInfo) {
                setShowPauseButton(false);
                setShowStopButton(false);
                setShowProgressBar(false);
                setShowClock(false, 0, 0);
                setStatusMessage(currentWatcher.notif.getTitle());

            } else if(currentWatcher.notif instanceof NotificationError) {
                setShowPauseButton(false);
                setShowStopButton(false);
                setShowProgressBar(false);
                setShowClock(false, 0, 0);
                setStatusMessage(currentWatcher.notif.getTitle());

            } else {
                NotificationTask task = (NotificationTask) currentWatcher.notif;
                RWorker action = task.getAction();

                StringBuilder statusMessage = new StringBuilder();
                statusMessage.append(currentWatcher.notif.getTitle());
                if(currentWatcher.previousTaskMsg != null) {
                    statusMessage.append(": ").append(currentWatcher.previousTaskMsg.renderTextualMessage());
                }
                setStatusMessage(statusMessage.toString());

                if(task.getProgressBarStyle() != null) {
                    ProgressBarStyle clrs = task.getProgressBarStyle();
                    pgbStatus.setForeground(clrs.getForeground());
                    pgbStatus.setBackground(clrs.getBackground());
//                    pgbStatus.setBorder(null);
                    pnlProg.setBorder(clrs.getBorder());
                }

                if(action != null) {
                    setShowPauseButton(action.canPause() && currentWatcher.previousStatus != RWorkerStatus.FINISHED);
                    setShowStopButton(action.canStop() && currentWatcher.previousStatus != RWorkerStatus.FINISHED);
                    setShowClock(true, action.getBackgroundStarted(), action.getBackgroundEnded());
                    if(action.canPause()) {
                        if(action.isPaused()) {
                            btnPause.setToolTipText("Unpause");
                            btnPause.setIcon(playImage);
                            setStatusMessage("(PAUSED) " + currentWatcher.notif.getTitle());
                        } else {
                            if(action.isPauseRequested()) {
                                btnPause.setToolTipText("Pause Requested");
                                btnPause.setIcon(pauseReqImage);
                                setStatusMessage("(PAUSE REQUESTED) " + currentWatcher.notif.getTitle());
                            } else {
                                btnPause.setToolTipText("Pause");
                                btnPause.setIcon(pauseImage);
                            }
                        }
                    }
                } else {
                    setShowPauseButton(false);
                    setShowStopButton(false);
                    setShowClock(false, 0, 0);
                }
                setShowProgressBar(true);

                ProgressMessage msg = currentWatcher.previousTaskMsg;
                if(action == null || msg == null || msg.isIndeterminate()) {
                    pgbStatus.setIndeterminate(true);
                    pgbStatus.setStringPainted(true);
                    pgbStatus.setString(" ");

                } else {
                    pgbStatus.setIndeterminate(false);
                    pgbStatus.setMinimum(0);
                    pgbStatus.setMaximum(100);
                    pgbStatus.setValue(msg.calculatePercentDone());
                    pgbStatus.setString(msg.renderNumericMessage());
                    pgbStatus.setStringPainted(true);
                }

                // action.isStopped() ?

                if(currentWatcher.previousStatus == RWorkerStatus.DECLINED) {
                    pgbStatus.setIndeterminate(false);
                    pgbStatus.setStringPainted(true);
                    pgbStatus.setString(" ");
                    pgbStatus.setMaximum(1);
                    pgbStatus.setValue(0);

                } else if(currentWatcher.previousStatus == RWorkerStatus.FINISHED) {
                    if(action == null || msg == null || msg.isIndeterminate()) {
                        pgbStatus.setIndeterminate(false);
                        pgbStatus.setStringPainted(true);
                        pgbStatus.setString(" ");
                        pgbStatus.setMaximum(1);
                        pgbStatus.setValue(1);

                        if(task.getProgressBarStyle() != null) {
                            ProgressBarStyle clrs = task.getProgressBarStyle();
                            lblStatus.setForeground(clrs.getCompletedForeground());
                        }
                        if(action != null && msg != null && !StringUtil.isBlank(msg.getMajorMessage())) {
                            setStatusMessage(getStatusMessage() + ": " + msg.getMajorMessage());
                        }
                    }
                }
            }
        }
    }

    public void setRightComponent(Component cmp) {
        remove(pnlMemoryBar);
        add(cmp, BorderLayout.EAST);
    }

    public void appendRightComponent(Component cmp) {
        remove(pnlMemoryBar);
        add(Lay.FL(pnlMemoryBar, cmp), BorderLayout.EAST);
        Lay.hn(pgbMemory, "prefw=50");
    }

    public String getStatusMessage() {
        return lblStatus.getText();
    }
    public void setStatusMessage(String message) {
        if(message != null) {
            lblStatus.setVisible(!message.equals(""));
            lblStatus.setText(message);
            Lay.hn(lblStatus, "eb=2l");
            repaint();
        }
    }

    public void setStopButtonProcess(String name) {
        btnStop.setToolTipText("Stop " + name);
    }

    protected String lastPauseButtonProcess = "";
    public void setPauseButtonProcess(String name) {
        lastPauseButtonProcess = name;
        if(btnPause.getIcon() == pauseImage) {
            btnPause.setToolTipText("Pause " + name);
        } else {
            btnPause.setToolTipText("Continue " + name);
        }
    }

    public void setShowStopButton(boolean visible) {
        btnStop.setVisible(visible);
    }

    public void setShowPauseButton(boolean visible) {
        btnPause.setVisible(visible);
        if(!visible) {
            btnPause.setIcon(pauseImage);
        }
    }

    public boolean isPlayIconVisible() {
        return btnPause.getIcon() == playImage;
    }

    public void setPlayIconVisible(boolean visible) {
        if(visible) {
            btnPause.setIcon(playImage);
        } else {
            btnPause.setIcon(pauseImage);
        }
    }

    public void setProgressBarIndeterminate(boolean i) {
        pgbStatus.setIndeterminate(i);
    }

    public void setShowProgressBar(boolean visible) {
        pgbStatus.setVisible(visible);
        pnlProg.setVisible(visible);
        if(!visible) {
            pgbStatus.setValue(0);
            pgbStatus.setString(" ");
            pgbStatus.setStringPainted(true);
        }
    }

    public void setProgressBar(int pctValue) {
        pgbStatus.setMaximum(100);
        pgbStatus.setValue(pctValue);
        pgbStatus.setString(pctValue + "%");
        pgbStatus.setStringPainted(true);
        pgbStatus.repaint();
        repaint();
    }

    public void setProgressBar(int curValue, int totValue) {
        pgbStatus.setMaximum(totValue);
        pgbStatus.setValue(curValue);
        pgbStatus.setString(curValue + "/" + totValue);
        pgbStatus.setStringPainted(true);
        pgbStatus.repaint();
        repaint();
    }

    public void setShowClock(boolean visible, long cs, long ce) {
        lblClock.setVisible(visible);
        clockStart = cs;
        clockEnd = ce;
        updateClock();
    }

    private void updateClock() {
        String time;
        if(clockStart == 0) {
            time = "0:00";
        } else {
            long end = clockEnd != 0 ? clockEnd : System.currentTimeMillis();
            long sec = (end - clockStart) / 1000L;
            long min = sec / 60;
            sec -= min * 60;
            String z = "";
            if(sec < 10) {
                z = "0";
            }
            time = min + ":" + z + sec;
        }
        lblClock.setText(" (" + time + ")");
    }

    protected void updateMemory() {
        Runtime runtime = Runtime.getRuntime();

        long free = runtime.freeMemory() / BYTES_PER_MEGABYTE;
        long total = runtime.totalMemory() / BYTES_PER_MEGABYTE;
        long max = runtime.maxMemory() / BYTES_PER_MEGABYTE;

        int value = (int) (100 * (total - free) / max);
        pgbMemory.setValue(value);
        pgbMemory.setString(value + "%");
        pgbMemory.setStringPainted(true);

        pgbMemory.setToolTipText((total - free) + " MB / " + total + " MB / " + max + " MB");
    }

    public void updateTimedInfo() {
        updateClock();
        updateMemory();
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    public static final int MAX_PRIORITY = 100;
    public static final int MIN_PRIORITY = 1;
    public static final int NORMAL_PRIORITY = 50;
    public static final int DEFAULT_TASK_PRIORITY = MIN_PRIORITY;
    public static final int DEFAULT_ERROR_PRIORITY = MAX_PRIORITY;
    public static final int DEFAULT_INFO_PRIORITY = NORMAL_PRIORITY;

    private class NotificationWatcher {
        private NotificationCommon notif;
        private int priority;
        private long enterTime;
        private long duration;

        // Not used for info nor error.
        private ProgressMessage previousTaskMsg;
        private boolean previousPaused;           // Unused as of right now
        private boolean previousPausedRequested;  // Unused as of right now
        private RWorkerStatus previousStatus;

        public NotificationWatcher(NotificationCommon notif, int priority,
                                   long enterTime, long duration) {
            this.notif     = notif;
            this.priority  = priority;
            this.enterTime = enterTime;
            this.duration  = duration;

            if(notif instanceof NotificationTask) {
                NotificationTask task = (NotificationTask) notif;
                final RWorker action = task.getAction();

                if(action != null) {
                    previousTaskMsg = null;
                    previousPausedRequested = action.isPauseRequested();
                    previousPaused          = action.isPaused();
                    previousStatus          = action.getStatus();

                    action.addPauseRequestedListener(e -> {
                        previousPausedRequested = action.isPauseRequested();
                        setUiState(NotificationWatcher.this);
                    });
                    action.addPauseListener(e -> {
                        previousPaused = action.isPaused();
                        setUiState(NotificationWatcher.this);
                    });
                    action.addProgressListener(e -> {
                        previousTaskMsg = e.getMessage();
                        setUiState(NotificationWatcher.this);
                    });
                    action.addStatusListener(e -> {
                        previousStatus = e.getCurrent();
                        if(previousStatus == RWorkerStatus.FINISHED) {
                            nQueue.setCountdown(NotificationWatcher.this);
                        }
                        setUiState(NotificationWatcher.this);
                    });
                }
            }
        }

        @Override
        public String toString() {
            return notif.getTitle() + ":P" + priority + "@" + enterTime + ":" + duration;
        }
    }

    private class NotificationQueue extends TrackedList<NotificationWatcher> {
        private javax.swing.Timer checker;

        public void addInstance(NotificationWatcher ni) {
            int i;
            for(i = 0; i < size() && get(i).priority > ni.priority; i++) {
                ;
            }
            add(i, ni);
            if(size() == 1) {
                startChecker();
            }
        }
        public void removeInstance(NotificationCommon nc) {
            for(int i = size() - 1; i >= 0; i--) {
                if(get(i).notif == nc) {
                    remove(i);
                    break;
                }
            }
            if(isEmpty()) {
                stopChecker();
            }
        }
        public void setCountdown(NotificationWatcher watcher) {
            for(int i = 0; i < size(); i++) {
                if(get(i) == watcher) {
                    get(i).enterTime = System.currentTimeMillis();
                    get(i).duration = 5000;
                    break;
                }
            }
        }

        private void startChecker() {
            checker = SwingTimerManager.create(100, e -> {
                long T = System.currentTimeMillis();
                for(int i = size() - 1; i >= 0; i--) {
                    if(get(i).duration >= 0 && T - get(i).enterTime > get(i).duration) {
                        remove(i);
                        break;
                    }
                }
                if(isEmpty()) {
                    stopChecker();
                }
            });
            checker.start();
        }
        private void stopChecker() {
            if(checker != null) {
                checker.stop();
                checker = null;
            }
        }
    }

    private class ToggleNotificationTabListener extends MouseAdapter {
        public static final int TASKS = 0;
        public static final int ERRORS = 1;
        public static final int INFOS = 2;
        public static final int DYNAMIC = -1;
        public static final int LEAVE = -2;
        private int selTab;
        public ToggleNotificationTabListener(int selTab) {
            this.selTab = selTab;
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            int selTab = this.selTab;
            if(selTab == LEAVE) {
                selTab = -1;
            } else if(selTab == DYNAMIC) {
                if(currentWatcher != null) {
                    if(currentWatcher.notif instanceof NotificationTask) {
                        selTab = TASKS;
                    } else if(currentWatcher.notif instanceof NotificationError) {
                        selTab = ERRORS;
                    } else if(currentWatcher.notif instanceof NotificationInfo) {
                        selTab = INFOS;
                    } else {
                        selTab = TASKS;
                    }
                } else {
                    selTab = TASKS;
                }
            }
            if(parent.isShowNotificationArea() && (selTab == -1 || selTab == parent.getTabbedPane().getSelectedIndex())) {
                parent.setShowNotificationArea(false);
            } else {
                parent.setShowNotificationArea(true);
                if(selTab != -1) {
                    parent.getTabbedPane().setSelectedIndex(selTab);
                }
            }
        }
    }
}
