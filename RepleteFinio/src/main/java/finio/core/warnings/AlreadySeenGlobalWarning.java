package finio.core.warnings;

import finio.core.FUtil;


public class AlreadySeenGlobalWarning implements UnexpandableWarning {


    ///////////
    // FIELD //
    ///////////

    private String objectInfo;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public AlreadySeenGlobalWarning(Object O) {
        objectInfo = FUtil.toStringBase(O);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getReason() {
        return "Already seen " + objectInfo;
    }
    @Override
    public String toString() {
        return "AlreadySeenGlobal[" + objectInfo + "]";
    }
}
