package replete.scripting.rscript.ui;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import replete.scripting.rscript.parser.gen.ASTKeyValuePair;
import replete.ui.images.concepts.ImageLib;

public class NodeKeyValuePair extends NodeASTNode {


    ////////////
    // FIELDS //
    ////////////

    protected static ImageIcon icon = ImageLib.get(TreeImageModel.KEY_VALUE);
    private ASTKeyValuePair node;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public NodeKeyValuePair(ASTKeyValuePair node) {
        this.node = node;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public ASTKeyValuePair getNode() {
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
