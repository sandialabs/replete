package replete.scripting.rscript.evaluation.functions;

public class TangentFunction extends Function {
    @Override
    public String getName() {
        return "tan";
    }

    @Override
    public String getDescription() {
        return "tangent";
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
        return Math.tan(((Number) args[0]).doubleValue());
    }
}
