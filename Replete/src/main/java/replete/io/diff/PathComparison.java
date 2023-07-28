package replete.io.diff;

// This class serves as a consistent top-level comparison
// result even for path combinations where one path existed
// and the corresponding one did not (PathType.NONE).  This
// was done this way so DirComparison did not have to maintain
// separate "onlyIn[Left|Right]" lists and can instead have
// a single unified and resortable "results" list.

public class PathComparison extends PossiblyDifferent {


    ////////////
    // FIELDS //
    ////////////

    private boolean diffCase;
    private PathType leftType;
    private PathType rightType;
    private ContentComparison contentComparison;  // Null if types are not equal


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PathComparison(boolean diffCase, PathType leftType, PathType rightType) {
        this.diffCase = diffCase;
        this.leftType = leftType;
        this.rightType = rightType;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isDiffCase() {
        return diffCase;
    }
    public PathType getLeftType() {
        return leftType;
    }
    public PathType getRightType() {
        return rightType;
    }
    public ContentComparison getContentComparison() {
        return contentComparison;
    }

    // Mutators

    public PathComparison setContentComparison(ContentComparison contentComparison) {
        this.contentComparison = contentComparison;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public boolean isDiff() {
        return
            diffCase ||
            leftType != rightType ||
            contentComparison != null && contentComparison.isDiff();
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if(diffCase) {
            buffer.append("(Path Case: Differs)");
        }
        if(leftType != rightType) {
            buffer.append("(Path Type: Left is ");
            buffer.append(leftType == PathType.DIR ? "Dir" : "File");
            buffer.append(" but Right is ");
            buffer.append(rightType == PathType.DIR ? "Dir" : "File");
            buffer.append(")");
        }
        return buffer.toString();
    }
}
