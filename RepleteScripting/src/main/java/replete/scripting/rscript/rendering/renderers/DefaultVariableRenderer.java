package replete.scripting.rscript.rendering.renderers;

import replete.scripting.rscript.parser.gen.ASTVariable;
import replete.scripting.rscript.parser.values.NodeValue;
import replete.scripting.rscript.rendering.ASTNodeRenderer;
import replete.scripting.rscript.rendering.ASTRenderingContext;

public class DefaultVariableRenderer implements ASTNodeRenderer<ASTVariable> {
    @Override
    public void render(ASTVariable node, ASTRenderingContext context) {
        NodeValue value = context.getValue(node);
        context.append(value);
    }
}
