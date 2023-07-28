package finio.examples.rules.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreePath;

import finio.examples.rules.model.Rule;
import finio.examples.rules.modelx.XRuleSet;
import finio.examples.rules.modelx.XRuleSetHierarchy;
import finio.examples.rules.ui.images.ReframeTestImageModel;
import finio.examples.rules.ui.nodes.NodeRoot;
import finio.examples.rules.ui.nodes.NodeRule;
import finio.examples.rules.ui.nodes.NodeRuleSet;
import replete.ui.GuiUtil;
import replete.ui.button.RButton;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;
import replete.ui.tree.NodeBase;
import replete.ui.tree.RTree;
import replete.ui.tree.RTreeModel;
import replete.ui.tree.RTreeNode;
import replete.ui.windows.Dialogs;

public class PdfParametersPanel extends RPanel {


    ////////////
    // FIELDS //
    ////////////

    // UI
    private RButton btnProcess;
    private RTreeNode nRoot;
    private RTree treParams;

    // Copy
    private RTreeNode nCopiedRuleSet;
    private RTreeNode nCopiedRule;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PdfParametersPanel(XRuleSetHierarchy xhier) {
        RButton btnAddRuleSet;
        RButton btnAddRule;
        RButton btnEdit;
        RButton btnRemove;
        RButton btnCopy;
        RButton btnPaste;
        RButton btnUp;
        RButton btnDown;
        RButton btnExpand;
        RButton btnExpandAll;
        RButton btnCollapse;
        RButton btnCollapseAll;

        nRoot = new RTreeNode(new NodeRoot());

        Lay.BLtg(this,
            "N", Lay.BL(
                "C", Lay.lb("<html><u>Parsing</u></html>",
                    CommonConcepts.SORT_ASC, "size=14"),
                "E", btnProcess = Lay.btn("&Parse", CommonConcepts.FORWARD, "htext=left")
            ),
            "C", Lay.BL(
                "C", Lay.p(Lay.sp(treParams = Lay.tr(nRoot, "lasso")), "eb=5bt"),
                "E", Lay.BxL("Y",
                    Lay.p(btnAddRuleSet = Lay.btn(ReframeTestImageModel.RULESET_ADD, 2), "eb=5tl,alignx=0.5,maxH=20"),
                    Lay.p(btnAddRule = Lay.btn(ReframeTestImageModel.RULE_ADD, 2), "eb=5tl,alignx=0.5,maxH=20"),
                    Lay.p(btnEdit = Lay.btn(CommonConcepts.EDIT, 2), "eb=5tl,alignx=0.5,maxH=20"),
                    Lay.p(btnRemove = Lay.btn(CommonConcepts.DELETE, 2), "eb=5tl,alignx=0.5,maxH=20"),
                    Box.createVerticalStrut(10),
                    Lay.p(btnCopy = Lay.btn(CommonConcepts.COPY, 2), "eb=5tl,alignx=0.5,maxH=20"),
                    Lay.p(btnPaste = Lay.btn(CommonConcepts.PASTE, 2), "eb=5tl,alignx=0.5,maxH=20"),
                    Box.createVerticalStrut(10),
                    Lay.p(btnUp = Lay.btn(CommonConcepts.UP, 2), "eb=5tl,alignx=0.5,maxH=20"),
                    Lay.p(btnDown = Lay.btn(CommonConcepts.DOWN, 2), "eb=5tl,alignx=0.5,maxH=20"),
                    Box.createVerticalStrut(10),
                    Lay.p(btnExpandAll = Lay.btn(CommonConcepts.EXPAND_ALL, 2), "eb=5tl,alignx=0.5,maxH=20"),
                    Lay.p(btnExpand = Lay.btn(CommonConcepts.EXPAND, 2), "eb=5tl,alignx=0.5,maxH=20"),
                    Lay.p(btnCollapseAll = Lay.btn(CommonConcepts.COLLAPSE_ALL, 2), "eb=5tl,alignx=0.5,maxH=20"),
                    Lay.p(btnCollapse = Lay.btn(CommonConcepts.COLLAPSE, 2), "eb=5tl,alignx=0.5,maxH=20"),
                    Box.createVerticalGlue()
                ),
                "S", Lay.FL("L", "nogap",
                    Lay.lb("Min Y: "), Lay.tx("", "selectall", 4),
                    Lay.lb("  Max Y: "), Lay.tx("", "selectall", 4)
                )
            ),
            "eb=5,bg=C9ECFF,chtransp"
        );

        treParams.setToggleClickCount(0);

        btnAddRuleSet.setToolTipText("Add Rule Set");
        btnAddRule.setToolTipText("Add Rule");
        btnEdit.setToolTipText("Edit...");
        btnRemove.setToolTipText("Remove");
        btnCopy.setToolTipText("Copy");
        btnPaste.setToolTipText("Paste");
        btnUp.setToolTipText("Move Up");
        btnDown.setToolTipText("Move Down");
        btnExpandAll.setToolTipText("Expand All");
        btnExpand.setToolTipText("Expand");
        btnCollapseAll.setToolTipText("Collapse All");
        btnCollapse.setToolTipText("Collapse");

        btnProcess.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        treParams.addDoubleClickListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                RTreeNode nSel = treParams.getSelNode();
                if(nSel != null) {
                    doEdit();
                }
            }
        });
        treParams.addEnterKeyListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                RTreeNode nSel = treParams.getSelNode();
                if(nSel != null) {
                    doEdit();
                }
            }
        });

        btnAddRuleSet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RTreeNode nSel = treParams.getSelNode();
                if(nSel == null) {
                    nSel = nRoot;
                }
                NodeBase uSel = nSel.get();
                if(uSel instanceof NodeRule) {
                    nSel = nSel.getRParent();
                }
                RTreeModel model = treParams.getRModel();
                RTreeNode nNew = model.append(nSel, new NodeRuleSet(new XRuleSet().setLevelLabel("TBD")));
                treParams.expand(nSel);
                treParams.select(nNew);
            }
        });

        btnAddRule.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Rule rule = new Rule();
                addRuleToTree(rule);
            }
        });

        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doEdit();
            }
        });

        btnRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RTreeNode[] nSels = treParams.getSelNodes();
                if(nSels != null) {
                    RTreeModel model = treParams.getRModel();
                    for(RTreeNode nSel : nSels) {
                        if(!nSel.isRoot()) {
                            model.remove(nSel);
                        }
                    }
                }
            }
        });

        btnCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RTreeNode nSel = treParams.getSelNode();
                if(nSel == null) {
                    return;
                }

                if(nSel.get() instanceof NodeRuleSet) {
                    nCopiedRuleSet = nSel;
                    nCopiedRule = null;

                } else if(nSel.get() instanceof NodeRule) {
                    nCopiedRule = nSel;
                    nCopiedRuleSet = null;
                }
            }
        });

        btnPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(treParams.getSelNode() == null) {
                    return;
                }

                if(nCopiedRule != null) {
                    for(RTreeNode nSel : treParams.getSelNodes()) {
                        if(nSel.get() instanceof NodeRuleSet) {
                            pasteRuleInto(nSel);
                        }
                    }

                } else if(nCopiedRuleSet != null) {
                    for(RTreeNode nSel : treParams.getSelNodes()) {
                        if(nSel.get() instanceof NodeRuleSet || nSel.get() instanceof NodeRoot) {
                            pasteRuleSetInto(nSel);
                        }
                    }
                }
            }
        });

        btnUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RTreeNode nSel = treParams.getSelNode();
                if(nSel == null) {
                    return;
                }
                if(nSel.get() instanceof NodeRoot) {
                    return;
                }
                RTreeNode nParent = nSel.getRParent();

                int i = nParent.getIndex(nSel);
                int x;
                if(nSel.get() instanceof NodeRule) {
                    x = 0;
                } else {
                    x = countRuleNodes(nParent);
                }

                if(i > x) {
                    RTreeModel model = treParams.getRModel();
                    model.remove(nSel);
                    model.insertNodeInto(nSel, nParent, i - 1);

                    TreePath path = treParams.getPath(nSel);
                    treParams.setSelectionPath(path);
                    treParams.scrollPathToVisible(path);
                    treParams.updateUI();
                }
            }
        });

        btnDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RTreeNode nSel = treParams.getSelNode();
                if(nSel == null) {
                    return;
                }
                if(nSel.get() instanceof NodeRoot) {
                    return;
                }
                RTreeNode nParent = nSel.getRParent();

                int i = nParent.getIndex(nSel);
                int x;
                if(nSel.get() instanceof NodeRule) {
                    x = countRuleNodes(nParent) - 1;
                } else {
                    x = nParent.getCount() - 1;
                }

                if(i < x) {
                    RTreeModel model = treParams.getRModel();
                    model.remove(nSel);
                    model.insertNodeInto(nSel, nParent, i + 1);

                    TreePath path = treParams.getPath(nSel);
                    treParams.setSelectionPath(path);
                    treParams.scrollPathToVisible(path);
                    treParams.updateUI();
                }
            }
        });

        btnCollapse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                treParams.collapseSelected();
            }
        });

        btnCollapseAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                treParams.collapseAllSelected();
            }
        });

        btnExpand.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                treParams.expandSelected();
            }
        });

        btnExpandAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                treParams.expandAllSelected();
            }
        });

        if(xhier.getRoot() != null) {
            XRuleSet rootRsn = xhier.getRoot();
            RTreeNode nAlmostRoot = new RTreeNode(new NodeRuleSet(rootRsn));
            nRoot.add(nAlmostRoot);
            populateFromParams(nAlmostRoot, rootRsn);
            treParams.expandAll();
            treParams.updateUI();
        }
    }

    private void populateFromParams(RTreeNode nAlmostRoot, XRuleSet rsn) {
        NodeRuleSet uRuleSet = nAlmostRoot.get();
        uRuleSet.setRuleSet(rsn);
        for(Rule rule : rsn.getRules()) {
            nAlmostRoot.add(new NodeRule(rule));
        }
        for(XRuleSet childRsn : rsn.getChildren()) {
            RTreeNode nChild = nAlmostRoot.add(new NodeRuleSet(childRsn));
            populateFromParams(nChild, childRsn);
        }
    }


    //////////
    // MISC //
    //////////

    // TODO - Turn this into real preprocessing rule...
    // For "DOE O 473.3 PPO.df" & "DOE M 470.4-2B UCNI.pdf" use
    //    78.6F and 753.0F
    // For "UCNI 5210-41M Vol 2.PDF" use
    //    88.0F and 714.0F  (though some page #'s start at 716....)
    // For "UCNI 5210-41M Vol 1.PDF" use
    //    85.8F and 690.0F
    // For "UCNI 5210-41M Vol 3.PDF" use
    //    93.0F and 713.0F

    private void doEdit() {
        RTreeNode nSel = treParams.getSelNode();
        if(nSel == null) {
            return;
        }

        if(nSel.get() instanceof NodeRuleSet) {
            NodeRuleSet uRuleSet = nSel.get();
            JFrame parent = GuiUtil.fra(PdfParametersPanel.this);
            RuleSetEditDialog dlg = new RuleSetEditDialog(parent,
                uRuleSet.getRuleSet().getLevelLabel(),
                uRuleSet.getRuleSet().getChildBulletPattern(), false);
            dlg.setVisible(true);
            if(dlg.getResult() == RuleSetEditDialog.OK) {
                String newLabel = dlg.getLevelLabel();
                if(alreadyUsed(newLabel, nSel)) {
                    Dialogs.showWarning(parent,
                        "This level label is used by another sibling rule set\n" +
                        "under their parent rule set.  It is recommended that\n" +
                        "you use unique level labels under a given parent rule\n" +
                        "set for clarity.",
                        "Level Label Warning"
                    );
                }
                uRuleSet.getRuleSet().setLevelLabel(newLabel);
                uRuleSet.getRuleSet().setChildBulletPattern(dlg.getChildBulletPattern());
                treParams.updateUI();
            }

        } else if(nSel.get() instanceof NodeRule) {
            NodeRule uRule = nSel.get();
            JFrame parent = GuiUtil.fra(PdfParametersPanel.this);
            RuleEditDialog dlg = new RuleEditDialog(parent, uRule.getRule(), false);
            dlg.setVisible(true);
            if(dlg.getResult() == RuleEditDialog.OK) {
                uRule.setRule(dlg.getRule());
                treParams.updateUI();
            }
        }
    }

    private boolean alreadyUsed(String newLabel, RTreeNode nSel) {
        for(RTreeNode nSib : nSel.getRSiblings(NodeRuleSet.class)) {
            NodeRuleSet uRuleSet = nSib.get();
            if(uRuleSet.getRuleSet().getLevelLabel().equals(newLabel)) {
                return true;
            }
        }
        return false;
    }

    public void setProcessButtonEnabled(boolean enabled) {
        btnProcess.setEnabled(enabled);
    }

    private int countRuleNodes(RTreeNode nSel) {
        int pos = 0;
        for(; pos < nSel.getCount(); pos++) {
            if(!(nSel.getObjectAt(pos) instanceof NodeRule)) {
                break;
            }
        }
        return pos;
    }

    private void pasteRuleInto(RTreeNode nSel) {
        RTreeModel model = treParams.getRModel();
        RTreeNode nNew = copyRuleNode(nCopiedRule);
        int pos = countRuleNodes(nSel);
        model.insertNodeInto(nNew, nSel, pos);
    }

    private void pasteRuleSetInto(RTreeNode nSel) {
        RTreeModel model = treParams.getRModel();
        RTreeNode nNew = copyRuleSetNode(nCopiedRuleSet);
        model.append(nSel, nNew);
    }

    public RTreeNode copyRuleNode(RTreeNode nRule) {
        NodeRule uCurRule = (NodeRule) nRule.get();
        NodeRule uNewRule = new NodeRule(uCurRule.getRule().copy());
        return new RTreeNode(uNewRule);
    }

    public RTreeNode copyRuleSetNode(RTreeNode nRuleSet) {
//        NodeRuleSet uPrev = nRuleSet.get();
//        NodeRuleSet uNewRuleSet = new NodeRuleSet(uPrev.copy());
//        RTreeNode nNewRuleSet = new RTreeNode(uNewRuleSet);
//
//        for(RTreeNode nChild : nRuleSet) {
//            if(nChild.get() instanceof NodeRuleSet) {
//                nNewRuleSet.add(copyRuleSetNode(nChild));
//
//            } else if(nChild.get() instanceof NodeRule) {
//                nNewRuleSet.add(copyRuleNode(nChild));
//            }
//        }
//
//        return nNewRuleSet;
        return null;
    }

    public void addRuleFromExternal(Rule derivedRule) {
        JFrame parent = GuiUtil.fra(PdfParametersPanel.this);
        RuleEditDialog dlg = new RuleEditDialog(parent, derivedRule, true);
        dlg.setVisible(true);
        if(dlg.getResult() == RuleEditDialog.OK) {
            Rule editedRule = dlg.getRule();
            addRuleToTree(editedRule);
        }
    }

    private void addRuleToTree(Rule rule) {
        RTreeNode nSel = treParams.getSelNode();
        if(nSel == null) {
            return;
        }
        NodeBase uSel = nSel.get();
        if(uSel instanceof NodeRoot) {
            return;
        }
        if(uSel instanceof NodeRule) {
            nSel = nSel.getRParent();
        }
        RTreeModel model = treParams.getRModel();
        int pos = countRuleNodes(nSel);
        RTreeNode nNew = new RTreeNode(new NodeRule(rule));
        model.insertNodeInto(nNew, nSel, pos);
        treParams.expand(nSel);
        treParams.select(nNew);
    }
}
