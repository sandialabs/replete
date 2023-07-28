package replete.scripting.rscript.evaluation;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import javax.measure.unit.SI;

import org.junit.Assert;
import org.junit.Test;

import replete.collections.RArrayList;
import replete.collections.RHashMap;
import replete.scripting.rscript.evaluation.functions.EvaluationException;
import replete.scripting.rscript.evaluation.functions.Function;
import replete.scripting.rscript.evaluation.functions.FunctionList;
import replete.scripting.rscript.evaluation.functions.ParameterSet;
import replete.scripting.rscript.inspection.RScriptInspector;
import replete.scripting.rscript.parser.RScript;
import replete.scripting.rscript.parser.RScriptParser;
import replete.scripting.rscript.parser.gen.ASTNode;
import replete.scripting.rscript.parser.gen.ASTStatement;
import replete.scripting.rscript.parser.gen.ParseException;
import replete.scripting.rscript.rendering.RScriptRenderer;
import replete.text.StringUtil;

/**
 * This tests the evaluation-related operations on
 * the abstract syntax tree (AST) produced by the
 * expression parser upon a successful parse of an
 * expression.  All expression strings parsed in this
 * test are syntactically valid and produce an AST.
 */

public class ASTNodeEvalTest {

    @Test
    public void testBadLiteralEvaluation() throws ParseException {
        String[] eval = {
            "nm = \"john\" + \"doe\"",
              "Invalid function arguments supplied for '+'.  Function not applicable for (String, String)."
        };

        for(int e = 0; e < eval.length; e += 2) {
            RScript script = parse(eval[e]);
            ASTStatement stmt0 = script.getFirstStatement();
            evalExpectFail(stmt0, eval[e + 1]);
        }
    }

    @Test
    public void testGoodLiteralEvaluation() throws ParseException {
        Object[] eval = {
            "x = 4",      4L,
            "y = 9.0",    9.0,
            "z = 4-5*3",  -11.0,
            "q = cos(12)", Math.cos(12),
            "x = 5 * 3 - 6 % 4 / 2 + cos(54)",
              14 + Math.cos(54)
        };

        RScriptInspector inspector = new RScriptInspector();
        for(int e = 0; e < eval.length; e += 2) {
            RScript script = parse((String) eval[e]);
            EvaluationResult result = evaluate(script);
            Object actual = result.get(inspector.getVariableName(script, true, true, true, false));
            Object expected = eval[e + 1];
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testSpecialVariables() throws ParseException {
        Object[] eval = {
            "x = " + SpecialVariables.PI, Math.PI,
            "y = " + SpecialVariables.E,  Math.E,
        };

        RScriptInspector inspector = new RScriptInspector();
        for(int e = 0; e < eval.length; e += 2) {
            RScript script = parse((String) eval[e]);
            EvaluationResult result = evaluate(script);
            Object actual = result.get(inspector.getVariableName(script, true, true, true, false));
            Object expected = eval[e + 1];
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testSmallSet() throws ParseException {
        String source = "x = 4; y = 9; z = x * y; q = z + x";

        Object[] expectedResults = {
            "x",  4L,
            "y",  9L,
            "z", 36.0,
            "q", 40.0
        };

        RScript script = parse(source);
        EvaluationResult result = evaluate(script);
        for(int e = 0; e < expectedResults.length; e += 2) {
            Object actual = result.get(expectedResults[e]);
            Object expected = expectedResults[e + 1];
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testSomeAssignments() throws ParseException {
        String source = "x = 3; z = 7 + (x += y = 1)";
        RScriptParser parser = new RScriptParser();
        RScript script = parser.parse(source);
        RScriptEvaluator evaluator = new RScriptEvaluator();
        EvaluationResult result = evaluator.evaluate(script);
        Map<String, Object> expected = new RHashMap<>("x", 4.0, "y", 1L, "z", 11.0, "$root", 11.0);
        assertEquals(expected, result);
    }

    @Test
    public void testSmallSetWithConstants() throws ParseException {
        String source = "z = x * y; q = z + x";

        Object[] expectedResults = {
            "z", 36.0,
            "q", 40.0
        };

        RScript script = parse(source);
        EvaluationResult result = evaluate(script, new RHashMap("x", 4L, "y", 9L));
        for(int e = 0; e < expectedResults.length; e += 2) {
            Object actual = result.get(expectedResults[e]);
            Object expected = expectedResults[e + 1];
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testLargeSet() throws ParseException {
        long x, y;
        double z, gg, hh, t, ff, rr;

        String source =
            "x = 4; y = 9; z = x * y; gg = sin(x); hh = 1 / 100; t = $pi / 4 + z;" +
            "ff = 45 + 8 / z - tan(3 - 7.0); rr = 0.33 ** 3.44 + t + 20;" +
            "q = z + x - 5 * (gg / hh + cos(t)) ** (ff * rr) - y";

        Object[] expectedResults = {
            "x",  x = 4L,
            "y",  y = 9L,
            "z",  z = 36.0,
            "gg", gg = Math.sin(x),
            "hh", hh = 0.01,
            "t",  t = Math.PI / 4 + z,
            "ff", ff = 45L + 8L / z - Math.tan(3 - 7.0),
            "rr", rr = Math.pow(0.33, 3.44) + t + 20,
            "q",  z + x - 5 * Math.pow(gg / hh + Math.cos(t), ff * rr) - y
        };

        RScript script = parse(source);
        EvaluationResult result = evaluate(script);
        for(int e = 0; e < expectedResults.length; e += 2) {
            Object actual = result.get(expectedResults[e]);
            Object expected = expectedResults[e + 1];
            if(!actual.equals(expected)) {
                System.out.println(expectedResults[e] + " was " + actual + " instead of " + expected);
            }
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testLargeSetPersistentContext() throws ParseException {
        long x, y;
        double z, gg, hh, t, ff, rr;

        String[] sources = {
            "x = 4",
            "y = 9",
            "z = x * y",
            "gg = sin(x)",
            "hh = 1 / 100",
            "t = $pi / 4 + z",
            "ff = 45 + 8 / z - tan(3 - 7.0)",
            "rr = 0.33 ** 3.44 + t + 20",
            "q = z + x - 5 * (gg / hh + cos(t)) ** (ff * rr) - y"
        };

        Object[] expectedResults = {
            "x",  x = 4L,
            "y",  y = 9L,
            "z",  z = 36.0,
            "gg", gg = Math.sin(x),
            "hh", hh = 0.01,
            "t",  t = Math.PI / 4 + z,
            "ff", ff = 45L + 8L / z - Math.tan(3 - 7.0),
            "rr", rr = Math.pow(0.33, 3.44) + t + 20,
            "q",  z + x - 5 * Math.pow(gg / hh + Math.cos(t), ff * rr) - y
        };

        RScriptEvaluator evaluator = new RScriptEvaluator();
        evaluator.setPersistContext(true);

        for(int s = 0; s < sources.length; s++) {
            RScript script = parse(sources[s]);
            evaluator.evaluate(script);
        }

        EvaluationResult result = evaluator.getLastResult();
        for(int e = 0; e < expectedResults.length; e += 2) {
            Object actual = result.get(expectedResults[e]);
            Object expected = expectedResults[e + 1];
            if(!actual.equals(expected)) {
                System.out.println(expectedResults[e] + " was " + actual + " instead of " + expected);
            }
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testUndefinedSymbols() throws ParseException {
        String[] eval = {
            "A = B + 5", "Could not locate the variable 'B'.",
            "B = A + 5", "Could not locate the variable 'A'.",
            "x = x / 5", "Could not locate the variable 'x'.",
            "x /= 5",    "Could not locate the variable 'x'."
        };

        for(int e = 0; e < eval.length; e += 2) {
            RScript script = parse(eval[e]);
            ASTStatement stmt0 = script.getFirstStatement();
            evalExpectFail(stmt0, eval[e + 1]);
        }
    }

    @Test
    public void testUndefinedFunction() throws ParseException {
        String[] eval = {
            "A = 5 + nonfunc()", "Function 'nonfunc' not implemented.",
        };

        for(int e = 0; e < eval.length; e += 2) {
            RScript script = parse(eval[e]);
            ASTStatement stmt0 = script.getFirstStatement();
            evalExpectFail(stmt0, eval[e + 1]);
        }
    }

    @Test
    public void testDefinedFunctionWrongArgs() throws ParseException {
        String[] eval = {
            "A = 5 + myfunc()", "Invalid function arguments supplied for 'myfunc'.  Function not applicable for ().",
        };

        FunctionList.register(new MyFuncFunction());

        for(int e = 0; e < eval.length; e += 2) {
            RScript script = parse(eval[e]);
            ASTStatement stmt0 = script.getFirstStatement();
            evalExpectFail(stmt0, eval[e + 1]);
        }
    }

    @Test
    public void testDefinedFunctionRightArgs() throws ParseException {

        String[] sources = {
            "A = myfunc(\"aaa\", \"bbb\")",
            "B = myfunc(\"aaa\", \"aaa\")",
            "C = myfunc(\"aaa\", \"...\")",
            "D = myfunc(\"aaa\", \"^.a.*$\")",
            "E = myfunc(\"aaa\", \"^.c.*$\")",
        };

        Object[] expectedResults = {
            "A", false,
            "B", true,
            "C", true,
            "D", true,
            "E", false
        };

        FunctionList.register(new MyFuncFunction());

        RScriptEvaluator evaluator = new RScriptEvaluator();
        evaluator.setPersistContext(true);

        for(int s = 0; s < sources.length; s++) {
            RScript script = parse(sources[s]);
            evaluator.evaluate(script);
        }

        EvaluationResult result = evaluator.getLastResult();
        for(int e = 0; e < expectedResults.length; e += 2) {
            Object actual = result.get(expectedResults[e]);
            Object expected = expectedResults[e + 1];
            if(!actual.equals(expected)) {
                System.out.println(expectedResults[e] + " was " + actual + " instead of " + expected);
            }
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testUnits() throws ParseException {
        Object[] eval = {
            "x = 4 + 5 {kg}", 4.0, SI.KILOGRAM,
            "x = 4 + 5 {kg}", 4.0, SI.KILOGRAM,
        };

        /*for(int e = 0; e < eval.length; e += 3) {
            RScript script = parse((String) eval[e]);
            EvaluationContext context = new EvaluationContext();
            context.addEquation(eq);
//            System.out.println(eq.toReadableLong());
            Object actual = eq.eval(context);
            assertTrue(actual instanceof Amount);
            Amount<?> amt = (Amount<?>) actual;
            assertEquals(eval[e + 1] instanceof Long, amt.isExact());
            assertTrue(amt.getEstimatedValue() - (Double) eval[e + 1] < 0.1);
            assertEquals(eval[e + 1], actual);
            assertEquals(eval[e + 2], amt.getUnit());
        }*/
    }

    @Test
    public void testListMaps() throws ParseException {
        String source = "y = 1; b = 2; c = 3; d = [y, b, c]";

        Object[] expectedResults = {
            "y", 1L,
            "b", 2L,
            "c", 3L,
            "d", new RArrayList(1L, 2L, 3L)
        };

        RScript script = parse(source);
        EvaluationResult result = evaluate(script);
        for(int e = 0; e < expectedResults.length; e += 2) {
            Object actual = result.get(expectedResults[e]);
            Object expected = expectedResults[e + 1];
            assertEquals(expected, actual);
        }
    }


    ///////////////////
    // SUPPPLEMENTAL //
    ///////////////////

    protected void evalExpectFail(ASTNode eq, String expectedError) {
        String input = render(eq, true);
        try {
            RScriptEvaluator evaluator = new RScriptEvaluator();
            evaluator.evaluate(eq);
            Assert.fail("Expected error but did not find one (" + input + ")");
        } catch(Exception ex) {
            String msg = ex.getMessage();
            BufferedReader reader = new BufferedReader(new StringReader(msg));
            if(msg.contains("\n") || msg.contains("\r")) {
                try {
                    msg = reader.readLine();
                } catch(IOException e) {}
            }
            if(!msg.equals(expectedError)) {
                System.out.println("INPUT=" + input);
                System.out.println("ERR=" + msg);
                Assert.fail("Expected error message '" + expectedError + "' but found '" + msg + "' (" + input + ")");
            }
        }
    }

    private RScript parse(String source) throws ParseException {
        RScriptParser parser = new RScriptParser();
        return parser.parse(source);
    }

    private String render(ASTNode node, boolean shortMode) {
        RScriptRenderer renderer = new RScriptRenderer();
        if(shortMode) {
            return renderer.renderShort(node);
        }
        return renderer.renderLong(node);
    }

    private EvaluationResult evaluate(RScript script) throws EvaluationException {
        return evaluate(script.getStart(), null);
    }
    private EvaluationResult evaluate(RScript script, Map<String, Object> vars) throws EvaluationException {
        return evaluate(script.getStart(), vars);
    }
    private EvaluationResult evaluate(ASTNode node) throws EvaluationException {
        return evaluate(node, null);
    }
    private EvaluationResult evaluate(ASTNode node, Map<String, Object> vars) throws EvaluationException {
        RScriptEvaluator evaluator = new RScriptEvaluator();
        if(vars != null) {
            evaluator.setInitialEnvironment(
                new EvaluationEnvironment()
                    .addLayer(vars)
            );
        }
        return evaluator.evaluate(node);
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    public static class MyFuncFunction extends Function {
        @Override
        public String getName() {
            return "myfunc";
        }

        @Override
        public String getDescription() {
            return "does stuff";
        }

        @Override
        public ParameterSet[] getAllowedParameterSets() {
            return new ParameterSet[] {
                new ParameterSet(
                    "!RET", "val", "pattern",
                    Boolean.class, String.class, String.class)
            };
        }

        @Override
        protected Object eval(Object[] args, int parameterSetIndex) {
            return StringUtil.matches((String) args[0], (String) args[1], true);
        }
    }
}
