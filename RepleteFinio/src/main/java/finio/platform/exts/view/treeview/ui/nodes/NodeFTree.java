package finio.platform.exts.view.treeview.ui.nodes;

import finio.core.FUtil;
import finio.core.warnings.UnexpandableWarning;
import replete.ui.tree.NodeBase;

public abstract class NodeFTree extends NodeBase {


    ////////////
    // FIELDS //
    ////////////

    protected boolean hover = false;
    protected boolean ws = false;
    protected boolean anchor = false;
    protected boolean zoomed = false;
    protected boolean editing = false;

    private static int constructedNodes = 0;
    public static int getConstructedNodes() {
        return constructedNodes;
    }

    public NodeFTree() {
        synchronized(NodeFTree.class) {
            constructedNodes++;
        }
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isHover() {
        return hover;
    }
    public boolean isWorkingScope() {
        return ws;
    }
    public boolean isAnchor() {
        return anchor;
    }
    public boolean isZoomed() {
        return zoomed;
    }
    public boolean isEditing() {
        return editing;
    }

    // Mutators (Builder)

    public NodeFTree setHover(boolean h) {
        hover = h;
        return this;
    }
    public NodeFTree setWorkingScope(boolean scope) {
        ws = scope;
        return this;
    }
    public NodeFTree setAnchor(boolean isAnchor) {
        anchor = isAnchor;
        return this;
    }
    public NodeFTree setZoomed(boolean isZoomed) {
        zoomed = isZoomed;
        return this;
    }
    public NodeFTree setEditing(boolean editing) {
        this.editing = editing;
        return this;
    }


    //////////////
    // ABSTRACT //
    //////////////

    public abstract Object getK();
    public abstract Object getV();
    public abstract NodeFTree setK(Object K);
    public abstract NodeFTree setV(Object V);


    //////////
    // MISC //
    //////////

    public String renderKey() {
        Object K = getK();
        if(K == null) {
            return FUtil.NULL_TEXT;
        }
        return FUtil.renderKey(K, false);
    }

    // This is for child classes to have consistent rendering
    // of value objects.  This isn't actually used right now,
    // as rendering is happening by the tree renderers/editors.
    public String renderValue(Object V) {

        if(FUtil.isNull(V)) {
            return FUtil.NULL_TEXT;

        } else if(FUtil.isStringOrChar(V)) {
            return "\"" + V + "\"";

        } else if(FUtil.isUnexpandableWarning(V)) {
            return "<" + ((UnexpandableWarning) V).getReason() + ">";

        } else if(FUtil.isUnrecognizedNativeObject(V)) {
            return FUtil.toDiagnosticString(V);
        }

        return V.toString();  // Number, Boolean
    }

    public NodeFTree copyFrom(NodeFTree other) {
         hover   = other.hover;
         ws      = other.ws;
         anchor  = other.anchor;
         zoomed  = other.zoomed;
         editing = other.editing;
         return this;
    }
}
