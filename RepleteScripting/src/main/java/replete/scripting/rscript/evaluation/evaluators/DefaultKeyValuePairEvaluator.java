package replete.scripting.rscript.evaluation.evaluators;

import replete.scripting.rscript.evaluation.ASTEvaluationContext;
import replete.scripting.rscript.evaluation.ASTNodeEvaluator;
import replete.scripting.rscript.evaluation.functions.EvaluationException;
import replete.scripting.rscript.parser.gen.ASTKeyValuePair;

public class DefaultKeyValuePairEvaluator implements ASTNodeEvaluator<ASTKeyValuePair> {
    @Override
    public Object evaluate(ASTKeyValuePair node, ASTEvaluationContext context) throws EvaluationException {
        throw new IllegalStateException("This evaluator has no practical meaning at this point.");
    }
}
