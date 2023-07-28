package replete.ui.windows.common;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import replete.ui.button.RButton;
import replete.ui.lay.Lay;
import replete.ui.text.RTextField;
import replete.ui.windows.common.RWindowDemo.WindowListable;


public class CenterPanel extends JPanel {
    private JTextField txtCFKey = new RTextField("cfKey", 10).setSelectAll(true);
    private JTextField txtCDKey = new RTextField("cdKey", 10).setSelectAll(true);
    private JTextArea txtOutput = new JTextArea();

    public CenterPanel(final ChildWindowManager parent, final WindowListable listable) {
        JButton btnCFSingle = new RButton("RFrame Single Instance");
        btnCFSingle.addActionListener(e -> {
            parent.openChildWindow("single-frame", null, "YELLOW: (RFrame Single)");
        });
        JButton btnCFMulti = new RButton("RFrame Multi Instance");
        btnCFMulti.addActionListener(e -> {
            parent.openChildWindow("multi-frame", txtCFKey.getText(), "GREEN: " + txtCFKey.getText() + " (RFrame Multi)");
        });
        JButton btnCDSingleNonModal = new RButton("RDialog Single Instance");
        btnCDSingleNonModal.addActionListener(e -> {
            parent.openChildWindow("single-dialog", null, "BLUE: (RDialog Single)");
        });
        JButton btnCDMultiNonModal = new RButton("RDialog Multi Instance");
        btnCDMultiNonModal.addActionListener(e -> {
            parent.openChildWindow("multi-dialog", txtCDKey.getText(), "RED: " + txtCDKey.getText() + " (RDialog Multi)");
        });
        JButton btnCDSingleModal = new RButton("RDialog Single Instance Modal");
        btnCDSingleModal.addActionListener(e -> {
            parent.openChildWindow("single-dialog-modal", null, "ORANGE: (RDialog Single Modal)");
        });

        JButton btnCWDF = new RButton("CommonWindowDemoFrame");
        btnCWDF.addActionListener(e -> {
            parent.openChildWindow("cwdf", "" + System.currentTimeMillis());
        });
        JButton btnCWDD = new RButton("CommonWindowDemoDialog");
        btnCWDD.addActionListener(e -> {
            parent.openChildWindow("cwdd", "" + System.currentTimeMillis(), false);
        });

        JButton btnList = new RButton("List Windows");
        btnList.addActionListener(e -> {
            txtOutput.setText(listable.listWindows());
        });

        txtOutput.setEditable(false);

        Lay.BLtg(this,
            "C", Lay.GL(8, 2,
                btnCFSingle, Lay.p(),
                btnCFMulti, txtCFKey,
                btnCDSingleNonModal, Lay.p(),
                btnCDMultiNonModal, txtCDKey,
                btnCDSingleModal, Lay.p(),
                btnCWDF, Lay.p(),
                btnCWDD, Lay.p(),
                btnList, Lay.p()
            ),
            "E", Lay.sp(txtOutput, "pref=[150, 300]")
        );
    }

    public void setOutput(String listWindows) {
        txtOutput.setText(listWindows);
    }
}
