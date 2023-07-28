package replete.scripting.rscript.evaluation.functions;

public class UnaryMinusFunction extends Function {
    @Override
    public String getName() {
        return "-";
    }

    @Override
    public String getDescription() {
        return "unary minus";
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
    protected Object eval(Object[] args, int parameterTypeIndex) {
        return ((Number) args[0]).doubleValue() * -1;
    }
}
