
package replete.ui.csv.nodes;

import javax.swing.Icon;

import replete.ui.csv.AbstractCommonCsvColumn;
import replete.ui.tree.NodeBase;

public class NodeColumn extends NodeBase {


    ////////////
    // FIELDS //
    ////////////

    private AbstractCommonCsvColumn column;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NodeColumn(AbstractCommonCsvColumn column) {
        this.column = column;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public AbstractCommonCsvColumn getColumn() {
        return column;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return column.getName();
    }
    @Override
    public Icon getIcon(boolean expanded) {
        return column.getIcon();
    }
}
