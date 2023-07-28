package replete.ui.help;

import javax.swing.Icon;

import replete.text.StringUtil;
import replete.ui.help.model.HelpTerm;
import replete.ui.images.concepts.ImageLib;
import replete.ui.tree.NodeBase;

public class NodeTermContext extends NodeBase {


    ////////////
    // FIELDS //
    ////////////

    private static final String UNKNOWN_CONTEXT = "(unknown context)";

    protected HelpTerm term;
    protected HelpTermGlob glob;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NodeTermContext(HelpTerm term, HelpTermGlob glob) {
        this.term = term;
        this.glob = glob;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public HelpTerm getTerm() {
        return term;
    }
    public HelpTermGlob getGlob() {
        return glob;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////


    @Override
    public boolean isCollapsible() {
        return false;
    }
    @Override
    public Icon getIcon(boolean expanded) {
        return ImageLib.get(HelpImageModel.TERM_CONTEXT);
    }
    @Override
    public String toString() {
        return
            "<html>" +
                (StringUtil.isBlank(glob.termLink.getContext()) ?
                    "<i>" + UNKNOWN_CONTEXT + "</i>" :
                    glob.termLink.getContext()) +
            "</html>"
        ;
    }
}
