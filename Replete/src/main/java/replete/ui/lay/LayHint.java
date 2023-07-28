package replete.ui.lay;

public class LayHint {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    public static final int WIN    = 0;
    public static final int NONWIN = 1;
    public static final int BOTH   = 2;

    // Core

    public String name;
    public String description;
    public ParamDescriptor params;
    public int applicableTo = NONWIN;
    public LayHintProcessor processor;
    public Class<?>[] applicableTargetClasses;


    //////////////
    // MUTATORS //
    //////////////

    public LayHint setName(String name) {
        this.name = name;
        return this;
    }
    public LayHint setApplicableTo(int applicableTo) {
        this.applicableTo = applicableTo;
        return this;
    }
    public LayHint setDescription(String description) {
        this.description = description;
        return this;
    }
    public LayHint setParams(ParamDescriptor params) {
        this.params = params;
        return this;
    }
    public LayHint setProcessor(LayHintProcessor processor) {
        this.processor = processor;
        return this;
    }
    public LayHint setApplicableTargetClasses(Class<?>[] applicableTargetClasses) {
        this.applicableTargetClasses = applicableTargetClasses;
        return this;
    }
}
