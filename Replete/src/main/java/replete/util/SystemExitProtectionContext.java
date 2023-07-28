package replete.util;

public class SystemExitProtectionContext {


    ////////////
    // FIELDS //
    ////////////

    private Integer status;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public Integer getStatus() {
        return status;
    }

    // Mutators

    public SystemExitProtectionContext setStatus(Integer status) {
        this.status = status;
        return this;
    }
}
