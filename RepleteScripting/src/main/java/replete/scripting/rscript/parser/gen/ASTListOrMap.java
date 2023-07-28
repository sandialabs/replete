/* Generated By:JJTree: Do not edit this line. ASTOpNode.java Version 4.3 */
/*
 * JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,
 * NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true
 */

package replete.scripting.rscript.parser.gen;

import replete.scripting.rscript.parser.values.ListOrMapValue;

public class ASTListOrMap extends ASTNode<ListOrMapValue> {


    ////////////
    // FIELDS //
    ////////////

    private boolean emptyMap = false;


    ////////////////////
    // AUTO-GENERATED //
    ////////////////////

    public ASTListOrMap(ListOrMapValue value) {
        super(value, RScriptParserGeneratedTreeConstants.JJTLISTORMAP);
    }

    public ASTListOrMap(int id) {
        super(id);
    }

    public ASTListOrMap(RScriptParserGenerated p, int id) {
        super(p, id);
    }

    /** Accept the visitor. **/
    @Override
    public Object jjtAccept(RScriptParserGeneratedVisitor visitor, Object data) throws ParseException {
        return visitor.visit(this, data);
    }


    ////////////
    // CUSTOM //
    ////////////

    public void setEmptyMap(boolean emptyMap) {
        this.emptyMap = emptyMap;
    }
}
/* JavaCC - OriginalChecksum=d551700b54e6299c518a2537b8744651 (do not edit this line) */
