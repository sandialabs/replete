package replete.pipeline.test;

import static org.junit.Assert.fail;

import org.junit.Ignore;

import replete.pipeline.AbstractAtomicStage;
import replete.pipeline.desc.InputDescriptor;
import replete.pipeline.desc.OutputDescriptor;
import replete.pipeline.errors.DescriptorException;
import replete.pipeline.errors.InputDescriptorException;
import replete.pipeline.errors.OutputDescriptorException;

@Ignore
public class MisregisterStage extends AbstractAtomicStage {

    public MisregisterStage() {
        super("Misregister");
    }

    @Override
    protected void init() {
        registerInputDescriptor(
            new InputDescriptor(
                this, "Name", "FriendlyName", "Description", Object.class, true
            )
        );
        try {
            registerInputDescriptor(
                new InputDescriptor(
                    this, "Name", "FriendlyName", "Description", Object.class, true
                )
            );
            fail();
        } catch(InputDescriptorException e) {}
        registerOutputDescriptor(
            new OutputDescriptor(
                this, "XName", "FriendlyName", "Description", Object.class
            )
        );
        try {
            registerOutputDescriptor(
                new OutputDescriptor(
                    this, "XName", "FriendlyName", "Description", Object.class
                )
            );
            fail();
        } catch(OutputDescriptorException e) {}
    }

    @Override
    protected void executeInner() {
        try {
            registerInputDescriptor(
                new InputDescriptor(
                    this, "Name2", "FriendlyName", "Description", Object.class, true
                )
            );
            fail();
        } catch(DescriptorException e) {}
        try {
            registerOutputDescriptor(
                new OutputDescriptor(
                    this, "Name3", "FriendlyName", "Description", Object.class
                )
            );
            fail();
        } catch(DescriptorException e) {}

        setOutput("XName", "X" + getInput("Name") + "X");
    }
}
