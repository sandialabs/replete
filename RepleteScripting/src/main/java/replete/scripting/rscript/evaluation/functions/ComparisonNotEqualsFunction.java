package replete.scripting.rscript.evaluation.functions;

public class ComparisonNotEqualsFunction extends Function {
    @Override
    public String getName() {
        return "!=";
    }

    @Override
    public String getDescription() {
        return "inequality comparison";
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
        return !args[0].equals(args[1]);
    }
}
