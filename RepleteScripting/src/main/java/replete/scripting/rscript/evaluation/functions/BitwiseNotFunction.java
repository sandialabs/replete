package replete.scripting.rscript.evaluation.functions;

public class BitwiseNotFunction extends Function {
    @Override
    public String getName() {
        return "~";
    }

    @Override
    public String getDescription() {
        return "bitwise not";
    }

    @Override
    public ParameterSet[] getAllowedParameterSets() {
        return new ParameterSet[] {
            new ParameterSet(
                "!RET", "val",
                Object.class, Object.class)
        };
    }

    @Override
    protected Object eval(Object[] args, int parameterTypeIndex) {
        return !((Boolean) args[0]);
    }
}
