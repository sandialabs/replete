package replete.scripting.rscript.evaluation.functions;

public class ModulusFunction extends Function {
    @Override
    public String getName() {
        return "%";
    }

    @Override
    public String getDescription() {
        return "arithmetic modulus";
    }

    @Override
    public ParameterSet[] getAllowedParameterSets() {
        return new ParameterSet[] {
            new ParameterSet(
                "!RET", "val1", "val2",
                Integer.class, Integer.class, Integer.class),
            new ParameterSet(
                "!RET", "val1", "val2",
                Long.class, Long.class, Long.class),
            new ParameterSet(
                "!RET", "val1", "val2",
                Long.class, Integer.class, Long.class),
            new ParameterSet(       // Only needed because !orderMatters not impl
                "!RET", "val1", "val2",
                Long.class, Long.class, Integer.class),
        };
    }

    @Override
    protected Object eval(Object[] args, int parameterTypeIndex) {
        if(args[0] instanceof Integer && args[1] instanceof Integer) {
            return ((Number) args[0]).intValue() % ((Number) args[1]).intValue();
        }
        return ((Number) args[0]).longValue() % ((Number) args[1]).longValue();
    }
}
