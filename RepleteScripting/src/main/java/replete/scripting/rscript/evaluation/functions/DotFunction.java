package replete.scripting.rscript.evaluation.functions;

public class DotFunction extends Function {

    @Override
    public String getName() {
        return ".";
    }

    @Override
    public String getDescription() {
        return "dot product";
    }

    @Override
    public ParameterSet[] getAllowedParameterSets() {
        return new ParameterSet[] {
            new ParameterSet(
                "!RET", "val1", "val2",
                Object.class, Object.class, Object.class)
        };
    }

    @Override
    protected Object eval(Object[] args, int parameterTypeIndex) {
        throw new EvaluationException("Function '" + getName() + "' not implemented.");
    }
}
