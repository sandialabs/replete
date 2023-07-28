package finio.examples.rules.ui.nodes;

import javax.swing.Icon;

import finio.examples.rules.modelx.XRuleSet;
import finio.examples.rules.ui.images.ReframeTestImageModel;
import replete.text.StringUtil;
import replete.ui.images.concepts.ImageLib;
import replete.ui.tree.NodeBase;

public class NodeRuleSet extends NodeBase {


    ////////////
    // FIELDS //
    ////////////

    private XRuleSet ruleSet;
    private boolean parentDisabled = false;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public NodeRuleSet(XRuleSet ruleSet) {
        this.ruleSet = ruleSet;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public XRuleSet getRuleSet() {
        return ruleSet;
    }
    public boolean isParentDisabled() {
        return parentDisabled;
    }

    // Mutator

    public NodeRuleSet setRuleSet(XRuleSet ruleSet) {
        this.ruleSet = ruleSet;
        return this;
    }
    public NodeRuleSet setParentDisabled(boolean parentDisabled) {
        this.parentDisabled = parentDisabled;
        return this;
    }


    //////////
    // MISC //
    //////////

//    public NodeRuleSet copy() {
//        return new NodeRuleSet(ruleSet.copy());
//    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Icon getIcon(boolean expanded) {
        if(parentDisabled || ruleSet.isDisabled()) {  // Gray if this rule or parent rule disabled
            return ImageLib.get(ReframeTestImageModel.RULESET_DIS);
        }
        return ImageLib.get(ReframeTestImageModel.RULESET);
    }

    @Override
    public String toString() {
        return "<" + ruleSet.getLevelLabel() + ">" +
            (!StringUtil.isBlank(ruleSet.getChildBulletPattern()) ?
                " [Child Bullets: " + ruleSet.getChildBulletPattern() + "]" : "");
    }

//    @Override
//    public String toString() {
//        return ruleSet.toSimpleString();  // "[DIS]" only shown if this very rule disabled
//    }
}
