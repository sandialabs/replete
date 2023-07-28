package replete.scripting.rscript.rendering.renderers;

import replete.scripting.rscript.parser.gen.ASTListOrMap;
import replete.scripting.rscript.parser.gen.ASTNode;
import replete.scripting.rscript.rendering.ASTNodeRenderer;
import replete.scripting.rscript.rendering.ASTRenderingContext;

public class DefaultListOrMapRenderer implements ASTNodeRenderer<ASTListOrMap> {
    @Override
    public void render(ASTListOrMap node, ASTRenderingContext context) {
        context.append("[");
        for(int i = 0; i < node.getCount(); i++) {
            ASTNode child = node.getChild(i);
            context.append(child);
            if(i != node.getCount() - 1) {
                context.append(", ");
            }
        }
        context.append("]");
    }
}
