package finio.examples.rules.model;

import java.util.ArrayList;
import java.util.List;

public class RuleSet {


    ////////////
    // FIELDS //
    ////////////

    // ?????? Where is child bullet pattern?
    private String levelLabel;
    private List<Rule> rules = new ArrayList<>();
    private boolean disabled = false;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getLevelLabel() {
        return levelLabel;
    }
    public List<Rule> getRules() {
        return rules;
    }
    public boolean isDisabled() {
        return disabled;
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

    // Mutators

    public RuleSet setLevelLabel(String levelLabel) {
        this.levelLabel = levelLabel;
        return this;
    }
    public void addRule(Rule rule) {
        rules.add(rule);
    }
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }


    //////////
    // MISC //
    //////////

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


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return rules.toString();
    }
    @Override
    public int hashCode() {          // Level label, disabled?
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rules == null) ? 0 : rules.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {    // Level label, disabled?
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RuleSet other = (RuleSet) obj;
        if (rules == null) {
            if (other.rules != null) {
                return false;
            }
        } else if (!rules.equals(other.rules)) {
            return false;
        }
        return true;
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