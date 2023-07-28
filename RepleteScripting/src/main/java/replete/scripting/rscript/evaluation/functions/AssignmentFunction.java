package replete.scripting.rscript.evaluation.functions;

public class AssignmentFunction extends Function {
    @Override
    public String getName() {
        return "=";
    }

    @Override
    public String getDescription() {
        return "assignment";
    }

    @Override
    public ParameterSet[] getAllowedParameterSets() {
        return new ParameterSet[] {
            new ParameterSet(
                "!RET", "var", "val",
                Object.class, Object.class, Object.class)
        };
    }

    @Override
    protected Object eval(Object[] args, int parameterTypeIndex) {
        return args[1];
    }

    @Override
    public boolean isAssignment() {
        return true;
    }
}
