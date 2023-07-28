package replete.ui.params.hier.test;

import replete.collections.Pair;
import replete.text.StringUtil;
import replete.text.patterns.PatternInterpretation;
import replete.text.patterns.PatternUtil;
import replete.web.UrlHostInfo;
import replete.web.UrlHostType;

public class HierarchicalHostRule extends Rule {


    ////////////
    // FIELDS //
    ////////////

    public String patternRegex;
    private PatternInterpretation interp;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public HierarchicalHostRule(String patternWithTag) {
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
        UrlHostInfo info = new UrlHostInfo(url);
        if(info.getType() != UrlHostType.HIERARCHY) {
            return false;
        }
        String host = info.getHost();
        return StringUtil.matches(host, patternRegex, interp);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        HierarchicalHostRule rule = new HierarchicalHostRule("*.blah.com");
        System.out.println(rule.appliesTo("http://a.b.blah.COM"));
        System.out.println(rule.appliesTo("http://. .blah.com"));
    }
}
