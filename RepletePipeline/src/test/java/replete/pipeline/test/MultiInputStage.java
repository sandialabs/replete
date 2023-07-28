package replete.pipeline.test;

import java.io.File;

import org.junit.Ignore;

import replete.pipeline.AbstractAtomicStage;
import replete.pipeline.desc.InputDescriptor;
import replete.pipeline.desc.OutputDescriptor;

@Ignore
public class MultiInputStage extends AbstractAtomicStage {

    public MultiInputStage(String name) {
        super(name);
    }

    @Override
    protected void init() {
        registerInputDescriptor(
            new InputDescriptor(
                this,
                "files",
                null,
                null,
                File.class,
                1, 3
            )
        );
        registerOutputDescriptor(
            new OutputDescriptor(
                this,
                "dir",
                null,
                null,
                File.class
            )
        );
    }

    @Override
    protected void executeInner() {

    }
}