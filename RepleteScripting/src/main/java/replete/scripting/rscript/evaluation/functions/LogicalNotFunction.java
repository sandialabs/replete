package replete.scripting.rscript.evaluation.functions;

public class LogicalNotFunction extends Function {
    @Override
    public String getName() {
        return "!";
    }

    @Override
    public String getDescription() {
        return "logical not";
    }

    @Override
    public ParameterSet[] getAllowedParameterSets() {
        return new ParameterSet[] {
            new ParameterSet(
                "!RET", "val",
                Boolean.class, Boolean.class)
        };
    }

    @Override
    protected Object eval(Object[] args, int parameterTypeIndex) {
        return !((Boolean) args[0]);
    }
}
