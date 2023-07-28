package replete.scripting.rscript.parser.gen;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import replete.scripting.rscript.parser.RScript;
import replete.scripting.rscript.parser.RScriptParser;

/**
 * This tests the low-level syntactic capabilities and
 * features of the expression parser generated by
 * JavaCC/JJTree.
 *
 * NOTE: This class only tests the first line of the exception
 * object's message.  Therefore, various "Was expecting one of:"
 * and "Source Line:" lines are not validated.
 */

public class RScriptParserTest {

    @Test(expected=IllegalArgumentException.class)
    public void testNull() throws ParseException {
        parse(null);
    }

    @Test
    public void testBlank() {
        parse("",       "Encountered \"\" at line 1, column 1.");
        parse("  ",     "Encountered \"\" at line 1, column 3.");
        parse("\t\n\n", "Encountered \"\" at line 3, column 1.");
    }

    @Test
    public void testGoodNumericLiteralExpressions() throws ParseException {
        String[] sources = {
            "3",
            "3 + 4",
            "3 - 5 * 6",
            "2 / 4.4 + 2",
            "8 % 2 + 4",
            "4 == 5",
            "5 > -8",
            "3.5 ** 7.5",
            "9 < 4 ** (5)",
            "6 <= 9",
            "4.5 >= 2.3",
            "5 != 5",
            "(6 && 4)",
            "4 > 6 || 3 == 3",
            "4 / 5 && 8 ** 3",
            "!4 - +6",
            "---5",
            "++4",
            "6 == ++4",
            "(6 == 6 || 5 > !7) && 3 == 9",
            "6*[5]",
            "null / null"
        };

        for(String source : sources) {
            parse(source);
        }
    }

    @Test
    public void testGoodBooleanLiterals() throws ParseException {
        String[] sources = {
            "perform = 5 > 4 || false && true",
            "x = !false + 4 - true == false"
        };

        for(String source : sources) {
            parse(source);
        }
    }

    @Test
    public void testBadBooleanLiterals() {
        // Proves true and false treated as literals, not variables.
        String[] sources = {
            "true = 4 + 4",  "Encountered \" \"=\" \"= \"\" at line 1, column 6.",
            "false = 4 + 4", "Encountered \" \"=\" \"= \"\" at line 1, column 7."
        };

        for(int e = 0; e < sources.length; e += 2) {
            parse(sources[e], sources[e + 1]);
        }
    }

    @Test
    public void testGoodStringLiteralExpressions() throws ParseException {
        String[] sources = {
            "\"earth\"",
            "\"earth\" + \"mars\"",
            "\"earth\" - \"mars\"",
            "\"earth\" ** \"venus\"",
            "\"earth\" == (\"jupiter\") && \"\" != \"pluto\"",
        };

        for(String source : sources) {
            parse(source);
        }
    }

    @Test
    public void testGoodVariableExpressions() throws ParseException {
        String[] sources = {
            "foo",
            "foo + bar",
            "foo - bar * baz",
            "foo / 4.4 + baz",
            "var.name == some.other",
            "var$name > some$other",
            "VAR_0933$name < A_B.t",
            "t' <= __5",
            "VAR$name9'''' >= 0.0",
            "_1_' != what",
            "A_Z09.$ && 4",
            "x > Y || z == q",
            "yy6 . r && r9 ** r",
            "!rrr - +vv",
            "++VAR_$.d3",
            "B == ++A",
            "!!-B",
            "($$ == 3.4 || xyz > !_123) && a.b == w.$",
        };

        for(String source : sources) {
            parse(source);
        }
    }

    @Test
    public void testBadVariableExpressions() {
        String[] sources = {
            "foo 1",                   "Encountered \" <INTEGER_LITERAL> \"1 \"\" at line 1, column 5.",
            "foo#bar",                 "Lexical error at line 1, column 4.  Encountered: \"#\" (35), after : \"\"",
            "foo - bar ^^ baz",        "Lexical error at line 1, column 11.  Encountered: \"^\" (94), after : \"\"",
            ".foo / 4.4 + baz",        "Encountered \"\" at line 1, column 1.",
            "var.name. == some.other", "Encountered \" \"==\" \"== \"\" at line 1, column 11.",
            "VAR$name9''' ' >= 0.0",   "Lexical error at line 1, column 14.  Encountered: \"\\'\" (39), after : \"\"",
            "_1_ ' != what",           "Lexical error at line 1, column 5.  Encountered: \"\\'\" (39), after : \"\"",
            "A_Z09. %$ && 4",          "Encountered \" \"%\" \"% \"\" at line 1, column 8.",
            "--abc.1 - 7",             "Encountered \" <FLOATING_POINT_LITERAL> \".1 \"\" at line 1, column 6.",
            "--abc.$23 - 7A",          "Encountered \" <INDENTIFIER> \"A \"\" at line 1, column 14.",
            "x..4 > Y || z == q",      "Encountered \" <FLOATING_POINT_LITERAL> \".4 \"\" at line 1, column 3.",
            "x . 0.4 > Y || z == q",   "Encountered \" <FLOATING_POINT_LITERAL> \"0.4 \"\" at line 1, column 5.",
            "x = false[9];",           "Encountered \" \"[\" \"[ \"\" at line 1, column 10.",
            "y = 3[3]",                "Encountered \" \"[\" \"[ \"\" at line 1, column 6.",
            "y = true.9",              "Encountered \" <FLOATING_POINT_LITERAL> \".9 \"\" at line 1, column 9.",
            "y = 3.z",                 "Encountered \" <INDENTIFIER> \"z \"\" at line 1, column 7.",
            "x = aaa.(z)",             "Encountered \" \"(\" \"( \"\" at line 1, column 9."
        };

        for(int e = 0; e < sources.length; e += 2) {
            parse(sources[e], sources[e + 1]);
        }
    }

    @Test
    public void testGoodComplexEquations() throws ParseException {
        String[] sources = {
            "a=\"dog\" ** derek(a=2, 3e3, cos(x-3))",
            "x=3*x**(2+3)--t**cos(theta')",
            "when = grid(x + y, t'', u[sin''(dd)]-i)"
        };

        for(String source : sources) {
            parse(source);
        }
    }

    @Test
    public void testBadComplexEquations() {
        String[] sources = {
            "x[3+a+cos(derek(1)),9]=b=c=8 && t == ujh ^ (g + u$ - tan(i))",
              "Encountered \" \",\" \", \"\" at line 1, column 20.",
            "x[3+a+cos(derek(1),9]=b=c=8 && t == ujh ^ (g + u$ - tan(i))",
              "Encountered \" \"]\" \"] \"\" at line 1, column 21.",
            "a=\"dog\" ** derek(a=2, 3ee3, cos(x-3))",
              "Encountered \" <INDENTIFIER> \"ee3 \"\" at line 1, column 24.",
            "when = grid(x + y, .t'', u-i)",
              "Encountered \"\" at line 1, column 20.",
            "a + $!3",
              "Encountered \" \"!\" \"! \"\" at line 1, column 6."
        };

        for(int e = 0; e < sources.length; e += 2) {
            parse(sources[e], sources[e + 1]);
        }
    }

    @Test
    public void testGoodEquations() throws ParseException {
        String[] sources = {
            "x = 3",
            "x = cos(x)",
            "sin(y, z) = cos(3)",
            "y = -tan(d == 4, 33)",
            "x[4] = t_p(cos(v), sin(123))",
            "d = func([y, b, c], [4], tan(var))",
            "x = y = z = 123"
        };

        for(String source : sources) {
            parse(source);
        }
    }

    @Test
    public void testBadEquations() {
        String[] sources = {
            "x = ",            "Encountered \"\" at line 1, column 5.",
            "3 = 3",           "Encountered \" \"=\" \"= \"\" at line 1, column 3.",
            "= f",             "Encountered \"\" at line 1, column 1.",
            "x$$.d := f**2",   "Encountered \" \":\" \": \"\" at line 1, column 7.",
            "x = y = 3 = 123", "Encountered \" \"=\" \"= \"\" at line 1, column 11.",
            "[3] = 3",         "Encountered \" \"=\" \"= \"\" at line 1, column 5.",
            "[a: b] += f",     "Encountered \" \"+=\" \"+= \"\" at line 1, column 8.",
            "true /= 3",       "Encountered \" \"/=\" \"/= \"\" at line 1, column 6.",
            "null = aa",       "Encountered \" \"=\" \"= \"\" at line 1, column 6.",
            "\"str\" = cs(x)", "Encountered \" \"=\" \"= \"\" at line 1, column 7."
        };

        for(int e = 0; e < sources.length; e += 2) {
            parse(sources[e], sources[e + 1]);
        }
    }

    @Test
    public void testGoodFunctionExpressions() throws ParseException {
        String[] sources = {
            "cos()",
            "cos(x)",
            "sin(y, z)",
            "-tan(d == 4, 33)",
            "t_p(cos(v), sin(123))",
            "func([y, b, c], [4], tan(var))",
        };

        for(String source : sources) {
            parse(source);
        }
    }

    @Test
    public void testBadFunctionExpressions() {
        String[] sources = {
            "cos(x",         "Encountered \"<EOF>\" at line 1, column 6.",
            "cosx)",         "Encountered \" \")\" \") \"\" at line 1, column 5.",
            "cos(,4,5)",     "Encountered \" \",\" \", \"\" at line 1, column 5.",
            "cos(y, b, )",   "Encountered \"\" at line 1, column 11.",
            " cos(y,b,,c)",  "Encountered \"\" at line 1, column 10.",
            "cos(3 2 4)",    "Encountered \" <INTEGER_LITERAL> \"2 \"\" at line 1, column 7.",
            "cos(y; b; c)",  "Encountered \" \";\" \"; \"\" at line 1, column 6.",
            "22cos(y, b$2)", "Encountered \" <INDENTIFIER> \"cos \"\" at line 1, column 3.",
            "cos!(43)",      "Encountered \" \"!\" \"! \"\" at line 1, column 4."
        };

        for(int e = 0; e < sources.length; e += 2) {
            parse(sources[e], sources[e + 1]);
        }
    }

    @Test
    public void testGoodListMapExpressions() throws ParseException {
        String[] sources = {
            "[]",
            "[:]",
            "[3]",
            "[3,4,5]",
            "[y, b, c]",
            "[cos(x), sin(y), tan(z)]",
            "[-33 + cc[3], r + cos(y), \"earth\" + 44]",
            "43 * [3, 4, 5]",
        };

        for(String source : sources) {
            parse(source);
        }
    }

    @Test
    public void testBadListMapExpressions() {
        String[] sources = {
            "[3",              "Encountered \"<EOF>\" at line 1, column 3.",
            "3]",              "Encountered \" \"]\" \"] \"\" at line 1, column 2.",
            "[,4,5]",          "Encountered \" \",\" \", \"\" at line 1, column 2.",
            "[y, b, ]",        "Encountered \"\" at line 1, column 8.",
            " [y,b,,c]",       "Encountered \"\" at line 1, column 7.",
            "[,]",             "Encountered \" \",\" \", \"\" at line 1, column 2.",
            "[;]",             "Encountered \" \";\" \"; \"\" at line 1, column 2.",
            "[3;4;5]",         "Encountered \" \";\" \"; \"\" at line 1, column 3.",
            " \t[y; b; c] \t", "Encountered \" \";\" \"; \"\" at line 1, column 11.",
            "[3; 3; ]",        "Encountered \" \";\" \"; \"\" at line 1, column 3.",
            "[; 3; 3]",        "Encountered \" \";\" \"; \"\" at line 1, column 2.",
            "[1, 2; 3]",       "Encountered \" \";\" \"; \"\" at line 1, column 6.",
            "[3 2 4]",         "Encountered \" <INTEGER_LITERAL> \"2 \"\" at line 1, column 4.",
            "22[y, b$2]",      "Encountered \" \"[\" \"[ \"\" at line 1, column 3.",
            "va$_.3/=[3,3,4]", "Encountered \" <FLOATING_POINT_LITERAL> \".3 \"\" at line 1, column 5.",
            "[1, 2, 3; 4, 5, 6; 7 ^ 2, 8, 9]",
                "Encountered \" \";\" \"; \"\" at line 1, column 9.",
            "[f(x); g(y); h(z)]",
                "Encountered \" \";\" \"; \"\" at line 1, column 6."
        };

        for(int e = 0; e < sources.length; e += 2) {
            parse(sources[e], sources[e + 1]);
        }
    }

    @Test
    public void testGoodListMapAccessExpressions() throws ParseException {
        String[] sources = {
            "alist[3]",
            "alist[x = 3]",
            "alist[cos(x**3) + a]",
            "alist[abc[1]]"
        };

        for(String source : sources) {
            parse(source);
        }
    }

    @Test
    public void testBadListMapAccessExpressions() {
        String[] sources = {
            "alist[3,4,5]",                  "Encountered \" \",\" \", \"\" at line 1, column 8.",
            "alist.foo[3,4,5]",              "Encountered \" \",\" \", \"\" at line 1, column 12.",
            "alist[y, b, c]",                "Encountered \" \",\" \", \"\" at line 1, column 8.",
            "alist \t[y, b, c]",             "Encountered \" \",\" \", \"\" at line 1, column 11.",
            "alist[cos(x), sin(y), tan(z)]", "Encountered \" \",\" \", \"\" at line 1, column 13.",
            "alist[3",                       "Encountered \"<EOF>\" at line 1, column 8.",
            "alist3]",                       "Encountered \" \"]\" \"] \"\" at line 1, column 7.",
            "alist[,4,5]",                   "Encountered \"\" at line 1, column 7.",
            "alist[y, b, ]",                 "Encountered \" \",\" \", \"\" at line 1, column 8.",
            "alist [y,b,,c]",                "Encountered \" \",\" \", \"\" at line 1, column 9.",
            "alist[]",                       "Encountered \"\" at line 1, column 7.",
            "alist[3 2 4]",                  "Encountered \" <INTEGER_LITERAL> \"2 \"\" at line 1, column 9.",
            "alist[y; b; c]",                "Encountered \" \";\" \"; \"\" at line 1, column 8.",
            "22[y, b$2, c]",                 "Encountered \" \"[\" \"[ \"\" at line 1, column 3.",
        };

        for(int e = 0; e < sources.length; e += 2) {
            parse(sources[e], sources[e + 1]);
        }
    }

    @Test
    public void testBadUnitSpecification() {
        String[] sources = {
            "y = x {kg",        "Lexical error at line 2, column 0.  Encountered: <EOF> after : \"\"",
            "y = x kg}",        "Encountered \" <INDENTIFIER> \"kg \"\" at line 1, column 7.",
            "y = x {}",         "Encountered \" \"}\" \"} \"\" at line 1, column 8.",
            "y = x {s+kg}",     "java.text.ParseException: not a number",
        };

        for(int e = 0; e < sources.length; e += 2) {
            parse(sources[e], sources[e + 1]);
        }
    }

    @Test
    public void testGoodUnitsSpecification() throws ParseException {
        String[] sources = {
            "6.0 {day_sidereal}",
            "y = m*x + b {kg}",
            "frc {N} = mass2 {kg} * acc {m}",
            "var = (4 + 3) {kg} - 1 {m}",
            "(4 {cm} + 7 {cm}) {m}",
            "y = x {nA} * 10",
            "weight = calcWeight(1, 2, 3) {kg}",
            "[1 {cm}, 2 {cm}, 3 {cm}] {km}",
            "y = 4 {m/s}"
        };

        int x = 0;
        for(String source : sources) {
//            System.out.println(x);
            RScript script = parse(source);
//            System.out.println(((ASTVarNode) node).getUnit());
//            if(x == 0) {
//                UnitHelper.printUnit(((ASTConstant) node).getUnit());
//            }
            x++;
//            System.out.println(node.toReadableLong());
//            node.dump("  ");
        }
    }


    ///////////////////
    // SUPPPLEMENTAL //
    ///////////////////

    private void parse(String line, String expectedError) {
        try {
            parse(line);
            fail("Expected error but did not receive one (" + line + ").");
        } catch(Exception e) {
            String msg = e.getMessage();
            if(msg.contains("\n") || msg.contains("\r")) {
                // Use line reader to handle all types of
                // newlines in the same manner.
                BufferedReader reader = new BufferedReader(new StringReader(msg));
                try {
                    msg = reader.readLine();
                } catch(IOException e1) {}
            }
            if(!msg.equals(expectedError)) {
                System.out.println("LINE=" + line);
                System.out.println("ERR=" + msg);
                fail("Expected error message '" + expectedError + "' but received '" + msg + "' (" + line + ").");
            }
        }
    }

    private RScript parse(String source) throws ParseException {
        RScriptParser parser = new RScriptParser();
        return parser.parse(source);
    }
}