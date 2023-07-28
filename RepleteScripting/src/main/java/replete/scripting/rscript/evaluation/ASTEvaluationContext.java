package replete.scripting.rscript.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import replete.scripting.rscript.evaluation.functions.EvaluationException;
import replete.scripting.rscript.parser.gen.ASTNode;
import replete.util.ReflectionUtil;

public class ASTEvaluationContext {


    ////////////
    // FIELDS //
    ////////////

    private ASTNodeEvaluatorMap defaultEvaluators;
    private ASTNodeEvaluatorMap currentEvaluators;
    private List<ASTNode> path = new ArrayList<>();
    private Map<String, Object> variableValues = new HashMap<>();
    private Map<String, Object> specialVariables = new HashMap<>();   // Needs to be in RScriptEvaluator!
    private EvaluationEnvironment environment;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ASTEvaluationContext(ASTNodeEvaluatorMap defaultEvaluators, ASTNodeEvaluatorMap currentEvaluators,
                                EvaluationEnvironment environment) {
        this.defaultEvaluators = defaultEvaluators;
        this.currentEvaluators = currentEvaluators;
        this.environment = environment;

        populateSpecialVariables();   // Needs to be enhanced in future...
    }

    private void populateSpecialVariables() {
        specialVariables.put(SpecialVariables.PI, Math.PI);
        specialVariables.put(SpecialVariables.E, Math.E);
    }

    public EvaluationResult saveResult() {
        EvaluationResult result = new EvaluationResult();
        result.putAll(variableValues);        // Shallow copy - values should be immutable if possible!
        return result;
    }

    public Object evaluate(ASTNode node) throws EvaluationException {
        path.add(node);
        try {
            ASTNodeEvaluator evaluator = currentEvaluators.get(node.getClass());
            return evaluator.evaluate(node, this);
        } finally {
            path.remove(node);
        }
    }
    public Object evaluateDefault(ASTNode node) throws EvaluationException {
        path.add(node);
        try {
            ASTNodeEvaluator evaluator = defaultEvaluators.get(node.getClass());
            return evaluator.evaluate(node, this);
        } finally {
            path.remove(node);
        }
    }


    //////////////////////
    // GET / SET VALUES //
    //////////////////////

    public Object getValueForVariable(String varName) throws EvaluationException {
        if(variableValues.containsKey(varName)) {
            return variableValues.get(varName);
        }
        if(environment != null) {
            for(Object layer : environment.getLayers()) {
                if(layer instanceof Map) {
                    Map map = (Map) layer;
                    if(map.containsKey(varName)) {
                        return map.get(varName);
                    }
                } else {
                    if(ReflectionUtil.hasField(layer, varName)) {
                        return ReflectionUtil.get(layer, varName);
                    }
                }
            }
        }
        // TODO: Think about whether to allow the redefinition of special variables.  Where to prevent?
        if(specialVariables.containsKey(varName)) {
            return specialVariables.get(varName);
        }
        throw new EvaluationException("Could not locate the variable '" + varName + "'.");
    }
    public void setValueForVariable(String varName, Object value) throws EvaluationException {
        variableValues.put(varName, value);
    }
    public <T extends ASTNode> void setEvaluator(Class<T> nodeClass, ASTNodeEvaluator<T> evaluator) {
        currentEvaluators.put(nodeClass, evaluator);
    }
}
