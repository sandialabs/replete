package replete.scripting.rscript.ui;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import replete.scripting.rscript.parser.gen.ASTFunction;
import replete.ui.images.concepts.ImageLib;

public class NodeFunction extends NodeASTNode {


    ////////////
    // FIELDS //
    ////////////

    protected static ImageIcon icon = ImageLib.get(TreeImageModel.FUNCTION);
    private ASTFunction node;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public NodeFunction(ASTFunction node) {
        this.node = node;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public ASTFunction getNode() {
        return node;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Icon getIcon(boolean expanded) {
        return icon;
    }

    @Override
    public String toString() {
        return node.toString() + "(...)";
    }
}
