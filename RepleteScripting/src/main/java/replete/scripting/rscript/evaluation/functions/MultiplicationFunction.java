package replete.scripting.rscript.evaluation.functions;

public class MultiplicationFunction extends Function {
    @Override
    public String getName() {
        return "*";
    }

    @Override
    public String getDescription() {
        return "arithmetic multiplication";
    }

    @Override
    public ParameterSet[] getAllowedParameterSets() {
        return new ParameterSet[] {
            new ParameterSet(
                "!RET", "val1", "val2",
                Number.class, Number.class, Number.class),
            new ParameterSet(
                    "!RET", "val1", "val2",
                    Number.class, Boolean.class, Number.class),
            new ParameterSet(
                    "!RET", "val1", "val2",
                    Number.class, Number.class, Boolean.class),
            new ParameterSet(
                    "!RET", "val1", "val2",
                    Number.class, Boolean.class, Boolean.class),

        };
    }

    @Override
    protected Object eval(Object[] args, int parameterTypeIndex) {
        Number result = 0.0;
        switch (parameterTypeIndex) {
        case 0:
            result = ((Number) args[0]).doubleValue() * ((Number) args[1]).doubleValue();
            break;
        case 1:
            result = (((Boolean) args[0]) ? 1.0 : 0.0) * ((Number) args[1]).doubleValue();
            break;
        case 2:
            result = (((Boolean) args[1]) ? 1.0 : 0.0) * ((Number) args[0]).doubleValue();
            break;
        case 3:
            result = (((Boolean) args[0]) ? 1.0 : 0.0) * (((Boolean) args[1]) ? 1.0 : 0.0);
            break;
        }
        return result;
    }
}
