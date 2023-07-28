package replete.ui.tree;

import java.awt.Color;
import java.io.Serializable;

import javax.swing.Icon;


// This class and all subclasses are used as the "UserObject" in the
// DefaultMutableTreeNode objects that make up the JTree's model.
// TODO: Why is this class Serializable??
public abstract class NodeBase implements INodeBase, Serializable, Cloneable {
    public Icon getIcon(boolean expanded) {
        return null;
    }
    public Color getForegroundColor() {
        return Color.black;
    }
    public boolean isBold() {
        return false;
    }
    public boolean isItalic() {
        return false;
    }

    // TODO: When nodes are added to the tree, we need to
    // check this value to know if we need to have it
    // initially expanded.  Currently RTree at *least*
    // checks the root's isCollapsible method when a new
    // model is set onto the tree.
    public boolean isCollapsible() {
        return true;
    }

    // The NodeBase, which is a specialized JTree's node's
    // user object that we use to easily provide visual
    // markup for tree nodes, can expose the underlying
    // data object that it is wrapping and using to determine
    // the visual representation this object provides to
    // the NodeBaseTreeRenderer.  If this gets traction
    // NodeBase *could* become generic (NodeBase<T>) but
    // who knows.
    public <T> T getWrappedObject() {
        return null;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch(CloneNotSupportedException e) {
            throw new RuntimeException(e.toString());
        }
    }
}
