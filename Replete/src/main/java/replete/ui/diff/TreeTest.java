package replete.ui.diff;

import replete.diff.DiffResult;

public class TreeTest {

    public static void main(String[] args) {
        DiffResult result = TreeTestData.createTestData1();
        makeTree(result);
    }

    public static void makeTree(DiffResult result) {
        TreeTestFrame frame = new TreeTestFrame();
        frame.setCurrentResult(result);
        frame.setVisible(true);
    }

}
