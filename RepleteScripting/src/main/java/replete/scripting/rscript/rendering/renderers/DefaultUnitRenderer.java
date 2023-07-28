package replete.scripting.rscript.rendering.renderers;

import replete.scripting.rscript.parser.gen.ASTNode;
import replete.scripting.rscript.parser.gen.ASTUnit;
import replete.scripting.rscript.parser.values.NodeValue;
import replete.scripting.rscript.rendering.ASTNodeRenderer;
import replete.scripting.rscript.rendering.ASTRenderingContext;

public class DefaultUnitRenderer implements ASTNodeRenderer<ASTUnit> {
    @Override
    public void render(ASTUnit node, ASTRenderingContext context) {
        NodeValue value = context.getValue(node);
        ASTNode child = node.getChild(0);
        context.append(child);
        context.append(" {");
        context.append(value);
        context.append("}");
    }
}
