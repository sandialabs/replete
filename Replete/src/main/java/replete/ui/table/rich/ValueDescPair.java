package replete.ui.table.rich;

// A special pair class just so RichTableRowList can
// be sure about the object it is examining.  This
// isn't the cleanest solution but RichTableRowList
// and RichTableRowTableModel are all about convenience
// so they can evolve fairly haphazardly sometimes.

public class ValueDescPair {


    ////////////
    // FIELDS //
    ////////////

    private Object value;
    private String description;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ValueDescPair(Object value, String description) {
        this.value = value;
        this.description = description;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public Object getValue() {
        return value;
    }
    public String getDescription() {
        return description;
    }
}
