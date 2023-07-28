package replete.scripting.rscript.ui;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import replete.scripting.rscript.parser.gen.ASTConstant;
import replete.ui.images.concepts.ImageLib;

public class NodeConstant extends NodeASTNode {


    ////////////
    // FIELDS //
    ////////////

    protected static ImageIcon icon = ImageLib.get(TreeImageModel.CONSTANT);
    private ASTConstant node;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public NodeConstant(ASTConstant node) {
        this.node = node;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public ASTConstant getNode() {
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
        return node.toString();
    }
}
