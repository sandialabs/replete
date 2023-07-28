package replete.scripting.rscript.evaluation;

import java.util.HashMap;

public class EvaluationResult extends HashMap<String, Object> {
    public Object getRootValue() {
        return get("$root");
    }
}
