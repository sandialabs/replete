package replete.scripting.rscript.inspection;

import java.util.LinkedHashSet;
import java.util.Set;

import replete.scripting.rscript.evaluation.functions.AssignmentFunction;
import replete.scripting.rscript.evaluation.functions.Function;
import replete.scripting.rscript.parser.RScript;
import replete.scripting.rscript.parser.gen.ASTFunction;
import replete.scripting.rscript.parser.gen.ASTNode;
import replete.scripting.rscript.parser.gen.ASTOperator;
import replete.scripting.rscript.parser.gen.ASTStatement;
import replete.scripting.rscript.parser.gen.ASTVariable;
import replete.scripting.rscript.parser.values.FunctionValue;

// Just like with rendering and evaluation, this functionality *could*
// be contained within the AST* classes themselves, but an implementation
// decision has been made to keep those classes as simple as possible,
// only including functionality directly related to *representing* the
// given parsed textual representation of the expression in Java objects.
public class RScriptInspector {
    public Set<String> getSymbols(RScript script) {
        return getSymbols(script.getStart(), SymbolType.BOTH);
    }
    public Set<String> getSymbols(RScript script, SymbolType type) {
        return getSymbols(script.getStart(), type);
    }
    public Set<String> getSymbols(ASTNode node) {
        return getSymbols(node, SymbolType.BOTH);
    }
    public Set<String> getSymbols(ASTNode node, SymbolType type) {
        Set<String> symbols = new LinkedHashSet<>();
        getSymbols(node, symbols, type);
        return symbols;
    }

    private void getSymbols(ASTNode node, Set<String> symbols, SymbolType type) {
        getMySymbols(node, symbols, type);
        for(int c = 0; c < node.getCount(); c++) {
            ASTNode child = node.getChild(c);
            getSymbols(child, symbols, type);
        }
    }
    protected void getMySymbols(ASTNode node, Set<String> symbols, SymbolType type) {
        // To be overridden
        if(node instanceof ASTFunction) {
            if(type == SymbolType.FUNCTION || type == SymbolType.BOTH) {
                FunctionValue value = (FunctionValue) node.getValue();
                symbols.add(value.getName());
            }

        } else if(node instanceof ASTVariable) {
            ASTVariable vNode = (ASTVariable) node;
            if(type == SymbolType.VARIABLE || type == SymbolType.BOTH) {
                symbols.add(vNode.getVariableName());
            }
        }
    }

    public String getVariableName(RScript script,
                                  boolean allowCompoundAssignment, boolean allowSingleSymbol,
                                  boolean allowNonZeroOrder, boolean includeOrder) {
        return getVariableName(script.getFirstStatement(),
            allowCompoundAssignment, allowSingleSymbol, allowNonZeroOrder, includeOrder);
    }

    // Returns the name of the variable on the left-hand
    // side of an assignment operator, or null if that is
    // not applicable for this node.  The arguments further
    // allow you to specify whether or not you want compound
    // assignments to be allowed (x += 3) and whether or
    // not you want non-zero order variables allowed (x' = 4y).
    public String getVariableName(ASTNode node,
                                  boolean allowCompoundAssignment, boolean allowSingleSymbol,
                                  boolean allowNonZeroOrder, boolean includeOrder) {

        ASTVariable varNode;

        if(node instanceof ASTStatement) {
            return getVariableName(node.getChild(0),
                allowCompoundAssignment, allowSingleSymbol,
                allowNonZeroOrder, includeOrder);

        } else if(node instanceof ASTVariable) {
            if(!allowSingleSymbol) {
                return null;
            }
            varNode = (ASTVariable) node;

        } else if(node instanceof ASTOperator) {
            Function func = ((ASTOperator) node).getFunction();
            if(!func.getClass().equals(AssignmentFunction.class)) {
                if(!func.isAssignment() || !allowCompoundAssignment) {
                    return null;
                }
            }

            ASTNode left = node.getChild(0);
            if(!(left instanceof ASTVariable)) {
                return null;
            }

            varNode = (ASTVariable) left;
        } else {
            return null;
        }

        if(!allowNonZeroOrder && varNode.getOrder() != 0) {
            return null;
        }
        if(includeOrder) {
            return varNode.getVariableNameWithOrder();
        }
        return varNode.getVariableName();
    }
}
