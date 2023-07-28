package replete.text.tokens;

public class MatchLocation {
    public int start;
    public int endNonIncl;
    public MatchLocation(int start, int end) {
        this.start = start;
        this.endNonIncl = end;
    }
    public int getStart() {
        return start;
    }
    public int getEndNonIncl() {
        return endNonIncl;
    }
    @Override
    public String toString() {
        return "start=" + start + ", end=" + endNonIncl;
    }
}
