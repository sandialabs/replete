package replete.ui.windows.notifications;

import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTree;

import replete.numbers.RandomUtil;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.text.RTextField;
import replete.ui.windows.Dialogs;
import replete.ui.windows.notifications.msg.NotificationError;
import replete.ui.windows.notifications.msg.NotificationInfo;
import replete.ui.windows.notifications.msg.NotificationTask;

public class TestNotificationFrame extends NotificationFrame {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TestNotificationFrame() {
        super("Test Notification Frame");

        JButton btnToggleGp, btnToggleStatus, btnShowDialog,
            btnAddRandomTask, btnAddSpecificTask, btnAddProblemTasks, btnAddError, btnAddInfo;
        final JCheckBox chkPausable, chkStoppable, chkInd;
        final RTextField txtDuration;

        Lay.BLtg(this,
            "C", Lay.BL(
                "N", Lay.BL(
                    "C", Lay.GL(2, 3, "hgap=5,vgap=5",
                        btnToggleGp      = Lay.btn("Toggle Notification Area", NotificationImageModel.COLLAPSE),
                        btnToggleStatus  = Lay.btn("Toggle Status", NotificationImageModel.COLLAPSE),
                        btnShowDialog    = Lay.btn("Show Dialog", NotificationImageModel.COLLAPSE),
                        btnAddRandomTask = Lay.btn("Add Random Task", CommonConcepts.PROGRESS),
                        btnAddError      = Lay.btn("Add Error", CommonConcepts.ERROR),
                        btnAddInfo       = Lay.btn("Add Info", CommonConcepts.INFO)
                    ),
                    "S", Lay.BL(
                        "C", Lay.FL("L", "nogap,eb=5t",
                            btnAddSpecificTask = Lay.btn("Add This Task:", CommonConcepts.PROGRESS),
                            chkPausable        = Lay.chk("PAUSABLE"),
                            chkStoppable       = Lay.chk("STOPPABLE"),
                            chkInd             = Lay.chk("INDETERMINATE"),
                            Lay.lb("DURATION: ", NotificationImageModel.DURATION, "eb=5l"),
                            txtDuration        = Lay.tx("5000", 5, "center")
                        ),
                        "E", Lay.FL(
                            btnAddProblemTasks = Lay.btn("&Add Problem Tasks", CommonConcepts.PROGRESS),
                            "nogap,eb=5t"
                        )
                    ),
                    "eb=5"
                ),
                "W", Lay.sp(new JTree()),
                "C", Lay.p("bg=CCCCFF")
            ),
            "size=[850,600],center=1"
        );
        btnToggleGp.addActionListener(e -> setShowNotificationArea(!isShowNotificationArea()));
        btnToggleStatus.addActionListener(e -> setShowStatusBar(!isShowStatusBar()));
        btnShowDialog.addActionListener(e -> {
            TestNotificationDialog dlg = new TestNotificationDialog(
                TestNotificationFrame.this, "Test Notification Dialog", true);
            Lay.hn(dlg, "size=400,center");
            dlg.setShowNotificationArea(true);
            dlg.setShowStatusBar(true);
            dlg.setVisible(true);
        });
        btnAddRandomTask.addActionListener(e -> {
            boolean cp   = RandomUtil.flip();
            boolean cs   = RandomUtil.flip();
            boolean ind  = RandomUtil.flip();
            int duration = ind ? (int) RandomUtil.getRandomWithinRange(4000, 10000) :
                                 (int) RandomUtil.getRandomWithinRange(7500, 15000);
            String title = ind ? "RandCompiling" : "RandTransferring";

            startTestWorker(title, ind, duration, cp, cs);
        });
        btnAddSpecificTask.addActionListener(e -> {
            boolean cp   = chkPausable.isSelected();
            boolean cs   = chkStoppable.isSelected();
            boolean ind  = chkInd.isSelected();
            int duration = txtDuration.getInteger();

            startTestWorker("SpecScanning", ind, duration, cp, cs);
        });
        btnAddProblemTasks.addActionListener(e -> {
            startTestWorker("ProblemFixing1", false, 6000, true, false);
            startTestWorker("ProblemFixing2", true, 2000, false, true);
        });
        btnAddError.addActionListener(e -> {
            NotificationError err = new NotificationError()
                .setTitle("ERROR! " + System.currentTimeMillis() % 1000)
                .setIcon(ImageLib.get(CommonConcepts._PLACEHOLDER))
            ;
            if(new Random().nextInt() % 2 == 0) {
                err.setError(new RuntimeException());
            }
            if(new Random().nextInt() % 2 == 0) {
                err.setClickAction(notif -> {
                    Dialogs.showMessage(TestNotificationFrame.this, "Error Clicked");
                });
            }
            getNotificationModel().getErrors().add(err);
        });
        btnAddInfo.addActionListener(e -> {
            NotificationInfo info = new NotificationInfo()
                .setTitle("INFO!" + System.currentTimeMillis() % 1000)
                .setIcon(ImageLib.get(CommonConcepts._PLACEHOLDER))
            ;
            if(new Random().nextInt() % 2 == 0) {
                info.setClickAction(notif -> {
                    Dialogs.showMessage(TestNotificationFrame.this,
                        "Info Clicked " + System.currentTimeMillis() % 1000);
                });
            }
            getNotificationModel().getInfos().add(info);
        });
    }

    private void startTestWorker(String title, boolean ind, int duration, boolean canPause, boolean canStop) {
        TestRWorker action;
        if(ind) {
            action = new TestRWorker(canPause, canStop, -1, duration);
        } else {
            action = new TestRWorker(canPause, canStop, duration / 100, -1);
        }
        NotificationTask prog = new NotificationTask()
            .setTitle(title + (System.currentTimeMillis() % 1000) +
                " {DUR=" + duration + "}" +
                (canPause ? " (PAUSABLE)" : "") +
                (canStop ? " (STOPPABLE)" : "") +
                (ind ? " (IND)" : "")
            )
            .setUseWaitCursor(true)
            .setIcon(ImageLib.get(CommonConcepts._PLACEHOLDER))
            .setAction(action)
        ;
        action.execute();
        getNotificationModel().getTasks().add(prog);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        TestNotificationFrame frame = new TestNotificationFrame();
        frame.setShowNotificationArea(true);
        frame.setShowStatusBar(true);
        frame.setVisible(true);
    }
}
