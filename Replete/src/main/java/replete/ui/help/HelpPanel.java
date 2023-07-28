package replete.ui.help;

import replete.ui.help.model.HelpDataModel;
import replete.ui.lay.Lay;
import replete.ui.tabbed.RNotifPanel;

public class HelpPanel extends RNotifPanel {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public HelpPanel(HelpUiController uiController, HelpDataModel dataModel) {

        Lay.BLtg(this,
            "C", Lay.SPL(
                Lay.TBL(
                    "Contents",
                        HelpImageModel.TABLE_OF_CONTENTS,
                        new HelpTableOfContentsPanel(uiController, dataModel),
                    "Search",
                        HelpImageModel.SEARCH,
                        new HelpSearchPanel(uiController, dataModel),
                    "Index",
                        HelpImageModel.INDEX,
                        new HelpIndexPanel(uiController, dataModel),
                    "Bookmarks",
                        HelpImageModel.BOOKMARKS,
                        new HelpBookmarksPanel(uiController, dataModel)
                ),
                new HelpContentPanel(uiController, dataModel),
                "divpixel=400"
            )
        );

        uiController.select(null);
    }
}
