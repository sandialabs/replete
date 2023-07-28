package replete.scripting.rscript.parser;

import replete.scripting.rscript.parser.gen.ASTNode;

public interface ASTNodeValueOverrider {
    public Object getValue(ASTNode node);
}
