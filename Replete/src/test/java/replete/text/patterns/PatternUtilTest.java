package replete.text.patterns;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import replete.collections.Pair;
import replete.text.Match;
import replete.text.StringUtil;

public class PatternUtilTest {

    @Test
    public void parsePatternInterpretation() {
        PatternInterpretationType LT  = PatternInterpretationType.LITERAL;
        PatternInterpretationType WC  = PatternInterpretationType.WILDCARDS;
        PatternInterpretationType RE  = PatternInterpretationType.REGEX;
        PatternInterpretationType HLR = PatternInterpretationType.HIER_LEFT_TO_RIGHT;
        PatternInterpretationType HRL = PatternInterpretationType.HIER_RIGHT_TO_LEFT;

        PatternInterpretation D = new PatternInterpretation()
            .setType(RE)
            .setCaseSensitive(true)
            .setWholeMatch(false)
            .setHierarchicalDelim(null)
        ;

        // Default interpretation errors
        testErr("a", null, "no default interpretation provided");
        testErr("a", new PatternInterpretation().setType(null),
            "misconfigured default interpretation");

        // No pattern
        test(null, D, null);
        test("",   D, "");
        test("a",  D, "a");
        test("a",  D, "a", RE, true, false, null);

        // Simple
        test("<:re:>",  D, "",  RE, true, false, null);
        test("<:RE:>",  D, "",  RE, true, false, null);
        test("<:re:>a", D, "a", RE, true, false, null);
        test("<:RE:>a", D, "a", RE, true, false, null);
        test("<:Re:>a", D, "a", RE, true, false, null);

        // All types + More Defaults
        test("<:LT:>a",  D, "a", LT,  true, false, null);
        test("<:WC:>a",  D, "a", WC,  true, false, null);
        test("<:RE:>a",  D, "a", RE,  true, false, null);
        test("<:HLR:>a", D, "a", HLR, true, false, ".");
        test("<:HRL:>a", D, "a", HRL, true, false, ".");

        // Malformed
        String values = PatternInterpretationType.createPipedList();
        String fmtMsg = "Invalid pattern interpretation format. Must match <:[-+]?[~=]?(" + values + ")(/.*)?:>";
        testErr("<::>",   D, fmtMsg);
        testErr("<:i:>",  D, fmtMsg);
        testErr("<:i:>",  D, fmtMsg);
        testErr("<:i:>a", D, fmtMsg);
        testErr("<:i:>a", D, fmtMsg);

        // Malformed (maybe) - clearly trying to use the special
        // prefix tag, but failing to get it right.  We don't throw
        // exceptions in these situations but rather it is treated
        // like a regular string, and the returned interpretation will
        // be the default interpretation provided.
        test(" <:i:>a",  D, " <:i:>a");
        test(" <:re:>a", D, " <:re:>a");
        test(" <:RE:>a", D, " <:RE:>a");
        test("<:re>a",   D, "<:re>a");
        test("<:RE:a",   D, "<:RE:a");

        // Delimiter
        testErr("<:LT/.:>a", D,   "cannot supply a delimiter for non-hierarchical interpretations");
        testErr("<:WC/.:>a", D,   "cannot supply a delimiter for non-hierarchical interpretations");
        testErr("<:RE/.:>a", D,   "cannot supply a delimiter for non-hierarchical interpretations");
        testErr("<:HLR/:>a", D,   "empty delimiter provided");
        testErr("<:HRL/:>a", D,   "empty delimiter provided");
        test("<:HLR/.:>a",  D, "a", HLR, true, false, ".");
        test("<:HRL/.:>a",  D, "a", HRL, true, false, ".");
        test("<:HLR/%:>a",  D, "a", HLR, true, false, "%");
        test("<:HRL/%:>a",  D, "a", HRL, true, false, "%");
        test("<:HLR/::>a",  D, "a", HLR, true, false, ":");
        test("<:HRL/::>a",  D, "a", HRL, true, false, ":");
        test("<:HLR//:>a",  D, "a", HLR, true, false, "/");
        test("<:HRL//:>a",  D, "a", HRL, true, false, "/");
        test("<:HLR/yy:>a", D, "a", HLR, true, false, "yy");
        test("<:HRL/yy:>a", D, "a", HRL, true, false, "yy");
        testErr("<:HLR/:>:>a", D, "empty delimiter provided");
        testErr("<:HRL/:>:>a", D, "empty delimiter provided");
        test("<:D/.:>a", D, "a", RE, true, false, ".");

        // Defaults
        test("<:LT:>a",  D, "a", LT,  true, false,  null);
        test("<:WC:>a",  D, "a", WC,  true, false,  null);
        test("<:RE:>a",  D, "a", RE,  true, false, null);
        test("<:HLR:>a", D, "a", HLR, true, false, ".");
        test("<:HRL:>a", D, "a", HRL, true, false, ".");

        // Other flag/delim changes
        test("<:+LT:>a",  D, "a", LT, true,  false,  null);   // true/true
        test("<:-LT:>a",  D, "a", LT, false, false,  null);
        test("<:=LT:>a",  D, "a", LT, true,  true,  null);
        test("<:~LT:>a",  D, "a", LT, true,  false, null);
        test("<:+=LT:>a", D, "a", LT, true,  true,  null);
        test("<:+~LT:>a", D, "a", LT, true,  false, null);
        test("<:-=LT:>a", D, "a", LT, false, true,  null);
        test("<:-~LT:>a", D, "a", LT, false, false, null);

        test("<:+RE:>a",  D, "a", RE, true,  false, null);   // false/false
        test("<:-RE:>a",  D, "a", RE, false, false, null);
        test("<:=RE:>a",  D, "a", RE, true,  true,  null);
        test("<:~RE:>a",  D, "a", RE, true,  false, null);
        test("<:+=RE:>a", D, "a", RE, true,  true,  null);
        test("<:+~RE:>a", D, "a", RE, true,  false, null);
        test("<:-=RE:>a", D, "a", RE, false, true,  null);
        test("<:-~RE:>a", D, "a", RE, false, false, null);

        test("<:+HLR/y:>a",   D, "a", HLR, true,  false, "y");   // false/false
        test("<:-HLR/yy:>a",  D, "a", HLR, false, false, "yy");
        test("<:=HLR/::>a",   D, "a", HLR, true,  true,  ":");
        test("<:~HLR/ :>a",   D, "a", HLR, true,  false, " ");
        test("<:+=HLR/\":>a", D, "a", HLR, true,  true,  "\"");
        test("<:+~HLR//:>a",  D, "a", HLR, true,  false, "/");
        test("<:-=HLR/\\:>a", D, "a", HLR, false, true,  "\\");
        test("<:-~HLR/_:>a",  D, "a", HLR, false, false, "_");
    }

    private void testErr(String input, PatternInterpretation defaultInterp, String exceptionMessage) {
        try {
            PatternUtil.parsePatternInterpretationTag(input, defaultInterp);
            fail("call should have thrown an exception");
        } catch(Exception e) {
            assertEquals(PatternInterpretationException.class, e.getClass());
            assertEquals(exceptionMessage, e.getMessage());
        }
    }
    private void test(String input, PatternInterpretation defaultInterp, String expectedTarget) {
        test(input, defaultInterp, expectedTarget, defaultInterp);
    }
    private void test(String input, PatternInterpretation defaultInterp,
                      String expectedTarget, PatternInterpretation expectedInterp) {
        Pair<String, PatternInterpretation> result =
            PatternUtil.parsePatternInterpretationTag(input, defaultInterp);
        assertEquals(expectedTarget, result.getValue1());
        PatternInterpretation interp = result.getValue2();
        assertEquals(expectedInterp == null, interp == null);
        if(expectedInterp != null) {
            assertEquals(expectedInterp, interp);   // expectedInterp == interp in our unit tests too
        }
    }
    private void test(String input, PatternInterpretation defaultInterp,
                      String expectedTarget, PatternInterpretationType expectedType,
                      boolean expectedCaseSensitive, boolean expectedWholeMatch,
                      String expectedHDelim) {
        Pair<String, PatternInterpretation> result =
            PatternUtil.parsePatternInterpretationTag(input, defaultInterp);
        assertEquals(expectedTarget, result.getValue1());
        PatternInterpretation interp = result.getValue2();
        assertEquals(expectedType == null, interp == null);
        if(expectedType != null) {
            assertEquals(expectedType, interp.getType());
            assertEquals(expectedCaseSensitive, interp.isCaseSensitive());
            assertEquals(expectedWholeMatch, interp.isWholeMatch());
            assertEquals(expectedHDelim, interp.getHierarchicalDelim());
        }
    }

    // This tests all the paths within PatternUtil.convertToRegex.
    // Shouldn't need to test 1) different patternAny's, 2)
    // the 1 overloaded method.
    @Test
    public void convertToRegex() {
        PatternInterpretationType LT  = PatternInterpretationType.LITERAL;
        PatternInterpretationType RE  = PatternInterpretationType.REGEX;
        PatternInterpretationType WC  = PatternInterpretationType.WILDCARDS;
        PatternInterpretationType HLR = PatternInterpretationType.HIER_LEFT_TO_RIGHT;
        PatternInterpretationType HRL = PatternInterpretationType.HIER_RIGHT_TO_LEFT;

        test("some.*pattern", LT, false, false, ".*\\Qsome.*pattern\\E.*");
        test("some.*pattern", LT, true,  false, "^\\Qsome.*pattern\\E$");
        test("some.*pattern", LT, false, true,  "(\\Qsome.*pattern\\E)");
        test("some.*pattern", LT, true,  true,  "^(\\Qsome.*pattern\\E)$");

        test("some.*pattern",   RE, false, false, ".*some.*pattern.*");
        test("^some.*pattern",  RE, false, false, "^some.*pattern.*");   // Anchor tests
        test("some.*pattern$",  RE, false, false, ".*some.*pattern$");   // Anchor tests
        test("some.*pattern",   RE, true,  false, "^some.*pattern$");
        test("^some.*pattern$", RE, true,  false, "^some.*pattern$");    // Ensure tests
        test("some.*pattern",   RE, false, true,  "(some.*pattern)");
        test("^some.*pattern$", RE, false, true,  "^(some.*pattern)$");  // checkRegexAnchors test
        test("some.*pattern",   RE, true,  true,  "^(some.*pattern)$");
        test("^some.*pattern$", RE, true,  true,  "^(some.*pattern)$");  // checkRegexAnchors test

        test("some*pat?tern",   WC, false, false, ".*\\Qsome\\E.*\\Qpat\\E.\\Qtern\\E.*");
        test("*some*pat?tern",  WC, false, false, ".*\\Qsome\\E.*\\Qpat\\E.\\Qtern\\E.*");    // Ensure tests
        test("some*pat?tern*",  WC, false, false, ".*\\Qsome\\E.*\\Qpat\\E.\\Qtern\\E.*");    // Ensure tests
        test("some*pat?tern",   WC, true,  false, "^\\Qsome\\E.*\\Qpat\\E.\\Qtern\\E$");
        test("some*pat?tern",   WC, false, true,  "(\\Qsome\\E.*\\Qpat\\E.\\Qtern\\E)");
        test("^some*pat?tern$", WC, false, true,  "(\\Q^some\\E.*\\Qpat\\E.\\Qtern$\\E)");    // checkRegexAnchors test
        test("some*pat?tern",   WC, true,  true,  "^(\\Qsome\\E.*\\Qpat\\E.\\Qtern\\E)$");
        test("^some*pat?tern$", WC, true,  true,  "^(\\Q^some\\E.*\\Qpat\\E.\\Qtern$\\E)$");  // checkRegexAnchors test

        // Hierarchical matching could use some more thinking.
        test("6.7.8.9",   HLR, false, false, ".*\\Q6.7.8.9\\E(?:\\Q.\\E[^\\Q.\\E]+)*$");
        test("*6.7.8.9",  HLR, false, false, ".*\\Q6.7.8.9\\E(?:\\Q.\\E[^\\Q.\\E]+)*$");      // Ensure tests
        test("6.7.8.9",   HLR, true,  false, "^\\Q6.7.8.9\\E(?:\\Q.\\E[^\\Q.\\E]+)*$");
        test("6.7.8.9",   HLR, false, true,  "(\\Q6.7.8.9\\E(?:\\Q.\\E[^\\Q.\\E]+)*)$");
        test("^6.7.8.9$", HLR, false, true,  "(\\Q^6.7.8.9$\\E(?:\\Q.\\E[^\\Q.\\E]+)*)$");    // checkRegexAnchors test
        test("6.7.8.9",   HLR, true,  true,  "^(\\Q6.7.8.9\\E(?:\\Q.\\E[^\\Q.\\E]+)*)$");
        test("^6.7.8.9$", HLR, true,  true,  "^(\\Q^6.7.8.9$\\E(?:\\Q.\\E[^\\Q.\\E]+)*)$");   // checkRegexAnchors test

        // Hierarchical matching could use some more thinking.
        test("b.a.com",   HRL, false, false, "^(?:[^\\Q.\\E]+\\Q.\\E)*\\Qb.a.com\\E.*");
        test("b.a.com*",  HRL, false, false, "^(?:[^\\Q.\\E]+\\Q.\\E)*\\Qb.a.com\\E.*");      // Ensure tests
        test("b.a.com",   HRL, true,  false, "^(?:[^\\Q.\\E]+\\Q.\\E)*\\Qb.a.com\\E$");
        test("b.a.com",   HRL, false, true,  "^((?:[^\\Q.\\E]+\\Q.\\E)*\\Qb.a.com\\E)");
        test("^b.a.com$", HRL, false, true,  "^((?:[^\\Q.\\E]+\\Q.\\E)*\\Q^b.a.com$\\E)");    // checkRegexAnchors test
        test("b.a.com",   HRL, true,  true,  "^((?:[^\\Q.\\E]+\\Q.\\E)*\\Qb.a.com\\E)$");
        test("^b.a.com$", HRL, true,  true,  "^((?:[^\\Q.\\E]+\\Q.\\E)*\\Q^b.a.com$\\E)$");   // checkRegexAnchors test
    }

    private void test(String patternAny, PatternInterpretationType type, boolean wholeMatch,
                      boolean findMode, String expectedPatternRegex) {

        PatternInterpretation D = new PatternInterpretation()   // Case-sensitive flag unneeded
            .setType(type)
            .setWholeMatch(wholeMatch)
            .setHierarchicalDelim(".")      // No need to vary this for HLR/HRL types
        ;

        String actualPatternRegex = PatternUtil.convertToRegex(patternAny, D, findMode);

        assertEquals(expectedPatternRegex, actualPatternRegex);
    }

    @Test
    public void convertWildcardsToRegex() {
        test("", "");
        test("a", "\\Qa\\E");
        test("a*", "\\Qa\\E.*");
        test("*a", ".*\\Qa\\E");
        test("*a*", ".*\\Qa\\E.*");
        test("*", ".*");
        test("**?", ".*.*.");
        test("??b", "..\\Qb\\E");
        test("b.x", "\\Qb.x\\E");
    }

    private void test(String patternWildccards, String expectedPatternRegex) {
        String actualPatternRegex = PatternUtil.convertWildcardsToRegex(patternWildccards);
        assertEquals(expectedPatternRegex, actualPatternRegex);
    }

    @Test
    public void matches() {
        PatternInterpretationType RE  = PatternInterpretationType.REGEX;

        PatternInterpretation D = new PatternInterpretation()
            .setType(RE)
            .setCaseSensitive(true)
            .setWholeMatch(false)
            .setHierarchicalDelim("/")
        ;

        test(true,  D, "<:RE:>xx",   "ffxx");
        test(false, D, "<:=RE:>xx",  "ffxx");
        test(false, D, "<:RE:>xx",   "FFXX");
        test(true,  D, "<:-RE:>xx",  "FFXX");
        test(true,  D, "<:=RE:>a*x", "x");
        test(true,  D, "<:=RE:>a*x", "ax");
        test(true,  D, "<:=RE:>a*x", "aax");
        test(false, D, "<:=RE:>a*x", "abx");

        test(false, D, "<:WC:>a*x",  "x");
        test(true,  D, "<:WC:>a*x",  "ax");
        test(true,  D, "<:WC:>a*x",  "abx");
        test(false, D, "<:WC:>a*x",  "bx");
        test(true,  D, "<:WC:>a*x",  "bax");
        test(false, D, "<:=WC:>a*x", "bax");

        test(true,  D, "<:D:>xx",   "ffxx");    // D refers to the type in the default interpretation
        test(false, D, "<:=D:>xx",  "ffxx");    // Used when you want to change the other aspects of
        test(false, D, "<:D:>xx",   "FFXX");    // the interpretation, but don't want to necessarily
        test(true,  D, "<:-D:>xx",  "FFXX");    // respecify whatever the type is (or maybe you don't
        test(true,  D, "<:=D:>a*x", "x");       // know, or know the code for).  Here D is simply a
        test(true,  D, "<:=D:>a*x", "ax");      // synonym for RE *in this case*.  Obviously the
        test(true,  D, "<:=D:>a*x", "aax");     // tag "<:D:>xxx" completely has no effect, because
        test(false, D, "<:=D:>a*x", "abx");     // nothing is being changed from the default.

        test(true,  D, "<:HLR:>a/b", "a/b");
        test(false, D, "<:HLR:>a/b", "a/b/");     // Syntactically invalid as far as the interp. type is concerned.
        test(true,  D, "<:HLR:>a/b", "a/b/c");    // The power of this mode - user doesn't have to choose
        test(true,  D, "<:HLR:>a/b", "a/b/c/d");  // between providing the / or not.

        test(true,  D, "<:HRL:>y/z", "y/z");
        test(false, D, "<:HRL:>y/z", "/y/z");     // Syntactically invalid as far as the interp. type is concerned.
        test(true,  D, "<:HRL:>y/z", "x/y/z");    // The power of this mode - user doesn't have to choose
        test(true,  D, "<:HRL:>y/z", "w/x/y/z");  // between providing the / or not.

        test(true,  D, "<:HRL/.:>y/z", "y/z");
        test(false, D, "<:HRL/.:>y/z", "/y/z");     // Syntactically invalid as far as the interp. type is concerned.
        test(false, D, "<:HRL/.:>y/z", "x/y/z");    // The power of this mode - user doesn't have to choose
        test(false, D, "<:HRL/.:>y/z", "w/x/y/z");  // between providing the / or not.

        test(true,  D, "<:HRL/.:>y.z", "y.z");
        test(false, D, "<:HRL/.:>y.z", ".y.z");     // Syntactically invalid as far as the interp. type is concerned.
        test(true,  D, "<:HRL/.:>y.z", "x.y.z");    // The power of this mode - user doesn't have to choose
        test(true,  D, "<:HRL/.:>y.z", "w.x.y.z");  // between providing the / or not.
    }

    private void test(boolean expectedMatches, PatternInterpretation interp, String patternWithTag, String target) {
        Pair<String, PatternInterpretation> result =
            PatternUtil.parsePatternInterpretationTag(patternWithTag, interp);
        String patternAny = result.getValue1();
        interp = result.getValue2();
        String patternRegex = PatternUtil.convertToRegex(patternAny, interp);
        boolean actualMatches = StringUtil.matches(target, patternRegex, interp);
        assertEquals(expectedMatches, actualMatches);
    }

    @Test
    public void find() {
        PatternInterpretationType RE  = PatternInterpretationType.REGEX;

        PatternInterpretation D = new PatternInterpretation()
            .setType(RE)
            .setCaseSensitive(true)
            .setWholeMatch(false)
            .setHierarchicalDelim(null)
        ;

        test(D, "<:RE:>abc", "abcxyzabc", new Match[] {
            new Match(0, 3, "abc"),
            new Match(6, 9, "abc")
        });
        test(D, "<:=RE:>abc", "abcxyzabc", new Match[] {});
        test(D, "<:WC:>a*c", "abcxyzabc", new Match[] {
            new Match(0, 9, "abcxyzabc"),
        });
        test(D, "<:RE:>a.*c", "abcxyzabc", new Match[] {
            new Match(0, 9, "abcxyzabc"),
        });
        test(D, "<:RE:>a.*?c", "abcxyzabc", new Match[] {     // Reluctant
            new Match(0, 3, "abc"),
            new Match(6, 9, "abc")
        });
        test(D, "<:RE:>ABC", "abcxyzABCxyz", new Match[] {
            new Match(6, 9, "ABC")
        });
        test(D, "<:RE:>a", "abcxyzABCxyzabc", new Match[] {
            new Match(0, 1, "a"),
            new Match(12, 13, "a")
        });
        test(D, "<:-RE:>a", "abcxyzABCxyzabc", new Match[] {
            new Match(0, 1, "a"),
            new Match(6, 7, "A"),
            new Match(12, 13, "a")
        });
        test(D, "<:RE:>c$", "abcxyzABCxyzabc", new Match[] {   // Only last c matches
            new Match(14, 15, "c")
        });
    }

    private void test(PatternInterpretation interp, String patternWithTag, String target, Match[] expectedMatches) {
        List<Match> actualMatches = StringUtil.find(target, patternWithTag, interp);
        assertEquals(expectedMatches.length, actualMatches.size());
        int m = 0;
        for(Match expectedMatch : expectedMatches) {
            Match actualMatch = actualMatches.get(m);
            assertEquals(expectedMatch, actualMatch);
            m++;
        }
    }
}
