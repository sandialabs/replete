package replete.scripting.rscript.rendering;

import replete.scripting.rscript.parser.gen.ASTNode;

public interface ASTNodeRenderer<T extends ASTNode> {
    public void render(T node, ASTRenderingContext context);
}
