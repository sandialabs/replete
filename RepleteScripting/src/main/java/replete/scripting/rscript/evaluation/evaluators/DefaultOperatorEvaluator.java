package replete.scripting.rscript.evaluation.evaluators;

import replete.scripting.rscript.evaluation.ASTEvaluationContext;
import replete.scripting.rscript.evaluation.ASTNodeEvaluator;
import replete.scripting.rscript.evaluation.functions.AssignmentFunction;
import replete.scripting.rscript.evaluation.functions.EvaluationException;
import replete.scripting.rscript.evaluation.functions.Function;
import replete.scripting.rscript.parser.gen.ASTOperator;
import replete.scripting.rscript.parser.gen.ASTVariable;

public class DefaultOperatorEvaluator implements ASTNodeEvaluator<ASTOperator> {

    // TODO: Shouldn't some of the logic below be moved to operator-specific
    // implementations?  Not sure...
    @Override
    public Object evaluate(ASTOperator node, ASTEvaluationContext context) throws EvaluationException {
        Function func = node.getFunction();
        if(func.isAssignment()) {
            if(!(node.getChild(0) instanceof ASTVariable) || ((ASTVariable)node.getChild(0)).getOrder() != 0) {
                throw new EvaluationException("Invalid left hand side for assignment.  Left hand side must be an order-0 variable.");
            }
        }
        Object[] params = new Object[node.getCount()];
        for(int c = 0; c < node.getCount(); c++) {

            // We don't want eval to scream if left hand side of a
            // assignment variable hasn't been defined yet.  In
            // other words we don't need/want to evaluate "x" in:
            //    x = 3 + y
            // But we have to in:
            //    x *= 3 + y
            if(func.getClass().equals(AssignmentFunction.class) && c == 0) {
                params[c] = null;
            } else {
                params[c] = context.evaluate(node.getChild(c));
            }
        }
        Object result = func.eval(params);
        if(func.isAssignment()) {
            context.setValueForVariable(((ASTVariable) node.getChild(0)).getVariableName(), result);
        }
        return result;
    }
}
