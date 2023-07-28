package replete.scripting.rscript.rendering.renderers;

import replete.scripting.rscript.parser.gen.ASTKeyValuePair;
import replete.scripting.rscript.rendering.ASTNodeRenderer;
import replete.scripting.rscript.rendering.ASTRenderingContext;

public class DefaultKeyValuePairRenderer implements ASTNodeRenderer<ASTKeyValuePair> {
    @Override
    public void render(ASTKeyValuePair node, ASTRenderingContext context) {
        context.append(node.getChild(0));
        context.append(": ");
        context.append(node.getChild(1));
    }
}
