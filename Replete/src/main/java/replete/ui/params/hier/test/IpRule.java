package replete.ui.params.hier.test;

import replete.collections.Pair;
import replete.text.StringUtil;
import replete.text.patterns.PatternInterpretation;
import replete.text.patterns.PatternUtil;
import replete.web.UrlHostInfo;
import replete.web.UrlHostType;

public class IpRule extends Rule {


    ////////////
    // FIELDS //
    ////////////

    public String patternRegex;
    private PatternInterpretation interp;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public IpRule(String patternWithTag) {
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
        if(info.getType() != UrlHostType.IP) {
            return false;
        }
        String ip = info.getIp();
        return StringUtil.matchesIgnoreCase(ip, patternRegex);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        IpRule rule = new IpRule("127.4");
//        System.out.println(rule.appliesTo("http://a.b.blah.COM"));
        System.out.println(rule.appliesTo("http://127.44.1.1/"));
    }
}
