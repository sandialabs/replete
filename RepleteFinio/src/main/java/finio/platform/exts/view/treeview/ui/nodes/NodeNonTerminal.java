package finio.platform.exts.view.treeview.ui.nodes;

import java.awt.Color;

import javax.swing.Icon;

import finio.core.FImages;
import finio.core.FUtil;
import replete.ui.lay.Lay;

public class NodeNonTerminal extends NodeExpandable {


    ////////////
    // FIELDS //
    ////////////

    // Static

    private static final Color metaColor = Lay.clr("87090D");

    // Core

    private Object K;
    private Object V;
    private boolean realm;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public NodeNonTerminal() {
        // Default constructor (for copy)
    }
    public NodeNonTerminal(Object K, Object V, boolean realm) {
        this.K = K;
        this.V = V;
        this.realm = realm;
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
    public boolean isRealm() {
        return realm;
    }

    // Mutators (Builder)

    @Override
    public NodeNonTerminal setK(Object K) {
        this.K = K;
        return this;
    }
    @Override
    public NodeNonTerminal setV(Object V) {
        this.V = V;
        return this;
    }
    public NodeNonTerminal setRealm(boolean realm) {
        this.realm = realm;
        return this;
    }

    // Maintain Builder
    @Override
    public NodeNonTerminal setPaused(boolean paused) {
        return (NodeNonTerminal) super.setPaused(paused);
    }
    @Override
    public NodeNonTerminal setSimplified(String simplified) {
        return (NodeNonTerminal) super.setSimplified(simplified);
    }
    @Override
    public NodeNonTerminal setHover(boolean hover) {
        return (NodeNonTerminal) super.setHover(hover);
    }
    @Override
    public NodeNonTerminal setWorkingScope(boolean ws) {
        return (NodeNonTerminal) super.setWorkingScope(ws);
    }
    @Override
    public NodeNonTerminal setAnchor(boolean anchor) {
        return (NodeNonTerminal) super.setAnchor(anchor);
    }
    @Override
    public NodeNonTerminal setZoomed(boolean zoomed) {
        return (NodeNonTerminal) super.setZoomed(zoomed);
    }
    @Override
    public NodeNonTerminal setEditing(boolean editing) {
        return (NodeNonTerminal) super.setEditing(editing);
    }


    //////////
    // MISC //
    //////////

    @Override
    public NodeNonTerminal copyFrom(NodeFTree other) {
        super.copyFrom(other);

        if(other instanceof NodeNonTerminal) {
            K = ((NodeNonTerminal) other).K;
            V = ((NodeNonTerminal) other).V;
            realm = ((NodeNonTerminal) other).realm;
        }

        return this;
    }

    public NodeNonTerminal copy() {
        return new NodeNonTerminal().copyFrom(this);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Color getForegroundColor() {
        if(FUtil.isSysMetaKey(K)) {
            return metaColor;  // TODO: is this being used?
        }
        return super.getForegroundColor();
    }
    @Override
    public Icon getIcon(boolean expanded) {
        return FImages.createIconForNonTerminal(K, V, anchor, paused, realm, simplified != null);
    }
    @Override
    public String toString() {
        String ret = renderKey();
        if(simplified != null) {
            ret += " = " + simplified;
        } else if(FUtil.isSemiTerminal(V)) {
            ret += " = SEMI";
        }
        return ret;
    }
}
