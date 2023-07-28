package replete.cli.bash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import replete.cli.ConsoleUtil;
import replete.text.LiteralTextSegment;
import replete.text.ReplacementResult;
import replete.text.StringUtil;
import replete.text.TextReplacementMap;
import replete.text.TextReplacementMap.NullValueBehavior;
import replete.text.TextReplacementMap.ReplacementPattern;
import replete.text.TextReplacementMap.UnknownVariableBehavior;
import replete.text.TextSegment;
import replete.text.VariableTextSegment;

// TODO:
//   Potentially could implement
//   * ; functionality
//   * ` ` functionality
//   * \$ escaping
//   * # functionality
// Might be nice to also keep track of the non-resolved argument
//   so if line is "test what$AB", then
//   argument might be "whatDMT" after parse, but might be nice
//   to also have the $X not resolved as well in a parallel
//   variable.

// Next steps:
// TODO: populate the topLevelArgMap, do testing, and then the parser will be
//       have almost zero information loss towards client code
// TODO: continue the junit tests
// TODO: review getDefaultAutoCompletePaths logic
// TODO: figure out why "cs ../." works the way it does
// TODO: figure out: ls "./Realm0-JSONLit   =TAB=>    ls "./Realm0-JSONLiteral"/
// TODO: parse existing previous arguments and had them into getAutoCompleteState
//       so the matching tokens can be dependent on the command line thus far
// TODO: we were in the middle of trying to provide parsed command line parameters
//       to commands, but wanted to get all of the argument-location information
//       out of the BashCommandLineParser so that we can determine which arguments are
//       "done" on the command line, and thus which ones to send into the
//       CommandLineParser, and which argument the cursor currently resides.  This will
//       also enable some day the feature of auto complete working when the cursor isn't
//       necessarily at the end of the line, but rather in the middle of the line within
//       any argument.

public class BashCommandLineParser {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    public static final char DBLQ = '"';
    public static final char SNGQ = '\'';
    public static final char SPC = ' ';
    public static final char BKSL = '\\';

    // Parser state

    private String originalLine;
    private ParseState state;
    private String currentArg;
    private String freeArg;
    private String quoteArg;
    private List<String> arguments;
    private ArgumentRangeMap topLevelArgumentRanges;

     // Internal state only
    private int topLevelArgCount;
    private int linePos = -1;

    // Environment

    private Map<String, String> env;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public BashCommandLineParser() {
        this(null);
    }
    public BashCommandLineParser(Map<String, String> env) {
        this.env = env;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public Map<String, String> getEnvironment() {
        if(env == null) {
            setEnvironment(new HashMap<String, String>());
        }
        return env;
    }
    public void setEnvironment(Map<String, String> env) {
        this.env = env;
    }


    //////////
    // INIT //
    //////////

    private void init(String line) {
        originalLine = line;
        state = ParseState.SPACE;
        arguments = new ArrayList<>();
        topLevelArgCount = 0;
        currentArg = null;
        freeArg = null;
        quoteArg = null;
        topLevelArgumentRanges = new ArgumentRangeMap();

        argOff();
    }


    ///////////
    // PARSE //
    ///////////

    public String[] parse(String originalLine) {
        ParseResult result = parseWithResult(originalLine);
        if(!result.isComplete()) {
            error("Unterminated quote");
        }
        return result.getArguments();
    }

    // Up to client app to decide whether or not command line
    // completion should take place depending on whether or
    // not the cursor followed space.  This parser will
    // return a ParseState of DONE in both the case
    // "test abc" and "test abc ".
    public ParseResult parseWithResult(String originalLine) {
        parseLineCharacters(originalLine);

        boolean complete =
            state == ParseState.FREE ||
            state == ParseState.SPACE;

        return new ParseResult(
            originalLine, state,
            currentArg, freeArg, quoteArg,
            arguments.toArray(new String[0]),
            complete,
            topLevelArgumentRanges
        );
    }

    private void parseLineCharacters(String originalLine) {

        init(originalLine);   // Reset parsing variables

        for(linePos = 0; linePos < originalLine.length(); linePos++) {
            char ch = originalLine.charAt(linePos);

            switch(state) {

                case SPACE:        // Parser starts out in the SPACE state

                    // Still inside an argument-delineating space.
                    if(Character.isWhitespace(ch)) {
                        // Do nothing

                    // Transition to quoted string.
                    } else if(ch == DBLQ) {
                        endSpace();
                        startQuotes(ParseState.DOUBLE_QUOTES);

                    // Transition to quoted string.
                    } else if(ch == SNGQ) {
                        endSpace();
                        startQuotes(ParseState.SINGLE_QUOTES);

                    // Else we're starting a free token.
                    } else {
                        endSpace();
                        startFree();

                        // Add escaped character.
                        if(checkAddEscaped(ch, false)) {

                        } else {
                            addFreeCharacter(ch);
                        }
                    }

                    break;

                case FREE:

                    // Hit delineating space, append argument.
                    if(Character.isWhitespace(ch)) {
                        endFree();
                        endArgument(false);
                        startSpace();

                    // Transition to quoted string.
                    } else if(ch == DBLQ) {
                        endFree();
                        startQuotes(ParseState.DOUBLE_QUOTES);

                    // Transition to quoted string.
                    } else if(ch == SNGQ) {
                        endFree();
                        startQuotes(ParseState.SINGLE_QUOTES);

                    } else {

                        // Add escaped character.
                        if(checkAddEscaped(ch, false)) {

                        } else {
                            addFreeCharacter(ch);
                        }
                    }
                    break;

                case DOUBLE_QUOTES:

                    // Close the double quotes.
                    if(ch == DBLQ) {
                        endQuotes(true);
                        startFree();

                    } else {

                        // Add escaped character.
                        if(checkAddEscaped(ch, true)) {

                        } else {
                            addQuoteCharacter(ch);
                        }
                    }

                    break;

                case SINGLE_QUOTES:

                    // Close the single quotes.
                    if(ch == SNGQ) {
                        endQuotes(false);
                        startFree();

                    } else {

                        // Add escaped character.
                        if(checkAddEscaped(ch, true)) {

                        } else {
                            addQuoteCharacter(ch);
                        }
                    }

                    break;
            }
        }

        if(state == ParseState.FREE) {
            endFree();
            endArgument(true, false);
        }
    }

    // Free state methods

    private void startFree() {
        state = ParseState.FREE;
        freeArg = "";
    }
    private void addFreeCharacter(char ch) {
        if(freeArg == null) {
            freeArg = "";
        }
        freeArg += ch;
    }
    private void endFree() {
        if(freeArg == null) {
            return;
        }

        // Any spaces inside the free arg (but not within any
        // ${variable names}) at this point are BINDING SPACES,
        // never to be treated as argument delimiters ever again.
        // The variables when replace can insert non-binding,
        // delimiting spaces.  This is why we need support
        // from the TextReplacementMap and the sophisticated
        // constructFreeParts method.  In other words, in
        // no case does a simple split("\\s+") work to
        // implement this specific feature.

        ReplacementResult result = replaceEnvironment(freeArg);
        List<String> freeParts = constructFreeParts(result);

        for(int p = 0; p < freeParts.size(); p++) {
            freeArg = freeParts.get(p);
            argOn();
            currentArg += freeArg;           // Only 1 level of env variable replacement
            if(p < freeParts.size() - 1) {
                endArgument(true);
                startSpace();
                startFree();
                // TODO: Populate topLevelArgMap
            }
        }
    }

    // Quote methods

    private List<String> constructFreeParts(ReplacementResult result) {
        List<String> parts = new ArrayList<>();
        String part = "";
        for(TextSegment segment : result.getSegments()) {
            if(segment instanceof LiteralTextSegment) {
                part += ((LiteralTextSegment) segment).getText();
            } else {
                String repl = ((VariableTextSegment) segment).getReplacement();
                boolean prevWasWhiteSpace = false;
                for(int r = 0; r < repl.length(); r++) {
                   char ch = repl.charAt(r);
                   if(Character.isWhitespace(ch)) {
                       if(!prevWasWhiteSpace) {
                           parts.add(part);
                           part = "";
                           prevWasWhiteSpace = true;
                       }
                   } else {
                       part += ch;
                       prevWasWhiteSpace = false;
                   }
                }
            }
        }
        if(!part.equals("")) {
            parts.add(part);
        }
        return parts;
    }
    private void startQuotes(ParseState quoteState) {
        state = quoteState;
        quoteArg = "";
        freeArg = null;
    }
    private void addQuoteCharacter(char ch) {
        quoteArg += ch;
    }
    private void endQuotes(boolean useEnv) {
        argOn();
        if(useEnv) {
            ReplacementResult result = replaceEnvironment(quoteArg);
            quoteArg = result.getResultText();
        }
        currentArg += quoteArg;    // Replacements are generally done here for "": $ and `
        quoteArg = null;
    }

    // Space methods

    private void startSpace() {
        state = ParseState.SPACE;
        freeArg = null;
        quoteArg = null;
    }
    private void endSpace() {
        ArgumentRange range = new ArgumentRange(linePos);
        topLevelArgumentRanges.put(topLevelArgCount, range);
    }

    // Argument methods

    private void endArgument(boolean insideExpansion) {
        endArgument(false, insideExpansion);
    }
    private void endArgument(boolean noArgOff, boolean insideExpansion) {
        if(currentArg != null) {
            if(!insideExpansion) {
                ArgumentRange range = topLevelArgumentRanges.get(topLevelArgCount++);
                range.setEndNonIncl(linePos);
            }
            arguments.add(currentArg);
            if(!noArgOff) {
                argOff();
            }
        }
    }
    private void argOn() {
        if(currentArg == null) {
            currentArg = "";
        }
    }
    private void argOff() {
        currentArg = null;
    }

    // Escape character methods

    private boolean checkAddEscaped(char ch, boolean useQuoteArg) {
        if(ch == '\\') {
            if(linePos == originalLine.length() - 1) {
                error("Invalid escape sequence");
            }
            char nch = originalLine.charAt(linePos + 1);

            if(state.getEscapeChars().indexOf(nch) != -1) {
                if(useQuoteArg) {
                    quoteArg += nch;
                } else {
                    addFreeCharacter(nch);
                }
                linePos++;
                return true;
            } else {
                // other escape characters?   bash
                //error("Invalid escape sequence");
            }
            return false;
        }
        return false;
    }

    // Other

    private void error(String why) {
        throw new BashCommandLineParseException("Error parsing: " + why);
    }

    private ReplacementResult replaceEnvironment(String str) {
        TextReplacementMap m;
        if(env == null) {
            m = new TextReplacementMap();
        } else {
            m = new TextReplacementMap(env);
        }
        m.setReplacementPattern(ReplacementPattern.BOTH);
        m.setUnknownBehavior(UnknownVariableBehavior.REMOVE);
        m.setNullValueBehavior(NullValueBehavior.INSERT_BLANK);
        return m.replaceWithResult(str);
    }


    //////////
    // TEST //
    //////////

    private static Map<String, String> envMap = new HashMap<>();
    private static BashCommandLineParser parser = new BashCommandLineParser(envMap);
    public static void main(String[] args) throws Exception {
        envMap.put("A", "derek\\ trumbo");
        System.out.println(parser.parseWithResult("test hi\\ $A"));
        if(true) {
            return;
        }
        parser.setEnvironment(null);
        runConsole();
    }

    private static void runConsole() {
        String cmd = "";
        while(!cmd.equals("quit")) {
            System.out.print("$ ");
            String line = ConsoleUtil.getLine();
            try {
                ParseResult result = parser.parseWithResult(line);
                String[] lineArgs = result.getArguments();
                System.out.println("PARSE RESULT:");
                for(int i = 0; i < lineArgs.length; i++) {
                    System.out.println("    " + i + ": <" + lineArgs[i] + ">");
                }
                System.out.println("    originalLine=[" + result.getOriginalLine() + "] LEN=" + result.getOriginalLine().length());
                System.out.println("    complete=[" + result.isComplete() + "]");
                System.out.println("    state=[" + result.getState() + "]");
                System.out.println("    currentArg=[" + result.getCurrentArgument() + "]");
                System.out.println("    quoteArg=[" + result.getQuoteArgument() + "]");
                System.out.println("    freeArg=[" + result.getFreeArgument() + "]");
                System.out.println("    topLevelArgumentRanges=[" + result.getTopLevelArgumentRanges() + "]");
                System.out.println("    topLevelArgumentMap=[" + result.getTopLevelArgumentMap() + "]");
                if(lineArgs.length != 0) {
                    cmd = lineArgs[0];
                    if(cmd.equals("set")) {
                        parser.getEnvironment().put(lineArgs[1], lineArgs[2]);
                    } else if(cmd.equals("echo")) {
                        System.out.println("COMMAND OUTPUT:");
                        System.out.println(StringUtil.join(StringUtil.slice(lineArgs, 1), " "));
                    } else if(cmd.equals("env")) {
                        System.out.println("COMMAND OUTPUT:");
                        System.out.println(parser.getEnvironment());
                    }
                }
            } catch(Exception e) {
                System.out.println("Invalid command line: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void test(String line, boolean complete, ParseState expectedState) {
        ParseResult result = parser.parseWithResult(line);
        System.out.print("Test: line=[" + line + "]");
        if(result.isComplete() != complete) {
            System.out.println(" ...ERROR: COMPLETE expected=" + expectedState + ", actual=" + result.getState());
            throw new RuntimeException("test failed");
        }
        if(result.getState() != expectedState) {
            System.out.println(" ...ERROR: expected=" + expectedState + ", actual=" + result.getState());
            throw new RuntimeException("test failed");
        }
        System.out.println(" ...PASS");
        System.out.println(result);
        for(String arg : result.getArguments()) {
            System.out.println("    <" + arg + ">");
        }
    }

    private static void test(String line, Class<? extends Exception> exception, String msg) {

    }
    private static void test(String line, String... expectedArgs) throws Exception {
        try {
            System.out.print("Test: line=[" + line + "]");
            String[] parts = parser.parse(line);
            ParseResult result = parser.parseWithResult(line);
            if(!result.isComplete()) {
                System.out.println("ERROR!" + result.getState());
                throw new RuntimeException("test failed");
            }
            int p = 0;
            if(expectedArgs.length != parts.length) {
                System.out.println(" ...ERROR: expected=" +
                    Arrays.toString(expectedArgs) + ", actual=" + Arrays.toString(parts));
                System.out.println(StringUtil.join(parts, "\n", "   <", ">"));
                throw new RuntimeException("test failed");
            }
            for(String arg : expectedArgs) {
                if(!arg.equals(parts[p++])) {
                    System.out.println(" ...ERROR: expected=" +
                        Arrays.toString(expectedArgs) + ", actual=" + Arrays.toString(parts));
                    System.out.println(StringUtil.join(parts, "\n", "   <", ">"));
                    throw new RuntimeException("test failed");
                }
            }
            System.out.println(" ...PASS");
            for(String arg : expectedArgs) {
                System.out.println("    <" + arg + ">");
            }
        } catch(Exception e) {
            System.err.println("ERROR with [" + line + "]");
            throw e;
        }
    }
}
