package replete.ui.nofire;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.tree.DefaultTreeModel;

import replete.ui.images.RepleteImageModel;
import replete.ui.lay.Lay;
import replete.ui.panels.GradientPanel;
import replete.ui.tree.RTreeNode;
import replete.ui.windows.escape.EscapeFrame;


public class NoFireDemoFrame extends EscapeFrame {


    ////////////
    // FIELDS //
    ////////////

    private JButton btn;
    private NoFireCheckBox chk;
    private NoFireRadioButton rad;
    private JRadioButton rad2;
    private NoFireComboBox cbo;
    private NoFireList lst;
    private NoFireTree tree;
    private NoFireTabbedPane tabs;
    private RTreeNode nRoot = new RTreeNode("Planets");
    private JTextArea txt;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NoFireDemoFrame() {
        super("No Fire Controls Demo");
        setIcon(RepleteImageModel.NOFIRE);

        btn = new JButton("button");
        chk = new NoFireCheckBox("NF Check Box");
        rad = new NoFireRadioButton("NF Radio Button");
        rad2 = new JRadioButton("other");
        cbo = new NoFireComboBox(new String[] {"blue", "red", "green"});
        lst = new NoFireList(new String[] {"jupiter", "mars", "venus", "earth"});
        nRoot.add(new RTreeNode("jupiter"));
        nRoot.add(new RTreeNode("mars"));
        nRoot.add(new RTreeNode("venus"));
        nRoot.add(new RTreeNode("earth"));
        tree = new NoFireTree(nRoot);
        tabs = new NoFireTabbedPane();
        tabs.add("One", Lay.FL(Lay.lb("One")));
        tabs.add("Two", Lay.FL(Lay.lb("Two")));
        tabs.add("Three", Lay.FL(Lay.lb("Three")));

        tabs.addChangeListener(e -> output("JTabbedPane changed (" + tabs.getSelectedIndex() + ")"));

        btn.addActionListener(e -> output("JButton clicked"));
        chk.addActionListener(e -> output("JCheckBox changed (actionL): " + chk.isSelected()));
        chk.addItemListener(e -> output("JCheckBox changed (itemL): " + chk.isSelected()));
        rad.addActionListener(e -> output("JRadioButton changed (actionL): "  + rad.isSelected()));
        rad.addItemListener(e -> output("JRadioButton changed (itemL): "  + rad.isSelected()));
        cbo.addActionListener(e -> output("NoFireComboBox changed (actionL): " + cbo.getSelectedIndex() + " " + cbo.getSelectedItem()));
        cbo.addItemListener(e -> output("NoFireComboBox changed (itemL): " + cbo.getSelectedIndex() + " " + cbo.getSelectedItem()));
        lst.addListSelectionListener(e -> output("NoFireList changed: " + lst.getSelectedIndex() + " " + lst.getSelectedValue()));
        tree.addTreeSelectionListener(e -> output("NoFireTree changed: " + Arrays.toString(tree.getSelectionPaths())));

        Lay.grp(rad, rad2);

        JButton btnCheck = new JButton("JCheckBox.setSelected(!chk.isSelected())");
        btnCheck.addActionListener(e -> chk.setSelected(!chk.isSelected()));

        JButton btnCheckNF = new JButton("NoFireCheckBox.setSelectedNoFire(!chk.isSelected())");
        btnCheckNF.addActionListener(e -> chk.setSelectedNoFire(!chk.isSelected()));

        JButton btnRadio = new JButton("JRadioButton.setSelected(true)");
        btnRadio.addActionListener(e -> rad.setSelected(true));

        JButton btnRadioNF = new JButton("NoFireRadioButton.setSelectedNoFire(true)");
        btnRadioNF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rad.setSelectedNoFire(true);
            }
        });
        JButton btnCombo = new JButton("JComboBox.setSelectedIndex(2);");
        btnCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cbo.setSelectedIndex(2);
            }
        });
        JButton btnComboNF = new JButton("NoFireComboBox.setSelectedIndexNoFire(2)");
        btnComboNF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cbo.setSelectedIndexNoFire(2);
            }
        });
        JButton btnList = new JButton("JList.setSelectedIndex(2)");
        btnList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lst.setSelectedIndex(2);
            }
        });
        JButton btnListNF = new JButton("NoFireList.setSelectedIndexNoFire(2)");
        btnListNF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lst.setSelectedIndexNoFire(2);
            }
        });
        JButton btnTree = new JButton("JTree.setSelectionInterval(1, 1)");
        btnTree.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tree.setSelectionInterval(1, 1);
//                tree.clearSelection();
            }
        });
        JButton btnTreeNF = new JButton("NoFireTree.setSelectionIntervalNoFire(1, 1)");
        btnTreeNF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tree.setSelectionIntervalNoFire(1, 1);
//                tree.clearSelectionNoFire();
            }
        });
        JButton btnTabs = new JButton("JTabbedPane.setSelectedIndex(1);");
        btnTabs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tabs.setSelectedIndex(1);
            }
        });
        JButton btnTabsNF = new JButton("NoFireTabbedPane.setSelectedIndexNoFire(1)");
        btnTabsNF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tabs.setSelectedIndexNoFire(1);
            }
        });

        JButton btnClearList = new JButton("Clear List");
        btnClearList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lst.setListData(new Object[0]);
            }
        });
        JButton btnClearListNF = new JButton("Clear List No Fire");
        btnClearListNF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lst.setNoFireEnabled(true);
                lst.setListData(new Object[0]);
                lst.setNoFireEnabled(false);
            }
        });
        JButton btnClearTree = new JButton("Clear Tree");
        btnClearTree.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DefaultTreeModel m = tree.getModel();
                for(int n = nRoot.getChildCount() - 1; n >= 0; n--) {
                    m.removeNodeFromParent((RTreeNode) nRoot.getChildAt(n));
                }
            }
        });
        JButton btnClearTreeNF = new JButton("Clear Tree No Fire");
        btnClearTreeNF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tree.setNoFireEnabled(true);
                DefaultTreeModel m = tree.getModel();
                for(int n = nRoot.getChildCount() - 1; n >= 0; n--) {
                    m.removeNodeFromParent((RTreeNode) nRoot.getChildAt(n));
                }
                tree.setNoFireEnabled(false);
            }
        });
        JButton btnRestoreList = new JButton("Restore List");
        btnRestoreList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lst.setListData(new String[] {"jupiter", "mars", "venus", "earth"});
            }
        });
        JButton btnRestoreTree = new JButton("Restore Tree");
        btnRestoreTree.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                nRoot.removeAllChildren();
                nRoot.add(new RTreeNode("jupiter"));
                nRoot.add(new RTreeNode("mars"));
                nRoot.add(new RTreeNode("venus"));
                nRoot.add(new RTreeNode("earth"));
                tree.updateUI();
            }
        });

//UIDebugUtil.enableColor();
        Color clr = Color.red;
        JPanel pnlTop = new GradientPanel(clr, clr.darker().darker());
        Lay.FLtg(pnlTop, "L", Lay.lb("No Fire Controls", "fg=white"), "exb=5tbl");
        JPanel pnlMid = new GradientPanel(clr, clr.darker().darker());
        Lay.FLtg(pnlMid, "L", Lay.lb("Programmatic Selection Change Buttons", "fg=white"), "exb=5tbl");
        JPanel pnlBot = new GradientPanel(clr, clr.darker().darker());
        Lay.FLtg(pnlBot, "L", Lay.lb("Selection Change Output", "fg=white"), "exb=5tbl");

        Lay.GLtg(this, 3, 1,
            Lay.BL(
                "N", pnlTop,
                "C", Lay.BxL("Y",
                    Lay.GL(1, 5,
                        Lay.FL("C", 0, 0, btn),
                        Lay.FL("C", 0, 0, chk),
                        Lay.FL("C", 0, 0, rad),
                        Lay.FL("C", 0, 0, rad2),
                        Lay.FL("C", 0, 0, cbo),
                        "dimH=35"
                    ),
                    Lay.GL(1, 3,
                        Lay.sp(lst),
                        Lay.sp(tree),
                        tabs
                    ),
                    "eb=10"
                )
            ),
            Lay.BL(
                "N", pnlMid,
                "C", Lay.GL(9, 2,
                    btnCheck, btnCheckNF,
                    btnRadio, btnRadioNF,
                    btnCombo, btnComboNF,
                    btnList, btnListNF,
                    btnTree, btnTreeNF,
                    btnTabs, btnTabsNF,
                    btnClearList, btnClearListNF,
                    btnClearTree, btnClearTreeNF,
                    btnRestoreList, btnRestoreTree,
                    "eb=10"
                )
            ),
            Lay.BL(
                "N", pnlBot,
                "C", Lay.sp(
                    txt = Lay.txa("", "editable=false"),
                    "augb=eb(10)"
                )
            ),
            "size=[600,700],center"
        );
    }

    private void output(String str) {
        txt.append(str + "\n");
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        NoFireDemoFrame frame = new NoFireDemoFrame();
        frame.setVisible(true);
    }
}
