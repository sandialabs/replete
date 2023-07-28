package finio.platform.exts.view.treeview.ui;

import finio.ui.view.SelectionContextSegment;
import replete.ui.tree.RTreePath;

public class TreePanelSelectionContextSegment extends SelectionContextSegment {


    ////////////
    // FIELDS //
    ////////////

    protected FNode node;
    protected RTreePath path;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TreePanelSelectionContextSegment(Object K, Object V, FNode node, RTreePath path) {
        super(K, V);
        this.node = node;
        this.path = path;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public FNode getNode() {
        return node;
    }
    public RTreePath getPath() {
        return path;
    }
}
