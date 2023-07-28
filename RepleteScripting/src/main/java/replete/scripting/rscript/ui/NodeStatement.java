package replete.scripting.rscript.ui;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import replete.scripting.rscript.parser.gen.ASTStatement;
import replete.ui.images.concepts.ImageLib;

public class NodeStatement extends NodeASTNode {


    ////////////
    // FIELDS //
    ////////////

    protected static ImageIcon icon = ImageLib.get(TreeImageModel.STATEMENT);
    private ASTStatement node;
    private int index;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public NodeStatement(ASTStatement node, int index) {
        this.node = node;
        this.index = index;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public ASTStatement getNode() {
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
        return node.toString() + " " + index;
    }
}
