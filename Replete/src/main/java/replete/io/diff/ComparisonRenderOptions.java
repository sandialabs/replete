package replete.io.diff;

public class ComparisonRenderOptions {


    ////////////
    // FIELDS //
    ////////////

    private boolean includeSame = false;
    private SortType sortType = SortType.ALPHA_IC;
    private String leftLabel;
    private String rightLabel;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isIncludeSame() {
        return includeSame;
    }
    public SortType getSortType() {
        return sortType;
    }
    public String getLeftLabel() {
        return leftLabel;
    }
    public String getRightLabel() {
        return rightLabel;
    }

    // Mutators

    public ComparisonRenderOptions setIncludeSame(boolean includeSame) {
        this.includeSame = includeSame;
        return this;
    }
    public ComparisonRenderOptions setSortType(SortType sortType) {
        this.sortType = sortType;
        return this;
    }
    public ComparisonRenderOptions setLeftLabel(String leftLabel) {
        this.leftLabel = leftLabel;
        return this;
    }
    public ComparisonRenderOptions setRightLabel(String rightLabel) {
        this.rightLabel = rightLabel;
        return this;
    }
}
