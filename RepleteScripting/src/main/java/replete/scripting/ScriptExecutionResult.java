package replete.scripting;

import java.util.HashMap;
import java.util.Map;

public class ScriptExecutionResult extends HashMap<String, Object> {
    private Map<String, Object> resultValues;
    private Object rootValue;

    public Map<String, Object> getResultValues() {
        return resultValues;
    }
    public Object getRootValue() {
        return rootValue;
    }

    public ScriptExecutionResult setResultValues(Map<String, Object> resultValues) {
        this.resultValues = resultValues;
        return this;
    }
    public ScriptExecutionResult setRootValue(Object rootValue) {
        this.rootValue = rootValue;
        return this;
    }
}
