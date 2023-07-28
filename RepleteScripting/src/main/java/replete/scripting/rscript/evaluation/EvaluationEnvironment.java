package replete.scripting.rscript.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EvaluationEnvironment {


    ////////////
    // FIELDS //
    ////////////

    private List<Object> layers = new ArrayList<>();     // Map<String, Object> or POJO's


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public EvaluationEnvironment() {}
    public EvaluationEnvironment(Object obj) {
        addLayer(obj);
    }
    public EvaluationEnvironment(Map<String, Object> map) {
        addLayer(map);
    }
    public EvaluationEnvironment(EvaluationEnvironment other) {
        layers = new ArrayList<>(other.layers);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public List<Object> getLayers() {
        return layers;
    }

    // Mutators

    public EvaluationEnvironment addLayer(Map<String, Object> envMap) {
        layers.add(envMap);
        return this;
    }
    public EvaluationEnvironment addLayer(Object obj) {
        layers.add(obj);
        return this;
    }
}
