package finio.platform.exts.view.treeview.ui.nodes;

import javax.swing.Icon;

import finio.core.FImages;
import finio.core.NonTerminal;
import finio.core.WorldRootKey;

public class NodeWorld extends NodeExpandable {


    ////////////
    // FIELDS //
    ////////////

    // Constant

    public static final String WORLD_KEY = "My World";   // Not a real key as it's above the top level map.

    // Core

    private NonTerminal W;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public NodeWorld() {
        // Default constructor (for copy)
    }
    public NodeWorld(NonTerminal W) {
        this.W = W;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    // The world node exists in an unknown context so has special key.
    @Override
    public Object getK() {
        return WorldRootKey.get();
    }
    @Override
    public Object getV() {
        return W;
    }

    // Mutators (Builder)

    @Override
    public NodeWorld setK(Object K) {
        throw new UnsupportedOperationException("Cannot set the root node's key.");
    }
    @Override
    public NodeWorld setV(Object W) {
        this.W = (NonTerminal) W;
        return this;
    }

    // Maintain Builder
    @Override
    public NodeWorld setPaused(boolean paused) {
        return (NodeWorld) super.setPaused(paused);
    }
    @Override
    public NodeWorld setSimplified(String simplified) {
        return (NodeWorld) super.setSimplified(simplified);
    }
    @Override
    public NodeWorld setHover(boolean hover) {
        return (NodeWorld) super.setHover(hover);
    }
    @Override
    public NodeWorld setWorkingScope(boolean ws) {
        return (NodeWorld) super.setWorkingScope(ws);
    }
    @Override
    public NodeWorld setAnchor(boolean anchor) {
        return (NodeWorld) super.setAnchor(anchor);
    }
    @Override
    public NodeWorld setZoomed(boolean zoomed) {
        return (NodeWorld) super.setZoomed(zoomed);
    }
    @Override
    public NodeWorld setEditing(boolean editing) {
        return (NodeWorld) super.setEditing(editing);
    }


    //////////
    // MISC //
    //////////

    @Override
    public NodeWorld copyFrom(NodeFTree other) {
        super.copyFrom(other);

        if(other instanceof NodeWorld) {
            W = ((NodeWorld) other).W;
        }

        return this;
    }

    public NodeWorld copy() {
        return new NodeWorld().copyFrom(this);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Icon getIcon(boolean expanded) {
        return FImages.createIconForWorld(paused);
    }
    @Override
    public boolean isCollapsible() {
        return false;
    }
    @Override
    public String toString() {
        return WORLD_KEY;
    }
}
