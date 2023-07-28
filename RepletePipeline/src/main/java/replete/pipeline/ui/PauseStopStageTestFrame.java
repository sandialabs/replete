package replete.pipeline.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import replete.pipeline.AbstractAtomicStage;
import replete.pipeline.Pipeline;
import replete.progress.FractionProgressMessage;
import replete.threads.ThreadUtil;
import replete.ui.button.RButton;
import replete.ui.lay.Lay;
import replete.ui.windows.notifications.NotificationFrame;
import replete.ui.windows.notifications.msg.NotificationTask;
import replete.ui.worker.RWorker;

public class PauseStopStageTestFrame extends NotificationFrame {


    public PauseStopStageTestFrame() {
        super("Example Frame for PauseStopStageTest");

        String content =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed eu tellus ex. Maecenas nec sapien est. Pellentesque aliquam urna quis velit convallis pellentesque. Curabitur quis mauris turpis. Phasellus egestas erat et ante gravida dignissim. Pellentesque sapien enim, gravida eget felis eu, bibendum faucibus odio.";

        RButton btnSubmit;
        Lay.BLtg(this,
            "N", Lay.lb("<html>" + content + "</html>", "bg=220,eb=5,augb=mb(1b,black)"),
            "C", btnSubmit = Lay.btn("&Submit"),
            "size=800,center"
        );

        btnSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final XStage stage = new XStage("Alligator");
                stage.setCanPause(true);
                stage.setCanStop(true);

                final XStage stage2 = new XStage("Bear");
                stage2.setCanPause(true);
                stage2.setCanStop(true);

                final XStage stage3 = new XStage("Gryphon");
                stage3.setCanPause(true);
                stage3.setCanStop(true);

                final Pipeline pipeline = new Pipeline();
                pipeline.setAggregateProgressMode(true);
                pipeline.addStage(stage);
                pipeline.addStage(stage2);
                pipeline.addStage(stage3);

                RWorker<Void, Void> worker = new RWorker<Void, Void>(pipeline) {
                    @Override
                    protected Void background(Void gathered) throws Exception {
                        pipeline.execute();
                        return null;
                    }
                };

                NotificationTask task = new NotificationTask()
                    .setAction(worker)
                    .setAddError(true)
                    .setTitle("Pipeline Task")
                    .setUseWaitCursor(true);

                getNotificationModel().getTasks().add(task);

                worker.execute();
            }
        });
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    public class XStage extends AbstractAtomicStage {
        public XStage(String name) {
            super(name);
        }
        @Override
        protected void executeInner() {
            for(int i = 0; i < 100; i++) {
                publishProgress(new FractionProgressMessage(getName(), i, 100));
                checkPauseAndStop();
                ThreadUtil.sleep(100);
                System.out.println(getName() + " // " + i);
            }
            publishProgress(new FractionProgressMessage(getName(), 100, 100));
        }
        @Override
        public String toString() {
            return "TestStage [id=" + id + ", name=" + name + "]";
        }
        @Override
        protected void init() {
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        PauseStopStageTestFrame frame = new PauseStopStageTestFrame();
        frame.setShowNotificationArea(true);
        frame.setShowStatusBar(true);
        frame.setVisible(true);
    }
}
