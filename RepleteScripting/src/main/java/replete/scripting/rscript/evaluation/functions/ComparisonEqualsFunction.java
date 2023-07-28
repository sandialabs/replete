package replete.scripting.rscript.evaluation.functions;

public class ComparisonEqualsFunction extends Function {
    @Override
    public String getName() {
        return "==";
    }

    @Override
    public String getDescription() {
        return "equality comparison";
    }

    @Override
    public ParameterSet[] getAllowedParameterSets() {
        return new ParameterSet[] {
            new ParameterSet(
                "!RET", "val1", "val2",
                Boolean.class, Object.class, Object.class)
        };
    }

    @Override
    protected Object eval(Object[] args, int parameterTypeIndex) {
        if (args[0] instanceof Number && args[1] instanceof Number) {
            return ((Number)args[0]).doubleValue() == ((Number)args[1]).doubleValue();
        }
        return args[0].equals(args[1]);
    }
}
