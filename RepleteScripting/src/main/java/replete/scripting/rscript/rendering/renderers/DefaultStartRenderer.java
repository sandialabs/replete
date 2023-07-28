package replete.scripting.rscript.rendering.renderers;

import replete.scripting.rscript.parser.gen.ASTStart;
import replete.scripting.rscript.parser.gen.ASTStatement;
import replete.scripting.rscript.rendering.ASTNodeRenderer;
import replete.scripting.rscript.rendering.ASTRenderingContext;

public class DefaultStartRenderer implements ASTNodeRenderer<ASTStart> {
    @Override
    public void render(ASTStart node, ASTRenderingContext context) {
        for(int i = 0; i < node.getCount(); i++) {
            ASTStatement stmt = (ASTStatement) node.getChild(i);
            context.append(stmt);
            if(i != node.getCount() - 1) {
                context.append("; ");
            }
        }
    }
}
