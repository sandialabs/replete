package replete.pipeline.test;

import org.junit.Ignore;

import replete.pipeline.AbstractAtomicStage;
import replete.pipeline.desc.InputDescriptor;
import replete.pipeline.desc.OutputDescriptor;

@Ignore
public class TestMultiplierStage extends AbstractAtomicStage {

    public TestMultiplierStage(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void init() {
        registerInputDescriptor(
            new InputDescriptor(
                this,
                "int1",
                "my int1",
                "my very nice int1",
                Integer.class,
                true
            )
        );
        registerInputDescriptor(
            new InputDescriptor(
                this,
                "int2",
                "my int2",
                "my very nice int2",
                Integer.class,
                true
            )
        );
        registerOutputDescriptor(
            new OutputDescriptor(
                this,
                "int",
                "my int",
                "my very nice int",
                Integer.class
            )
        );
    }

    @Override
    protected void executeInner() {
        System.out.println("TestMultiplierStage '" + getName() + "' executed on '" + getInput("int1") + "' and '" + getInput("int2") + "'");
        setOutput("int", (Integer)getInput("int1") * (Integer)getInput("int2"));
    }

}
