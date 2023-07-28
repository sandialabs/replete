package replete.ui.help;

import javax.swing.Icon;

import replete.ui.help.model.HelpTerm;
import replete.ui.images.concepts.ImageLib;
import replete.ui.tree.NodeBase;

public class NodeTerm extends NodeBase {


    ////////////
    // FIELDS //
    ////////////

    protected HelpTerm term;
    protected HelpTermGlob glob;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NodeTerm(HelpTerm term, HelpTermGlob glob) {
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
        return glob == null;
    }
    @Override
    public Icon getIcon(boolean expanded) {
        return ImageLib.get(glob == null ? HelpImageModel.TERM_WITH_CONTEXTS : HelpImageModel.TERM);
    }
    @Override
    public String toString() {
        return term.toString();
    }
}
