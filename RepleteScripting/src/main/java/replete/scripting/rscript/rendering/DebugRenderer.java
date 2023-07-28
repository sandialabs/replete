package replete.scripting.rscript.rendering;

import replete.scripting.rscript.parser.gen.ASTNode;
import replete.scripting.rscript.parser.values.NodeValue;
import replete.text.StringUtil;

// Useful to debug complicated trees.
// renderer.setAllRenderers(new DebugRenderer());
public class DebugRenderer implements ASTNodeRenderer {
    @Override
    public void render(ASTNode node, ASTRenderingContext context) {
        NodeValue value = node.getValue();
        context.append(
            StringUtil.spaces((context.getPath().size() - 1) * 2) +
            "NODE: " + node.getClass().getSimpleName() +
            ", CH#: " +
            node.getCount() +
            ", VALUE: " + value + " (" + value.getClass().getSimpleName() + ")\n"
        );
        for(int i = 0; i < node.getCount(); i++) {
            context.append(node.getChild(i));
        }
    }
}
