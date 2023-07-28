package replete.ui.help.model;

import java.util.ArrayList;
import java.util.List;

public class HelpPage {


    ////////////
    // FIELDS //
    ////////////

    private HelpAlbum album;
    private HelpPage parent;   // Parent ("back") reference strategy chosen for help page hierarchy
    private String name;
    private HelpPageContent content = new HelpPageContent();
    private List<HelpPage> children = new ArrayList<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public HelpPage(String name) {
        this.name = name;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public HelpAlbum getAlbum() {
        return album;
    }
    public HelpPage getParent() {
        return parent;
    }
    public String getName() {
        return name;
    }
    public HelpPageContent getContent() {
        return content;
    }
    public List<HelpPage> getChildren() {
        return children;
    }

    // Accessors (Computed)

    public boolean hasContent() {
        return content != null && !content.isBlank();
    }
    public HelpPage[] getPagePath() {   // Includes self, requires parent references to be correct
        List<HelpPage> path = new ArrayList<>();
        path.add(0, this);
        HelpPage current = parent;
        while(current != null) {
            path.add(0, current);
            current = current.parent;
        }
        return path.toArray(new HelpPage[0]);
    }

    // Mutators

    public HelpPage setAlbum(HelpAlbum album) {
        this.album = album;
        return this;
    }
    public HelpPage setParent(HelpPage parent) {
        this.parent = parent;
        return this;
    }
    public HelpPage setName(String name) {
        this.name = name;
        return this;
    }
    public HelpPage setContent(HelpPageContent content) {
        this.content = content;
        return this;
    }
    public HelpPage addChildPage(HelpPage child) {
        children.add(child);
        return this;
    }


    //////////
    // MISC //
    //////////

    public void save() {
        album.save();
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return name;
    }
}
