package finio.platform.exts.view.treeview.ui.nodes;


public abstract class NodeExpandable extends NodeFTree {


    ////////////
    // FIELDS //
    ////////////

    protected boolean paused;
    protected String simplified;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isPaused() {
        return paused;
    }
    public String getSimplified() {
        return simplified;
    }

    // Mutators (Builder)

    public NodeExpandable setPaused(boolean paused) {
        this.paused = paused;
        return this;
    }
    public NodeExpandable setSimplified(String simplified) {
        this.simplified = simplified;
        return this;
    }

    // Maintain Builder
    @Override
    public NodeExpandable setHover(boolean hover) {
        return (NodeExpandable) super.setHover(hover);
    }
    @Override
    public NodeExpandable setWorkingScope(boolean ws) {
        return (NodeExpandable) super.setWorkingScope(ws);
    }
    @Override
    public NodeExpandable setAnchor(boolean anchor) {
        return (NodeExpandable) super.setAnchor(anchor);
    }
    @Override
    public NodeExpandable setZoomed(boolean zoomed) {
        return (NodeExpandable) super.setZoomed(zoomed);
    }
    @Override
    public NodeExpandable setEditing(boolean editing) {
        return (NodeExpandable) super.setEditing(editing);
    }


    //////////
    // MISC //
    //////////

    @Override
    public NodeExpandable copyFrom(NodeFTree other) {
        super.copyFrom(other);

        if(other instanceof NodeExpandable) {
            paused = ((NodeExpandable) other).paused;
            simplified = ((NodeExpandable) other).simplified;
        }

        return this;
    }
}
