package replete.plugins.ui;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import replete.plugins.state.ExtensionPointState;
import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.ImageLib;
import replete.ui.tree.NodeBase;

public class NodeExtPoint extends NodeBase {


    ////////////
    // FIELDS //
    ////////////

    private static final ImageIcon icon = ImageLib.get(RepleteImageModel.EXTENSION_POINT);

    private ExtensionPointState extPointState;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NodeExtPoint(ExtensionPointState extPointState) {
        this.extPointState = extPointState;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Icon getIcon(boolean expanded) {
        return icon;
    }

    @Override
    public boolean isCollapsible() {
        return false;
    }

    @Override
    public String toString() {
        return extPointState.getId();
    }
}
