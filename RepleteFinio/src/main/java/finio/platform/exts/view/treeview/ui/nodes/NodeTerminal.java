package finio.platform.exts.view.treeview.ui.nodes;

import javax.swing.Icon;

import finio.core.FImages;

public class NodeTerminal extends NodeFTree {


    ////////////
    // FIELDS //
    ////////////

    // Core

    private Object K;
    private Object V;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public NodeTerminal() {
        // Default constructor (for copy)
    }
    public NodeTerminal(Object K, Object V) {
        this.K = K;
        this.V = V;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    @Override
    public Object getK() {
        return K;
    }
    @Override
    public Object getV() {
        return V;
    }

    // Mutators (Builder)

    @Override
    public NodeTerminal setK(Object K) {
        this.K = K;
        return this;
    }
    @Override
    public NodeTerminal setV(Object V) {
        this.V = V;
        return this;
    }

    // Maintain Builder
    @Override
    public NodeTerminal setHover(boolean hover) {
        return (NodeTerminal) super.setHover(hover);
    }
    @Override
    public NodeTerminal setWorkingScope(boolean ws) {
        return (NodeTerminal) super.setWorkingScope(ws);
    }
    @Override
    public NodeTerminal setAnchor(boolean anchor) {
        return (NodeTerminal) super.setAnchor(anchor);
    }
    @Override
    public NodeTerminal setZoomed(boolean zoomed) {
        return (NodeTerminal) super.setZoomed(zoomed);
    }
    @Override
    public NodeTerminal setEditing(boolean editing) {
        return (NodeTerminal) super.setEditing(editing);
    }


    //////////
    // MISC //
    //////////

    @Override
    public NodeTerminal copyFrom(NodeFTree other) {
        super.copyFrom(other);

        if(other instanceof NodeTerminal) {
            K = ((NodeTerminal) other).K;
            V = ((NodeTerminal) other).V;
        }

        return this;
    }

    public NodeTerminal copy() {
        return new NodeTerminal().copyFrom(this);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Icon getIcon(boolean expanded) {
        return FImages.createIconForTerminal(K, V, anchor);
    }
    @Override
    public String toString() {
        return renderKey() + " = " + renderValue(V);
    }
}
