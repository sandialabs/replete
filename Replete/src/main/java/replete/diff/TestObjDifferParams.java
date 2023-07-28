package replete.diff;

public class TestObjDifferParams extends DifferParams {

    private boolean includeY;
    private SubObjDifferParams subObjDifferParams;

    public TestObjDifferParams setIncludeY(boolean includeY) {
        this.includeY = includeY;
        return this;
    }

    public boolean isIncludeY() {
        return includeY;
    }

    public TestObjDifferParams setSubObjDifferParams(SubObjDifferParams subObjDifferParams) {
        this.subObjDifferParams = subObjDifferParams;
        return this;
    }
    public SubObjDifferParams getSubObjDifferParams() {
        return subObjDifferParams;
    }

}
