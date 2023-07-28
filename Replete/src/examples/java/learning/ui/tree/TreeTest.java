package learning.ui.tree;

import javax.swing.JButton;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import replete.ui.lay.Lay;
import replete.ui.tree.RTree;
import replete.ui.tree.RTreeNode;

public class TreeTest {

    public static void main(String[] args) {

//        JPanel pnl = Lay.BL("W", cmp1, "E", cmp2);

//        Object[] treeData = new Object[] {
//            "Winter",
//            "Summer",
//            "Fall",
//            "Spring"
//        };

        RTreeNode grandchild1 = new RTreeNode("Denver");
        RTreeNode grandchild2 = new RTreeNode("Boulder");

        RTreeNode child1 = new RTreeNode("Colorado");
        RTreeNode child2 = new RTreeNode("New Mexico");

        RTreeNode root = new RTreeNode(new NodeCountry("United States"));
        root.add(child1);
        root.add(child2);

        child1.add(grandchild1);
        child1.add(grandchild2);

//        RTree treCenter = new RTree(root);
//        JScrollPane scrCenter = new JScrollPane(treCenter);

//        treCenter.setCellRenderer(new TreeCellRenderer() {
//            @Override
//            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
//                                                          boolean expanded, boolean leaf, int row,
//                                                          boolean hasFocus) {
//                return null;
//            }
//        });

//        EscapeFrame fraWin;
//        JButton btnClose;

        JButton btnRemove;
        RTree treCenter;

        Lay.BLtg(Lay.fr(),
            "N", Lay.lb("Here is a title label", "eb=5,bg=200"),
//            "C", Lay.BL("C", scrCenter, "eb=10"),
            "C", Lay.BL("C", Lay.sp(treCenter = Lay.tr(root)), "eb=10"),
            "S", Lay.FL("R", btnRemove = Lay.btn("Remove Node"), Lay.btn("&Close", "closer"), "bg=100"),
            "size=600,center,visible=true"
        );

        treCenter.setShowsRootHandles(false);
        treCenter.setRootVisible(true);

        treCenter.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                System.out.println("Tree Selection Changed:");
                for(TreePath path : e.getPaths()) {
                    System.out.println(" - " + path);
                }
                System.out.println(" * NLSP - " + e.getNewLeadSelectionPath());
                System.out.println(" * OLSP - " + e.getOldLeadSelectionPath());

                System.out.println("Selected Paths: #" + treCenter.getSelectionCount());
                if(treCenter.getSelectionPaths() != null) {
                    for(TreePath path : treCenter.getSelectionPaths()) {
                        System.out.println(" - " + path);
                    }
                }
            }
        });


        btnRemove.addActionListener(e -> {
            root.remove(child2);
            treCenter.updateUI();
        });

//        btnClose.addActionListener(e -> fraWin.close());
    }

}
