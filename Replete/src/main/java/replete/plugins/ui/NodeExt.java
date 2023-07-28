package replete.plugins.ui;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import replete.plugins.state.ExtensionState;
import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.ImageLib;
import replete.ui.tree.NodeBase;

public class NodeExt extends NodeBase {


    ////////////
    // FIELDS //
    ////////////

    // Static

    private static final ImageIcon icon    = ImageLib.get(RepleteImageModel.EXTENSION);
    private static final ImageIcon iconInv = ImageLib.get(RepleteImageModel.EXTENSION_INVALID);

    // Core

    private ExtensionState extState;
    private boolean valid;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NodeExt(ExtensionState e, boolean invalid) {
        extState = e;
        valid = invalid;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Icon getIcon(boolean expanded) {
        return valid ? icon : iconInv;
    }

    @Override
    public boolean isCollapsible() {
        return false;
    }

    @Override
    public String toString() {
        return extState.getId();
    }
}
