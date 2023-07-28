package replete.scripting.rscript.evaluation.evaluators;

import replete.scripting.rscript.evaluation.ASTEvaluationContext;
import replete.scripting.rscript.evaluation.ASTNodeEvaluator;
import replete.scripting.rscript.evaluation.functions.EvaluationException;
import replete.scripting.rscript.parser.gen.ASTStatement;

public class DefaultStatementEvaluator implements ASTNodeEvaluator<ASTStatement> {
    @Override
    public Object evaluate(ASTStatement node, ASTEvaluationContext context) throws EvaluationException {
        return context.evaluate(node.getChild(0));
    }
}
