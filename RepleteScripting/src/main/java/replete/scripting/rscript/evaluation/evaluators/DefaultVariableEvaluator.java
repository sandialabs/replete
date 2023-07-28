package replete.scripting.rscript.evaluation.evaluators;

import replete.scripting.rscript.evaluation.ASTEvaluationContext;
import replete.scripting.rscript.evaluation.ASTNodeEvaluator;
import replete.scripting.rscript.evaluation.functions.EvaluationException;
import replete.scripting.rscript.parser.gen.ASTVariable;

public class DefaultVariableEvaluator implements ASTNodeEvaluator<ASTVariable> {
    @Override
    public Object evaluate(ASTVariable node, ASTEvaluationContext context) throws EvaluationException {
        return context.getValueForVariable(node.getVariableName());
    }
}
