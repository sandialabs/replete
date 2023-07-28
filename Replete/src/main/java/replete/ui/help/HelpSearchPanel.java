package replete.ui.help;

import java.awt.event.ActionListener;

import replete.ui.help.model.HelpDataModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.text.RTextField;
import replete.ui.tree.NodeEmptyRoot;
import replete.ui.tree.RTree;
import replete.ui.tree.RTreeNode;
import replete.ui.windows.Dialogs;

public class HelpSearchPanel extends HelpComponentPanel {


    ////////////
    // FIELDS //
    ////////////

    private HelpUiController uiController;
    private HelpDataModel dataModel;
    private RTree treSearch;
    private RTreeNode nRoot = new RTreeNode(new NodeEmptyRoot());
    private RTextField txtQuery;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public HelpSearchPanel(HelpUiController uiController, HelpDataModel dataModel) {
        this.uiController = uiController;
        this.dataModel = dataModel;

        Lay.BLtg(this,
            "N", Lay.BL(
                "W", Lay.lb("Search:", CommonConcepts.SEARCH),
                "C", txtQuery = Lay.tx(),
                "E", Lay.btn("Go", (ActionListener) e -> performSearch()),
                "gap=5,eb=5"
            ),
            "C", Lay.sp(treSearch = Lay.tr(nRoot))
        );

        treSearch.setMouseDragSelection(false);
        treSearch.setShowsRootHandles(true);
    }

    private void performSearch() {
        String query = txtQuery.getText();
        Dialogs.notImpl(getWindow(), "Search with query '" + query + "'");
    }
}
