package replete.scripting.rscript.evaluation.evaluators;

import replete.scripting.rscript.evaluation.ASTEvaluationContext;
import replete.scripting.rscript.evaluation.ASTNodeEvaluator;
import replete.scripting.rscript.evaluation.functions.EvaluationException;
import replete.scripting.rscript.parser.gen.ASTStart;

public class DefaultStartEvaluator implements ASTNodeEvaluator<ASTStart> {
    @Override
    public Object evaluate(ASTStart node, ASTEvaluationContext context) throws EvaluationException {
        Object lastValue = null;
        for(int i = 0; i < node.getCount(); i++) {
            lastValue = context.evaluate(node.getChild(i));
        }
        return lastValue;
    }
}
