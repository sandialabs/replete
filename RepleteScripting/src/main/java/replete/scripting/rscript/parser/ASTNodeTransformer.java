package replete.scripting.rscript.parser;

import replete.scripting.rscript.parser.gen.ASTNode;

public interface ASTNodeTransformer {

    // Depending on the node given, this transformer
    // should change the value (payload) of the node
    // as desired or do nothing.
    public void transform(ASTNode node);
}