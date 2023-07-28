package replete.ui.worker.test;

import replete.progress.FractionProgressMessage;
import replete.progress.IndeterminateProgressMessage;
import replete.progress.PercentProgressMessage;
import replete.threads.ThreadUtil;
import replete.ttc.TransparentTaskContext;


public class LongTask {
    public static String task(TransparentTaskContext ttContext, String userInput, int waitMillis, boolean causeError) {
        System.out.println("BEGIN-TASK @ " + userInput);

        for(int i = 0; i < 5; i++) {
            ttContext.checkPauseAndStop();
            if((i + 1) % 5 == 0 && causeError) {
                throw new RuntimeException("error in task");
            }
            ThreadUtil.sleep(waitMillis);
            ttContext.publishProgress(new FractionProgressMessage("Processing", i + 1, 5));
        }

        for(int i = 0; i < 7; i++) {
            ttContext.checkPauseAndStop();
            if((i + 1) % 5 == 0 && causeError) {
                throw new RuntimeException("error in task");
            }
            ThreadUtil.sleep(waitMillis);
            ttContext.publishProgress(new PercentProgressMessage("Processing", "images", (int) Math.round((i + 1) / 7.0 * 100)));
        }

        ttContext.publishProgress(new IndeterminateProgressMessage("More Processing", "of stuff"));
        ThreadUtil.sleep(5000);

        System.out.println("END-TASK @ " + userInput);
        return "task(" + userInput + ") = " + userInput.length();
    }
}
