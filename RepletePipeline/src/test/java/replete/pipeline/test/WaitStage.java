package replete.pipeline.test;

import org.junit.Ignore;

import replete.pipeline.AbstractAtomicStage;
import replete.threads.ThreadUtil;

@Ignore
public class WaitStage extends AbstractAtomicStage {
    public int wait;

    public WaitStage(int wait) {
        this.wait = wait;
    }

    @Override
    protected void init() {}

    @Override
    protected void executeInner() {
        System.out.println("WaitStage beginning.  Waiting for " + wait + "ms @ " +
            System.currentTimeMillis());
        ThreadUtil.sleep(wait);
        System.out.println("WaitStage ending.  Waited for " + wait + "ms @ " +
            System.currentTimeMillis());
    }
}
