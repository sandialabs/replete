package replete.diff;

import replete.text.StringLib;

public class TestObj {
    public int x;
    public int y;

    public SubObj subObj;

    public TestObj(int x, int y) {
        this(x, y, null);
    }
    public TestObj(int x, int y, SubObj subObj) {
        this.x = x;
        this.y = y;
        this.subObj = subObj;
    }
    public TestObj setX(int x) {
        this.x = x;
        return this;
    }
    public TestObj setY(int y) {
        this.y = y;
        return this;
    }
    public TestObj setSubObj(SubObj subObj) {
        this.subObj = subObj;
        return this;
    }

    @Override
    public String toString() {
        return "[" + x + "," + y + "," + (subObj == null ? StringLib.NULL : subObj) + "]";
    }
}
