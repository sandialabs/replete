package replete.bash.evaluation;

import java.util.Map;

import replete.math.parser.EquationParser;
import replete.math.parser.ParsedEquation;
import replete.math.parser.gen.ParseException;

public class EquationEvaluator {
    public static Object evaluate(String expression) throws ParseException {
        return evaluate(expression, null);
    }
    public static Object evaluate(String expression, Map<String, Object> vars) throws ParseException {
        return evaluate(EquationParser.parse(expression), vars);
    }
    public static Object evaluate(ParsedEquation eq) {
        return evaluate(eq, null);
    }
    public static Object evaluate(ParsedEquation eq, Map<String, Object> vars) {
        EvaluationContext context = new EvaluationContext();
        if(vars != null) {
            for(String var : vars.keySet()) {
                context.setValueForVariable(var, vars.get(var));
            }
        }
        return eq.getTree().eval(context);
    }
}
