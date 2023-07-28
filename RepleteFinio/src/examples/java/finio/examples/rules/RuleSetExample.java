package finio.examples.rules;

import java.io.File;
import java.io.IOException;

import finio.examples.rules.model.Rule;
import finio.examples.rules.model.RuleSet;
import finio.examples.rules.model.RuleSetHierarchy;
import finio.examples.rules.model.RuleSetNode;
import finio.examples.rules.modelx.XRuleSet;
import finio.examples.rules.modelx.XRuleSetHierarchy;
import finio.examples.rules.ui.RuleSetFrame;
import replete.xstream.XStreamWrapper;

public class RuleSetExample {

    public static void main(String[] args) throws IOException {
        addAliases(
            RuleSetHierarchy.class,
            RuleSetNode.class,
            RuleSet.class,
            Rule.class,
            XRuleSetHierarchy.class,
            XRuleSet.class
        );
        File file = new File("C:\\Users\\dtrumbo\\work\\eclipse-main\\RepleteFinio\\supplemental\\hier-test.xml");
        RuleSetHierarchy hier = (RuleSetHierarchy) XStreamWrapper.loadTarget(file);
        XRuleSetHierarchy xhier = convertRuleSetNodes(hier);
        XStreamWrapper.writeToFile(xhier, new File(file.getParent(), "hier-test-c.xml"));
        RuleSetFrame frame = new RuleSetFrame(xhier);
        frame.setVisible(true);
    }

    private static XRuleSetHierarchy convertRuleSetNodes(RuleSetHierarchy hier) {
        XRuleSetHierarchy xhier = new XRuleSetHierarchy();
        XRuleSet xroot = new XRuleSet();
        xhier.setRoot(xroot);
        RuleSetNode root = hier.getRoot();
        convertRuleSetNode(root, xroot);
        return xhier;
    }

    private static void convertRuleSetNode(RuleSetNode root, XRuleSet root2) {
        root2.setLevelLabel(root.getLevelLabel());
        root2.setChildBulletPattern(root.getChildBulletPattern());
        root2.setDisabled(root.getRuleSet().isDisabled());
        for(Rule rule : root.getRuleSet().getRules()) {
            root2.addRule(rule);
        }
        for(RuleSetNode child : root.getChildren()) {
            XRuleSet xchild = new XRuleSet();
            xchild.setParent(root2);
            root2.addChild(xchild);
            convertRuleSetNode(child, xchild);
        }
    }

    private static void addAliases(Class... classes) {
        for(Class clazz : classes) {
            XStreamWrapper.addAlias(clazz.getSimpleName(), clazz);
        }
    }
}
