package replete.diff.generic;

public class OuterObject {
    public PrimitivesObject innerObject;
    public Integer intField;
    public String stringField;

    public OuterObject(PrimitivesObject innerObject, boolean fieldSet) {
        this.innerObject = innerObject;
        if(fieldSet) {
            intField = 1;
            stringField = "One";
        } else {
            intField = 2;
            stringField = "Two";
        }
    }
}
