package replete.ui.tree;

import javax.swing.Icon;

import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;

public class NodeSimpleLabel extends NodeBase {


    ////////////
    // FIELDS //
    ////////////

    protected String label;
    protected Icon icon;
    protected boolean collapsible = true;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NodeSimpleLabel() {
        this(null, (Icon) null);
    }
    public NodeSimpleLabel(boolean collapsible) {
        this(null, (Icon) null);
        this.collapsible = collapsible;
    }
    public NodeSimpleLabel(String label) {
        this(label, (Icon) null);
    }
    public NodeSimpleLabel(String label, boolean collapsible) {
        this(label, (Icon) null);
        this.collapsible = collapsible;
    }
    public NodeSimpleLabel(String label, Icon icon) {
        this.label = label;
        this.icon = icon;
    }
    public NodeSimpleLabel(String label, ImageModelConcept concept) {
        this.label = label;
        icon = ImageLib.get(concept);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getLabel() {
        return label;
    }
    @Override
    public Icon getIcon(boolean expanded) {
        return icon;
    }
    @Override
    public boolean isCollapsible() {
        return collapsible;
    }

    // Mutators

    public void setLabel(String label) {
        this.label = label;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        if(label == null) {
            return "";
        }
        return label;
    }
}
