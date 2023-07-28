package finio.examples.rules.model;

import java.util.ArrayList;
import java.util.List;

import replete.text.StringUtil;

public class RuleSetNode {


    ////////////
    // FIELDS //
    ////////////

    private String levelLabel;
    private String childBulletPattern;
    private RuleSet ruleSet;
    private RuleSetNode parent;
    private List<RuleSetNode> children = new ArrayList<>();


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
    public RuleSet getRuleSet() {
        return ruleSet;
    }
    public RuleSetNode getParent() {
        return parent;
    }
    public List<RuleSetNode> getChildren() {
        return children;
    }
    public Rule getRule(CaptureAs cap) {
        return ruleSet.getRule(cap);
    }

    // Mutators (Builder)

    public RuleSetNode setLevelLabel(String levelLabel) {
        this.levelLabel = levelLabel;
        return this;
    }
    public RuleSetNode setChildBulletPattern(String childBulletPattern) {
        this.childBulletPattern = childBulletPattern;
        return this;
    }
    public RuleSetNode setRuleSet(RuleSet ruleSet) {
        this.ruleSet = ruleSet;
        return this;
    }
    public RuleSetNode setParent(RuleSetNode parent) {
        this.parent = parent;
        return this;
    }
    public RuleSetNode addChild(RuleSetNode child) {
        children.add(child);
        child.setParent(this);
        return this;
    }

//    public RuleSetNodeMatchResult match(List<PdfElement> stream, int start) {
//        RuleSetMatchResult matchResult = getRuleSet().match(stream, start);
//        return new RuleSetNodeMatchResult()
//            .setNode(this)
//            .setResult(matchResult);
//    }


    //////////
    // MISC //
    //////////

    public String toString(int i) {
        String sp = StringUtil.spaces(i * 4);
        String rs = (ruleSet == null || ruleSet.isEmpty()) ? "(no rule set)" : ruleSet.toString();
        String ret = sp + "<" + levelLabel + "> " + rs + "\n";
        for(RuleSetNode child : children) {
            ret += child.toString(i + 1);
        }
        return ret;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ruleSet == null) ? 0 : ruleSet.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RuleSetNode other = (RuleSetNode) obj;
        if (ruleSet == null) {
            if (other.ruleSet != null) {
                return false;
            }
        } else if (!ruleSet.equals(other.ruleSet)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RuleSetNode [levelLabel=" + levelLabel +
            ", childBulletPattern=" + childBulletPattern +
            ", ruleSet=" + ruleSet +
            ", children=" + children +
            ", " + hashCode() + "]";
    }
}
