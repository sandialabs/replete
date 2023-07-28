package replete.ui.help.events;

import replete.ui.help.model.HelpPage;

public class PageEvent {


    ////////////
    // FIELDS //
    ////////////

    public HelpPage page;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PageEvent(HelpPage page) {
        this.page = page;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public HelpPage getPage() {
        return page;
    }
}
