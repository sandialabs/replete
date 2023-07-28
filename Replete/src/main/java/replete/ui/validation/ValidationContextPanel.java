package replete.ui.validation;

import javax.swing.JPanel;

import replete.text.StringUtil;
import replete.ui.BeanPanel;
import replete.ui.lay.Lay;
import replete.ui.tree.RTree;
import replete.ui.tree.RTreeNode;

public class ValidationContextPanel extends BeanPanel<ValidationContext> {


    ////////////
    // FIELDS //
    ////////////

    private JPanel pnlSummary;
    private RTree treContext;
    private boolean showEmptyFrames;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ValidationContextPanel(boolean showEmptyFrames) {
        this.showEmptyFrames = showEmptyFrames;
        Lay.BLtg(this,
            "N", pnlSummary = Lay.FL("L", "nogap,eb=5b"),
            "C", Lay.sp(treContext = Lay.tr())
        );
        treContext.setRootVisible(true);
    }

    @Override
    public ValidationContext get() {
        return null;
    }

    @Override
    public void set(ValidationContext context) {
        RTreeNode nRoot = new RTreeNode(new NodeRoot());
        ValidationFrame frame = context.getRootFrame();
        populate(nRoot, frame);
        treContext.setModel(nRoot);
        treContext.expandAll();
        pnlSummary.removeAll();
        int[] counts = context.getQuickCount();
        pnlSummary.add(Lay.lb("Summary:"));
        int info = counts[0];
        if(info != 0) {
            pnlSummary.add(Lay.lb(info + " Notice" + StringUtil.s(info), MessageType.INFO.getIcon(), "eb=5l"));
        }
        int warn = counts[1];
        if(warn != 0) {
            pnlSummary.add(Lay.lb(warn + " Warning" + StringUtil.s(warn), MessageType.WARN.getIcon(), "eb=5l"));
        }
        int err = counts[2];
        if(err != 0) {
            pnlSummary.add(Lay.lb(err + " Error" + StringUtil.s(err), MessageType.ERROR.getIcon(), "eb=5l"));
        }
        updateUI();
    }

    private boolean populate(RTreeNode nRoot, ValidationFrame frame) {
        boolean hasContent = false;
        for(ValidationMessage msg : frame.getMessages()) {
            RTreeNode nMsg  = nRoot.add(new NodeMessage(msg));
            hasContent = true;
            if(msg.getException() != null) {
                nMsg.add(new NodeException(msg));
            }
        }
        for(String key : frame.children.keySet()) {
            ValidationFrame child = frame.children.get(key);
            RTreeNode nChild = new RTreeNode(new NodeFrame(key));
            boolean childHasContent = populate(nChild, child);
            if(showEmptyFrames || childHasContent) {
                nRoot.add(nChild);
            }
            hasContent = hasContent || childHasContent;
        }
        return hasContent;
    }

    public void setRootLabel(String label) {
        NodeRoot root = treContext.getRoot().get();
        root.setLabel(label);
        treContext.updateUI();
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        ValidationContextPanel pnl;
        Lay.fr("Test",
            Lay.BL(
                "C", pnl = new ValidationContextPanel(false)
            ),
            "size=[800,800],center,visible"
        );
        pnl.set(ValidationContext.createTestContext());
    }
}
