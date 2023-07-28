package replete.scripting.rscript.evaluation.functions;

public class SineFunction extends Function {
    @Override
    public String getName() {
        return "sin";
    }

    @Override
    public String getDescription() {
        return "sine";
    }

    @Override
    public ParameterSet[] getAllowedParameterSets() {
        return new ParameterSet[] {
            new ParameterSet(
                "!RET", "val",
                Number.class, Number.class)
        };
    }

    @Override
    protected Object eval(Object[] args, int parameterSetIndex) {
        return Math.sin(((Number) args[0]).doubleValue());
    }
}
