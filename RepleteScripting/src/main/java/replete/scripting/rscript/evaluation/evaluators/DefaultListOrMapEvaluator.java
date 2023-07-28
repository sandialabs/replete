package replete.scripting.rscript.evaluation.evaluators;

import java.util.ArrayList;
import java.util.List;

import replete.scripting.rscript.evaluation.ASTEvaluationContext;
import replete.scripting.rscript.evaluation.ASTNodeEvaluator;
import replete.scripting.rscript.evaluation.functions.EvaluationException;
import replete.scripting.rscript.parser.gen.ASTListOrMap;

public class DefaultListOrMapEvaluator implements ASTNodeEvaluator<ASTListOrMap> {
    @Override
    public Object evaluate(ASTListOrMap node, ASTEvaluationContext context) throws EvaluationException {
        List<Object> params = new ArrayList<>(node.getCount());
        for(int c = 0; c < node.getCount(); c++) {
            params.add(context.evaluate(node.getChild(c)));
            // TODO: needs update for MAP, need validation on mixed list/map nodes during parsing
        }
        return params;
    }
}
