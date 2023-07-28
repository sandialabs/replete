package replete.scripting.rscript;

import replete.errors.ExceptionUtil;
import replete.scripting.rscript.parser.RScript;
import replete.scripting.rscript.parser.RScriptParser;
import replete.scripting.rscript.parser.gen.ASTStatement;
import replete.scripting.rscript.rendering.RScriptRenderer;
import replete.util.DemoMain;

public class RScriptDemoMain extends DemoMain {


    //////////
    // MAIN //
    //////////

    public static void main(String[] args) {
        String[] sources = {
            "2",
            "x = 2 + 8 * 9",
            "x = a; a = 100; x = y = 3 + 33 + 333 ** 666 ** 999 % .1 * .2 / 10; z = 8 + ~~-+!~10;",
            "x = 5 * derek.v().x().y()",
            "x = a[1][2].y[3].z.q[2] + abc(\"asdf\", ((t[t])))[3]",
            "x = (a[1][2]).y",
            "x = false[9]; y = 3[3]; y = true.9; y = 3.z",
            "x = aaa.(z)",
            "x = (a.a {m}) {km} ** b {m}",
            "x = a + b + c; x = a ** b ** c",
            "x=y=2+2",
            "x = a > 5 && b == c",
            "y = b > 5 == b > 6",
            "x == 5 > 9 == y + -4**2",
            "x == 5 > x.y.z[5][6].r.t(d).r.t == y + x()",
            "a == b == c",
            "null + [5, 5, 5, 5, 5, 5, x = 7]",
            "[[aa=5], key:b, 44:cos(x), \"\"]",
            "[:] + []",
            "func([y, b, c], [4], tan(var))",
            "\"earth\" ** \"venus\"",
            "x = 10",
            "cos(x) = 10",
            "x.y = 10",
            "x[3] = 10",
            "[3, 4] = 10",
            "x = (3 + 4)[3]",
            "-(3 + 4)"
        };

        RScriptRenderer renderer = new RScriptRenderer();
//        renderer.setAllRenderers(new DebugRenderer());

        int c = 0;
        for(String source : sources) {
            System.out.println("<Source " + (c++) + ": " + source + ">");
            RScript script;
            try {
                RScriptParser parser = new RScriptParser();
                script = parser.parse(source);
            } catch(Exception e) {
                System.out.println(ExceptionUtil.toCompleteString(e, 4));
                continue;
            }
            try {
                int s = 0;
                for(ASTStatement statement : script.getStatements()) {
                    System.out.println("  [Statement " + s + "]");
                    statement.dump("    ");
                    System.out.println("    >Short: " + renderer.renderShort(statement));
                    System.out.println("    >Long:  " + renderer.renderLong(statement));
                    s++;
                }
            } catch(Exception e) {
                System.out.println("Problem Rendering");
                System.out.println(ExceptionUtil.toCompleteString(e, 4));
            }
            c++;
        }

//        Map<String, Object> vars = new HashMap<>();
//        vars.put("a", 30);
//        Object o = EquationEvaluator.evaluate(source, vars);
//        System.out.println(o);
    }
}
