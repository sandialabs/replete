package replete.ui.help.events;

import replete.ui.help.model.HelpPage;

public class PageContentChangedEvent extends PageEvent {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PageContentChangedEvent(HelpPage page) {
        super(page);
    }
}
