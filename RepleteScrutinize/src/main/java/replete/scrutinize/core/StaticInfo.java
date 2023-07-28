package replete.scrutinize.core;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class StaticInfo implements Serializable {

    // This class is so all instances of a target class can share
    // a single object to store all their shared static class
    // information.


    ////////////
    // FIELDS //
    ////////////

    private Map<String, ScFieldResult> staticFields;       // Field map just like BaseSc object would have.
    private Set<String> otherActions = new TreeSet<>();  // Just other methods that could be called
                                                         // that don't share a name with any extracted
                                                         // field method names nor the Object class.

    private transient Map<String, List<Method>> allMethods;
    private transient Map<String, Field> allFields;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public Map<String, ScFieldResult> getStaticFields() {
        return staticFields;
    }
    public Set<String> getOtherActions() {
        return otherActions;
    }
    public Map<String, List<Method>> getAllMethods() {
        return allMethods;
    }
    public Map<String, Field> getAllFields() {
        return allFields;
    }

    // Mutators

    public void setStaticFields(Map<String, ScFieldResult> staticFields) {
        this.staticFields = staticFields;
    }
    public void setAllMethods(Map<String, List<Method>> allMethods) {
        this.allMethods = allMethods;
    }
    public void setAllFields(Map<String, Field> allFields) {
        this.allFields = allFields;
    }
}
