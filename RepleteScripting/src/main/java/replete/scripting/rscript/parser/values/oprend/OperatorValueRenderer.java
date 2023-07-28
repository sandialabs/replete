package replete.scripting.rscript.parser.values.oprend;

import replete.scripting.rscript.evaluation.functions.Associativity;
import replete.scripting.rscript.parser.gen.ASTNode;
import replete.scripting.rscript.parser.gen.ASTOperator;
import replete.scripting.rscript.parser.gen.ASTUnit;
import replete.scripting.rscript.parser.values.NodeValue;
import replete.scripting.rscript.parser.values.OperatorValue;
import replete.scripting.rscript.rendering.ASTRenderingContext;

public abstract class OperatorValueRenderer {
    public abstract void render(ASTOperator node, ASTRenderingContext context);

    protected boolean isUseParentheses(ASTOperator node, ASTRenderingContext context) {
        ASTNode parent = node.getParent();
        OperatorValue value = node.getValue();
        boolean useParens = false;

        if(parent instanceof ASTUnit || parent instanceof ASTOperator) {
            NodeValue pValue = parent.getValue();
            int myIndex = parent.getIndexOf(node);
            boolean isParentIndex = pValue == OperatorValue.INDEX;

            if(context.isShortMode()) {
                double pPrecLevel;
                Associativity pAssoc;
                if(pValue instanceof OperatorValue) {
                    OperatorValue opValue = (OperatorValue) pValue;
                    pPrecLevel = OperatorValue.getPrecedenceLevel(opValue);
                    pAssoc = OperatorValue.getAssociativity(opValue);

                // Figure out the #'s for units node
                } else {
                    int exponLevel = OperatorValue.getPrecedenceLevel(OperatorValue.EXPON);
                    pPrecLevel = exponLevel + 0.5;  // HACK
                    pAssoc = Associativity.LEFT_TO_RIGHT;
                }

                // Parent operator is binary operator
                if(parent.getCount() == 2) {

                    // I'm to the left of my parent's symbol
                    if(myIndex == 0) {
                        int rightPrecLevel = OperatorValue.getPrecedenceLevel(value);
                        if(pPrecLevel > rightPrecLevel ||
                                pPrecLevel == rightPrecLevel &&
                                pAssoc == Associativity.RIGHT_TO_LEFT) {
                            useParens = true;
                        }

                    // I'm to the right of my parent's symbol
                    } else {
                        int rightPrecLevel = OperatorValue.getPrecedenceLevel(value);
                        if(pPrecLevel > rightPrecLevel ||
                                pPrecLevel == rightPrecLevel &&
                                pAssoc == Associativity.LEFT_TO_RIGHT) {
                            useParens = true;
                            if(isParentIndex && myIndex == 1) {
                                useParens = false;
                            }
                        }
                    }

                // Parent operator is unary operator && I'm to the right of my parent's symbol
                } else if(parent.getCount() == 1) {
                    int rightPrecLevel = OperatorValue.getPrecedenceLevel(value);
                    if(pPrecLevel > rightPrecLevel ||
                            pPrecLevel == rightPrecLevel &&
                            pAssoc == Associativity.LEFT_TO_RIGHT) {
                        useParens = true;
                        if(isParentIndex && myIndex == 1) {
                            useParens = false;
                        }
                    }
                }

            } else {
                useParens = true;
                if(isParentIndex && myIndex == 1) {
                    useParens = false;
                }
            }
        }
        return useParens;
    }
}
