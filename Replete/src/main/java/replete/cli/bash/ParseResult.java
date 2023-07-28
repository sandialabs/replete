package replete.cli.bash;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParseResult {


    ////////////
    // FIELDS //
    ////////////

    private String originalLine;
    private ParseState state;
    private String currentArg;
    private String freeArg;
    private String quoteArg;
    private String[] arguments;
    private boolean complete;
    private ArgumentRangeMap topLevelArgumentRanges;
    private Map<String, String[]> topLevelArgumentMap;       // TODO: Not being fully populated yet


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ParseResult(
            String originalLine,
            ParseState state,
            String currentArg,
            String freeArg,
            String quoteArg,
            String[] arguments,
            boolean complete,
            ArgumentRangeMap topLevelArgumentRanges) {

        this.originalLine = originalLine;
        this.state = state;
        this.currentArg = currentArg;
        this.freeArg = freeArg;
        this.quoteArg = quoteArg;
        this.arguments = arguments;
        this.complete = complete;
        this.topLevelArgumentRanges = topLevelArgumentRanges;

        // Temporarily populate topLevelArgumentMap with keys
        topLevelArgumentMap = new LinkedHashMap<String, String[]>();
        for(Integer index : topLevelArgumentRanges.keySet()) {
            ArgumentRange range = topLevelArgumentRanges.get(index);
            if(range.getEndNonIncl() != -1) {
                topLevelArgumentMap.put(originalLine.substring(range.getStart(), range.getEndNonIncl()), null);
            }
        }
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getOriginalLine() {
        return originalLine;
    }
    public ParseState getState() {
        return state;
    }
    public String getCurrentArgument() {
        return currentArg;
    }
    public String getFreeArgument() {
        return freeArg;
    }
    public String getQuoteArgument() {
        return quoteArg;
    }
    public String[] getArguments() {
        return arguments;
    }
    public boolean isComplete() {
        return complete;
    }
    public ArgumentRangeMap getTopLevelArgumentRanges() {
        return topLevelArgumentRanges;
    }
    public Map<String, String[]> getTopLevelArgumentMap() {
        return topLevelArgumentMap;
    }

    // Derived

    public String getArgumentPrefix() {
        String ca = currentArg == null ? "" : currentArg;
        String qa = quoteArg == null ? "" : quoteArg;
        return ca + qa;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        String result =
            "originalLine = " + originalLine + "\n" +
            "state = " + state + "\n" +
            "currentArg = " + currentArg + "\n" +
            "freeArg = " + freeArg + "\n" +
            "quoteArg = " + quoteArg + "\n" +
            "arguments = " + Arrays.toString(arguments) + "\n" +
            "complete = " + complete + "\n" +
            "topLevelArgumentRanges = " + topLevelArgumentRanges + "\n" +
            "topLevelArgumentMap = " + topLevelArgumentMap + "\n";
        return result;
    }
}
