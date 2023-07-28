
package replete.scripting.rscript.evaluation.functions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import replete.plugins.ExtensionPoint;
import replete.plugins.PluginManager;

public class FunctionList {

    public static String OP_ASSIGN   = "=";
    public static String OP_AASSIGN  = "+=";
    public static String OP_SASSIGN  = "-=";
    public static String OP_MASSIGN  = "*=";
    public static String OP_DASSIGN  = "/=";
    public static String OP_UASSIGN  = "%=";
    public static String OP_PASSIGN  = "**=";

    public static String OP_OR       = "||";
    public static String OP_AND      = "&&";

    public static String OP_EQ       = "==";
    public static String OP_NE       = "!=";

    public static String OP_LT       = "<";
    public static String OP_LE       = "<=";
    public static String OP_GT       = ">";
    public static String OP_GE       = ">=";

    public static String OP_ADD      = "+";
    public static String OP_SUBTRACT = "-";
    public static String OP_MULTIPLY = "*";
    public static String OP_DIVIDE   = "/";
    public static String OP_MOD      = "%";
    public static String OP_POWER    = "**";

    public static String OP_UPLUS    = "-UP";
    public static String OP_UMINUS   = "-UM";
    public static String OP_NOT      = "!";
    public static String OP_BNOT     = "~";

    public static String OP_DOT      = ".";
    public static String OP_ELEMENT  = "[]";

    private static Map<String, Function> functions = new HashMap<>();
    private static void addFunc(String name, Function func) {
        functions.put(name, func);
    }

    static {

        // Arithmetic
        addFunc(OP_ADD,      new AdditionFunction());
        addFunc(OP_SUBTRACT, new SubtractionFunction());
        addFunc(OP_MULTIPLY, new MultiplicationFunction());
        addFunc(OP_DIVIDE,   new DivisionFunction());
        addFunc(OP_MOD,      new ModulusFunction());
        addFunc(OP_POWER,    new ExponentiationFunction());
        addFunc(OP_UPLUS,    new UnaryPlusFunction());
        addFunc(OP_UMINUS,   new UnaryMinusFunction());

        // Logical
        addFunc(OP_AND,      new LogicalAndFunction());
        addFunc(OP_OR,       new LogicalOrFunction());
        addFunc(OP_NOT,      new LogicalNotFunction());
        addFunc(OP_BNOT,     new BitwiseNotFunction());

        // Comparison
        addFunc(OP_EQ,       new ComparisonEqualsFunction());
        addFunc(OP_NE,       new ComparisonNotEqualsFunction());

        // Relational
        addFunc(OP_LT,       new RelationalLTFunction());
        addFunc(OP_LE,       new RelationalLEFunction());
        addFunc(OP_GT,       new RelationalGTFunction());
        addFunc(OP_GE,       new RelationalGEFunction());

        // Assignment & Compound Assignment
        addFunc(OP_ASSIGN,   new AssignmentFunction());
        addFunc(OP_AASSIGN,  new AdditionAssignmentFunction());
        addFunc(OP_SASSIGN,  new SubtractionAssignmentFunction());
        addFunc(OP_MASSIGN,  new MultiplicationAssignmentFunction());
        addFunc(OP_DASSIGN,  new DivisionAssignmentFunction());
        addFunc(OP_UASSIGN,  new ModulusAssignmentFunction());
        addFunc(OP_PASSIGN,  new ExponentiationAssignmentFunction());

        // List
        addFunc(OP_ELEMENT,  new ListSubscriptExpression());

        // Vector (not impl)
        addFunc(OP_DOT,      new DotFunction());

        // Other
        addFunc("sin",       new SineFunction());
        addFunc("cos",       new CosineFunction());
        addFunc("tan",       new TangentFunction());
        addFunc("exp",       new ExponentialFunction());
        addFunc("gaussian",  new GaussianDistributionFunction());
        addFunc("lognormal", new LognormalDistributionFunction());
        addFunc("uniform",   new UniformDistributionFunction());
    }

    public static void initFromPlugins() {
        List<ExtensionPoint> exts = PluginManager.getExtensionsForPoint(Function.class);
        for(ExtensionPoint ext : exts) {
            Function f = (Function) ext;
            addFunc(f.getName(), f);
        }
    }

    public static Function get(String name) {
        return functions.get(name);
    }
    public static Function getOrUnknown(String name) {
        Function func = functions.get(name);
        if(func == null) {
            func = new UnknownFunction(name);
        }
        return func;
    }

    public static void register(Function f) {
        functions.put(f.getName(), f);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        for(String key : functions.keySet()) {
            Function func = functions.get(key);
            System.out.println(key + " (" + func.getDescription() + ")");
            for(ParameterSet set : func.getAllowedParameterSets()) {
                System.out.println("    " + set);
            }
        }
    }
}
