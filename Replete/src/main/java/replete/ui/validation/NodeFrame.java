
package replete.ui.validation;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.ImageLib;
import replete.ui.tree.NodeBase;

public class NodeFrame extends NodeBase {


    ////////////
    // FIELDS //
    ////////////

    protected static ImageIcon icon = ImageLib.get(RepleteImageModel.VALIDATION_FRAME);
    private String name;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NodeFrame(String name) {
        this.name = name;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return name;
    }
    @Override
    public Icon getIcon(boolean expanded) {
        return icon;
    }
    @Override
    public Color getForegroundColor() {
        return Color.blue;
    }
}
