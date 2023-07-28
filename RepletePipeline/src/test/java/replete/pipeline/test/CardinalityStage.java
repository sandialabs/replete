package replete.pipeline.test;

import org.junit.Ignore;

import replete.pipeline.AbstractAtomicStage;
import replete.pipeline.desc.InputDescriptor;

@Ignore
public class CardinalityStage extends AbstractAtomicStage {

    @Override
    protected void init() {
        registerInputDescriptor(
            new InputDescriptor(
                this,
                "dir0",
                null,
                null,
                String.class,
                0, 1
            )
        );
        registerInputDescriptor(
            new InputDescriptor(
                this,
                "dir1",
                null,
                null,
                String.class,
                0, Integer.MAX_VALUE
            )
        );
        registerInputDescriptor(
            new InputDescriptor(
                this,
                "dir2",
                null,
                null,
                String.class,
                false,
                4, 6
            )
        );
        registerInputDescriptor(
            new InputDescriptor(
                this,
                "dir3",
                null,
                null,
                String.class,
                6, Integer.MAX_VALUE
            )
        );
    }

    @Override
    protected void executeInner() {
        // Nothing needed here
    }

}
