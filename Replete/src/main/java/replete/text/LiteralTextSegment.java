package replete.text;

public class LiteralTextSegment extends TextSegment {


    ///////////
    // FIELD //
    ///////////

    private String text;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public LiteralTextSegment(int start, int endNonIncl, String text) {
        super(start, endNonIncl);
        this.text = text;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getText() {
        return text;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "<LITERAL> " + super.toString() + ",text=(" + text + ")";
    }
}
