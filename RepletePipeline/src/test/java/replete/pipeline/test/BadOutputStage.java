package replete.pipeline.test;

import org.junit.Ignore;

import replete.pipeline.AbstractAtomicStage;
import replete.pipeline.Pipeline;
import replete.pipeline.desc.OutputDescriptor;

@Ignore
public class BadOutputStage extends AbstractAtomicStage {
    public BadOutputStage(String name) {
        super(name);
    }
    @Override
    protected void init() {
        registerOutputDescriptor(
            new OutputDescriptor(
                this,
                "stri" + Pipeline.PIPELINE_NAME_SEPARATOR + "ng1",
                "my string1",
                "my very nice string1",
                String.class
            )
        );
    }
    @Override
    protected void executeInner() {
    }
}
