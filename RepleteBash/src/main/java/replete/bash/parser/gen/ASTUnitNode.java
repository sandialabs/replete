/* Generated By:JJTree: Do not edit this line. ASTUnitNode.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package replete.bash.parser.gen;

public
class ASTUnitNode extends ASTNodeBase {
  public ASTUnitNode(int id) {
    super(id);
  }

  public ASTUnitNode(ExpressionParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ExpressionParserVisitor visitor, Object data) throws ParseException {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=abeabce36bbaedd0cf05d11360c723f5 (do not edit this line) */
