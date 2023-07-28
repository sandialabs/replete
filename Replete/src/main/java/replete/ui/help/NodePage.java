package replete.ui.help;

import javax.swing.Icon;

import replete.ui.help.model.HelpPage;
import replete.ui.images.concepts.ImageLib;
import replete.ui.tree.NodeBase;

public class NodePage extends NodeBase {


    ////////////
    // FIELDS //
    ////////////

    protected HelpPage page;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NodePage(HelpPage page) {
        this.page = page;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public HelpPage getPage() {
        return page;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Icon getIcon(boolean expanded) {
        boolean root = page.getParent() == null;
        return ImageLib.get(
            root ? (expanded ? HelpImageModel.TOC_PAGE_ROOT_EXP : HelpImageModel.TOC_PAGE_ROOT) :
                (page.getChildren().isEmpty() ? HelpImageModel.TOC_PAGE_LEAF :
                    (page.hasContent() ? HelpImageModel.TOC_PAGE_MID_CONT :
                        HelpImageModel.TOC_PAGE_MID_EMPTY))
        );
    }
    @Override
    public String toString() {
        return page.getName();
    }
}
