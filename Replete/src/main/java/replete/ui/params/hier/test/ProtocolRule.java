package replete.ui.params.hier.test;

import java.net.URL;

import replete.collections.Pair;
import replete.text.StringUtil;
import replete.text.patterns.PatternInterpretation;
import replete.text.patterns.PatternUtil;
import replete.web.UrlUtil;


public class ProtocolRule extends Rule {


    ////////////
    // FIELDS //
    ////////////

    public String patternRegex;
    private PatternInterpretation interp;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ProtocolRule(String patternWithTag) {
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
    public boolean appliesTo(String urlStr) {
        URL url = UrlUtil.url(urlStr);
        String protocol = url.getProtocol();
        return StringUtil.matches(protocol, patternRegex, interp);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        test("http", "http://www.cnn.com", true);
        test("https", "https://www.cnn.com", true);
        test("http", "https://www.cnn.com", false);
        test("https", "http://www.cnn.com", false);
        test("ftp", "ftp://www.cnn.com", true);
    }

    private static void test(String pattern, String target, boolean applies) {
        ProtocolRule pr = new ProtocolRule(pattern);
        if(applies != pr.appliesTo(target)) {
            throw new RuntimeException(pattern + " / " + target);
        }
    }
}
