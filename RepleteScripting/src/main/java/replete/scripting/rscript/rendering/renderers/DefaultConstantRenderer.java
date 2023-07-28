package replete.scripting.rscript.rendering.renderers;

import replete.scripting.rscript.parser.gen.ASTConstant;
import replete.scripting.rscript.parser.values.NodeValue;
import replete.scripting.rscript.rendering.ASTNodeRenderer;
import replete.scripting.rscript.rendering.ASTRenderingContext;

public class DefaultConstantRenderer implements ASTNodeRenderer<ASTConstant> {
    @Override
    public void render(ASTConstant node, ASTRenderingContext context) {
        NodeValue value = context.getValue(node);
        context.append(value);
    }
}
