package replete.diff.generic;

public class PrimitivesObject {
    public boolean boolField;
    public byte byteField;
    public short shortField;
    public int intField;
    public long longField;
    public float floatField;
    public double doubleField;
    public char charField;

    public String stringField;

    public PrimitivesObject(boolean version) {
        if(version) {
            boolField = true;
            byteField = 1;
            shortField = 11;
            intField = 111;
            longField = 1111;
            floatField = 1.1f;
            doubleField = 1.11;
            charField = '1';
            stringField = "One";
        } else {
            boolField = false;
            byteField = 2;
            shortField = 22;
            intField = 222;
            longField = 2222;
            floatField = 2.2f;
            doubleField = 2.22;
            charField = '2';
            stringField = "Two";
        }
    }
}
