package replete.text;

public class VariableTextSegment extends TextSegment {


    ///////////
    // FIELD //
    ///////////

    private String originalCharacters;
    private String variableName;
    private String replacement;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public VariableTextSegment(int start, int endNonIncl, String originalCharacters, String variableName, String replacement) {
        super(start, endNonIncl);
        this.originalCharacters = originalCharacters;
        this.variableName = variableName;
        this.replacement = replacement;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getOriginalCharacters() {
        return originalCharacters;
    }
    public String getVariableName() {
        return variableName;
    }
    public String getReplacement() {
        return replacement;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "<VARIABLE> " + super.toString() + ",orig=" + originalCharacters + ",var=" + variableName + ",repl=(" + replacement + ")";
    }
}
