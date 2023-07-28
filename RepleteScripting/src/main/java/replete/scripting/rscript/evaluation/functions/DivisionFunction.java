package replete.scripting.rscript.evaluation.functions;

public class DivisionFunction extends Function {
    @Override
    public String getName() {
        return "/";
    }

    @Override
    public String getDescription() {
        return "arithmetic division";
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
        return ((Number) args[0]).doubleValue() / ((Number) args[1]).doubleValue();
    }
}
