
package replete.ui.validation;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.ImageLib;
import replete.ui.tree.NodeBase;

public class NodeRoot extends NodeBase {


    ////////////
    // FIELDS //
    ////////////

    protected static ImageIcon icon = ImageLib.get(RepleteImageModel.VALIDATION_ROOT);
    protected static final String DEFAULT_LABEL = "Validation Problems";

    private String label = DEFAULT_LABEL;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getLabel() {
        return label;
    }

    // Mutators

    public void setLabel(String label) {
        this.label = label;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public boolean isCollapsible() {
        return false;
    }
    @Override
    public String toString() {
        return label;
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
