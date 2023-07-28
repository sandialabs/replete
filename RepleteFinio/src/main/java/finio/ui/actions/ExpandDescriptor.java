package finio.ui.actions;

import finio.platform.exts.view.treeview.ui.FNode;

public class ExpandDescriptor {


    ////////////
    // FIELDS //
    ////////////

    private FNode node;
    private int level;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ExpandDescriptor(FNode node, int level) {
        this.node = node;
        this.level = level;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public FNode getNode() {
        return node;
    }
    public int getLevel() {
        return level;
    }
}
