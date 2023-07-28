package replete.scripting.rscript.rendering.renderers;

import replete.scripting.rscript.parser.gen.ASTFunction;
import replete.scripting.rscript.parser.gen.ASTNode;
import replete.scripting.rscript.parser.values.NodeValue;
import replete.scripting.rscript.rendering.ASTNodeRenderer;
import replete.scripting.rscript.rendering.ASTRenderingContext;

public class DefaultFunctionRenderer implements ASTNodeRenderer<ASTFunction> {
    @Override
    public void render(ASTFunction node, ASTRenderingContext context) {
        NodeValue value = context.getValue(node);
        context.append(value);
        context.append("(");
        for(int i = 0; i < node.getCount(); i++) {
            ASTNode child = node.getChild(i);
            context.append(child);
            if(i != node.getCount() - 1) {
                context.append(", ");
            }
        }
        context.append(")");
    }
}
