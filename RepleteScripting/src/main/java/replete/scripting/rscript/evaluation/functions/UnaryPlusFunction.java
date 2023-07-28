package replete.scripting.rscript.evaluation.functions;

public class UnaryPlusFunction extends Function {
    @Override
    public String getName() {
        return "+";
    }

    @Override
    public String getDescription() {
        return "unary plus";
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
        return args[0];
    }
}
