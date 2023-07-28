
package replete.ui.csv.nodes;

import java.awt.Color;

import javax.swing.Icon;

import replete.ui.csv.CsvColumnType;
import replete.ui.images.concepts.ImageLib;
import replete.ui.tree.NodeBase;

public class NodeType extends NodeBase {


    ////////////
    // FIELDS //
    ////////////

    private CsvColumnType type;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NodeType(CsvColumnType type) {
        this.type = type;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public CsvColumnType getType() {
        return type;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public boolean isCollapsible() {
        return false;
    }
    @Override
    public String toString() {
        return type.getDescription();
    }
    @Override
    public Icon getIcon(boolean expanded) {
        return ImageLib.get(type.getParentIcon());
    }
    @Override
    public Color getForegroundColor() {
        return Color.blue;
    }
}
