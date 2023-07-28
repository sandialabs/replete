package replete.scripting.rscript.parser.gen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import replete.scripting.rscript.evaluation.functions.FunctionList;
import replete.scripting.rscript.parser.RScript;
import replete.scripting.rscript.parser.RScriptParser;
import replete.scripting.rscript.rendering.RScriptRenderer;

/**
 * This tests the non-evaluation-related operations on
 * the abstract syntax tree (AST) produced by the
 * expression parser upon a successful parse of an
 * expression.  All expression strings parsed in this
 * test are syntactically valid and produce an AST.
 */

public class ASTNodeRendererTest {
    @Test
    public void testNodeRenderer() {
        try {
            String expr = "x = y + 4";
            RScript script = parse(expr);
            RScriptRenderer renderer = new RScriptRenderer();
            String prefix = "space";
            renderer.setRenderer(ASTVariable.class, (node, context) -> {
                context.append(prefix);
                context.append(":");
                context.appendDefault(node);
            });
            String actual = renderer.renderShort(script);
            String expected = "space:x = space:y + 4";
            assertEquals(expected, actual);
        } catch(Exception e) {
            fail("Should not have failed!");
        }
    }

    @Test
    public void testPowerChange() {
        try {
            String expr = "x = a ** (b + c)";
            RScript script = parse(expr);
            RScriptRenderer renderer = new RScriptRenderer();
            renderer.setRenderer(ASTOperator.class, (node, context) -> {
                ASTOperator op = node;
                if(op.getFunction() == FunctionList.get(FunctionList.OP_POWER)) {
                    context.append("pow(");
                    context.append(op.getChild(0));
                    context.append(", ");
                    context.append(op.getChild(1));
                    context.append(")");
                } else {
                    context.appendDefault(node);
                }
            });
            String actual = renderer.renderLong(script);
            String expected = "x = pow(a, (b + c))";
            assertEquals(expected, actual);
            actual = renderer.renderShort(script);
            expected = "x = pow(a, (b + c))";              // Parentheses now look out of place because
            assertEquals(expected, actual);                // rendering doesn't know that this custom renderer
        } catch(Exception e) {                             // has changed the syntactic structure.
            e.printStackTrace();
            fail("Should not have failed!");
        }
    }

    private RScript parse(String source) throws ParseException {
        RScriptParser parser = new RScriptParser();
        return parser.parse(source);
    }
}
