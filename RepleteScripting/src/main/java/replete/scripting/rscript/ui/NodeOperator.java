package replete.scripting.rscript.ui;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import replete.scripting.rscript.parser.gen.ASTOperator;
import replete.ui.images.concepts.ImageLib;

public class NodeOperator extends NodeASTNode {


    ////////////
    // FIELDS //
    ////////////

    protected static ImageIcon icon = ImageLib.get(TreeImageModel.OPERATOR);
    private ASTOperator node;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public NodeOperator(ASTOperator node) {
        this.node = node;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public ASTOperator getNode() {
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
