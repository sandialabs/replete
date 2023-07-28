package replete.scripting.rscript.evaluation;

import java.util.HashMap;

import replete.scripting.rscript.parser.gen.ASTNode;

public class ASTNodeEvaluatorMap extends HashMap<Class<? extends ASTNode>, ASTNodeEvaluator<? extends ASTNode>> {

    // This class simply acts as short hand for the
    // HashMap base class to improve readability.

}

