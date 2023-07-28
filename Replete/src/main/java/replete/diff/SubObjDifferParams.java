package replete.diff;

public class SubObjDifferParams extends DifferParams {

    private boolean includeT;

    public SubObjDifferParams setIncludeT(boolean includeT) {
        this.includeT = includeT;
        return this;
    }

    public boolean isIncludeT() {
        return includeT;
    }

}
