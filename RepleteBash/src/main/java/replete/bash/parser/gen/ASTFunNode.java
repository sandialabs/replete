/* Generated By:JJTree: Do not edit this line. ASTFunNode.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package replete.bash.parser.gen;

public
class ASTFunNode extends ASTNodeBase {
  public ASTFunNode(int id) {
    super(id);
  }

  public ASTFunNode(ExpressionParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ExpressionParserVisitor visitor, Object data) throws ParseException {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=daefabd8dbc59d627587beb92cd2cd17 (do not edit this line) */