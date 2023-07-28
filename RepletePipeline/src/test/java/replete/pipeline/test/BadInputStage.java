package replete.pipeline.test;

import org.junit.Ignore;

import replete.pipeline.AbstractAtomicStage;
import replete.pipeline.Pipeline;
import replete.pipeline.desc.InputDescriptor;

@Ignore
public class BadInputStage extends AbstractAtomicStage {
    public BadInputStage(String name) {
        super(name);
    }
    @Override
    protected void init() {
        registerInputDescriptor(
            new InputDescriptor(
                this,
                "stri" + Pipeline.PIPELINE_NAME_SEPARATOR + "ng1",
                "my string1",
                "my very nice string1",
                String.class,
                true
            )
        );
    }
    @Override
    protected void executeInner() {
    }
}