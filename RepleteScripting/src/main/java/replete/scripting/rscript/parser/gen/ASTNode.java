package replete.scripting.rscript.parser.gen;

import replete.scripting.rscript.parser.ASTNodeTransformer;
import replete.scripting.rscript.parser.ASTTransformationContext;
import replete.scripting.rscript.parser.values.NodeValue;

public abstract class ASTNode<T extends NodeValue> extends SimpleNode {


    ////////////////////
    // AUTO-GENERATED //
    ////////////////////

    public ASTNode(T value, int id) {
        super(id);
        setValue(value);
    }

    public ASTNode(int id) {
        super(id);
    }

    public ASTNode(RScriptParserGenerated p, int id) {
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

    // Source is the original line of text parsed to create
    // the tree.  Right now this value is only set on the
    // root node.
    private String source;
    public void setSource(String src) {
        source = src;
    }
    public String getSource() {
        return source;
    }

    // Returns whether or not the expression tree is in the form:
    //   Variable = Expression
    //   Variable' = Expression
    // The top node must be an OpNode with the function = and the left
    // child of that node must be a VarNode.
    // Right now, this is NOT allowed (should it be?):
    //   cos(x) = Expression
    public boolean isSimpleAssignment() {
        return false;
    }

    // TODO: isSimpleAssignment & isAssignment do different things due
    // to left hand side possibly being different!
    // Right now this IS allowed (should it be?):
    //  cos(x) = Expression
    public boolean isAssignment() {
        return false;
    }

    public boolean isSingleSymbol(){
        return this instanceof ASTVariable;
    }

    public ASTVariable getVarNode() {
        if(isSingleSymbol()) {
            return (ASTVariable) this;
        } else if(isAssignment()) {
            ASTNode left = getChild(0);
            if(left instanceof ASTVariable) {  // Need additional check for isAssignment.
                return (ASTVariable) left;
            }
        }
        return null;
    }

    // Convenience methods for improved readability
    // and typing ("jjt" prefix is annoying).
    public <T extends ASTNode> T getChild(int i) {
        return (T) jjtGetChild(i);
    }
    public void setChild(Node n, int i) {
        jjtAddChild(n, i);
    }
    public int getCount() {
        return jjtGetNumChildren();
    }
    public int getIndexOf(ASTNode child) {
        for(int i = 0; i < getCount(); i++) {
            if(child == getChild(i)) {
                return i;
            }
        }
        return -1;
    }
    public T getValue() {
        return (T) jjtGetValue();
    }
    public void setValue(T o) {
        jjtSetValue(o);
    }
    public ASTNode<?> getParent() {
        return (ASTNode<?>) jjtGetParent();
    }


    ////////////////////
    // TRANSFORMATION //
    ////////////////////

    public void transform(ASTTransformationContext context) {
        if(context.hasTransformer(this)) {
            ASTNodeTransformer trans = context.getTransformer(this);
            trans.transform(this);
        }
        for(int ch = 0; ch < getCount(); ch++) {
            getChild(ch).transform(context);
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return getValue().toString();
    }
}
