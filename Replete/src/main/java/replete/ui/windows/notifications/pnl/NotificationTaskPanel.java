package replete.ui.windows.notifications.pnl;

import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import replete.event.ProgressEvent;
import replete.event.ProgressListener;
import replete.progress.ProgressMessage;
import replete.ui.GuiUtil;
import replete.ui.button.RButton;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.text.RLabel;
import replete.ui.windows.Dialogs;
import replete.ui.windows.ExceptionDetails;
import replete.ui.windows.notifications.NotificationImageModel;
import replete.ui.windows.notifications.NotificationModel;
import replete.ui.windows.notifications.msg.NotificationError;
import replete.ui.windows.notifications.msg.NotificationTask;
import replete.ui.worker.RWorker;
import replete.ui.worker.RWorkerStatus;
import replete.ui.worker.events.RWorkerStatusEvent;
import replete.ui.worker.events.RWorkerStatusListener;
import replete.util.DateUtil;

public class NotificationTaskPanel extends NotificationCommonPanel {


    ////////////
    // FIELDS //
    ////////////

    private static final ImageIcon DEFAULT_ICON = ImageLib.get(CommonConcepts.PROGRESS);

    private NotificationTask notif;
    private RWorker action;

    private JLabel lblTitle;
    private JProgressBar pgb;
    private RLabel lblStep;
    private JLabel lblStarted;
    private JLabel lblDuration;
    private JPanel pnlButtonsInner;
    private RButton btnPause;
    private JButton btnStop;
    protected long clockStart;
    protected long clockEnd;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NotificationTaskPanel(NotificationModel model, NotificationTask notif) {
        this.notif = notif;
        action = notif.getAction();

        if(notif.isAutoRemove() && notif.getAction() != null) {
            notif.getAction().addStatusListener(new RWorkerStatusListener() {
                @Override
                public void stateChanged(RWorkerStatusEvent e) {
                    if(e.getCurrent() == RWorkerStatus.FINISHED) {
                        fireRemoveNotifier();
                    }
                }
            });
        }

        addMouseListener(clickActionListener);

        List<JButton> buttons = new ArrayList<>();

        if(notif.getClickAction() != null) {
            buttons.add(
                Lay.btn(
                    NotificationImageModel.GOTO, "icon,ttt=Go-To-Task",
                    (ActionListener) e -> notif.getClickAction().clicked(notif)
                )
            );
        }

        if(action != null) {
            if(action.canPause()) {
                buttons.add(
                    btnPause = Lay.btn(
                        CommonConcepts.PAUSE, "icon,ttt=Pause",
                        (ActionListener) e -> togglePause()
                    )
                );
            }

            if(action.canStop()) {
                buttons.add(
                    btnStop = Lay.btn(
                        CommonConcepts.STOP, "icon,ttt=Stop",
                        (ActionListener) e -> action.stopContext()
                    )
                );
            }
        }

        JLabel lblIcon;
        Lay.BLtg(this,
            "W", Lay.p(
                lblIcon = Lay.lb(notif.getIcon() == null ? DEFAULT_ICON : notif.getIcon()),
                "eb=3r,dimw=26"
            ),
            "C", Lay.GL(3, 1,
                lblTitle = Lay.lb(notif.getTitle(), "size=13"),
                Lay.BL(
                    "C", Lay.BL(
                        "C", pgb = Lay.pgb(true, "bg=white,fgx=C8FFC1,border=false"),
                        "mb=[1,587055],augb=eb(1tb)"
                    ),
                    "E", Lay.GBL(
                        pnlButtonsInner = Lay.GL(1, buttons.size(),
                            buttons,
                            buttons.size() == 0 ? "" : "eb=5l"
                        )
                    )
                ),
                Lay.FL("L",
                    lblStep = Lay.lb(),
                    lblStarted = Lay.lb("(?)", CommonConcepts.PLAY, "eb=10l"),
                    lblDuration = Lay.lb("(0:00)", NotificationImageModel.DURATION, "eb=10l"),
                    "vgap=2"
                )
            ),
            "eb=5lr"
        );
        lblTitle.addMouseListener(clickActionListener);
        lblStep.addMouseListener(clickActionListener);
        lblIcon.addMouseListener(clickActionListener);
        pgb.addMouseListener(clickActionListener);

        if(action == null && notif.getStep() != null) {
            lblStep.setText("<html><i>" + notif.getStep() + "</i></html>");
        }

        if(action != null) {
            action.addPauseRequestedListener(e -> setUiState(notif));
            action.addPauseListener(e -> setUiState(notif));

            action.addProgressListener(new ProgressListener() {
                @Override
                public void stateChanged(ProgressEvent e) {
                    ProgressMessage msg = e.getMessages().get(e.getMessages().size() - 1);
                    if(msg.isIndeterminate()) {
                        pgb.setIndeterminate(true);
                        pgb.setStringPainted(false);
                    } else {
                        pgb.setIndeterminate(false);
                        pgb.setValue(msg.calculatePercentDone());
                        pgb.setMinimum(0);
                        pgb.setMaximum(100);
                        pgb.setString(msg.renderNumericMessage());
                        pgb.setStringPainted(true);
                    }
                    lblStep.setText("<html><i>" + msg.renderTextualMessage() + "</i></html>");
                    setShowClock(true, action.getBackgroundStarted(), action.getBackgroundEnded());
                }
            });

            action.addStatusListener(new RWorkerStatusListener() {
                @Override
                public void stateChanged(RWorkerStatusEvent e) {
                    setShowClock(true, action.getBackgroundStarted(), action.getBackgroundEnded());
                    if(e.getCurrent() == RWorkerStatus.GATHER) {
                        lblStep.setText("Gathering user input...");
                    }

                    if(e.getPrevious() == RWorkerStatus.PROCEED) {
                        lblStep.setText("Preparing...");
                    }

                    if(e.getCurrent() == RWorkerStatus.DECLINED) {
                        lblStep.setText("Task canceled by the user.");
                        lblStep.setIcon(CommonConcepts.CANCEL);

                        pgb.setIndeterminate(false);
                        pgb.setStringPainted(false);
                        pgb.setValue(0);
                        pgb.setMaximum(1);
                    }

                    if(e.getCurrent() == RWorkerStatus.BACKGROUND) {
                        lblStep.setText("<html><i>Executing</i></html>");
                        lblStep.setIcon(CommonConcepts.IN_PROGRESS);
                    }

                    if(e.getCurrent() == RWorkerStatus.FINISHED) {
                        final RWorker action = e.getSource();
                        if(action.isStopped()) {
                            lblStep.setText("Task stopped.");
                            lblStep.setIcon(ImageLib.get(CommonConcepts.STOP));

                        } else if(action.getError() != null) {
                            if(notif.isAddError()) {
                                NotificationError error = new NotificationError()
                                    .setError(action.getError())
                                    .setTitle("Error with task '" + notif.getTitle() + "'.")
                                    .setClickAction(notif.getClickAction());
                                model.getErrors().add(error);
                            }
                            String msg;
                            final Throwable error;
                            if(action.getError() instanceof ExecutionException) {
                                error = action.getError().getCause();
                                msg = error.getMessage();
                            } else {
                                error = action.getError();
                                msg = "Interrupted";
                            }
                            lblStep.setText("Task error: " + msg);
                            lblStep.setIcon(CommonConcepts.ERROR);
                            lblStep.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                            lblStep.removeMouseListener(clickActionListener);
                            lblStep.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseReleased(MouseEvent e) {
                                    Dialogs.showDetails(GuiUtil.win(NotificationTaskPanel.this),
                                        new ExceptionDetails()
                                            .setMessage(notif.getTitle())
                                            .setTitle("Error")
                                            .setError(error)
                                            .setInitiallyOpen(true));
                                }
                            });

                        } else if(action.canProceed()) {
                            lblStep.setText("Task complete.");
                            lblStep.setIcon(CommonConcepts.COMPLETE);
                        }

                        if(pgb.isIndeterminate()) {
                            pgb.setIndeterminate(false);   // NOT YET FULLY COMPLETE - needs to be reading most recent ProgressMessage, which only status bar has properly implemented
//                            pgb.setStringPainted(false);
                            pgb.setValue(1);
                            pgb.setMaximum(1);
                            pgb.setStringPainted(true);
                            pgb.setString(" ");
                        }

                        if(btnStop != null) {
                            pnlButtonsInner.remove(btnStop);
                        }
                        if(btnPause != null) {
                            pnlButtonsInner.remove(btnPause);
                        }

                        JButton btnRemove = Lay.btn(CommonConcepts.CLOSE, "icon");
                        btnRemove.setToolTipText("Remove Task");
                        btnRemove.addActionListener(ev -> fireRemoveNotifier());
                        pnlButtonsInner.add(btnRemove);
                        if(pnlButtonsInner.getBorder() == null) {
                            pnlButtonsInner.setBorder(Lay.eb("5l"));
                        }

                        pnlButtonsInner.updateUI();
                    }
                }
            });
        } else {
            setShowClock(false, 0, 0);   // Do this or set own timer somehow?
        }
    }

    private MouseListener clickActionListener = new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            if(e.getClickCount() >= 2 && notif.getClickAction() != null) {
                notif.getClickAction().clicked(notif);
            }
        }
    };

    public void setShowClock(boolean visible, long cs, long ce) {
        lblDuration.setVisible(visible);
        clockStart = cs;
        clockEnd = ce;
        updateClock();
    }

    private void updateClock() {

        if(clockStart == 0) {
            lblStarted.setText("<html><i>(Not Started)</i></html>");
        } else {
            lblStarted.setText(DateUtil.toLongString(clockStart));
        }

        // TODO: Hours
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
        lblDuration.setText(time);
    }

    @Override
    public void updateTimedInfo() {
        updateClock();
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    protected void togglePause() {
        if(action.isPaused()) {
            action.unpause();
        } else {
            action.pause();
        }
    }
    public NotificationTask getNotification() {
        return notif;
    }

    private void setUiState(final NotificationTask notif) {
        setShowClock(true, action.getBackgroundStarted(), action.getBackgroundEnded());

        if(action.isPaused()) {
            lblTitle.setText("(PAUSED) " + notif.getTitle());
        } else if(action.isPauseRequested()) {
            lblTitle.setText("(PAUSE REQUESTED) " + notif.getTitle());
        } else {
            lblTitle.setText(notif.getTitle());
        }

        if(btnPause != null) {
            if(action.isPaused()) {
                btnPause.setToolTipText("Unpause");
                btnPause.setIcon(ImageLib.get(CommonConcepts.PLAY));
                lblStep.setIcon(ImageLib.get(CommonConcepts.PAUSE));
           } else {
               if(action.isPauseRequested()) {
                   btnPause.setToolTipText("Pause Requested");
                   btnPause.setIcon(NotificationImageModel.PAUSE_REQUESTED);
               } else {
                   btnPause.setToolTipText("Pause");
                   btnPause.setIcon(ImageLib.get(CommonConcepts.PAUSE));
               }
               lblStep.setIcon(CommonConcepts.IN_PROGRESS);
            }
        }
    }
}
