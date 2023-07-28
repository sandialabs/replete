package replete.scrutinize.core;

public class ExtractedField {


    ////////////
    // FIELDS //
    ////////////

    private boolean count;
    private boolean sort;
    private boolean useField;
    private boolean usePrivate;
    private String fieldName;
    private String prettyName;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ExtractedField(String fieldCode) {
        fieldName = fieldCode;

        if(fieldName.endsWith(";count")) {
            count = true;
            int semi = fieldName.indexOf(';');
            fieldName = fieldName.substring(0, semi);
        }

        if(fieldName.endsWith(";sort")) {
            sort = true;
            int semi = fieldName.indexOf(';');
            fieldName = fieldName.substring(0, semi);
        }

        if(fieldName.endsWith(";field")) {
            useField = true;
            int semi = fieldName.indexOf(';');
            fieldName = fieldName.substring(0, semi);
        }

        if(fieldName.endsWith(";private")) {
            usePrivate = true;
            int semi = fieldName.indexOf(';');
            fieldName = fieldName.substring(0, semi);
        }

        int colon = fieldName.indexOf(':');
        if(colon != -1) {
            prettyName = fieldName.substring(colon + 1);
            fieldName = fieldName.substring(0, colon);
        }
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isCount() {
        return count;
    }
    public boolean isSort() {
        return sort;
    }
    public boolean isUseField() {
        return useField;
    }
    public boolean isUsePrivate() {
        return usePrivate;
    }
    public String getPrettyName() {
        return prettyName;
    }
    public String getFieldName() {
        return fieldName;
    }
}
