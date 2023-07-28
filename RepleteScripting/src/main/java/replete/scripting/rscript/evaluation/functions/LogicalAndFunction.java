package replete.scripting.rscript.evaluation.functions;

public class LogicalAndFunction extends Function {
    @Override
    public String getName() {
        return "&&";
    }

    @Override
    public String getDescription() {
        return "logical and";
    }

    @Override
    public ParameterSet[] getAllowedParameterSets() {
        return new ParameterSet[] {
            new ParameterSet(
                "!RET", "val1", "val2",
                Boolean.class, Boolean.class, Boolean.class)
        };
    }

    @Override
    protected Object eval(Object[] args, int parameterTypeIndex) {
        return (Boolean) args[0] && (Boolean) args[1];
    }
}
