package replete.pipeline.test;

import org.junit.Ignore;

import replete.pipeline.AbstractAtomicStage;
import replete.threads.ThreadUtil;

@Ignore
public class BlankStage extends AbstractAtomicStage {
    public static final int WAIT = 500;

    @Override
    protected void init() {}

    @Override
    protected void executeInner() {
        ThreadUtil.sleep(WAIT);
    }
}
