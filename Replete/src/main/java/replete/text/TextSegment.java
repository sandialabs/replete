package replete.text;

public abstract class TextSegment {


    ////////////
    // FIELDS //
    ////////////

    private int start;
    private int endNonIncl;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TextSegment(int start, int endNonIncl) {
        this.start = start;
        this.endNonIncl = endNonIncl;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getStart() {
        return start;
    }
    public int getEndNonIncl() {
        return endNonIncl;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "start=" + start + ",endNonIncl=" + endNonIncl;
    }
}
