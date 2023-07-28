package replete.diff;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import replete.collections.Triple;
import replete.text.StringUtil;
import replete.ui.button.RButton;
import replete.ui.button.RCheckBox;
import replete.ui.diff.NodeContainerDiff;
import replete.ui.diff.NodeSimpleDiff;
import replete.ui.diff.NodeSimpleDiffComment;
import replete.ui.diff.NodeSimpleDiffLeft;
import replete.ui.diff.NodeSimpleDiffRight;
import replete.ui.diff.TreeTestData;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;
import replete.ui.tree.RTree;
import replete.ui.tree.RTreeNode;

public class DiffTreePanel extends RPanel{


    ////////////
    // FIELDS //
    ////////////

    private RTree treDiff;
    private DiffResult currentResult;
    private String currentLabel;
    private String leftLabel;
    private String rightLabel;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public DiffTreePanel() {
        treDiff = new RTree(new RTreeNode());
        treDiff.setRootCollapsible(false);
        treDiff.setRootVisible(false);

        treDiff.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent arg0) {
                System.out.println("Tree selection changed");
            }
        });

        RButton btnExpandAll;
        RCheckBox chkIncludeSame;

        Lay.BLtg(this,
            "C", Lay.BL("C", Lay.sp(treDiff), "eb=5"),
            "S", Lay.FL("L",
                btnExpandAll = Lay.btn(CommonConcepts.EXPAND_ALL, "icon"),
                chkIncludeSame = Lay.chk("Show &Same", true, "fg=white"),
                "nogap, bg=100, chtransp"
            )
        );

        btnExpandAll.addActionListener(e -> treDiff.expandAll());
        chkIncludeSame.addActionListener(e -> changeIncludeSame(chkIncludeSame.isSelected()));
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public DiffResult getCurrentResult() {
        return currentResult;
    }
    public String getCurrentLabel() {
        return currentLabel;
    }
    public String getLeftLabel() {
        return leftLabel;
    }
    public String getRightLabel() {
        return rightLabel;
    }

    // Mutators

    public DiffTreePanel setCurrentResult(DiffResult currentResult, String label) {
        return setCurrentResult(currentResult, label, "", "");
    }

    public DiffTreePanel setCurrentResult(DiffResult currentResult, String currentLabel, String leftLabel, String rightLabel) {
        this.currentResult = currentResult;
        this.currentLabel = currentLabel;
        this.leftLabel = leftLabel;
        this.rightLabel = rightLabel;
        if(currentResult == null) {
            treDiff.setModel(new RTreeNode());
            treDiff.setRootVisible(false);
        } else {
            RTreeNode nRoot = createTree(true);
            treDiff.setModel(nRoot);
            treDiff.setRootVisible(true);
        }
        return this;
    }


    /////////////
    // HELPERS //
    /////////////

    private void changeIncludeSame(boolean isSelected) {
        if(currentResult != null) {
            RTreeNode nRoot = createTree(isSelected);
            treDiff.setModel(nRoot);
        }
    }

    private RTreeNode createTree(boolean displayNonDifferences) {
        String label;
        if(StringUtil.isBlank(leftLabel) || StringUtil.isBlank(rightLabel)) {
            label = currentLabel;
        } else {
            label = currentLabel + ": " + leftLabel + " (Left) vs. " + rightLabel + " (Right)";
        }
        return createTree(
            currentResult.getComparison(),
            null,
            label,
            displayNonDifferences
        );
    }
    private RTreeNode createTree(Comparison diff, Importance importance, String label, boolean displayNonDifferences) {
        RTreeNode nDiff;
        if(diff instanceof ContainerComparison) {
            ContainerComparison cDiff = (ContainerComparison) diff;
            nDiff = new RTreeNode(new NodeContainerDiff(importance, label, cDiff));

            for(Triple<Importance, String, Comparison> i : cDiff.getComparisons()) {
                boolean shouldAdd = displayNonDifferences || i.getValue3().isDiff();
                if(shouldAdd) {
                    RTreeNode n = createTree(i.getValue3(), i.getValue1(), i.getValue2(), displayNonDifferences);
                    nDiff.add(n);
                }
            }
        } else {
            SimpleComparison sDiff = (SimpleComparison) diff;
            nDiff = new RTreeNode(new NodeSimpleDiff(importance, label, sDiff));

            if(sDiff.getComment() != null) {
                nDiff.add(new NodeSimpleDiffComment(sDiff.getComment()));
            }
            if(sDiff.getLeftClue() != null) {
                nDiff.add(new NodeSimpleDiffLeft(sDiff.getLeftClue()));
            }
            if(sDiff.getRightClue() != null) {
                nDiff.add(new NodeSimpleDiffRight(sDiff.getRightClue()));
            }
        }
        return nDiff;
    }


    //////////
    // MAIN //
    //////////

    public static void main(String[] args) {
        DiffTreePanel diffPanel = new DiffTreePanel();
        diffPanel.setCurrentResult(TreeTestData.createTestData1(), "Head_Label", "Left_Label", "Right_Label");
        DiffTreePanel diffPanel2 = new DiffTreePanel();
        Lay.BLtg(Lay.fr(),
            "N", Lay.lb("Top Label", "bg=125,center"),
            "W", Lay.lb("Left Label", "bg=125,center"),
            "C", diffPanel,
            "E", diffPanel2,//Lay.lb("Right Label", "bg=125,center"),
            "S", Lay.BL(
                "C", Lay.lb("Bottom Label", "bg=125,center"),
                "E", Lay.btn("&Close", "closer")
            ),
            "size=800,center,visible"
        );
    }
}
