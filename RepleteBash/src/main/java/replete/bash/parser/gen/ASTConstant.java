/* Generated By:JJTree: Do not edit this line. ASTConstant.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package replete.bash.parser.gen;

public
class ASTConstant extends ASTNodeBase {
  public ASTConstant(int id) {
    super(id);
  }

  public ASTConstant(ExpressionParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ExpressionParserVisitor visitor, Object data) throws ParseException {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=0d5fecec4b0cc7277647691899647bb2 (do not edit this line) */
