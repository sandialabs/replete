package finio.examples.rules.modelx;

import java.util.ArrayList;
import java.util.List;

import finio.examples.rules.model.CaptureAs;
import finio.examples.rules.model.Rule;
import replete.text.StringUtil;

public class XRuleSet {


    ////////////
    // FIELDS //
    ////////////

    private String levelLabel;
    private String childBulletPattern;
    private boolean disabled = false;
    private List<Rule> rules = new ArrayList<>();
    private XRuleSet parent;
    private List<XRuleSet> children = new ArrayList<>();


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getLevelLabel() {
        return levelLabel;
    }
    public String getChildBulletPattern() {
        return childBulletPattern;
    }
    public boolean isDisabled() {
        return disabled;
    }
    public List<Rule> getRules() {
        return rules;
    }
    public XRuleSet getParent() {
        return parent;
    }
    public List<XRuleSet> getChildren() {
        return children;
    }

    // Accessors (Computed)

    public Rule getRule(CaptureAs cap) {
        for(Rule rule : rules) {
            if(rule.getCaptureAs() == cap) {
                return rule;
            }
        }
        return null;
    }
    public boolean isEmpty() {
        return rules.isEmpty();
    }

    // Mutators (Builder)

    public XRuleSet setLevelLabel(String levelLabel) {
        this.levelLabel = levelLabel;
        return this;
    }
    public XRuleSet setChildBulletPattern(String childBulletPattern) {
        this.childBulletPattern = childBulletPattern;
        return this;
    }
    public XRuleSet setDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }
    public XRuleSet addRule(Rule rule) {
        rules.add(rule);
        return this;
    }
    public XRuleSet setParent(XRuleSet parent) {
        this.parent = parent;
        return this;
    }
    public XRuleSet addChild(XRuleSet child) {
        children.add(child);
        child.setParent(this);
        return this;
    }


    ///////////
    // MATCH //
    ///////////

//    public RuleSetNodeMatchResult match(List<PdfElement> stream, int start) {
//        RuleSetMatchResult matchResult = getRuleSet().match(stream, start);
//        return new RuleSetNodeMatchResult()
//        .setNode(this)
//        .setResult(matchResult);
//    }

    // Doesn't reference 'disabled' itself... maybe.
//    public RuleSetMatchResult match(List<PdfElement> stream, int start) {
//        boolean matches = true;
//        List<RuleMatchResult> matchResults = new ArrayList<>();
//        String bullet = "";
//        String title = "";
//        StringBuilder matchingText = new StringBuilder();
//        int startPage = 0;
//        if(rules.isEmpty()) {
//            return new RuleSetMatchResult(false, "", "", "", startPage, matchResults);
//        }
//        for(Rule rule : rules) {
//            RuleMatchResult matchResult = rule.match(stream, start);
//            matchResults.add(matchResult);
//            matches = matches && (matchResult.isMatches() || rule.isOptional());
//            if(matches) {
//                start += matchResult.getMatchingText().length();
//                if(rule.getCaptureAs() == CaptureAs.BULLET) {
//                    bullet += matchResult.getMatchingText();
//                } else if(rule.getCaptureAs() == CaptureAs.TITLE) {
//                    title += matchResult.getMatchingText();
//                }
//                matchingText.append(matchResult.getMatchingText());
//                if(startPage == 0) {
//                    startPage = matchResult.getStartPage();
//                }
//            } else {
//                return new RuleSetMatchResult(false, "", "", "", startPage, matchResults);
//            }
//        }
//        return new RuleSetMatchResult(true, bullet, title, matchingText.toString(), startPage, matchResults);
//    }


    //////////
    // MISC //
    //////////

    public String toString(int i) {
        String sp = StringUtil.spaces(i * 4);
        String rs = rules.isEmpty() ? "(no rule set)" : rules.toString();
        String ret = sp + "<" + levelLabel + "> " + rs + "\n";
        for(XRuleSet child : children) {
            ret += child.toString(i + 1);
        }
        return ret;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "RuleSet [levelLabel=" + levelLabel +
            ", childBulletPattern=" + childBulletPattern +
            ", disabled=" + disabled +
            ", rules=" + rules +
            ", children=" + children +
            ", " + hashCode() + "]";
    }


    //////////
    // TEST //
    //////////

//    public static void main(String[] args) {
//        List<PdfElement> stream = new ArrayList<>();
////        stream.add(new TextInfo(" ", "Times", 12.0f, false, false, 10.0f, 10.0f, 1));
//        stream.add(new PdfElement("a", "Times", 18.0f, true, true, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement(".", "Times", 18.0f, true, true, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement(" ", "Times", 12.0f, false, false, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement("h", "Times", 12.0f, false, false, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement("e", "Times", 12.0f, false, false, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement("l", "Times", 12.0f, false, false, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement("l", "Times", 12.0f, false, false, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement("o", "Times", 12.0f, false, false, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement(" ", "Times", 12.0f, false, false, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement("d", "Times", 12.0f, false, false, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement("o", "Times", 12.0f, false, false, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement("g", "Times", 12.0f, false, false, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement("!", "Times", 11.0f, false, false, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//
//        Rule rule1 = new Rule()
//            .setBold(true)
//            .setItalic(true)
//            .setMinFontSize(18.0F)
//            .setMaxFontSize(18.0F)
//            .setPattern("[a-z]\\.")
//        ;
//        Rule rule2 = new Rule()
//            .setMinFontSize(12.0F)
//            .setMaxFontSize(12.0F)
//            .setPattern(".+")
//        ;
//        RuleSet ruleSet = new RuleSet();
//        ruleSet.addRule(rule1);
//        ruleSet.addRule(rule2);
//        System.out.println(ruleSet.match(stream, 0));
//    }
}
