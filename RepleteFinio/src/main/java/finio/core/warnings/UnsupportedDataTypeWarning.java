package finio.core.warnings;

import finio.core.FUtil;


public class UnsupportedDataTypeWarning implements UnexpandableWarning {


    ///////////
    // FIELD //
    ///////////

    private String objectInfo;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public UnsupportedDataTypeWarning(Object O) {
        objectInfo = FUtil.toStringBase(O);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getReason() {
        return "Would recurse on " + objectInfo;
    }
    @Override
    public String toString() {
        return "UnsupportedDataTypeWarning[" + objectInfo + "]";
    }
}
