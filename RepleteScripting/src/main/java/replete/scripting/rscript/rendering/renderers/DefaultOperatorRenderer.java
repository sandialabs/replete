package replete.scripting.rscript.rendering.renderers;

import replete.scripting.rscript.parser.gen.ASTOperator;
import replete.scripting.rscript.parser.values.OperatorValue;
import replete.scripting.rscript.rendering.ASTNodeRenderer;
import replete.scripting.rscript.rendering.ASTRenderingContext;

public class DefaultOperatorRenderer implements ASTNodeRenderer<ASTOperator> {
    @Override
    public void render(ASTOperator node, ASTRenderingContext context) {
        OperatorValue value = context.getValue(node);
        value.render(node, context);
    }
}
