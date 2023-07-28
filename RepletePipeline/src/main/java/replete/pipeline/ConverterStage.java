package replete.pipeline;

import replete.pipeline.desc.InputDescriptor;
import replete.pipeline.desc.OutputDescriptor;

public abstract class ConverterStage<I, O> extends AbstractAtomicStage {

    public static final String INPUT_PORT_NAME = "input";
    public static final String OUTPUT_PORT_NAME = "output";

    public ConverterStage(String name) {
        super(name);
    }

    @Override
    protected void init() {
        registerInputDescriptor(
            new InputDescriptor(
                this,
                INPUT_PORT_NAME,
                "Conversion Input",
                "Input to be converted.",
                Object.class,
                true
            )
        );
        registerOutputDescriptor(
            new OutputDescriptor(
                this,
                OUTPUT_PORT_NAME,
                "Conversion Output",
                "Output from conversion.",
                Object.class
            )
        );
    }

    @Override
    protected void executeInner() {
        O out = convert((I) getInput(INPUT_PORT_NAME));
        setOutput(OUTPUT_PORT_NAME, out);
    }

    protected abstract O convert(I input);
}
