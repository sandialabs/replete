package finio.platform.exts.view.treeview.ui;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class FTreeOptionsPanel extends JPanel {


    ////////////
    // FIELDS //
    ////////////

    private FTreeOptionsModel optModel;

    private JCheckBox chkA;
    private JCheckBox chkB;
    private JCheckBox chkC;

    public FTreeOptionsPanel(FTreeOptionsModel optM) {
        optModel = optM;
//
//        Lay.BxLtg(this, "Y",
//            chkA = new JCheckBox("Generic Map Icons"),
//            chkB = new JCheckBox("Double Quoted Values"),
//            chkC = new JCheckBox("Hide " + AConst.SYS_META_KEY)
//        );
//
//        chkA.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                optModel.setOptionA(chkA.isSelected());
//            }
//        });
//        chkB.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                optModel.setOptionA(chkB.isSelected());
//            }
//        });
//        chkC.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                optModel.setOptionA(chkC.isSelected());
//            }
//        });
    }
}
