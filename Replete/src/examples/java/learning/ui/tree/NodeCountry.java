package learning.ui.tree;

import java.awt.Color;

import javax.swing.Icon;

import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.tree.NodeBase;

public class NodeCountry extends NodeBase {

    private String country;

    public NodeCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public boolean isBold() {
        return true;
    }

    @Override
    public Color getForegroundColor() {
        return Color.blue;
    }

    @Override
    public Icon getIcon(boolean expanded) {
        return ImageLib.get(CommonConcepts.BOOKMARK);
    }

    @Override
    public String toString() {
        return country;
    }

}
