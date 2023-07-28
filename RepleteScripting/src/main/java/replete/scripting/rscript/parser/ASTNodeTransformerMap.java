package replete.scripting.rscript.parser;

import java.util.HashMap;

import replete.scripting.rscript.parser.gen.ASTNode;

public class ASTNodeTransformerMap extends HashMap<Class<? extends ASTNode>, ASTNodeTransformer> {

    // This class simply acts as short hand for the
    // HashMap base class to improve readability.

}
