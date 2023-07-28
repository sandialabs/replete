package replete.scripting.rscript.evaluation;

import java.util.Map;

import replete.scripting.rscript.evaluation.evaluators.DefaultConstantEvaluator;
import replete.scripting.rscript.evaluation.evaluators.DefaultFunctionEvaluator;
import replete.scripting.rscript.evaluation.evaluators.DefaultKeyValuePairEvaluator;
import replete.scripting.rscript.evaluation.evaluators.DefaultListOrMapEvaluator;
import replete.scripting.rscript.evaluation.evaluators.DefaultOperatorEvaluator;
import replete.scripting.rscript.evaluation.evaluators.DefaultStartEvaluator;
import replete.scripting.rscript.evaluation.evaluators.DefaultStatementEvaluator;
import replete.scripting.rscript.evaluation.evaluators.DefaultUnitEvaluator;
import replete.scripting.rscript.evaluation.evaluators.DefaultVariableEvaluator;
import replete.scripting.rscript.evaluation.functions.EvaluationException;
import replete.scripting.rscript.inspection.RScriptInspector;
import replete.scripting.rscript.parser.RScript;
import replete.scripting.rscript.parser.RScriptParser;
import replete.scripting.rscript.parser.gen.ASTConstant;
import replete.scripting.rscript.parser.gen.ASTFunction;
import replete.scripting.rscript.parser.gen.ASTKeyValuePair;
import replete.scripting.rscript.parser.gen.ASTListOrMap;
import replete.scripting.rscript.parser.gen.ASTNode;
import replete.scripting.rscript.parser.gen.ASTOperator;
import replete.scripting.rscript.parser.gen.ASTStart;
import replete.scripting.rscript.parser.gen.ASTStatement;
import replete.scripting.rscript.parser.gen.ASTUnit;
import replete.scripting.rscript.parser.gen.ASTVariable;
import replete.scripting.rscript.parser.gen.ParseException;
import replete.scripting.rscript.rendering.RScriptRenderer;

////////////////
// EVALUATION //
////////////////

// Every node has the ability to evaluate itself
// to a single result object.  The context is a
// where the tree gets and sets the/ variable values
// to be used in the evaluation.
//
// In other words, when you evaluate
//    y = x + z
// You need to have a context that can provide the
// values for x and z, and set the value for y.
// The latter is needed since this evaluation process
// is happening recursively, and y might be used in
// a later evaluation:
//    x = 10
//    z = 20
//    y = x + z
//    c = y ** 2

public class RScriptEvaluator {


    ////////////
    // FIELDS //
    ////////////

    private ASTNodeEvaluatorMap defaultEvaluators = new ASTNodeEvaluatorMap();
    private ASTNodeEvaluatorMap currentEvaluators = new ASTNodeEvaluatorMap();
    private boolean persistContext = false;
    private EvaluationEnvironment initialEnvironment = new EvaluationEnvironment();   // For new contexts
//    private ASTNodeValueOverriderMap valueOverrides = new ASTNodeValueOverriderMap();

    private ASTEvaluationContext persistedContext = null;
    private EvaluationResult lastResult = null;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public RScriptEvaluator() {
        initDefaultEvaluators();
    }

    protected void initDefaultEvaluators() {
        defaultEvaluators.put(ASTConstant.class,     new DefaultConstantEvaluator());
        defaultEvaluators.put(ASTFunction.class,     new DefaultFunctionEvaluator());
        defaultEvaluators.put(ASTKeyValuePair.class, new DefaultKeyValuePairEvaluator());
        defaultEvaluators.put(ASTListOrMap.class,    new DefaultListOrMapEvaluator());
        defaultEvaluators.put(ASTOperator.class,     new DefaultOperatorEvaluator());
        defaultEvaluators.put(ASTStart.class,        new DefaultStartEvaluator());
        defaultEvaluators.put(ASTStatement.class,    new DefaultStatementEvaluator());
        defaultEvaluators.put(ASTUnit.class,         new DefaultUnitEvaluator());
        defaultEvaluators.put(ASTVariable.class,     new DefaultVariableEvaluator());

        currentEvaluators.putAll(defaultEvaluators);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public ASTNodeEvaluatorMap getDefaultEvaluators() {
        return defaultEvaluators;
    }
    public ASTNodeEvaluatorMap getCurrentEvaluators() {
        return currentEvaluators;
    }
    public boolean isPersistContext() {
        return persistContext;
    }
    public EvaluationEnvironment getInitialEnvironment() {
        return initialEnvironment;
    }
    public EvaluationResult getLastResult() {
        return lastResult;
    }
//    public ASTNodeValueOverriderMap getValueOverrides() {
//        return valueOverrides;
//    }

    // Mutators

    public <T extends ASTNode> void setEvaluator(Class<T> nodeClass, ASTNodeEvaluator<T> evaluator) {
        currentEvaluators.put(nodeClass, evaluator);
        if(persistedContext != null) {       // No synchronization yet
            persistedContext.setEvaluator(nodeClass, evaluator);
        }
    }
    public RScriptEvaluator setPersistContext(boolean persistContext) {
        this.persistContext = persistContext;
        if(!persistContext) {
            persistedContext = null;
        }
        return this;
    }
    public RScriptEvaluator setInitialEnvironment(EvaluationEnvironment initialEnvironment) {
        this.initialEnvironment = initialEnvironment;
        return this;
    }
//    public <T extends ASTNode> void setValueOverrider(Class<T> nodeClass, ASTNodeValueOverrider overrider) {
//        valueOverrides.put(nodeClass, overrider);
//    }

    public void setAllEvaluators(ASTNodeEvaluator evaluator) {
        currentEvaluators.put(ASTConstant.class,     evaluator);
        currentEvaluators.put(ASTFunction.class,     evaluator);
        currentEvaluators.put(ASTKeyValuePair.class, evaluator);
        currentEvaluators.put(ASTListOrMap.class,    evaluator);
        currentEvaluators.put(ASTOperator.class,     evaluator);
        currentEvaluators.put(ASTStart.class,        evaluator);
        currentEvaluators.put(ASTStatement.class,    evaluator);
        currentEvaluators.put(ASTUnit.class,         evaluator);
        currentEvaluators.put(ASTVariable.class,     evaluator);

        if(persistedContext != null) {       // No synchronization yet
            for(Class clazz : currentEvaluators.keySet()) {
                persistedContext.setEvaluator(clazz, currentEvaluators.get(clazz));
            }
        }
    }


    ////////////////
    // EVALUATION //
    ////////////////

    public EvaluationResult evaluate(RScript script) throws EvaluationException {
        return evaluate(script.getStart());
    }
    public EvaluationResult evaluate(ASTNode node) throws EvaluationException {
        ASTEvaluationContext context;

        if(persistContext) {
            if(persistedContext == null) {
                persistedContext = createContext();
            }
            context = persistedContext;
        } else {
            context = createContext();
        }

        Object rootValue = context.evaluate(node);
        EvaluationResult result = context.saveResult();
        result.put("$root", rootValue);
        lastResult = result;
        return result;
    }

    private ASTEvaluationContext createContext() {
        return new ASTEvaluationContext(defaultEvaluators, currentEvaluators, initialEnvironment);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) throws Exception {
//        String source = "z = x * y; q = z + x";
//
//        Object[] expectedResults = {
//            "z", 36.0,
//            "q", 40.0
//        };
//
//        RScript script = parse(source);
//        System.out.println(render(script.getStart(), true));
////        EvaluationResult result = evaluate2(script, new RHashMap("x", 4L, "y", 9L));
//        EvaluationResult result = evaluate2(script, new Person());
//        for(int e = 0; e < expectedResults.length; e += 2) {
//            Object actual = result.get(expectedResults[e]);
//            Object expected = expectedResults[e + 1];
//            assertEquals(expected, actual);
//
//            System.out.println(actual);
//        }
        Object[] eval = {
            "x = " + SpecialVariables.PI, Math.PI,
            "y = " + SpecialVariables.E,  Math.E,
        };

        RScriptInspector inspector = new RScriptInspector();
        for(int e = 0; e < eval.length; e += 2) {
            RScript script = parse((String) eval[e]);
            EvaluationResult result = evaluate2(script);
            Object actual = result.get(inspector.getVariableName(script, true, true, true, false));
            Object expected = eval[e + 1];
            if(!expected.equals(actual)) {
                throw new RuntimeException("expected != actual: " + expected + " != " + actual);
            }
        }
    }

    private static class Person {
        int x = 4;
        int y = 9;
    }

    private static RScript parse(String source) throws ParseException {
        RScriptParser parser = new RScriptParser();
        return parser.parse(source);
    }

    private static String render(ASTNode node, boolean shortMode) {
        RScriptRenderer renderer = new RScriptRenderer();
        if(shortMode) {
            return renderer.renderShort(node);
        }
        return renderer.renderLong(node);
    }
    private static EvaluationResult evaluate2(RScript script) throws EvaluationException {
        return evaluate2(script.getStart(), null);
    }
    private static EvaluationResult evaluate2(RScript script, Map<String, Object> vars) throws EvaluationException {
        return evaluate2(script.getStart(), vars);
    }
    private static EvaluationResult evaluate2(RScript script, Object varSrc) throws EvaluationException {
        return evaluate2(script.getStart(), varSrc);
    }
    private static EvaluationResult evaluate2(ASTNode node) throws EvaluationException {
        return evaluate2(node, null);
    }
    private static EvaluationResult evaluate2(ASTNode node, Map<String, Object> vars) throws EvaluationException {
        RScriptEvaluator evaluator = new RScriptEvaluator();
        if(vars != null) {
            evaluator.setInitialEnvironment(
                new EvaluationEnvironment()
                    .addLayer(vars)
            );
        }
        return evaluator.evaluate(node);
    }
    private static EvaluationResult evaluate2(ASTNode node, Object varSrc) throws EvaluationException {
        RScriptEvaluator evaluator = new RScriptEvaluator();
        if(varSrc != null) {
            evaluator.setInitialEnvironment(
                new EvaluationEnvironment()
                    .addLayer(varSrc)
            );
        }
        return evaluator.evaluate(node);
    }
}
