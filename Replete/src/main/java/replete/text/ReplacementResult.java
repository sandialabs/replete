package replete.text;

public class ReplacementResult {


    ////////////
    // FIELDS //
    ////////////

    private String originalText;
    private String resultText;
    private TextSegment[] segments;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ReplacementResult(String originalText, String resultText, TextSegment[] segments) {
        this.originalText = originalText;
        this.resultText = resultText;
        this.segments = segments;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getOriginalText() {
        return originalText;
    }
    public String getResultText() {
        return resultText;
    }
    public TextSegment[] getSegments() {
        return segments;
    }
}
