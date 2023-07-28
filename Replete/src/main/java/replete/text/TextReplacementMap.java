package replete.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Derek Trumbo
 */

// This class will do variable replacement on a string.
// "This is a ${var} string".  Now, this class also
// supports placing arbitrary quotes around the replacement
// characters.  This is to support placing double quotes
// around strings in the map.  You can either set a
// default "use quotes when" policy on the map itself,
// or on individual replacement tokens.
// "This is a ${var/A} string" (means use quotes all
// the time, regardless of what type of object is in the
// map for the "var" variable.

// TODO: This class could still use some escaping capability
// so that $ can still appear in the string without being
// modified.

public class TextReplacementMap extends HashMap<String, Object> {


    ////////////
    // FIELDS //
    ////////////

    private char quoteChar = '"';
    private char paramsChar = 0;      // 0 means not active
    private UseQuotesWhen defaultUseQuotesWhen = UseQuotesWhen.NEVER;
    private QuoteEscapeStrategy qStrat = new QuoteEscapeStrategy() {
        public String escapeQuotes(String str) {
            return str;
        }
    };
    private UnknownVariableBehavior unkBehavior = UnknownVariableBehavior.LEAVE;
    private ReplacementPattern replPattern = ReplacementPattern.DOLLAR_BRACES;
    private NullValueBehavior nvBehavior = NullValueBehavior.INSERT_NULL_LITERAL;
    private boolean caseInsensitive = false;


    ///////////////////////
    // INTERFACE / ENUMS //
    ///////////////////////

    public interface QuoteEscapeStrategy {   // Could be changed to a general replacement modifier, but this is fine for now.
        public String escapeQuotes(String str);
    }

    public enum UnknownVariableBehavior {
        LEAVE,
        REMOVE,
        QUESTION_MARK
    }

    public enum NullValueBehavior {
        INSERT_NULL_LITERAL,
        INSERT_BLANK
    }

    public enum ReplacementPattern {
        DOLLAR,
        DOLLAR_BRACES,
        BOTH
    }

    public enum UseQuotesWhen {
        ALWAYS('A'),
        NEVER('N'),
        SOMETIMES('S');

        private char sym;
        private UseQuotesWhen(char s) {
            sym = s;
        }
        public char getSym() {
            return sym;
        }

        private static String regexChars() {
            String chars = "";
            for(UseQuotesWhen uqw : values()) {
                chars += uqw.getSym();
                if(Character.isUpperCase(uqw.getSym())) {
                    chars += Character.toLowerCase(uqw.getSym());
                }
                if(Character.isLowerCase(uqw.getSym())) {
                    chars += Character.toUpperCase(uqw.getSym());
                }
            }
            return chars;
        }

        private static UseQuotesWhen lookup(String sym) {
            if(sym == null) {
                return null;
            }
            char symC = sym.charAt(0);
            if(Character.toUpperCase(symC) ==
                    Character.toUpperCase(ALWAYS.getSym())) {
                return ALWAYS;
            } else if(Character.toUpperCase(symC) ==
                    Character.toUpperCase(NEVER.getSym())) {
                return NEVER;
            }
            return SOMETIMES;
        }
    }


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TextReplacementMap() {}
    public TextReplacementMap(int arg0) {
        super(arg0);
    }
    public TextReplacementMap(Map<? extends String, ? extends Object> arg0) {
        super(arg0);
    }
    public TextReplacementMap(int arg0, float arg1) {
        super(arg0, arg1);
    }

    public TextReplacementMap(boolean caseSensitive) {
        caseInsensitive = !caseSensitive;
    }
    public TextReplacementMap(int arg0, boolean caseSensitive) {
        super(arg0);
        caseInsensitive = !caseSensitive;
    }
    public TextReplacementMap(Map<? extends String, ? extends Object> arg0, boolean caseSensitive) {
        super(arg0);
        caseInsensitive = !caseSensitive;
    }
    public TextReplacementMap(int arg0, float arg1, boolean caseSensitive) {
        super(arg0, arg1);
        caseInsensitive = !caseSensitive;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public char getQuoteChar() {
        return quoteChar;
    }
    public char getParamsChar() {
        return paramsChar;
    }
    public QuoteEscapeStrategy getQuoteEscapeStrategy() {
        return qStrat;
    }
    public UseQuotesWhen getDefaultUseQuotesWhen() {
        return defaultUseQuotesWhen;
    }
    public UnknownVariableBehavior getUnknownVariableBehavior() {
        return unkBehavior;
    }
    public ReplacementPattern getReplacementPattern() {
        return replPattern;
    }
    public NullValueBehavior getNullValueBehavior() {
        return nvBehavior;
    }
    public boolean isCaseSensitive() {
        return !caseInsensitive;
    }

    // Mutators

    public void setQuoteChar(char quoteChar) {
        this.quoteChar = quoteChar;
    }
    public void setParamsChar(char paramsChar) {
        this.paramsChar = paramsChar;
    }
    public void setQuoteEscapeStrategy(QuoteEscapeStrategy qStrat) {
        this.qStrat = qStrat;
    }
    public void setDefaultUseQuotesWhen(UseQuotesWhen defaultUseQuotesWhen) {
        this.defaultUseQuotesWhen = defaultUseQuotesWhen;
    }
    public void setUnknownBehavior(UnknownVariableBehavior unkBehavior) {
        this.unkBehavior = unkBehavior;
    }
    public void setReplacementPattern(ReplacementPattern replPattern) {
        this.replPattern = replPattern;
    }
    public void setNullValueBehavior(NullValueBehavior nvBehavior) {
        this.nvBehavior = nvBehavior;
    }


    /////////////
    // REPLACE //
    /////////////

    public String replace(String content) {
        ReplacementResult result = replaceWithResult(content);
        return result.getResultText();
    }

    public ReplacementResult replaceWithResult(String content) {
        String uqw = UseQuotesWhen.regexChars();

        // Variables can take one of the following forms:
        //   $variable
        //   ${variable}
        //   ${variable/A}

        // The two patterns' regexes:
        String dlrBrcStr = "\\$\\{([a-zA-Z0-9_ :.-]+)(?:/([" + uqw + "]))?}";
        String dlrStr = "\\$([a-zA-Z0-9_]+)";

        // Choose pattern
        Pattern pattern;
        switch(replPattern) {
            case DOLLAR:
                pattern = Pattern.compile(dlrStr);
                break;
            case DOLLAR_BRACES:
                pattern = Pattern.compile(dlrBrcStr);
                break;
            default:
                pattern = Pattern.compile(dlrBrcStr + "|" + dlrStr);
                break;
        }

        return replacePattern(content, pattern);
    }

    private ReplacementResult replacePattern(String content, Pattern p) {

        // Initialize
        Matcher m = p.matcher(content);
        StringBuilder buf = new StringBuilder();
        int end = -1;
        List<TextSegment> segments = new ArrayList<>();

        // Loop over matched segments
        while(m.find()) {

            // Choose the key
            String key;
            switch(replPattern) {
                case DOLLAR:         key = m.group(1);  break;
                case DOLLAR_BRACES:  key = m.group(1);  break;
                default:
                    String key1 = m.group(1);
                    String key2 = m.group(3);
                    key = key1 == null ? key2 : key1;
                    break;
            }

            String origKey = key;
            String varParams = null;

            if(paramsChar != 0) {
                int i = key.indexOf(paramsChar);
                if(i != -1) {
                    varParams = key.substring(i + 1);
                    key = key.substring(0, i);
                }
            }

            // Modify the key
            if(caseInsensitive) {
                key = key.toLowerCase();
            }

            String replaceSym = m.group(2);
            String repl;

            // If key exists in map, calculate the replacement text.
            if(containsKey(key)) {
                Object value = get(key);
                if(value instanceof Function) {
                    value = ((Function<String, Object>) value).apply(varParams);
                }
                repl = getReplacementChars(UseQuotesWhen.lookup(replaceSym), value);

            // If key does not exist in map, choose appropriate replacement value.
            } else {
                if(unkBehavior == UnknownVariableBehavior.LEAVE) {
                    repl = m.group();
                } else if(unkBehavior == UnknownVariableBehavior.QUESTION_MARK) {
                    repl = "?";
                } else {
                    repl = "";
                }
            }

            // Append a literal text segment for the characters
            // before this matching section.
            String literalText = content.substring(end + 1, m.start());
            if(!literalText.equals("")) {
                LiteralTextSegment litSeg = new LiteralTextSegment(end + 1, m.start(), literalText);
                segments.add(litSeg);
                buf.append(literalText);
            }

            // Append the variable text segment.
            String variableText = content.substring(m.start(), m.end());
            TextSegment varSeg = new VariableTextSegment(m.start(), m.end(), variableText, origKey, repl);
            segments.add(varSeg);
            buf.append(repl);

            end = m.end() - 1;
        }

        // Append any trailing literal text segment.
        String literalText = content.substring(end + 1);
        if(!literalText.equals("")) {
            LiteralTextSegment litSeg = new LiteralTextSegment(end + 1, content.length(), literalText);
            segments.add(litSeg);
            buf.append(literalText);
        }

        return new ReplacementResult(content, buf.toString(), segments.toArray(new TextSegment[0]));
    }

    private String getReplacementChars(UseQuotesWhen when, Object value) {
        String rep;

        if(value == null) {
            if(nvBehavior == NullValueBehavior.INSERT_NULL_LITERAL) {
                rep = "NULL";
            } else {
                rep = "";
            }
        } else {
            rep = value.toString();
        }

        // Choose default if nothing specified in replacement pattern
        if(when == null) {
            when = defaultUseQuotesWhen;
            if(when == null) {
                when = UseQuotesWhen.NEVER;
            }
        }

        // Never
        if(when == UseQuotesWhen.NEVER) {
            // Do nothing

        // Always
        } else if(when == UseQuotesWhen.ALWAYS) {
            rep = quoteChar + qStrat.escapeQuotes(rep) + quoteChar;

        // Sometimes (String only) - for null no type to inspect
        } else if(value != null) {
            Class<?> clazz = value.getClass();
            if(clazz.equals(String.class) || clazz.equals(char.class) || clazz.equals(Character.class)) {
                rep = quoteChar + qStrat.escapeQuotes(rep) + quoteChar;
            }
        }

        return rep;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Object put(String key, Object value) {  // putAll calls this
        if(caseInsensitive) {
            key = key.toLowerCase();
        }
        return super.put(key, value);
    }
    public Object put(String key, Function<String, Object> value) {  // For type identification
        if(caseInsensitive) {
            key = key.toLowerCase();
        }
        return super.put(key, value);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        String rawContent = "test content ${HERE} ok $here${hErE} ${what}";
//        String rawContent = "${here}";
        TextReplacementMap trm = new TextReplacementMap(false);
        trm.setDefaultUseQuotesWhen(UseQuotesWhen.ALWAYS);
        trm.setUnknownBehavior(UnknownVariableBehavior.LEAVE);
        trm.setReplacementPattern(ReplacementPattern.BOTH);
        trm.setNullValueBehavior(NullValueBehavior.INSERT_BLANK);
        trm.setQuoteEscapeStrategy(new QuoteEscapeStrategy() {
            public String escapeQuotes(String str) {
                return str.replaceAll("a", "b");
            }
        });
        trm.setParamsChar(':');
        trm.setQuoteChar('|');
        trm.put("here", "ABC");
        trm.put("what", a -> "x" + a);
        trm.put("xyz", a -> a + "a");
        System.out.println("((" + trm.replace(rawContent) + "))");

        ReplacementResult result = trm.replaceWithResult(rawContent);
        for(TextSegment seg : result.getSegments()) {
            System.out.println(seg);
        }
    }
}
