package replete.scripting.rscript.parser.values.oprend;

import replete.scripting.rscript.parser.gen.ASTNode;
import replete.scripting.rscript.parser.gen.ASTOperator;
import replete.scripting.rscript.parser.values.OperatorValue;
import replete.scripting.rscript.rendering.ASTRenderingContext;

public class IndexOperatorValueRenderer extends OperatorValueRenderer {

    @Override
    public void render(ASTOperator node, ASTRenderingContext context) {
        OperatorValue value = node.getValue();
        ASTNode parent = node.getParent();

        // Short rendering
        if(context.isShortMode()) {
            context.append(node.getChild(0));
            context.append("[");
            context.append(node.getChild(1));
            context.append("]");

        // Long rendering
        } else {
            context.append(node.getChild(0));
            context.append("[");
            context.append(node.getChild(1));
            context.append("]");
        }
    }
}
