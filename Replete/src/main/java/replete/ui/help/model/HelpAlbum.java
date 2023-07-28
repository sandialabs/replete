package replete.ui.help.model;

import java.util.ArrayList;
import java.util.List;

import replete.ui.help.HelpProvider;

public class HelpAlbum {


    ////////////
    // FIELDS //
    ////////////

    private transient HelpProvider provider;       // Back-reference to context that provided this album (not serialized)
    private String id;                                         // Defined by the extension point, not modifiable by the user.
    private List<HelpPage>     pageRoots = new ArrayList<>();  // Each HelpPage is a hierarchy of HelpPage's
    private List<HelpTerm>     terms     = new ArrayList<>();
    private List<HelpTermLink> termLinks = new ArrayList<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public HelpAlbum(String id) {
        this.id = id;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public HelpProvider getProvider() {
        return provider;
    }
    public String getId() {
        return id;
    }
    public List<HelpPage> getPageRoots() {
        return pageRoots;
    }
    public List<HelpTerm> getTerms() {
        return terms;
    }
    public List<HelpTermLink> getTermLinks() {
        return termLinks;
    }

    // Mutators

    public HelpAlbum setProvider(HelpProvider contextManager) {
        this.provider = contextManager;
        return this;
    }
    public HelpAlbum addPageRoot(HelpPage pageRoot) {
        pageRoots.add(pageRoot);
        return this;
    }
    public void removePageRoot(HelpPage page) {
        pageRoots.remove(page);
    }
    public HelpAlbum addTerm(HelpTerm term) {
        terms.add(term);
        return this;
    }
    public HelpAlbum addTermLink(HelpTermLink termLink) {
        termLinks.add(termLink);
        return this;
    }


    //////////
    // MISC //
    //////////

    public void save() {
        provider.saveAlbum();
    }

    // This method is a compromise between not having parent references
    // (which makes each UI component have to keep track of "page paths"
    // instead) and requiring developers when constructing page
    // hierarchies by hand to remember to set the parent references
    // on individual pages.  This method is not just a convenience, but
    // also ensures any mistakes within the hierarchy with respect to
    // parent references are eliminated.
    public void initParentReferences() {
        for(HelpPage page : pageRoots) {
            page.setAlbum(this);
            page.setParent(null);
            for(HelpPage child : page.getChildren()) {
                initParentReferences(page, child);
            }
        }
    }
    private void initParentReferences(HelpPage parent, HelpPage page) {
        page.setAlbum(this);
        page.setParent(parent);
        for(HelpPage child : page.getChildren()) {
            initParentReferences(page, child);
        }
    }
}
