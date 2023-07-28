package replete.scripting.rscript.evaluation.functions;

public class UnknownFunction extends Function {

    private String name;
    public UnknownFunction(String nm) {
        name = nm;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public ParameterSet[] getAllowedParameterSets() {
        return null;
    }

    @Override
    protected Object eval(Object[] args, int parameterSetIndex) {
        throw new EvaluationException("Function '" + getName() + "' not implemented.");
    }
}
