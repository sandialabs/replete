package replete.scripting.rscript.evaluation.functions;

public class RelationalLEFunction extends Function {
    @Override
    public String getName() {
        return "<=";
    }

    @Override
    public String getDescription() {
        return "relational less than or equal to";
    }

    @Override
    public ParameterSet[] getAllowedParameterSets() {
        return new ParameterSet[] {
            new ParameterSet(
                "!RET", "val1", "val2",
                Boolean.class, Number.class, Number.class)
        };
    }

    @Override
    protected Object eval(Object[] args, int parameterTypeIndex) {
        return ((Number) args[0]).doubleValue() <= ((Number) args[1]).doubleValue();
    }
}
