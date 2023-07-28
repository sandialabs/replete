package replete.scripting.rscript.ui;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import replete.scripting.rscript.parser.gen.ASTUnit;
import replete.ui.images.concepts.ImageLib;

public class NodeUnit extends NodeASTNode {


    ////////////
    // FIELDS //
    ////////////

    protected static ImageIcon icon = ImageLib.get(TreeImageModel.UNIT);
    private ASTUnit node;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public NodeUnit(ASTUnit node) {
        this.node = node;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public ASTUnit getNode() {
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
