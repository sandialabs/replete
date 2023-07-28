package replete.scripting.rscript.parser.gen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import replete.scripting.rscript.evaluation.functions.AdditionFunction;
import replete.scripting.rscript.evaluation.functions.AssignmentFunction;
import replete.scripting.rscript.evaluation.functions.CosineFunction;
import replete.scripting.rscript.evaluation.functions.DotFunction;
import replete.scripting.rscript.evaluation.functions.ExponentiationFunction;
import replete.scripting.rscript.evaluation.functions.Function;
import replete.scripting.rscript.evaluation.functions.FunctionList;
import replete.scripting.rscript.evaluation.functions.ListSubscriptExpression;
import replete.scripting.rscript.evaluation.functions.MultiplicationFunction;
import replete.scripting.rscript.evaluation.functions.SubtractionFunction;
import replete.scripting.rscript.evaluation.functions.UnknownFunction;
import replete.scripting.rscript.inspection.RScriptInspector;
import replete.scripting.rscript.inspection.SymbolType;
import replete.scripting.rscript.parser.ASTNodeTransformerMap;
import replete.scripting.rscript.parser.ASTTransformationContext;
import replete.scripting.rscript.parser.RScript;
import replete.scripting.rscript.parser.RScriptParser;
import replete.scripting.rscript.parser.values.ConstantValue;
import replete.scripting.rscript.parser.values.FunctionValue;
import replete.scripting.rscript.parser.values.ListOrMapValue;
import replete.scripting.rscript.parser.values.OperatorValue;
import replete.scripting.rscript.rendering.RScriptRenderer;

/**
 * This tests the non-evaluation-related operations on
 * the abstract syntax tree (AST) produced by the
 * expression parser upon a successful parse of an
 * expression.  All expression strings parsed in this
 * test are syntactically valid and produce an AST.
 */

public class ASTNodeTest {

    private String[] testSources;

    // TODO: Does not yet test Long / Integer data types.

    @Before
    public void setup() {
        testSources = new String[] {
            "+2.0",
            "x",
            " \t y = m* x +   (( b )) ",
            "2.0 + 2.0",
            "(2.0 % t.y) * (  (  u$$)) && !p__.$f",
            "cos(y)",
            "V' += ( m + a') - 35.0 + tanh(v$)",
            "foo(x) = f ** 2.0 - (10.3e20 ** tan(3.0) + u.t) * 22.0 - o[3.0 + r]",
            "u__4=2.0-cos(h+=   5.0, tanh(rr) *t[r] / \"earth\")*t$.f0    ** ((6.0+!k))",
            "x[3.0+a+cos(derek(1.0))/9.0]=b=c=0.0",
            "a=\"dog\" ** derek(a=2.0, 3e3, cos(x-3.0))",
            "x=3.0*x**(2.0+3.0)-t**cos(theta')",
            "yy = !false + 4.0 - (true == ++false)",
            "alpha_m = (25 - V) / (10 * (exp ((25 - V) / 10) - 1))",
            "x = (3 + 4)[a]",
            "-(3 + 4)",

            // Precedence
            "x = 3 - 4 / 6 + 7",
            "x = (3 - 4) / 6 + 7",
            "x = 3 - 4 / (6 + 7)",
            "x = (3 - 4) / (6 + 7)",
            "x = 3 * 4 / 6 % 7",
            "x = (3 * 4) / 6 % 7",
            "x = 3 * 4 / (6 % 7)",
            "x = (3 * 4) / (6 % 7)",

            // More precedence for proper parenthesization
            "x = 3 + 4 * 5",
            "x = 3 + (4 * 5)",
            "x = (3 + 4) * 5",
            "x = -(3 + 4)",
            "x = (3 + 4)[a + b]",
            "x = (3 + 4)[(a + b)]",
            "x = (3 + 4)[a]",
            "x = (3 + 4) {m^2}",
            "x = 3 + 4 {m^2}",
            "x = [a: 3 + 3]",
            "x = [a: (3 + 3)]"

            // null, null, null, null, null, null, null, null, null, null, null
        };
    }

    @Test
    public void testSource() throws ParseException {
        for(int e = 0; e < testSources.length; e++) {
            RScript script = parse(testSources[e]);
            ASTStatement stmt0 = script.getFirstStatement();
            assertEquals(testSources[e], script.getSource());
            for(int c = 0; c < stmt0.getCount(); c++) {
                assertNull(stmt0.getChild(c).getSource());
            }
        }
    }

    @Test
    public void testIndexedSymbol() throws ParseException {
        String source = "Layer.var[7]";
        RScript script = parse(source);
        ASTStatement stmt0 = script.getFirstStatement();
        ASTOperator n = stmt0.getChild(0);
        checkOpNode(n, "[]", ListSubscriptExpression.class,
            FunctionList.OP_ELEMENT, "(Layer.var)[7]", "Layer.var[7]", 2);
        ASTOperator n_0 = n.getChild(0);
        checkOpNode(n_0, ".", DotFunction.class,
            FunctionList.OP_DOT, "(Layer.var)", "Layer.var", 2);
        ASTConstant n_1 = n.getChild(1);
        checkConNode(n_1, "7", Long.class, 7L, "7");

        // Example of how to get these parts using a ParsedEquation object:
        // ParsedEquation pe = EquationParser.parse(test);
        // String sym = ((ASTVarNode) pe.getTree().getChild(0)).getVariableNameWithOrder();
        // Number index = (Number) (pe.getTree().getChild(1).getChild(0)).getValue();
    }

    @Test
    public void testAssignmentVariableName() throws ParseException {
        String[] syms = {
            "",
            "",
            "y",
            "",
            "",
            "",
            "V'",
            "",
            "u__4",
            "",
            "a",
            "x",
            "yy",
            "alpha_m",
            "x",
            "",
            "x", "x", "x", "x", "x", "x", "x", "x", // Precedence

            // More precedence for proper parenthesization
            null, null, null, null, null, null, null, null, null, null, null
        };

        RScriptInspector inspector = new RScriptInspector();
        for(int e = 0; e < testSources.length; e++) {
            String expected = syms[e];
            if(expected == null) {
                continue;
            }
            RScript script = parse(testSources[e]);
            if(expected.isEmpty()) {
                expected = null;
            }
            assertEquals(expected, inspector.getVariableName(script, true, false, true, false));
        }

        // V' += ...
        RScript script = parse(testSources[6]);
        assertEquals("V'", inspector.getVariableName(script, true, true, true, true));
        assertEquals("V", inspector.getVariableName(script, true, true, true, false));
        assertEquals(null, inspector.getVariableName(script, true, true, false, true));
        assertEquals(null, inspector.getVariableName(script, true, true, false, false));
        assertEquals("V'", inspector.getVariableName(script, true, false, true, true));
        assertEquals("V", inspector.getVariableName(script, true, false, true, false));
        assertEquals(null, inspector.getVariableName(script, true, false, false, true));
        assertEquals(null, inspector.getVariableName(script, true, false, false, false));

        assertEquals(null, inspector.getVariableName(script, false, true, true, true));
        assertEquals(null, inspector.getVariableName(script, false, true, true, false));
        assertEquals(null, inspector.getVariableName(script, false, true, false, true));
        assertEquals(null, inspector.getVariableName(script, false, true, false, false));
        assertEquals(null, inspector.getVariableName(script, false, false, true, true));
        assertEquals(null, inspector.getVariableName(script, false, false, true, false));
        assertEquals(null, inspector.getVariableName(script, false, false, false, true));
        assertEquals(null, inspector.getVariableName(script, false, false, false, false));

        // x
        script = parse(testSources[1]);
        assertEquals("x", inspector.getVariableName(script, true, true, true, true));
        assertEquals("x", inspector.getVariableName(script, true, true, true, false));
        assertEquals("x", inspector.getVariableName(script, true, true, false, true));
        assertEquals("x", inspector.getVariableName(script, true, true, false, false));
        assertEquals(null, inspector.getVariableName(script, true, false, true, true));
        assertEquals(null, inspector.getVariableName(script, true, false, true, false));
        assertEquals(null, inspector.getVariableName(script, true, false, false, true));
        assertEquals(null, inspector.getVariableName(script, true, false, false, false));

        assertEquals("x", inspector.getVariableName(script, false, true, true, true));
        assertEquals("x", inspector.getVariableName(script, false, true, true, false));
        assertEquals("x", inspector.getVariableName(script, false, true, false, true));
        assertEquals("x", inspector.getVariableName(script, false, true, false, false));
        assertEquals(null, inspector.getVariableName(script, false, false, true, true));
        assertEquals(null, inspector.getVariableName(script, false, false, true, false));
        assertEquals(null, inspector.getVariableName(script, false, false, false, true));
        assertEquals(null, inspector.getVariableName(script, false, false, false, false));
    }

    @Test
    public void testSymbolsBoth() throws ParseException {
        String[] syms = {
            "",
            "x",
            "y|m|x|b",
            "",
            "t|y|u$$|p__|$f",
            "cos|y",
            "V'|m|a'|tanh|v$",
            "foo|x|f|u|t|o|r|tan",
            "u__4|cos|h|tanh|rr|t|r|t$|f0|k",
            "x|a|cos|derek|b|c",
            "derek|a|cos|x",
            "x|t|cos|theta'",
            "yy",
            "alpha_m|V|exp",
            "x|a",
            "",
            "x", "x", "x", "x", "x", "x", "x", "x", // Precedence

            // More precedence for proper parenthesization
            null, null, null, null, null, null, null, null, null, null, null
        };

        RScriptInspector inspector = new RScriptInspector();
        for(int e = 0; e < testSources.length; e++) {
            if(syms[e] == null) {
                continue;
            }
            Set<String> expected;
            if(syms[e].equals("")) {
                expected = toSet(new String[0]);
            } else {
                String[] expectedArray = syms[e].split("\\|");
                expected = toSet(expectedArray);
            }
            RScript script = parse(testSources[e]);
            ASTStatement stmt0 = script.getFirstStatement();
            assertEquals(expected, inspector.getSymbols(stmt0));

            // Check a specific child node's symbols.
            if(e == 6) {
                expected = toSet(new String[] {"m", "a'"});
                ASTOperator n = stmt0.getChild(0);
                ASTOperator n_1 = n.getChild(1);
                ASTOperator n_1_0 = n_1.getChild(0);
                assertEquals(expected, inspector.getSymbols(n_1_0));
            }
        }
    }

    @Test
    public void testSymbolsVariables() throws ParseException {
        String[] syms = {
            "",
            "x",
            "y|m|x|b",
            "",
            "t|y|u$$|p__|$f",
            "y",
            "V'|m|a'|v$",
            "x|f|u|t|o|r",
            "u__4|h|rr|t|r|t$|f0|k",
            "x|a|b|c",
            "a|x",
            "x|t|theta'",
            "yy",
            "alpha_m|V",
            "x|a",
            "",
            "x", "x", "x", "x", "x", "x", "x", "x", // Precedence

            // More precedence for proper parenthesization
            null, null, null, null, null, null, null, null, null, null, null
        };

        RScriptInspector inspector = new RScriptInspector();
        for(int e = 0; e < testSources.length; e++) {
            if(syms[e] == null) {
                continue;
            }
            Set<String> expected;
            if(syms[e].equals("")) {
                expected = toSet(new String[0]);
            } else {
                String[] expectedArray = syms[e].split("\\|");
                expected = toSet(expectedArray);
            }
            RScript script = parse(testSources[e]);
            ASTStatement stmt0 = script.getFirstStatement();
            assertEquals(expected, inspector.getSymbols(stmt0, SymbolType.VARIABLE));

            // Check a specific child node's symbols.
            if(e == 6) {
                expected = toSet(new String[] {"m", "a'"});
                ASTOperator n = stmt0.getChild(0);
                ASTOperator n_1 = n.getChild(1);
                ASTOperator n_1_0 = n_1.getChild(0);
                assertEquals(expected, inspector.getSymbols(n_1_0, SymbolType.VARIABLE));
            }
        }
    }

    @Test
    public void testIsSimpleAssignment() throws ParseException {
        Boolean[] isSimple = {
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false,  // TODO: Consistency with isAssignment!
            true,
            false,  // TODO: Consistency with isAssignment!
            true,
            true,
            true,
            true,
            true,
            false,
            true, true, true, true, true, true, true, true,

            // More precedence for proper parenthesization
            null, null, null, null, null, null, null, null, null, null, null
        };

        for(int e = 0; e < testSources.length; e++) {
            Boolean expected = isSimple[e];
            if(expected == null) {
                continue;
            }
            RScript script = parse(testSources[e]);
            ASTStatement stmt0 = script.getFirstStatement();
            assertEquals(expected, stmt0.isSimpleAssignment());
        }
    }

    @Test
    public void testIsAssignment() throws ParseException {
        Boolean[] isSimple = {
            false,
            false,
            true,
            false,
            false,
            false,
            true,
            true,  // TODO: Consistency with isSimpleAssignment!
            true,
            true,  // TODO: Consistency with isSimpleAssignment!
            true,
            true,
            true,
            true,
            true,
            false,
            true, true, true, true, true, true, true, true,

            // More precedence for proper parenthesization
            null, null, null, null, null, null, null, null, null, null, null
        };

        for(int e = 0; e < testSources.length; e++) {
            Boolean expected = isSimple[e];
            if(expected == null) {
                continue;
            }
            RScript script = parse(testSources[e]);
            ASTStatement stmt0 = script.getFirstStatement();
            assertEquals(expected, stmt0.isAssignment());
        }
    }

    // TODO: Not implemented
    @Test
    public void testIsSingleSymbol() throws ParseException {
        /*boolean[] isSimple = {
            false,
            false,
            true,
            false,
            false,
            false,
            true,
            true,  // TODO: Consistency with isSimpleAssignment!
            true,
            true,  // TODO: Consistency with isSimpleAssignment!
            true,
            true,
            true,
            true,
            true, true, true, true, true, true, true, true
        };

        for(int e = 0; e < testExprs.length; e++) {
            RScript script = parse(testExprs[e]);
            assertEquals(isSimple[e], root.isAssignment());
        }*/
    }

    // TODO: Not implemented
    @Test
    public void testGetVarNode() throws ParseException {
        /*boolean[] isSimple = {
            false,
            false,
            true,
            false,
            false,
            false,
            true,
            true,  // TODO: Consistency with isSimpleAssignment!
            true,
            true,  // TODO: Consistency with isSimpleAssignment!
            true,
            true,
            true,
            true,
            true, true, true, true, true, true, true, true
        };

        for(int e = 0; e < testExprs.length; e++) {
            RScript script = parse(testExprs[e]);
            assertEquals(isSimple[e], root.isAssignment());
        }*/
    }

    private Set<String> toSet(String[] args) {
        return new LinkedHashSet<>(Arrays.asList(args));
    }

    @Test
    public void testReadableLong() throws ParseException {
        String[] rdLongs = {
            "+2.0",
            "x",
            "y = ((m * x) + b)",
            "2.0 + 2.0",
            "((2.0 % (t.y)) * u$$) && (!(p__.$f))",
            "cos(y)",
            "V' += (((m + a') - 35.0) + tanh(v$))",
            "foo(x) = (((f ** 2.0) - (((1.03E21 ** tan(3.0)) + (u.t)) * 22.0)) - o[3.0 + r])",
            "u__4 = (2.0 - (cos(h += 5.0, (tanh(rr) * t[r]) / \"earth\") * ((t$.f0) ** (6.0 + (!k)))))",
            "x[(3.0 + a) + (cos(derek(1.0)) / 9.0)] = (b = (c = 0.0))",
            "a = (\"dog\" ** derek(a = 2.0, 3000.0, cos(x - 3.0)))",
            "x = ((3.0 * (x ** (2.0 + 3.0))) - (t ** cos(theta')))",
            "yy = (((!false) + 4.0) - (true == (+(+false))))",
            "alpha_m = ((25 - V) / (10 * (exp((25 - V) / 10) - 1)))",
            "x = (3 + 4)[a]",
            "-(3 + 4)",

            // Precedence
            "x = ((3 - (4 / 6)) + 7)",
            "x = (((3 - 4) / 6) + 7)",
            "x = (3 - (4 / (6 + 7)))",
            "x = ((3 - 4) / (6 + 7))",
            "x = (((3 * 4) / 6) % 7)",
            "x = (((3 * 4) / 6) % 7)",
            "x = ((3 * 4) / (6 % 7))",
            "x = ((3 * 4) / (6 % 7))",

            // More precedence for proper parenthesization
            "x = (3 + (4 * 5))",
            "x = (3 + (4 * 5))",
            "x = ((3 + 4) * 5)",
            "x = (-(3 + 4))",
            "x = (3 + 4)[a + b]",
            "x = (3 + 4)[a + b]",
            "x = (3 + 4)[a]",
            "x = (3 + 4) {m²}",
            "x = (3 + 4 {m²})",
            "x = [a: 3 + 3]",
            "x = [a: 3 + 3]"
        };

        for(int e = 0; e < testSources.length; e++) {
            String expected = rdLongs[e];
            RScript script = parse(testSources[e]);
            String actual = render(script, false);
            if(!actual.equals(expected)) {
                System.out.println("Long:");
                System.out.println("  Expected: " + expected);
                System.out.println("  Actual:   " + actual);
            }
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testReadableShort() throws ParseException {
        String[] rdShorts = {
            "2.0",
            "x",
            "y = m * x + b",
            "2.0 + 2.0",
            "2.0 % t.y * u$$ && !p__.$f",
            "cos(y)",
            "V' += m + a' - 35.0 + tanh(v$)",
            "foo(x) = f ** 2.0 - (1.03E21 ** tan(3.0) + u.t) * 22.0 - o[3.0 + r]",
            "u__4 = 2.0 - cos(h += 5.0, tanh(rr) * t[r] / \"earth\") * t$.f0 ** (6.0 + !k)",
            "x[3.0 + a + cos(derek(1.0)) / 9.0] = b = c = 0.0",
            "a = \"dog\" ** derek(a = 2.0, 3000.0, cos(x - 3.0))",
            "x = 3.0 * x ** (2.0 + 3.0) - t ** cos(theta')",
            "yy = !false + 4.0 - (true == false)",
            "alpha_m = (25 - V) / (10 * (exp((25 - V) / 10) - 1))",
            "x = (3 + 4)[a]",
            "-(3 + 4)",

            // Precedence
            "x = 3 - 4 / 6 + 7",
            "x = (3 - 4) / 6 + 7",
            "x = 3 - 4 / (6 + 7)",
            "x = (3 - 4) / (6 + 7)",
            "x = 3 * 4 / 6 % 7",
            "x = 3 * 4 / 6 % 7",
            "x = 3 * 4 / (6 % 7)",
            "x = 3 * 4 / (6 % 7)",

            // More precedence for proper parenthesization
            "x = 3 + 4 * 5",
            "x = 3 + 4 * 5",
            "x = (3 + 4) * 5",
            "x = -(3 + 4)",
            "x = (3 + 4)[a + b]",
            "x = (3 + 4)[a + b]",
            "x = (3 + 4)[a]",
            "x = (3 + 4) {m²}",
            "x = 3 + 4 {m²}",
            "x = [a: 3 + 3]",
            "x = [a: 3 + 3]"
        };

        for(int e = 0; e < testSources.length; e++) {
            String expected = rdShorts[e];
            RScript script = parse(testSources[e]);
            String actual = render(script, true);
            if(!actual.equals(expected)) {
                System.out.println("Short:");
                System.out.println("  Expected: " + expected);
                System.out.println("  Actual:   " + actual);
            }
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testReadableShortCustom() throws ParseException {
        String[] rdShorts = {
            "2.0",
            "x#",
            "y# = m# * x# + b#",
            "2.0 + 2.0",
            "2.0 % t#.y# * u$$# && !p__#.$f#",
            "cos@|y#|",
            "V'# += m# + a'# - 35.0 + tanh@|v$#|",
            "foo@|x#| = f# ** 2.0 - (1.03E21 ** tan@|3.0| + u#.t#) * 22.0 - o#[3.0 + r#]",
            "u__4# = 2.0 - cos@|h# += 5.0, tanh@|rr#| * t#[r#] / \"earth\"| * t$#.f0# ** (6.0 + !k#)",
            "x#[3.0 + a# + cos@|derek@|1.0|| / 9.0] = b# = c# = 0.0",
            "a# = \"dog\" ** derek@|a# = 2.0, 3000.0, cos@|x# - 3.0||",
            "x# = 3.0 * x# ** (2.0 + 3.0) - t# ** cos@|theta'#|",
            "yy# = !false + 4.0 - (true == false)",
            "alpha_m# = (25 - V#) / (10 * (exp@|(25 - V#) / 10| - 1))",
            "x# = (3 + 4)[a#]",
            "-(3 + 4)",

            // Precedence
            "x# = 3 - 4 / 6 + 7",
            "x# = (3 - 4) / 6 + 7",
            "x# = 3 - 4 / (6 + 7)",
            "x# = (3 - 4) / (6 + 7)",
            "x# = 3 * 4 / 6 % 7",
            "x# = 3 * 4 / 6 % 7",
            "x# = 3 * 4 / (6 % 7)",
            "x# = 3 * 4 / (6 % 7)",

            // More precedence for proper parenthesization
            null, null, null, null, null, null, null, null, null, null, null
        };

        RScriptRenderer renderer = new RScriptRenderer();
        renderer.setRenderer(ASTVariable.class, (node, context) -> {
            context.appendDefault(node);
            context.append("#");
        });
        renderer.setRenderer(ASTFunction.class, (node, context) -> {
            context.append(node.getValue());
            context.append("@|");
            for(int a = 0; a < node.getCount(); a++) {
                context.append(node.getChild(a));
                if(a != node.getCount() - 1) {
                    context.append(", ");
                }
            }
            context.append("|");
        });

        for(int e = 0; e < testSources.length; e++) {
            String expected = rdShorts[e];
            if(expected == null) {
                continue;
            }
            RScript script = parse(testSources[e]);
            String actual = renderer.renderShort(script);
            if(!actual.equals(expected)) {
                System.out.println("Custom:");
                System.out.println("  Expected: " + expected);
                System.out.println("  Actual:   " + actual);
            }
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testReadableCustomPrefix() throws ParseException {
        String[] custom = {
            "+(2.0)",
            "x",
            "=(y, +(*(m, x), b))",
            "+(2.0, 2.0)",
            "&&(*(%(2.0, .(t, y)), u$$), !(.(p__, $f)))",
            "cos(y)",
            "+=(V', +(-(+(m, a'), 35.0), tanh(v$)))",
            "=(foo(x), -(-(**(f, 2.0), *(+(**(1.03E21, tan(3.0)), .(u, t)), 22.0)), [](o, +(3.0, r))))",
            "=(u__4, -(2.0, *(cos(+=(h, 5.0), /(*(tanh(rr), [](t, r)), \"earth\")), **(.(t$, f0), +(6.0, !(k))))))",
            "=([](x, +(+(3.0, a), /(cos(derek(1.0)), 9.0))), =(b, =(c, 0.0)))",
            "=(a, **(\"dog\", derek(=(a, 2.0), 3000.0, cos(-(x, 3.0)))))",
            "=(x, -(*(3.0, **(x, +(2.0, 3.0))), **(t, cos(theta'))))",
            "=(yy, -(+(!(false), 4.0), ==(true, +(+(false)))))",
            "=(alpha_m, /(-(25, V), *(10, -(exp(/(-(25, V), 10)), 1))))",
            "=(x, [](+(3, 4), a))",
            "-(+(3, 4))",

            // Precedence
            "=(x, +(-(3, /(4, 6)), 7))",
            "=(x, +(/(-(3, 4), 6), 7))",
            "=(x, -(3, /(4, +(6, 7))))",
            "=(x, /(-(3, 4), +(6, 7)))",
            "=(x, %(/(*(3, 4), 6), 7))",
            "=(x, %(/(*(3, 4), 6), 7))",
            "=(x, /(*(3, 4), %(6, 7)))",
            "=(x, /(*(3, 4), %(6, 7)))",

            // More precedence for proper parenthesization
            null, null, null, null, null, null, null, null, null, null, null
        };

        RScriptRenderer renderer = new RScriptRenderer();

        // Change rendering of operators to render like functions (prefix notation).
        renderer.setRenderer(ASTOperator.class, (node, context) -> {
            context.append(node.getValue());
            context.append("(");
            for(int a = 0; a < node.getCount(); a++) {
                context.append(node.getChild(a));
                if(a != node.getCount() - 1) {
                    context.append(", ");
                }
            }
            context.append(")");
        });

        for(int e = 0; e < testSources.length; e++) {
            String expected = custom[e];
            if(expected == null) {
                continue;
            }
            RScript script = parse(testSources[e]);
            String actual = renderer.renderShort(script);
            if(!actual.equals(expected)) {
                System.out.println("Prefix:");
                System.out.println("  Expected: " + expected);
                System.out.println("  Actual:   " + actual);
            }
            assertEquals(expected, actual);
        }
    }

    // Right now doesn't test anything but constant and function
    // overrides.  In future should test var and op nodes too.
    @Test
    public void testValueOverrides() throws ParseException {
        String[] rdShorts = {
            "2.0",
            "x",
            "y = m * x + b",
            "2.0 + 2.0",
            "2.0 % t.y * u$$ && !p__.$f",
            "cos(y)",
            "V' += m + a' - 35.0 + cos(v$)",
            "foo(x) = f ** 2.0 - (1.03E21 ** tan(3.0) + u.t) * 22.0 - o[3.0 + r]",
            "u__4 = 2.0 - cos(h += 5.0, cos(rr) * t[r] / \"earth\") * t$.f0 ** (6.0 + !k)",
            "x[3.0 + a + cos(derek(1.0)) / 9.0] = b = c = 0.0",
            "a = \"dog\" ** derek(a = 2.0, 3000.0, cos(x - 3.0))",
            "x = 3.0 * x ** (2.0 + 3.0) - t ** cos(theta')",
            "yy = !false + 4.0 - (true == false)",
            "alpha_m = (25 - V) / (10 * (exp((25 - V) / 10) - 1))",
            "x = (333 + 4)[a]",
            "-(333 + 4)",

            // Precedence
            "x = 333 - 4 / 6 + 7",
            "x = (333 - 4) / 6 + 7",
            "x = 333 - 4 / (6 + 7)",
            "x = (333 - 4) / (6 + 7)",
            "x = 333 * 4 / 6 % 7",
            "x = 333 * 4 / 6 % 7",
            "x = 333 * 4 / (6 % 7)",
            "x = 333 * 4 / (6 % 7)",

            // More precedence for proper parenthesization
            null, null, null, null, null, null, null, null, null, null, null
        };

        RScriptRenderer renderer = new RScriptRenderer();

        // Replace all value-occurrences of numeric literal 3 with 333.
        renderer.setValueOverrider(ASTConstant.class, node -> {
            if(node.getValue().equals(new ConstantValue(new Long(3)))) {
                return new ConstantValue(new Long(333));
            }
            return node.getValue();
        });

        // Replace all value-occurrences of function "tanh" with "cos".
        renderer.setValueOverrider(ASTFunction.class, node -> {
            FunctionValue value = (FunctionValue) node.getValue();
            Function func = FunctionList.getOrUnknown(value.getName());
            if(func.getName().equals("tanh")) {
                return new FunctionValue("cos");
            }
            return node.getValue();
        });

        for(int e = 0; e < testSources.length; e++) {
            String expected = rdShorts[e];
            if(expected == null) {
                continue;
            }
            RScript script = parse(testSources[e]);
            String actual = renderer.renderShort(script);
            if(!actual.equals(expected)) {
                System.out.println("Value Override:");
                System.out.println("  Expected: " + expected);
                System.out.println("  Actual:   " + actual);
            }
            assertEquals(expected, actual);
        }
    }

    // TODO: Demonstrates only -- doesn't actually test yet.
    @Test
    public void testTransform() throws ParseException {
        String s = "alpha_m = (25 - V) / (10 * (exp ((25 - V) / 10) - 1))";
        RScript script = parse(s);
        ASTStatement stmt0 = script.getFirstStatement();
        //System.out.println("BEFORE: " + pe);
        ASTNodeTransformerMap transMap = new ASTNodeTransformerMap();
        transMap.put(ASTConstant.class, node -> {
            ConstantValue value = (ConstantValue) node.getValue();
            if(value.getValue() instanceof Long) {
                Long i = (Long) value.getValue();
                node.setValue(new ConstantValue(new Long(i + 5)));
            }
        });
        ASTTransformationContext context = new ASTTransformationContext(transMap);
        stmt0.transform(context);
        //System.out.println("AFTER: " + pe);
    }

    @Test
    public void testIsValidVariableName() {
        Object[] values = {
            // NAME      ($)   (')   RESULT
            "",          true, true, false,
            " ",         true, true, false,
            "123",       true, true, false,
            "!@#$",      true, true, false,
            "ABC",       true, true, true,
            "abc",       true, true, true,
            "_abc",      true, true, true,
            "abc'",      true, false, false,
            "abc'",      true, true, true,
            "'a",        true, true, false,
            "ab'c'",     true, true, false,
            "abc_123",   true, true, true,
            "abc_$123",  true, true, true,
            "$$$$$$$$",  true, true, true,
            "abc_$123",  false, true, false,
            "abc_.123",  true, true, false,
            "abc_.a123", true, true, false,
            "abc_.a12.", true, true, false,
            "abc_.a123", true, true, false,
            ".abc",      true, true, false,
            "1.abc",     true, true, false,
        };

        for(int e = 0; e < values.length; e += 4) {
            String name = (String) values[e];
            boolean allowDollar = (Boolean) values[e + 1];
            boolean allowTickMark = (Boolean) values[e + 2];
            boolean expected = (Boolean) values[e + 3];
            boolean actual = ASTVariable.isValidVariableName(name, allowDollar, allowTickMark);
            if(actual != expected) {
                System.out.println("Problem with: " + name);
            }
            assertEquals(expected, actual);
        }

        try {
            ASTVariable.isValidVariableName(null, false, false);
        } catch(NullPointerException e) {
            return;
        }
        fail();
    }

    @Test
    public void testTreeStructureAndContents() throws ParseException {
        String expr = "foo(x, \"A\" + [1,2,3,4], t.j'[4.0]) = f$'' ** 2.0 - (10.3e20 ** cos(3.0) + u.t) * true - o[3.0 + r]";
        RScript script = parse(expr);
        ASTStatement stmt0 = script.getFirstStatement();
        ASTOperator n = stmt0.getChild(0);

        // foo(x, "A", t.j'[4.0]) = f$'' ** 2.0 - (10.3e20 ** cos(3.0) + u.t) * true - o[3.0, r]
        checkOpNode(n, "=", AssignmentFunction.class,
            FunctionList.OP_ASSIGN,
            "foo(x, \"A\" + [1, 2, 3, 4], (t.j')[4.0]) = (((f$'' ** 2.0) - (((1.03E21 ** cos(3.0)) + (u.t)) * true)) - o[3.0 + r])",
            "foo(x, \"A\" + [1, 2, 3, 4], t.j'[4.0]) = f$'' ** 2.0 - (1.03E21 ** cos(3.0) + u.t) * true - o[3.0 + r]",
            2);

        // foo(x, "A" + [1,2;3,4], t.j'[4.0])
        ASTNode n_0 = n.getChild(0);
        checkFunNode(n_0, "foo", UnknownFunction.class, null,
            "foo(x, \"A\" + [1, 2, 3, 4], (t.j')[4.0])",
            "foo(x, \"A\" + [1, 2, 3, 4], t.j'[4.0])",
            3);

        // x
        ASTNode n_0_0 = n_0.getChild(0);
        checkVarNode(n_0_0, "x", 0, "x");

        // "A" + [1,2;3,4]
        ASTNode n_0_1 = n_0.getChild(1);
        checkOpNode(n_0_1, "+", AdditionFunction.class,
            FunctionList.OP_ADD,
            "\"A\" + [1, 2, 3, 4]",
            "\"A\" + [1, 2, 3, 4]",
            2);

        // "A"
        ASTNode n_0_1_0 = n_0_1.getChild(0);
        checkConNode(n_0_1_0, "\"A\"", String.class, "A", "\"A\"");

        // [1,2,3,4]
        ASTNode n_0_1_1 = n_0_1.getChild(1);
        checkListOrMapNode(n_0_1_1, 4,
            "[1, 2, 3, 4]",
            "[1, 2, 3, 4]",
            new String[] {"1", "2", "3", "4"});

        // t.j'[4.0]
        ASTNode n_0_2 = n_0.getChild(2);
        checkOpNode(n_0_2, "[]", ListSubscriptExpression.class,
            FunctionList.OP_ELEMENT, "(t.j')[4.0]", "t.j'[4.0]", 2);

        // t.j'
        ASTNode n_0_2_0 = n_0_2.getChild(0);
        checkOpNode(n_0_2_0, ".", DotFunction.class,
            FunctionList.OP_DOT, "(t.j')", "t.j'", 2);

        // t
        ASTNode n_0_2_0_0 = n_0_2_0.getChild(0);
        checkVarNode(n_0_2_0_0, "t", 0, "t");

        // j'
        ASTNode n_0_2_0_1 = n_0_2_0.getChild(1);
        checkVarNode(n_0_2_0_1, "j'", 1, "j'");

        // 4.0
        ASTNode n_0_2_1 = n_0_2.getChild(1);
        checkConNode(n_0_2_1, "4.0", Double.class, 4.0, "4.0");

        // f$'' ** 2.0 - (10.3e20 ** cos(3.0) + u.t) * true - o[3.0, r]
        ASTNode n_1 = n.getChild(1);
        checkOpNode(n_1, "-", SubtractionFunction.class,
            FunctionList.OP_SUBTRACT,
            "(((f$'' ** 2.0) - (((1.03E21 ** cos(3.0)) + (u.t)) * true)) - o[3.0 + r])",
            "f$'' ** 2.0 - (1.03E21 ** cos(3.0) + u.t) * true - o[3.0 + r]",
            2);

        // f$'' ** 2.0 - (10.3e20 ** cos(3.0) + u.t) * true
        ASTNode n_1_0 = n_1.getChild(0);
        checkOpNode(n_1_0, "-", SubtractionFunction.class,
            FunctionList.OP_SUBTRACT,
            "((f$'' ** 2.0) - (((1.03E21 ** cos(3.0)) + (u.t)) * true))",
            "f$'' ** 2.0 - (1.03E21 ** cos(3.0) + u.t) * true",
            2);

        // f$'' ** 2.0
        ASTNode n_1_0_0 = n_1_0.getChild(0);
        checkOpNode(n_1_0_0, "**", ExponentiationFunction.class,
            FunctionList.OP_POWER,
            "(f$'' ** 2.0)",
            "f$'' ** 2.0",
            2);

        // f$''
        ASTNode n_1_0_0_0 = n_1_0_0.getChild(0);
        checkVarNode(n_1_0_0_0, "f$''", 2, "f$''");

        // 2.0
        ASTNode n_1_0_0_1 = n_1_0_0.getChild(1);
        checkConNode(n_1_0_0_1, "2.0", Double.class, 2.0, "2.0");

        // (10.3e20 ** cos(3.0) + u.t) * true
        ASTNode n_1_0_1 = n_1_0.getChild(1);
        checkOpNode(n_1_0_1, "*", MultiplicationFunction.class,
            FunctionList.OP_MULTIPLY,
            "(((1.03E21 ** cos(3.0)) + (u.t)) * true)",
            "(1.03E21 ** cos(3.0) + u.t) * true",
            2);

        // 10.3e20 ** cos(3.0) + u.t
        ASTNode n_1_0_1_0 = n_1_0_1.getChild(0);
        checkOpNode(n_1_0_1_0, "+", AdditionFunction.class,
            FunctionList.OP_ADD,
            "((1.03E21 ** cos(3.0)) + (u.t))",
            "(1.03E21 ** cos(3.0) + u.t)",
            2);

        // 10.3e20 ** cos(3.0)
        ASTNode n_1_0_1_0_0 = n_1_0_1_0.getChild(0);
        checkOpNode(n_1_0_1_0_0, "**", ExponentiationFunction.class,
            FunctionList.OP_POWER,
            "(1.03E21 ** cos(3.0))",
            "1.03E21 ** cos(3.0)",
            2);

        // 10.3e20
        ASTNode n_1_0_1_0_0_0 = n_1_0_1_0_0.getChild(0);
        checkConNode(n_1_0_1_0_0_0, "1.03E21", Double.class, 10.3e20, "1.03E21");

        // cos(3.0)
        ASTNode n_1_0_1_0_0_1 = n_1_0_1_0_0.getChild(1);
        checkFunNode(n_1_0_1_0_0_1, "cos", CosineFunction.class, "cos",
            "cos(3.0)", "cos(3.0)", 1);

        // 3.0
        ASTNode n_1_0_1_0_0_1_0 = n_1_0_1_0_0_1.getChild(0);
        checkConNode(n_1_0_1_0_0_1_0, "3.0", Double.class, 3.0, "3.0");

        // u.t
        ASTNode n_1_0_1_0_1 = n_1_0_1_0.getChild(1);
        checkOpNode(n_1_0_1_0_1, ".", DotFunction.class,
            FunctionList.OP_DOT, "(u.t)", "u.t", 2);

        // u
        ASTNode n_1_0_1_0_1_0 = n_1_0_1_0_1.getChild(0);
        checkVarNode(n_1_0_1_0_1_0, "u", 0, "u");

        // t
        ASTNode n_1_0_1_0_1_1 = n_1_0_1_0_1.getChild(1);
        checkVarNode(n_1_0_1_0_1_1, "t", 0, "t");

        // true
        ASTNode n_1_0_1_1 = n_1_0_1.getChild(1);
        checkConNode(n_1_0_1_1, "true", Boolean.class, true, "true");

        // o[3.0, r]
        ASTNode n_1_1 = n_1.getChild(1);
        checkOpNode(n_1_1, "[]", ListSubscriptExpression.class,
            FunctionList.OP_ELEMENT, "o[3.0 + r]", "o[3.0 + r]", 2);

        // o
        ASTNode n_1_1_0 = n_1_1.getChild(0);
        checkVarNode(n_1_1_0, "o", 0, "o");

        // [3.0, r]
        ASTNode n_1_1_1 = n_1_1.getChild(1);
//        checkListNode(n_1_1_1, 2, "3.0", "r");

        // 3.0
        ASTNode n_1_1_1_0 = n_1_1_1.getChild(0);
        checkConNode(n_1_1_1_0, "3.0", Double.class, 3.0, "3.0");

        // r
        ASTNode n_1_1_1_1 = n_1_1_1.getChild(1);
        checkVarNode(n_1_1_1_1, "r", 0, "r");
    }

    private void checkOpNode(ASTNode node, String ts, Class<?> opClass,
                             String fName, String expLong, String expShort, int cnt) {
        assertTrue(node instanceof ASTOperator);
        ASTOperator op = (ASTOperator) node;
        assertTrue(node.getValue().getClass().equals(OperatorValue.class));
        assertEquals(ts, node.toString());
        assertTrue(op.getFunction().getClass().equals(opClass));
        assertEquals(FunctionList.get(fName), ((ASTOperator) node).getFunction());
        String actualLong = render(node, false);
        String actualShort = render(node, true);
        if(!expLong.equals(actualLong)) {
            System.out.println("Long:");
            System.out.println("  Expected: " + expLong);
            System.out.println("  Actual:   " + actualLong);
        }
        if(!expShort.equals(actualShort)) {
            System.out.println("Short:");
            System.out.println("  Expected: " + expShort);
            System.out.println("  Actual:   " + actualShort);
        }
        assertEquals(expLong, actualLong);
        assertEquals(expShort, actualShort);
        assertEquals(cnt, node.getCount());
    }

    private void checkFunNode(ASTNode node, String ts, Class<?> opClass, String fName, String expLong, String expShort, int cnt) {
        assertTrue(node instanceof ASTFunction);
        assertTrue(node.getValue().getClass().equals(FunctionValue.class));
        assertEquals(ts, node.toString());
        FunctionValue value = (FunctionValue) node.getValue();
        Function func = FunctionList.getOrUnknown(value.getName());
        assertTrue(func.getClass().equals(opClass));
        if(fName != null) {
            assertEquals(FunctionList.get(fName), func);
        }
        String actualLong = render(node, false);
        String actualShort = render(node, true);
        if(!expLong.equals(actualLong)) {
            System.out.println("Long:");
            System.out.println("  Expected: " + expLong);
            System.out.println("  Actual:   " + actualLong);
        }
        if(!expShort.equals(actualShort)) {
            System.out.println("Short:");
            System.out.println("  Expected: " + expShort);
            System.out.println("  Actual:   " + actualShort);
        }
        assertEquals(expLong, actualLong);
        assertEquals(expShort, actualShort);
        assertEquals(cnt, node.getCount());
    }

    private void checkVarNode(ASTNode node, String ts, int order, String varN) {
        assertTrue("Node type was '" + node.getClass().getSimpleName() + "'", node instanceof ASTVariable);
        ASTVariable var = (ASTVariable) node;
        String name = var.getValue().getName();
        assertEquals(ts, node.toString());
        assertEquals(order, ((ASTVariable) node).getOrder());
        assertEquals(varN, ((ASTVariable) node).getVariableName());
        assertEquals(varN, name);
        assertEquals(0, node.getCount());
    }

    private void checkConNode(ASTNode node, String ts, Class<?> valClass, Object val, String rd) {
        assertTrue(node instanceof ASTConstant);
        ASTConstant con = (ASTConstant) node;
        Object conVal = con.getValue().getValue();
        assertTrue(node.getValue().getClass().equals(ConstantValue.class));
        assertTrue(conVal.getClass().equals(valClass));
        assertEquals(ts, node.toString());
        assertEquals(val, conVal);
        assertEquals(rd, render(node, false));
        assertEquals(rd, render(node, true));
        assertEquals(0, node.getCount());
    }

    private void checkListOrMapNode(ASTNode node, int count, String rdLong, String rdShort, String[] elemRenderShort) {
        assertTrue(node instanceof ASTListOrMap);
        assertTrue(node.getValue().getClass().equals(ListOrMapValue.class));
        assertEquals("[]", node.toString());
        assertEquals(rdLong, render(node, false));
        assertEquals(rdShort, render(node, true));
        assertEquals(count, node.getCount());
        for(int i = 0; i < count; i++) {
            assertEquals(render(node.getChild(i), true), elemRenderShort[i]);
        }
    }

    private RScript parse(String source) throws ParseException {
        RScriptParser parser = new RScriptParser();
        return parser.parse(source);
    }
    private String render(RScript script, boolean shortMode) {
        return render(script.getStart(), shortMode);
    }
    private String render(ASTNode node, boolean shortMode) {
        RScriptRenderer renderer = new RScriptRenderer();
        if(shortMode) {
            return renderer.renderShort(node);
        }
        return renderer.renderLong(node);
    }
}
