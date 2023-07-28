package replete.scripting.rscript.rendering.renderers;

import replete.scripting.rscript.parser.gen.ASTNode;
import replete.scripting.rscript.parser.gen.ASTStatement;
import replete.scripting.rscript.rendering.ASTNodeRenderer;
import replete.scripting.rscript.rendering.ASTRenderingContext;

public class DefaultStatementRenderer implements ASTNodeRenderer<ASTStatement> {
    @Override
    public void render(ASTStatement node, ASTRenderingContext context) {
        ASTNode child = node.getChild(0);
        context.append(child);
    }
}
