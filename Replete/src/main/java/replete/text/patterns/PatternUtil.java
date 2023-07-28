package replete.text.patterns;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import replete.collections.Pair;
import replete.errors.UnicornException;
import replete.text.StringUtil;

public class PatternUtil {


    ////////////
    // FIELDS //
    ////////////

    public static final String CASE_SENSITIVE_ON  = "+";
    public static final String CASE_SENSITIVE_OFF = "-";
    public static final String WHOLE_MATCH_ON     = "=";
    public static final String WHOLE_MATCH_OFF    = "~";
    public static final String DELIM_DIVIDER      = "/";

    private static final String OUTER_PATTERN = "^<:(.*):>.*$";
    private static final String INNER_PATTERN = createInnerPattern();


    //////////
    // CORE //
    //////////

    // Handles the standard * and ? characters in most wildcard
    // implementations.  Also, basic wildcard implementations do
    // assume the anchoring at the start and end of lines.
    // Currently only used by PatternUtil but public in case others
    // want to use it as well.
    public static String convertWildcardsToRegex(String patternWildcards) {
        if(patternWildcards.isEmpty()) {
            return "";       // Cleaner than "\Q\E"
        }

        StringBuilder buffer = new StringBuilder();
        boolean inLiteral;
        if(patternWildcards.startsWith("*") || patternWildcards.startsWith("?")) {
            inLiteral = false;
        } else {
            buffer.append("\\Q");
            inLiteral = true;
        }

        for(int i = 0; i < patternWildcards.length(); i++) {
            char ch = patternWildcards.charAt(i);
            if(ch == '*') {
                if(inLiteral) {
                    buffer.append("\\E");
                }
                buffer.append(".*");
                inLiteral = false;
            } else if(ch == '?') {
                if(inLiteral) {
                    buffer.append("\\E");
                }
                buffer.append(".");
                inLiteral = false;
            } else {
                if(!inLiteral) {
                    buffer.append("\\Q");
                }
                buffer.append(ch);
                inLiteral = true;
            }
        }

        if(inLiteral) {
            buffer.append("\\E");
        }

        return buffer.toString();
    }

    // Parses strings like "<:LT:>hi there" and "<:HRL/.:>data file"
    // Should only be used on target strings that the developer knows
    // have a semi-limited syntax range.  For example, when asking the
    // user for a MIME type, one knows their inputs will always be
    // in the format "aaa/bbb".  Thus, the presence of a really weird
    // string on the front like "<:R:>" will never be incorrectly
    // assumed to NOT be apart of the string (it would be a correct
    // assumption that it is not a part of the string).  However,
    // executing this on any general String object, like if that
    // String contains the contents of any arbitrary web page, comes
    // with it some risks that the "special" pattern will appear
    // but it has nothing to do with this technique.
    // One design choice that is really just a coin-flip is that
    // this method does indeed throw exceptions if the identified
    // prefix tag has an invalid format (e.g. "<:@#$:>derek").  One
    // could make the argument that the method should just return the
    // entire string back as normal if it doesn't conform to the
    // exact set of allowable patterns.  There are pros to that approach
    // and the cons are that first, it would be a silent error and users
    // truly using the functionality wouldn't necessarily know their
    // pattern was bad and secondly, it would create an inconsistency
    // between properly formatted strings of this type and just
    // slightly mal-formed strings of this type.  The decision to
    // throw exceptions has the con that exceptions could be thrown
    // in potentially non-exceptional situations where a pattern was found
    // in a string but it has nothing to do with this method.  We can
    // mitigate that by ensuring this method only gets called in those
    // circumstances in which the developer is reasonably certain that
    // the special prefix tag would almost never conceivably appear
    // on the front of the string under normal circumstances, unless
    // the user was attempting to specify a pattern interpretation.
    // If this becomes a problem, perhaps the chosen prefix tag
    // (i.e. "<:something:>string") needs to be changed.  However,
    // If the string even starts with 1 space, then the string is
    // assumed to have nothing to do with pattern interpretation.
    // So, this justification itself can seem somewhat inconsistent.
    // In other words the string " <:-R:>dog.*cat" will not throw
    // an exception.  THERE IS NO EASY ANSWER.  Just have to choose
    // one of two differently suboptimal strategies.
    // Default interpretations passed in CAN have a null
    // case sensitive, whole match or hier delim, but the returned
    // object WILL NOT have a null case sensitive or whole match
    // (possibly a null hier delim though depending on the type).
    // This method returns the non-tag portion AS IS WITHOUT converting
    // it to a regular expression yet.  Didn't want to overload the
    // purpose of this meaning, though that might increase the
    // complexity some.  This is done this way in case the user
    // interface needs to know or display the user's original string
    // and they just want the pattern interpretation tag removed
    // correctly from the front.  You MUST CALL
    //   convertToRegex(String pattern, PatternInterpretation interp)
    // To change the pattern into a regular expression based on the
    // interpretation.
    public static Pair<String, PatternInterpretation> parsePatternInterpretationTag(
             String patternWithTag, PatternInterpretation defaultInterp) {

        // Forcing the developer to consciously create a default
        // interpretation before calling this method is quick
        // and serves the purpose of forcing him/her to think
        // about how their pattern will really be used.  In practice
        // this is to support the "D" type which allows someone
        // to simply change the case sensitivity or whole match
        // properties without having to know what the default
        // interpretation is. It should be OK to force the developer
        // to pass in a non-null default interpretation because well,
        // they are going to interpret the pattern in some way, correct?
        // This just forces them to explicitly encode that logic
        // in their code.
        if(defaultInterp == null) {
            throw new PatternInterpretationException("no default interpretation provided");   // Developer must provide
        }
        if(defaultInterp.getType() == null) {
            throw new PatternInterpretationException("misconfigured default interpretation"); // Developer must provide
        }

        // If the string is null, then simply return the default
        // interpretation as this method is trying to be as least
        // invasive as possible with regards to the string itself.
        if(patternWithTag == null) {
            return new Pair<>(patternWithTag, defaultInterp);
        }

        // If the beginning of the string does not have the special
        // pattern interpretation indicator, simply return the
        // target as is along with whatever default interpretation
        // was provided.
        if(!StringUtil.matches(patternWithTag, OUTER_PATTERN, false)) {   // If it starts with <:anything:> enter this branch
            return new Pair<>(patternWithTag, defaultInterp);
        }

        // Grab the inside of the special tag.
        int c1 = patternWithTag.indexOf(":");
        int c2 = patternWithTag.indexOf(":>", c1 + 1);    // Takes care of any extra :> that may have matched ("<:aa:>bb:>")
        String inner = patternWithTag.substring(c1 + 1, c2);

        // If the inside does not match the specified pattern, throw
        // an exception.
        if(!StringUtil.matches(inner, INNER_PATTERN, false)) {
            throw new PatternInterpretationException(
                "Invalid pattern interpretation format. Must match <:[-+]?[~=]?(" +
                PatternInterpretationType.createPipedList() + ")(/.*)?:>");
        }

        // Check case-sensitive flag
        Boolean caseSensitive;
        if(inner.startsWith(CASE_SENSITIVE_ON) || inner.startsWith(CASE_SENSITIVE_OFF)) {
            caseSensitive = inner.startsWith(CASE_SENSITIVE_ON);
            inner = inner.substring(1);
        } else {
            caseSensitive = null;   // Signifies to fill in with type's default after type found
        }

        // Check whole-match flag
        Boolean wholeMatch;
        if(inner.startsWith(WHOLE_MATCH_ON) || inner.startsWith(WHOLE_MATCH_OFF)) {
            wholeMatch = inner.startsWith(WHOLE_MATCH_ON);
            inner = inner.substring(1);
        } else {
            wholeMatch = null;      // Signifies to fill in with default after type found
        }

        // Check hierarchical delimiter
        int sl = inner.indexOf(DELIM_DIVIDER);
        String hDelim;
        if(sl != -1) {
            hDelim = inner.substring(sl + 1);
            if(hDelim.isEmpty()) {             // e.g. "<:HLR/:>string" (no delimiter after /)
                throw new PatternInterpretationException("empty delimiter provided");
            }
            inner = inner.substring(0, sl);
        } else {
            hDelim = null;          // Signifies to fill in with default after type found
        }

        // Find the corresponding type enum value
        PatternInterpretationType type = null;
        if(inner.equalsIgnoreCase(PatternInterpretationType.DEFAULT_SHORT_LABEL)) {
            type = defaultInterp.getType();
        } else {
            for(PatternInterpretationType t : PatternInterpretationType.values()) {
                if(t.getShortLabel().equalsIgnoreCase(inner)) {
                    type = t;
                }
            }
        }

        // Perform a consistency check, thought this should not be
        // possible (regex match happened earlier).
        if(type == null) {
            throw new UnicornException("did not find pattern interpretation");
        }

        // Ensure hierarchical delimiter is not accidentally used with wrong type.
        boolean isHier =
            type == PatternInterpretationType.HIER_LEFT_TO_RIGHT ||
            type == PatternInterpretationType.HIER_RIGHT_TO_LEFT;
        if(!inner.equalsIgnoreCase(PatternInterpretationType.DEFAULT_SHORT_LABEL) &&
                !StringUtil.isBlank(hDelim) &&
                !isHier) {
            throw new PatternInterpretationException(
                "cannot supply a delimiter for non-hierarchical interpretations");
        }

        // Defaults...

        // If the user didn't express a preference for case sensitivity in the tag...
        if(caseSensitive == null) {

            // If the default interpretation expresses a preference, then use that one.
            if(defaultInterp.isCaseSensitive() != null) {
                caseSensitive = defaultInterp.isCaseSensitive();
            }

            // If there still is no preference, use the type's base preference instead.
            if(caseSensitive == null) {
                caseSensitive = type.isDefaultCaseSensitive();
            }
        }

        // If the user didn't express a preference for whole match in the tag...
        if(wholeMatch == null) {

            // If the default interpretation expresses a preference, then use that one.
            if(defaultInterp.isWholeMatch() != null) {
                wholeMatch = defaultInterp.isWholeMatch();
            }

            // If there still is no preference, use the type's base preference instead.
            if(wholeMatch == null) {
                wholeMatch = type.isDefaultWholeMatch();
            }
        }

        // If we're using a hierarchical type...
        if(isHier) {

            // If the user didn't express a preference for the hierarchical delimiter...
            if(hDelim == null) {

                // If the default interpretation expresses a preference, then use that one.
                if(defaultInterp.getHierarchicalDelim() != null) {
                    hDelim = defaultInterp.getHierarchicalDelim();
                }

                // If there still is no preference, use the type's base preference instead.
                if(hDelim == null) {
                    hDelim = type.getDefaultHierDelimiter();
                }
            }
        }

        // Cut off the special prefix part
        String patternAny = patternWithTag.substring(c2 + 2);

        // Build and return new pattern interpretation
        PatternInterpretation foundInterp = new PatternInterpretation()
            .setType(type)
            .setCaseSensitive(caseSensitive)
            .setWholeMatch(wholeMatch)
            .setHierarchicalDelim(hDelim)
        ;
        return new Pair<>(patternAny, foundInterp);
    }

    public static String convertToRegex(String patternAny, PatternInterpretation interp) {
        return convertToRegex(patternAny, interp, false);
    }

    public static String convertToRegex(String patternAny, PatternInterpretation interp, boolean findMode) {
        PatternInterpretationType type = interp.getType();
        boolean wholeMatch = interp.isWholeMatch();

        if(type == null) {
            throw new PatternInterpretationException("no interpretation provided");

        // Interpret the pattern to be a literal.
        } else if(type == PatternInterpretationType.LITERAL) {

            patternAny = checkAddGroup(Pattern.quote(patternAny), findMode, false);

            // For whole match, quote the entire pattern as is
            // (removing the special RE meaning of all characters
            // like * and ?) and add the anchors.
            if(wholeMatch) {
                return "^" + patternAny + "$";
            }

            // For non-whole match quote the entire pattern as is
            // (removing the special RE meaning of all characters
            // like * and ?), allow for any characters to appear
            // on the ends.
            if(!findMode) {
                return ".*" + patternAny + ".*";
            }

            return patternAny;

        // Interpret the pattern to be a regular expression.
        } else if(type == PatternInterpretationType.REGEX) {

            patternAny = checkAddGroup(patternAny, findMode, true);

            // For whole match, just make sure the start and
            // end of the pattern have the anchors.  Makes no
            // attempt, however, to strip any leading or
            // trailing .* from the users initial pattern as
            // that would erroneously change the meaning of
            // their pattern.
            if(wholeMatch) {
                patternAny = StringUtil.ensureStartsWith(patternAny, "^");
                patternAny = StringUtil.ensureEndsWith(patternAny, "$");
                return patternAny;
            }

            if(!findMode) {
                if(!patternAny.startsWith("^")) {
                    patternAny = StringUtil.ensureStartsWith(patternAny, ".*");
                }
                if(!patternAny.endsWith("$")) {
                    patternAny = StringUtil.ensureEndsWith(patternAny, ".*");
                }
            }

            return patternAny;

        // Interpret the pattern to be a pattern using wildcards.
        // Replete's wildcards only support * (.* in regex) and
        // ? (. in regex).
        } else if(type == PatternInterpretationType.WILDCARDS) {

            patternAny = checkAddGroup(convertWildcardsToRegex(patternAny), findMode, false);
            // ^false here because ^ and $ are not special characters in wildcards.

            if(wholeMatch) {
                return "^" + patternAny + "$";
            }

            if(!findMode) {
                patternAny = StringUtil.ensureStartsWith(patternAny, ".*");
                patternAny = StringUtil.ensureEndsWith(patternAny, ".*");
            }

            return patternAny;

        // Interpret the pattern to be a left-to-right hierarchical
        // pattern (good for IP addresses and file paths, for example).
        // This type uses wildcards for the base interpretation.
        // Of note: ^ and $ within the pattern are not interpreted as
        // special characters.
        } else if(type == PatternInterpretationType.HIER_LEFT_TO_RIGHT) {

            String quotedDelim = Pattern.quote(interp.getHierarchicalDelim());
            patternAny =
                convertWildcardsToRegex(patternAny) +
                "(?:" + quotedDelim + "[^" + quotedDelim + "]+)*$";
            patternAny = checkAddGroup(patternAny, findMode, true);

            if(wholeMatch) {
                return "^" + patternAny;
            }

            if(!findMode) {
                patternAny = StringUtil.ensureStartsWith(patternAny, ".*");
            }

            return patternAny;

        // Interpret the pattern to be a right-to-left hierarchical
        // pattern (good for domain names, for example).
        // This type uses wildcards for the base interpretation.
        // Of note: ^ and $ within the pattern are not interpreted as
        // special characters.
        } else if(type == PatternInterpretationType.HIER_RIGHT_TO_LEFT) {

            String quotedDelim = Pattern.quote(interp.getHierarchicalDelim());
            patternAny =
                "^(?:[^" + quotedDelim + "]+" + quotedDelim + ")*" +
                convertWildcardsToRegex(patternAny);
            patternAny = checkAddGroup(patternAny, findMode, true);

            if(wholeMatch) {
                return patternAny + "$";
            }

            if(!findMode) {
                patternAny = StringUtil.ensureEndsWith(patternAny, ".*");
            }

            return patternAny;
        }

        throw new UnicornException("Unexpected pattern interpretation type.");
    }

    public static Pattern createPattern(String patternRegex, PatternInterpretation interp, boolean dotAll) {
        return Pattern.compile(
            patternRegex,
            (interp.isCaseSensitive() ? 0 : Pattern.CASE_INSENSITIVE) +
            (!dotAll ? 0 : Pattern.DOTALL)
        );
    }


    ////////////
    // HELPER //
    ////////////

    private static String createInnerPattern() {
        String values = PatternInterpretationType.createPipedList();
        String csOff = Pattern.quote(CASE_SENSITIVE_OFF);
        String csOn  = Pattern.quote(CASE_SENSITIVE_ON);
        String wmOff = Pattern.quote(WHOLE_MATCH_OFF);
        String wmOn  = Pattern.quote(WHOLE_MATCH_ON);
        String dd    = Pattern.quote(DELIM_DIVIDER);
        return String.format("(%s|%s)?(%s|%s)?(%s)(%s.*)?", csOn, csOff, wmOn, wmOff, values, dd);
    }

    private static String checkAddGroup(String pattern, boolean addGroup, boolean checkRegexAnchors) {
        if(addGroup) {
            if(checkRegexAnchors) {
                if(pattern.startsWith("^")) {
                    pattern = "^(" + pattern.substring(1);
                } else {
                    pattern = "(" + pattern;
                }
                if(pattern.endsWith("$")) {
                    pattern = pattern.substring(0, pattern.length() - 1) + ")$";
                } else {
                    pattern = pattern + ")";
                }
                return pattern;
            }
            return "(" + pattern + ")";
        }
        return pattern;
    }


    //////////
    // MISC //
    //////////

    public static boolean isValidRegex(String regex) {
        try {
            Pattern.compile(regex);
            return true;
        } catch(PatternSyntaxException exception) {
            return false;
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        System.out.println(isValidRegex("dog"));
        System.out.println(isValidRegex("dog["));
//        System.out.println("".matches("h"));
        if(true) {
            return;
        }

        String input = "<:re:>derek";
        PatternInterpretation defaultInterp = null;
        Pair<String, PatternInterpretation> result = parsePatternInterpretationTag(input, defaultInterp);
        String newTarget = result.getValue1();
        PatternInterpretation interp = result.getValue2();
        System.out.println(newTarget + " " + interp);
    }
}
