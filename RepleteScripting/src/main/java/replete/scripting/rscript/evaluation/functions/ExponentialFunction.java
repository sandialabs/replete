package replete.scripting.rscript.evaluation.functions;

public class ExponentialFunction extends Function {
    @Override
    public String getName() {
        return "exp";
    }

    @Override
    public String getDescription() {
        return "exponential";
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
        return Math.pow(Math.E, ((Number) args[0]).doubleValue());
    }
}
