package replete.scripting.rscript.evaluation.functions;

public class CosineFunction extends Function {
    @Override
    public String getName() {
        return "cos";
    }

    @Override
    public String getDescription() {
        return "cosine";
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
        return Math.cos(((Number) args[0]).doubleValue());
    }
}
