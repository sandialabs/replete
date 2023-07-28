package replete.scripting.rscript.evaluation;

import replete.scripting.rscript.evaluation.functions.EvaluationException;
import replete.scripting.rscript.parser.gen.ASTNode;

public interface ASTNodeEvaluator<T extends ASTNode> {
    public Object evaluate(T node, ASTEvaluationContext context) throws EvaluationException;
}
