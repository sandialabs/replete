package replete.ui.help.model;

public class Bookmark {


    ////////////
    // FIELDS //
    ////////////

    private HelpPage page;
    private String comment;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public Bookmark(HelpPage page, String comment) {
        this.page = page;
        this.comment = comment;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public HelpPage getPage() {
        return page;
    }
    public String getComment() {
        return comment;
    }

    // Mutators

    public Bookmark setPage(HelpPage page) {
        this.page = page;
        return this;
    }
    public Bookmark setComment(String comment) {
        this.comment = comment;
        return this;
    }
}
