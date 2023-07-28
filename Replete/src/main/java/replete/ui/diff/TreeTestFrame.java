package replete.ui.diff;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import replete.collections.Triple;
import replete.diff.Comparison;
import replete.diff.ContainerComparison;
import replete.diff.DiffResult;
import replete.diff.Importance;
import replete.diff.SimpleComparison;
import replete.ui.button.RButton;
import replete.ui.button.RCheckBox;
import replete.ui.lay.Lay;
import replete.ui.tree.RTree;
import replete.ui.tree.RTreeNode;
import replete.ui.windows.escape.EscapeFrame;

public class TreeTestFrame extends EscapeFrame {
    private DiffResult currentResult;
    private RTree treCenter;


    public TreeTestFrame() {

        treCenter = new RTree(new RTreeNode());
        treCenter.setRootCollapsible(false);
        treCenter.setRootVisible(false);

        treCenter.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent arg0) {
                System.out.println("Tree selection changed");
            }
        });

        RButton btnExpandAll;
        RCheckBox chkIncludeSame;

        Lay.BLtg(this,
            "N", Lay.lb("Differences", "eb=5,bg=200"),
            "C", Lay.BL("C", Lay.sp(treCenter), "eb=10"),
            "S", Lay.BL(
                "W", Lay.FL("L", btnExpandAll = Lay.btn("&Expand All")),
                "C", Lay.FL("C", chkIncludeSame = Lay.chk("Include &Same", true, "fg=white")),
                "E", Lay.FL("R", Lay.btn("&Close", "closer")),
                "bg=100, chtransp"
            ),
            "size=600,center"
        );

        btnExpandAll.addActionListener(e -> treCenter.expandAll());
        chkIncludeSame.addActionListener(e -> changeIncludeSame(chkIncludeSame.isSelected()));
    }

    public TreeTestFrame setCurrentResult(DiffResult currentResult) {
        this.currentResult = currentResult;
        RTreeNode nRoot = createTree(currentResult, "HTTP Resource Difference", true);
        treCenter.setModel(nRoot);
        treCenter.setRootVisible(true);
        return this;
    }

    private void changeIncludeSame(boolean isSelected) {
        RTreeNode nRoot = createTree(currentResult, "HTTP Resource Difference", isSelected);
        treCenter.setModel(nRoot);
        //setRootVisible(true)?  prolly doesn't need to be called because will be already visible
    }

    private RTreeNode createTree(DiffResult result, String label, boolean displayNonDifferences) {
        return createTree(result.getComparison(), null,  label, displayNonDifferences);
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


}
