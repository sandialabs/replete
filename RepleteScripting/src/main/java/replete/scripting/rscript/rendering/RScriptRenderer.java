package replete.scripting.rscript.rendering;

import replete.scripting.rscript.parser.ASTNodeValueOverrider;
import replete.scripting.rscript.parser.ASTNodeValueOverriderMap;
import replete.scripting.rscript.parser.RScript;
import replete.scripting.rscript.parser.gen.ASTConstant;
import replete.scripting.rscript.parser.gen.ASTFunction;
import replete.scripting.rscript.parser.gen.ASTKeyValuePair;
import replete.scripting.rscript.parser.gen.ASTListOrMap;
import replete.scripting.rscript.parser.gen.ASTNode;
import replete.scripting.rscript.parser.gen.ASTOperator;
import replete.scripting.rscript.parser.gen.ASTStart;
import replete.scripting.rscript.parser.gen.ASTStatement;
import replete.scripting.rscript.parser.gen.ASTUnit;
import replete.scripting.rscript.parser.gen.ASTVariable;
import replete.scripting.rscript.rendering.renderers.DefaultConstantRenderer;
import replete.scripting.rscript.rendering.renderers.DefaultFunctionRenderer;
import replete.scripting.rscript.rendering.renderers.DefaultKeyValuePairRenderer;
import replete.scripting.rscript.rendering.renderers.DefaultListOrMapRenderer;
import replete.scripting.rscript.rendering.renderers.DefaultOperatorRenderer;
import replete.scripting.rscript.rendering.renderers.DefaultStartRenderer;
import replete.scripting.rscript.rendering.renderers.DefaultStatementRenderer;
import replete.scripting.rscript.rendering.renderers.DefaultUnitRenderer;
import replete.scripting.rscript.rendering.renderers.DefaultVariableRenderer;

public class RScriptRenderer {


    ////////////
    // FIELDS //
    ////////////

    private ASTNodeRendererMap defaultRenderers = new ASTNodeRendererMap();
    private ASTNodeRendererMap currentRenderers = new ASTNodeRendererMap();
    private ASTNodeValueOverriderMap valueOverrides = new ASTNodeValueOverriderMap();


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public RScriptRenderer() {
        initDefaultRenderers();
    }

    protected void initDefaultRenderers() {
        defaultRenderers.put(ASTConstant.class,     new DefaultConstantRenderer());
        defaultRenderers.put(ASTFunction.class,     new DefaultFunctionRenderer());
        defaultRenderers.put(ASTKeyValuePair.class, new DefaultKeyValuePairRenderer());
        defaultRenderers.put(ASTListOrMap.class,    new DefaultListOrMapRenderer());
        defaultRenderers.put(ASTOperator.class,     new DefaultOperatorRenderer());
        defaultRenderers.put(ASTStart.class,        new DefaultStartRenderer());
        defaultRenderers.put(ASTStatement.class,    new DefaultStatementRenderer());
        defaultRenderers.put(ASTUnit.class,         new DefaultUnitRenderer());
        defaultRenderers.put(ASTVariable.class,     new DefaultVariableRenderer());

        currentRenderers.putAll(defaultRenderers);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public ASTNodeRendererMap getDefaultRenderers() {
        return defaultRenderers;
    }
    public ASTNodeRendererMap getCurrentRenderers() {
        return currentRenderers;
    }
    public ASTNodeValueOverriderMap getValueOverrides() {
        return valueOverrides;
    }

    // Mutators

    public <T extends ASTNode> void setRenderer(Class<T> nodeClass, ASTNodeRenderer<T> renderer) {
        currentRenderers.put(nodeClass, renderer);
    }
    public <T extends ASTNode> void setValueOverrider(Class<T> nodeClass, ASTNodeValueOverrider overrider) {
        valueOverrides.put(nodeClass, overrider);
    }

    public void setAllRenderers(ASTNodeRenderer renderer) {
        currentRenderers.put(ASTConstant.class,     renderer);
        currentRenderers.put(ASTFunction.class,     renderer);
        currentRenderers.put(ASTKeyValuePair.class, renderer);
        currentRenderers.put(ASTListOrMap.class,    renderer);
        currentRenderers.put(ASTOperator.class,     renderer);
        currentRenderers.put(ASTStart.class,        renderer);
        currentRenderers.put(ASTStatement.class,    renderer);
        currentRenderers.put(ASTUnit.class,         renderer);
        currentRenderers.put(ASTVariable.class,     renderer);
    }


    ///////////////
    // RENDERING //
    ///////////////

    public String renderShort(RScript script) {
        ASTStart start = script.getStart();
        return render(start, true);
    }
    public String renderLong(RScript script) {
        ASTStart start = script.getStart();
        return render(start, false);
    }
    public String renderShort(ASTNode node) {
        return render(node, true);
    }
    public String renderLong(ASTNode node) {
        return render(node, false);
    }
    private String render(ASTNode node, boolean shortMode) {
        ASTRenderingContext context = new ASTRenderingContext(
            shortMode, defaultRenderers, currentRenderers, valueOverrides);
        context.append(node);
        return context.getImage();
    }
}
