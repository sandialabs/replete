package replete.scripting.rscript.evaluation.evaluators;

import replete.scripting.rscript.evaluation.ASTEvaluationContext;
import replete.scripting.rscript.evaluation.ASTNodeEvaluator;
import replete.scripting.rscript.evaluation.functions.EvaluationException;
import replete.scripting.rscript.evaluation.functions.Function;
import replete.scripting.rscript.evaluation.functions.FunctionList;
import replete.scripting.rscript.parser.gen.ASTFunction;
import replete.scripting.rscript.parser.values.FunctionValue;

public class DefaultFunctionEvaluator implements ASTNodeEvaluator<ASTFunction> {
    @Override
    public Object evaluate(ASTFunction node, ASTEvaluationContext context) throws EvaluationException {
        Object[] params = new Object[node.getCount()];
        for(int c = 0; c < node.getCount(); c++) {
            params[c] = context.evaluate(node.getChild(c));
        }
        FunctionValue value = node.getValue();
        Function func = FunctionList.getOrUnknown(value.getName());
        return func.eval(params);
    }
}
