package replete.scripting.rscript.evaluation.functions;

public class ExponentiationFunction extends Function {
    @Override
    public String getName() {
        return "**";
    }

    @Override
    public String getDescription() {
        return "arithmetic exponentiation";
    }

    @Override
    public ParameterSet[] getAllowedParameterSets() {
        return new ParameterSet[] {
            new ParameterSet(
                "!RET", "val1", "val2",
                Number.class, Number.class, Number.class)
        };
    }

    @Override
    protected Object eval(Object[] args, int parameterTypeIndex) {
        return Math.pow(((Number) args[0]).doubleValue(), ((Number) args[1]).doubleValue());
    }
}
