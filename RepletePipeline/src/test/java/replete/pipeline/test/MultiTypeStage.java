package replete.pipeline.test;

import org.junit.Ignore;

import replete.pipeline.AbstractAtomicStage;
import replete.pipeline.desc.InputDescriptor;
import replete.pipeline.desc.OutputDescriptor;

@Ignore
public class MultiTypeStage extends AbstractAtomicStage {

    public MultiTypeStage(String name) {
        super(name);
    }

    @Override
    protected void init() {
        registerInputDescriptor(
            new InputDescriptor(
                this,
                "string",
                "my string",
                "my very nice string",
                String.class,
                true
            )
        );
        registerInputDescriptor(
            new InputDescriptor(
                this,
                "int",
                "my int",
                "my very nice int",
                Integer.class,
                true
            )
        );
        registerOutputDescriptor(
            new OutputDescriptor(
                this,
                "string",
                "my string",
                "my very nice string",
                String.class
            )
        );
    }

    @Override
    protected void executeInner() {
        System.out.println("TestStage '" + getName() + "' executed on '" + getInput("string1") + "' and '" + getInput("string2") + "'");
        setOutput("string", getInput("string") + "*");
    }
}
