package finio.examples.rules.ui.nodes;

import javax.swing.Icon;

import finio.examples.rules.model.Rule;
import finio.examples.rules.ui.images.ReframeTestImageModel;
import replete.ui.images.concepts.ImageLib;
import replete.ui.tree.NodeBase;

public class NodeRule extends NodeBase {


    ////////////
    // FIELDS //
    ////////////

    private Rule rule;
    private boolean parentDisabled = false;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public NodeRule(Rule rule) {
        this.rule = rule;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public Rule getRule() {
        return rule;
    }
    public boolean isParentDisabled() {
        return parentDisabled;
    }

    // Mutators

    public NodeRule setRule(Rule rule) {
        this.rule = rule;
        return this;
    }
    public NodeRule setParentDisabled(boolean parentDisabled) {
        this.parentDisabled = parentDisabled;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Icon getIcon(boolean expanded) {
        if(parentDisabled || rule.isDisabled()) {  // Gray if this rule or parent rule disabled
            return ImageLib.get(ReframeTestImageModel.RULE_DIS);
        }
        if(rule.isExclusion()) {
            return ImageLib.get(ReframeTestImageModel.RULE_EXCL);
        }
        return ImageLib.get(ReframeTestImageModel.RULE);
    }

    @Override
    public String toString() {
        return rule.toSimpleString();  // "[DIS]" only shown if this very rule disabled
    }
}
