package finio.platform.exts.manager.trivial;

import finio.manager.ManagedParameters;

public class TrivialManagedParameters extends ManagedParameters {


    ////////////
    // FIELDS //
    ////////////

    private String param1;
    private String param2;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TrivialManagedParameters() {
        // Nothing
    }
    public TrivialManagedParameters(String param1, String param2) {
        this.param1 = param1;
        this.param2 = param2;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getParam1() {
        return param1;
    }
    public String getParam2() {
        return param2;
    }

    // Mutators (Builder)

    public TrivialManagedParameters setParam1(String param1) {
        this.param1 = param1;
        return this;
    }
    public TrivialManagedParameters setParam2(String param2) {
        this.param2 = param2;
        return this;
    }
}
