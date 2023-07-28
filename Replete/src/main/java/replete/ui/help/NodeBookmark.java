package replete.ui.help;

import javax.swing.Icon;

import replete.text.StringUtil;
import replete.ui.help.model.Bookmark;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.tree.NodeBase;

public class NodeBookmark extends NodeBase {


    ////////////
    // FIELDS //
    ////////////

    protected Bookmark bookmark;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NodeBookmark(Bookmark bookmark) {
        this.bookmark = bookmark;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public Bookmark getBookmark() {
        return bookmark;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Icon getIcon(boolean expanded) {
        return ImageLib.get(CommonConcepts.BOOKMARK);
    }
    @Override
    public String toString() {
        String comment = bookmark.getComment();
        String extra = !StringUtil.isBlank(comment) ? " - <i>" + comment + "</i>" : "";
        return bookmark.getPage().getName() + extra;
    }
}
