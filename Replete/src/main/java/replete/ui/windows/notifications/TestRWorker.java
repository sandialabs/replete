package replete.ui.windows.notifications;

import replete.progress.FractionProgressMessage;
import replete.threads.ThreadUtil;
import replete.ui.worker.RWorker;

public class TestRWorker extends RWorker<Void, Void> {
    private int totalSteps;
    private int sleepTime;

    public TestRWorker(boolean canPause, boolean canStop, int totalSteps, int sleepTime) {
        super(canPause, canStop);

        this.totalSteps = totalSteps;
        this.sleepTime = sleepTime;
    }

    @Override
    protected Void background(Void gathered) throws Exception {
        if(totalSteps > 0) {
            for(int i = 0; i < totalSteps; i++) {
                ThreadUtil.sleep(100);   // Do stuff
                ttContext.checkPauseAndStop();
                FractionProgressMessage pm = new FractionProgressMessage("HI", "WHAT", i + 1, totalSteps);
                publish(pm);
            }
        } else {
            ThreadUtil.sleep(sleepTime);
        }
        return null;
    }
}
