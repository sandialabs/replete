package replete.pipeline.test;

import org.junit.Ignore;

import replete.pipeline.AbstractAtomicStage;
import replete.pipeline.desc.InputDescriptor;
import replete.pipeline.desc.OutputDescriptor;

@Ignore
public class BasicStage extends AbstractAtomicStage {

    private int executionCount = 0;

    public BasicStage(String name) {
        super(name);
    }

    @Override
    protected void init() {
        registerInputDescriptor(
            new InputDescriptor(
                this,
                "string1",
                "my string1",
                "my very nice string1",
                String.class,
                true
            )
        );
        registerInputDescriptor(
            new InputDescriptor(
                this,
                "string2",
                "my string2",
                "my very nice string2",
                String.class,
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
        executionCount++;
        System.out.println("TestStage '" + getName() + "' executed on '" + getInput("string1") + "' and '" + getInput("string2") + "'");
        setOutput("string", getInput("string1") + "*");
    }

    @Override
    public String toString() {
        return "TestStage [id=" + id + ", name=" + name + "]";
    }

    public int getExecutionCount() {
        return executionCount;
    }
}
