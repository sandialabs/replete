package replete.scripting.rscript.evaluation.evaluators;

import replete.scripting.rscript.evaluation.ASTEvaluationContext;
import replete.scripting.rscript.evaluation.ASTNodeEvaluator;
import replete.scripting.rscript.evaluation.functions.EvaluationException;
import replete.scripting.rscript.parser.gen.ASTConstant;
import replete.scripting.rscript.parser.values.ConstantValue;

public class DefaultConstantEvaluator implements ASTNodeEvaluator<ASTConstant> {
    @Override
    public Object evaluate(ASTConstant node, ASTEvaluationContext context) throws EvaluationException {
        ConstantValue value = node.getValue();
        return value.getValue();
    }
}
