package learning.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;

import replete.ui.lay.Lay;

public class LayLearningWindow extends JFrame /*implements ActionListener*/ {

//    private static int _im_Name = 2;

//    private Handler handler = new Handler();

    public LayLearningWindow() {
        super("Learning Window");
        // construct sub components and add to window

        // Swing & AWT?

        // AWT was Java's first UI library
        // - Heavyweight (uses LOTS of native comp)
        // Swing was released with 1.4?
        // - Lightweight (uses only native comp for top-level windows)

        //initBasicSwingUI();

        initLayUI();
    }

    private void initLayUI() {
//        JButton btn = new JButton("save");
//        JButton btn2 = new JButton("save2");
//
//        JPanel pnlBL = new JPanel();
//
//        Lay.BLtg(pnlBL,
//            "C", btn,
//            "S", Lay.FL("mb=[4,red]", "R",
//                btn2,
//                "bg=yellow,prefh=200"
//            )
//        );
//
//        add(pnlBL);
//
//        setSize(500, 500);
//        setLocationRelativeTo(null);

        Object[] colNames = new Object[] {"First", "Last"};
        Object[][] data = new Object[][] {
            {"Derek", "Trumbo"},
            {"Brenda", "Medina"}
        };
        JTable tblNames = new JTable(data, colNames);



        JButton btnCancel;
        Lay.BLtg(this,
            "N", Lay.lb("Hello there this is the application.", "bg=red"),
            "C", Lay.GL(2, 1,
                Lay.sp(tblNames),
                Lay.sp(Lay.lst("bg=green"))
            ),
            "W", Lay.sp(new JTree()),
            "S", Lay.FL("R",
                Lay.btn("&Save"),
                btnCancel = Lay.btn("&Cancel"),
                "bg=blue"
            ),
            "size=500,center"
        );

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void initBasicSwingUI() {
        BorderLayout layout = new BorderLayout();
        setLayout(layout);

        JLabel lbl = new JLabel("Hello there this is the application.");
        lbl.setBackground(Color.red);
        lbl.setOpaque(true);
        add(lbl, BorderLayout.NORTH);

        JPanel pnlButtons = new JPanel();
        pnlButtons.setBackground(Color.blue);
        pnlButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");

        JTree treLeft = new JTree();
        JScrollPane scrTree = new JScrollPane(treLeft);

        add(scrTree, BorderLayout.WEST);

        JPanel pnlCenter = new JPanel();
        pnlCenter.setLayout(new GridLayout(2, 1));

        Object[] colNames = new Object[] {"First", "Last"};
        Object[][] data = new Object[][] {
            {"Derek", "Trumbo"},
            {"Brenda", "Medina"}
        };
        JTable tblNames = new JTable(data, colNames);
        JScrollPane scrTable = new JScrollPane(tblNames);

        JList lstColors = new JList();
        JScrollPane scrList = new JScrollPane(lstColors);

        pnlCenter.add(scrTable);
        pnlCenter.add(scrList);

        add(pnlCenter, BorderLayout.CENTER);

        pnlButtons.add(btnSave);  // under management of FlowLayout
        pnlButtons.add(btnCancel);

        add(pnlButtons, BorderLayout.SOUTH);

        // Intelli-Sense

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                System.out.println("closing!");
//                dispose();

                // we need the UI to be responsive
                // never have long-running OPS on UI thread
                // all UI listeners executed on UI threadt
                // EDT (event dispatch thread)

                // we need the UI to be responsive
                // we will talk about worker threads
// the following loop would LOCK UP the UI
//                while(true) {
//
//                }
            }
        });

        setSize(500, 500);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        LayLearningWindow win = new LayLearningWindow();
        win.setVisible(true);
    }



    /*
    @Override
    public void actionPerformed(ActionEvent arg0) {
    }

    private class Handler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void actionPerformed(ActionEvent e) {
            // custom code for MYhandler
        }
    }
    */
}
