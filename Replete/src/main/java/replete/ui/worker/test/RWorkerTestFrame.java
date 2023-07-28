package replete.ui.worker.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;

import replete.event.ProgressEvent;
import replete.event.ProgressListener;
import replete.ui.button.RButton;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.text.RTextArea;
import replete.ui.windows.Dialogs;
import replete.ui.windows.notifications.NotificationClickAction;
import replete.ui.windows.notifications.NotificationFrame;
import replete.ui.windows.notifications.msg.NotificationCommon;
import replete.ui.windows.notifications.msg.NotificationError;
import replete.ui.windows.notifications.msg.NotificationInfo;
import replete.ui.windows.notifications.msg.NotificationTask;
import replete.ui.worker.RWorker;
import replete.ui.worker.events.RWorkerStatusEvent;
import replete.ui.worker.events.RWorkerStatusListener;

public class RWorkerTestFrame extends NotificationFrame {


    ////////////
    // FIELDS //
    ////////////

    private RButton btn1, btn2, btn3;
    private RTextArea txt;
    private RWorker curAction;
    private JCheckBox chk;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RWorkerTestFrame() {
        super("RWorker Test");

        setShowStatusBar(true);

        Lay.BLtg(this,
            "N", Lay.WL(
                btn1 = Lay.btn("+&Task"),
                btn2 = Lay.btn("+&Error"),
                btn3 = Lay.btn("+In&fo"),
                Lay.btn("&Clear", new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        txt.clear();
                    }
                }),
                Lay.btn("Pause", new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        curAction.pause();
                    }
                }),
                Lay.btn("Unpause", new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        curAction.unpause();
                    }
                }),
                Lay.btn("Stop", new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        curAction.stopContext();
                    }
                }),
                chk = Lay.chk("&Cause error")
            ),
            "C", Lay.sp(Lay.p(txt = Lay.txa("Output\n", "editable=false"))),
            "size=600,center"
        );

        btn1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final ExampleRWorker action = new ExampleRWorker(RWorkerTestFrame.this, chk.isSelected());
                curAction = action;

                action.addProgressListener(new ProgressListener() {
                    public void stateChanged(ProgressEvent e) {
                        txt.appendln(e.getMessages().toString());
                    }
                });

                action.addStatusListener(new RWorkerStatusListener() {
                    public void stateChanged(RWorkerStatusEvent e) {
                        txt.appendln("STATUS@LISTENER=" + e.getCurrent() + " [EDT? " + SwingUtilities.isEventDispatchThread() + "]");
                    }
                });

                NotificationTask prog = new NotificationTask()
                    .setTitle("Import File")
                    .setIcon(ImageLib.get(CommonConcepts._PLACEHOLDER))
                    .setAction(action)
                    .setStep("substep")
                    .setAutoRemove(false)
                    .setClickAction(new NotificationClickAction() {
                        public void clicked(NotificationCommon notif) {
                            Dialogs.showMessage(RWorkerTestFrame.this, "You clicked! " + notif);
                        }
                    })
                ;

                getNotificationModel().getTasks().add(prog);
                setShowNotificationArea(true);
                action.execute();
            }
        });
        btn2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getNotificationModel().getErrors().add(
                    new NotificationError()
                        .setTitle("An error occurred Yo! " + System.currentTimeMillis() % 10000)
                        .setError(new RuntimeException("YABBA DABBA"))
                );
                setShowNotificationArea(true);
            }
        });
        btn3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getNotificationModel().getInfos().add(
                    new NotificationInfo()
                        .setTitle("Info! " + System.currentTimeMillis() % 10000)
                );
                setShowNotificationArea(true);
            }
        });
    }

    public void appendln(String result) {
        txt.appendln(result);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        RWorkerTestFrame frame = new RWorkerTestFrame();
        frame.setVisible(true);
    }
}
