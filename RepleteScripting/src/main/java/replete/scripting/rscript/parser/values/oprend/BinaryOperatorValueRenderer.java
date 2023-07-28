package replete.scripting.rscript.parser.values.oprend;

import replete.scripting.rscript.parser.RScript;
import replete.scripting.rscript.parser.RScriptParser;
import replete.scripting.rscript.parser.gen.ASTOperator;
import replete.scripting.rscript.parser.gen.ParseException;
import replete.scripting.rscript.parser.values.OperatorValue;
import replete.scripting.rscript.rendering.ASTRenderingContext;
import replete.scripting.rscript.rendering.RScriptRenderer;

public class BinaryOperatorValueRenderer extends OperatorValueRenderer {

    @Override
    public void render(ASTOperator node, ASTRenderingContext context) {
        boolean useParens = isUseParentheses(node, context);

        if(useParens) {
            context.append("(");
        }
        context.append(node.getChild(0));

        OperatorValue value = node.getValue();
        if(value != OperatorValue.DOT) {
            context.append(" ");
            context.append(value);
            context.append(" ");
        } else {
            context.append(value);
        }

        context.append(node.getChild(1));
        if(useParens) {
            context.append(")");
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) throws ParseException {

        String[] sources = {
            "3 + 3 + 3",
            "x = 3 + 4 * 5",
            "x = 3 + (4 * 5)",
            "x = (3 + 4) * 5",
            "x = -(3 + 4)",
            "x = -3 + 4",
            "x = -(3 ** 4)",
            "x = -3 ** 4",
            "x = (-3) ** 4",
            "true == ++false",
//            "x = -3 ** 4",
//            "x = (3 + 4)[a + b]",
//            "x = (3 + 4)[a]",
//            "x = (3 ** 4) {m^2}",        // L: x = ((3 ** 4) {m�})
//            "x = 3 ** 4 {m^2}",          // L: x = (3 ** (4 {m�}))
//            "x = (x.y) {m^2}",           // L: x = ((x.y) {m�})
//            "x = x.y {m^2}",             // L: x = ((x.y) {m�})
//            "x = [a: 3 + 3]",
            "Layer.var[7]",
//
//            "2",
//            "x = 2 + 8 * 9",
//            "x = a; a = 100; x = y = 3 + 33 + 333 ** 666 ** 999 % .1 * .2 / 10; z = 8 + ~~-+!~10;",
//            "x = 5 * derek.v().x().y()",
//            "x = a[1][2].y[3].z.q[2] + abc(\"asdf\", ((t[t])))[3]",
//            "x = (a[1][2]).y",
////            "x = false[9]; y = 3[3]; y = true.9; y = 3.z",
////            "x = aaa.(z)",
//            "x = (a.a {m}) {km} ** b {m}",
//            "x = a + b + c; x = a ** b ** c",
//            "x=y=2+2",
//            "x = a > 5 && b == c",
//            "y = b > 5 == b > 6",
//            "x == 5 > 9 == y + -4**2",
//            "x == 5 > x.y.z[5][6].r.t(d).r.t == y + x()",
//            "a == b == c",
//            "null + [5, 5, 5, 5, 5, 5, x = 7]",
//            "[[aa=5], key:b, 44:cos(x), \"\"]",
//            "[:] + []",
//            "func([y, b, c], [4], tan(var))",
//            "\"earth\" ** \"venus\"",
//            "x = 10",
//            "cos(x) = 10",
//            "x.y = 10",
//            "x[3] = 10",
////            "[3, 4] = 10",
//            "x = (3 + 4)[3]",
//            "-(3 + 4)",
//
//            "+2.0",
//            "x",
//            " \t y = m* x +   (( b )) ",
//            "2.0 + 2.0",
//            "(2.0 % t.y) * (  (  u$$)) && !p__.$f",
//            "cos(y)",
//            "V' += ( m + a') - 35.0 + tanh(v$)",
//            "foo(x) = f ** 2.0 - (10.3e20 ** tan(3.0) + u.t) * 22.0 - o[3.0 + r]",
//            "u__4=2.0-cos(h+=   5.0, tanh(rr) *t[r] / \"earth\")*t$.f0    ** ((6.0+!k))",
//            "x[3.0+a+cos(derek(1.0))/9.0]=b=c=0.0",
//            "a=\"dog\" ** derek(a=2.0, 3e3, cos(x-3.0))",
//            "x=3.0*x**(2.0+3.0)-t**cos(theta')",
//            "yy = !false + 4.0 - (true == ++false)",
//            "alpha_m = (25 - V) / (10 * (exp ((25 - V) / 10) - 1))",
//            "x = (3 + 4)[a]",
//            "-(3 + 4)",
//
//            // Precedence
//            "x = 3 - 4 / 6 + 7",
//            "x = (3 - 4) / 6 + 7",
//            "x = 3 - 4 / (6 + 7)",
//            "x = (3 - 4) / (6 + 7)",
//            "x = 3 * 4 / 6 % 7",
//            "x = (3 * 4) / 6 % 7",
//            "x = 3 * 4 / (6 % 7)",
//            "x = (3 * 4) / (6 % 7)",
//
//            // More precedence for proper parenthesization
//            "x = 3 + 4 * 5",
//            "x = 3 + (4 * 5)",
//            "x = (3 + 4) * 5",
//            "x = -(3 + 4)",
//            "x = (3 + 4)[a + b]",
//            "x = (3 + 4)[(a + b)]",
//            "x = (3 + 4)[a]",
//            "x = (3 + 4) {m^2}",
//            "x = 3 + 4 {m^2}",
//            "x = [a: 3 + 3]",
//            "x = [a: (3 + 3)]"
        };
        RScriptParser parser = new RScriptParser();
        RScriptRenderer renderer = new RScriptRenderer();
        for(String source : sources) {
            RScript script = parser.parse(source);

            String ns = renderer.renderShort(script);
            String nl = renderer.renderLong(script);
            System.out.println(source);
            System.out.println("  [--NEW--]");
            System.out.println("    S: " + ns);
            System.out.println("    L: " + nl);

            System.out.println("-------------------------------------");
        }
    }
}
