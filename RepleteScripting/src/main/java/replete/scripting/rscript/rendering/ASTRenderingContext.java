package replete.scripting.rscript.rendering;

import java.util.ArrayList;
import java.util.List;

import replete.scripting.rscript.parser.ASTNodeValueOverrider;
import replete.scripting.rscript.parser.ASTNodeValueOverriderMap;
import replete.scripting.rscript.parser.gen.ASTNode;
import replete.scripting.rscript.parser.values.NodeValue;

public class ASTRenderingContext {


    ////////////
    // FIELDS //
    ////////////

    private boolean shortMode;
    private ASTNodeRendererMap defaultRenderers;
    private ASTNodeRendererMap currentRenderers;
    private ASTNodeValueOverriderMap valueOverrides;
    private StringBuilder buffer;

    private List<ASTNode> path = new ArrayList<>();


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ASTRenderingContext(boolean shortMode, ASTNodeRendererMap defaultRenderers,
                               ASTNodeRendererMap currentRenderers,
                               ASTNodeValueOverriderMap valueOverrides) {
        this.shortMode = shortMode;
        this.defaultRenderers = defaultRenderers;
        this.currentRenderers = currentRenderers;
        this.valueOverrides = valueOverrides;
        buffer = new StringBuilder();
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isShortMode() {
        return shortMode;
    }
    public List<ASTNode> getPath() {
        return path;
    }

    // Accessors (Computed)

    public String getImage() {
        return buffer.toString();
    }

    public <T extends NodeValue> T getValue(ASTNode node) {
        ASTNodeValueOverrider overrider = valueOverrides.get(node.getClass());
        if(overrider != null) {
            return (T) overrider.getValue(node);
        }
        return (T) node.getValue();
    }

    // Mutators

    public void append(String str) {
        if(path.isEmpty()) {
            throw new IllegalStateException();   // Can't be appending a random string unless a
        }                                        // node has been asked to be rendered first.
        buffer.append(str);
    }
    public void append(Object obj) {
        if(path.isEmpty()) {
            throw new IllegalStateException();   // Can't be appending a random string unless a
        }                                        // node has been asked to be rendered first.
        buffer.append(obj.toString());
    }
    public void append(ASTNode node) {
        path.add(node);
        ASTNodeRenderer renderer = currentRenderers.get(node.getClass());
        renderer.render(node, this);
        path.remove(node);
    }
    public void appendDefault(ASTNode node) {
        path.add(node);
        ASTNodeRenderer renderer = defaultRenderers.get(node.getClass());
        renderer.render(node, this);
        path.remove(node);
    }
}
