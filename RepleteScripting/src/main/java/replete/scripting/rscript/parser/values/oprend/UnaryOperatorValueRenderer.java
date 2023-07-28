package replete.scripting.rscript.parser.values.oprend;

import replete.scripting.rscript.parser.gen.ASTOperator;
import replete.scripting.rscript.parser.values.OperatorValue;
import replete.scripting.rscript.rendering.ASTRenderingContext;

public class UnaryOperatorValueRenderer extends OperatorValueRenderer {
    @Override
    public void render(ASTOperator node, ASTRenderingContext context) {
        boolean useParens = isUseParentheses(node, context);

        if(useParens) {
            context.append("(");
        }
        OperatorValue value = node.getValue();
        if(!context.isShortMode() || value != OperatorValue.UPLUS) {
            context.append(value);
        }
        context.append(node.getChild(0));
        if(useParens) {
            context.append(")");
        }
    }
}
