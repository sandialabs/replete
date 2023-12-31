/* Generated By:JJTree: Do not edit this line. ASTUnit.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package replete.scripting.rscript.parser.gen;

import replete.scripting.rscript.parser.values.UnitValue;

public class ASTUnit extends ASTNode<UnitValue> {


    ///////////
    // NOTES //
    ///////////

    // Optional unit information (provided by JScience) can
    // be present on any node.  This unit information is only
    // relevant on certain nodes.  Specifically, nodes that
    // are meant to evaluate to a single real number or vector
    // of real numbers.  This information may be present on
    // these nodes:
    //     ASTConstant (Number, not String/Boolean)
    //     ASTFunction
    //     ASTListNode
    //     ASTOpNode (+, -, *, /, ^, =, +=, -=, *=, /=)
    //     ASTVarNode
    // If unit information is present on a node it represents
    // something different depending on the context.  They are
    // currently only actively used during tree *evaluation*.
    // During evaluation this is the effect they have:
    //     Numerical Constants: describes what units the number
    //         is in, and more or less what quantity is being
    //         measured (e.g. m/s implies Velocity, 1/s can
    //         imply Frequency, but could imply other quantities
    //         as well).  Example syntax:
    //             -47.5 {m/s^2}, 100 {kg}
    //     Variables, Functions, Lists, & Operators:
    //         After a node of this type has been evaluated to
    //         a single value two things happen:
    //             1) a consistency check is performed by verifying
    //                that the units of the result value are
    //                compatible with the units specified on the
    //                node.  For example, the following expression
    //                would not pass this check (example syntax):
    //                    (4 {kg} + 10 {kg}) {m/s}
    //                    x = 4 {N} ; y = x {C} * 10
    //                    weight = calcWeight(1, 2, 3) {m/s}
    //                    [1 {cm}, 2 {cm}, 3 {cm}] {s}  (NOT IMPL)
    //             2) if the units are compatible, the result value
    //                is converted properly to the node's desired
    //                units.  Here is an example:
    //                    (4 {cm} + 7 {cm}) {m}
    //                    x = 4 {mA} ; y = x {nA} * 10
    //                    weight = calcWeight(1, 2, 3) {kg}
    //                    [1 {cm}, 2 {cm}, 3 {cm}] {km}  (NOT IMPL)


    ////////////////////
    // AUTO-GENERATED //
    ////////////////////

    public ASTUnit(int id) {
        super(id);
    }

    public ASTUnit(RScriptParserGenerated p, int id) {
        super(p, id);
    }

    /** Accept the visitor. **/
    @Override
    public Object jjtAccept(RScriptParserGeneratedVisitor visitor, Object data) throws ParseException {
        return visitor.visit(this, data);
    }
}
/* JavaCC - OriginalChecksum=4155bb5987a157303897ebe3631b23cd (do not edit this line) */
