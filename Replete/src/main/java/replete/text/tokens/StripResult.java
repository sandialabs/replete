package replete.text.tokens;

public class StripResult {
    public String changed;
    public int fromLeft;
    public int fromRight;
    public StripResult(String changed, int fromLeft, int fromRight) {
        super();
        this.changed = changed;
        this.fromLeft = fromLeft;
        this.fromRight = fromRight;
    }

}
