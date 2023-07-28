package replete.ui.params.hier.test;

import replete.collections.Pair;
import replete.text.StringUtil;
import replete.text.patterns.PatternInterpretation;
import replete.text.patterns.PatternUtil;


public class FullUrlRule extends Rule {


    ////////////
    // FIELDS //
    ////////////

    private String patternRegex;
    private PatternInterpretation interp;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public FullUrlRule(String patternWithTag) {
        Pair<String, PatternInterpretation> result =
            PatternUtil.parsePatternInterpretationTag(patternWithTag, DEFAULT_PATTERN_INTERPRETATION);
        String patternAny = result.getValue1();
        interp = result.getValue2();
        patternRegex = PatternUtil.convertToRegex(patternAny, interp);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public boolean appliesTo(String url) {
        return StringUtil.matches(url, patternRegex, interp);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        FullUrlRule rule = new FullUrlRule("http://www.blah.com*");
        System.out.println(rule.appliesTo("http://www.blah.com"));
        System.out.println(rule.appliesTo("http://www.blah.com/"));
        System.out.println(rule.appliesTo("http://www.blah.com/suchandsuch"));
        System.out.println(rule.appliesTo("blah"));
        System.out.println(rule.appliesTo("http://subdomain.blah.com/suchandsuch"));
        System.out.println(rule.appliesTo("http://www.blah.org/suchandsuch"));
    }
}
