package replete.ui.help.model;

public class HelpTermLink {


    ////////////
    // FIELDS //
    ////////////

    private HelpTerm term;
    private String context;
    private HelpPage page;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public HelpTerm getTerm() {
        return term;
    }
    public String getContext() {
        return context;
    }
    public HelpPage getPage() {
        return page;
    }

    // Mutators

    public HelpTermLink setTerm(HelpTerm term) {
        this.term = term;
        return this;
    }
    public HelpTermLink setContext(String context) {
        this.context = context;
        return this;
    }
    public HelpTermLink setPage(HelpPage page) {
        this.page = page;
        return this;
    }
}
