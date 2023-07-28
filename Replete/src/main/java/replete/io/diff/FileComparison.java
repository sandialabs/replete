package replete.io.diff;

public class FileComparison extends ContentComparison {


    ////////////
    // FIELDS //
    ////////////

    private long leftLength;
    private long rightLength;
    private String leftMd5;
    private String rightMd5;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public long getLeftLength() {
        return leftLength;
    }
    public long getRightLength() {
        return rightLength;
    }
    public String getLeftMd5() {
        return leftMd5;
    }
    public String getRightMd5() {
        return rightMd5;
    }

    // Mutators

    public FileComparison setLeftLength(long leftLength) {
        this.leftLength = leftLength;
        return this;
    }
    public FileComparison setRightLength(long rightLength) {
        this.rightLength = rightLength;
        return this;
    }
    public FileComparison setLeftMd5(String leftMd5) {
        this.leftMd5 = leftMd5;
        return this;
    }
    public FileComparison setRightMd5(String rightMd5) {
        this.rightMd5 = rightMd5;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public boolean isDiff() {
        return
            leftLength != rightLength ||
            leftMd5 != null && !leftMd5.equals(rightMd5);     // Null check unnecessary given how we construct this object
    }

    @Override
    public String toString() {
        String extra = "";
        if(leftLength != rightLength) {
            extra += "(Size: " + leftLength + " B != " + rightLength + " B)";
        }
        if(leftMd5 != null && !leftMd5.equals(rightMd5)) {
            extra += "(MD5: " + leftMd5 + " != " + rightMd5 + ")";
        }
        return extra;
    }
}
