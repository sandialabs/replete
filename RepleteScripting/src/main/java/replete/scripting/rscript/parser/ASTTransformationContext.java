package replete.scripting.rscript.parser;

import replete.scripting.rscript.parser.gen.ASTNode;

public class ASTTransformationContext {


    ////////////
    // FIELDS //
    ////////////

    private ASTNodeTransformerMap transformers;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ASTTransformationContext(ASTNodeTransformerMap trans) {
        transformers = trans;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public ASTNodeTransformer getTransformer(ASTNode node) {
        return transformers.get(node.getClass());
    }
    public boolean hasTransformer(ASTNode node) {
        return transformers != null && transformers.containsKey(node.getClass());
    }
}
