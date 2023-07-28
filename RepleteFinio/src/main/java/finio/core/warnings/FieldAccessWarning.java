package finio.core.warnings;

import replete.text.StringUtil;

public class FieldAccessWarning implements UnexpandableWarning {


    ////////////
    // FIELDS //
    ////////////

    private Object O;
    private String fieldName;
    private String errorMessage;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FieldAccessWarning(Object O, String fieldName, String errorMessage) {
        this.O = O;
        this.fieldName = fieldName;
        this.errorMessage = errorMessage;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getReason() {
        return
            "An error occurred expanding " +
            StringUtil.toStringObject(O) + "/" +
            fieldName + " because of " +
            errorMessage;
    }
    @Override
    public String toString() {
        return "FieldAccessWarning[" + StringUtil.toStringObject(O) + "/" + fieldName + "]";
    }
}
