package replete.scripting.rscript.evaluation.functions;

import java.lang.reflect.Array;

public class ListSubscriptExpression extends Function {

    @Override
    public String getName() {
        return "[]";
    }

    @Override
    public String getDescription() {
        return "selection of a specific element within a list";
    }

    @Override
    public ParameterSet[] getAllowedParameterSets() {
        return new ParameterSet[] {
            new ParameterSet(
                "!RET", "val1", "val2",
                Object.class, Object.class, Integer.class)
        };
    }

    @Override
    protected Object eval(Object[] args, int parameterSetIndex) {
        if(!args[0].getClass().isArray()) {
            throw new EvaluationException("can only index into an array");
        }
        return Array.get(args[0], (Integer) args[1]);
    }
}
