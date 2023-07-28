package replete.ui.help.events;

import replete.ui.help.model.HelpPage;

public class PageSelectionEvent extends PageEvent {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PageSelectionEvent(HelpPage page) {
        super(page);
    }
}
