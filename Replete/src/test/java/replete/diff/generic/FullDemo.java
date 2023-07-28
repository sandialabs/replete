package replete.diff.generic;

import replete.diff.DiffTreePanel;
import replete.diff.generic.GenericObjectDiffer;
import replete.diff.generic.GenericObjectDifferParams;
import replete.diff.generic.GenericDifferDemo.GenericDifferDemoObject;
import replete.ui.button.RButton;
import replete.ui.button.RCheckBox;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;
import replete.ui.text.RTextArea;

public class FullDemo {

    private RTextArea txaWhiteList;
    private RTextArea txaBlackList;

    private RCheckBox chkUseBlacklist;
    private RCheckBox chkUseWhitelist;

    private RButton btnRediff = new RButton("Refresh Diff");

    private Object o1;
    private Object o2;

    DiffTreePanel centerDiffPanel;

    public FullDemo(Object o1, Object o2) {
        txaWhiteList = new RTextArea();
        txaBlackList = new RTextArea();

        chkUseBlacklist = new RCheckBox("Use Blacklist");
        chkUseWhitelist = new RCheckBox("Use Whitelist");

        this.o1 = o1;
        this.o2 = o2;

        centerDiffPanel = new DiffTreePanel();

        GenericObjectDifferParams params = new GenericObjectDifferParams();
        centerDiffPanel.setCurrentResult(new GenericObjectDiffer(params).diff(o1, o2), "Comparison Example", "LeftObject", "RightObject");

        chkUseBlacklist.addActionListener(e -> rediff());
        chkUseWhitelist.addActionListener(e -> rediff());
        btnRediff.addActionListener(e -> rediff());

        RPanel paramPanel = new RPanel();
        Lay.BxLtg(paramPanel,
            chkUseWhitelist,
            chkUseBlacklist,
            Lay.lb("Whitelist: "),
            txaWhiteList,
            Lay.lb("Blacklist: "),
            txaBlackList,
            btnRediff
        );

        Lay.GLtg(Lay.fr(), 1, 2,
            "C", centerDiffPanel,
            "E", Lay.BxL(paramPanel,
                chkUseWhitelist,
                chkUseBlacklist,
                Lay.lb("Whitelist: "),
                txaWhiteList,
                Lay.lb("Blacklist: "),
                txaBlackList,
                btnRediff
            ),
            "size=800,center,visible"
        );
    }

    private void rediff() {
        GenericObjectDifferParams params = new GenericObjectDifferParams();
        params.setUseFunctionWhitelist(chkUseWhitelist.isSelected());
        params.setUseFunctionBlacklist(chkUseBlacklist.isSelected());

        String[] wholeWhiteList = txaWhiteList.getText().split("\n");
        for(int i = 0; i < wholeWhiteList.length; i++) {
            params.addFieldToWhitelist(wholeWhiteList[i].trim());
        }
        String[] wholeBlackList = txaBlackList.getText().split("\n");
        for(int i = 0; i < wholeBlackList.length; i++) {
            params.addFieldToBlacklist(wholeBlackList[i].trim());
        }
        centerDiffPanel.setCurrentResult(new GenericObjectDiffer(params).diff(o1, o2), "Comparison Example", "LeftObject", "RightObject");
    }

    public static void main(String[] args) {
        Object o1 = new GenericDifferDemoObject(new GenericDifferDemoObject(null, false), false);
        Object o2 = new GenericDifferDemoObject(new GenericDifferDemoObject(null, true), true);

        new FullDemo(o1, o2);
    }
}

