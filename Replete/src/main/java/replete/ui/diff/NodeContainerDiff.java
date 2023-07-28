
package replete.ui.diff;

import java.awt.Color;

import javax.swing.Icon;

import replete.diff.ContainerComparison;
import replete.diff.Importance;
import replete.ui.ColorLib;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.tree.NodeBase;

public class NodeContainerDiff extends NodeBase {


    ////////////
    // FIELDS //
    ////////////

    private Importance importance;
    private String key;
    private ContainerComparison difference;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NodeContainerDiff(Importance importance, String key, ContainerComparison difference) {
        this.importance = importance;
        this.key = key;
        this.difference = difference;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public Importance getImportance() {
        return importance;
    }
    public String getKey() {
        return key;
    }
    public ContainerComparison getDifference() {
        return difference;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Icon getIcon(boolean expanded) {
        if(difference.isDiff()) {
            if(importance == null) {
                return ImageLib.get(CommonConcepts.CHANGE);
            } else if(importance == Importance.LOW) {
                return ImageLib.get(CommonConcepts.INFO);
            } else if(importance == Importance.MEDIUM) {
                return ImageLib.get(CommonConcepts.WARNING);
            }
            return ImageLib.get(CommonConcepts.EXCEPTION);
        }
        return ImageLib.get(CommonConcepts.COMPLETE);
    }
    @Override
    public Color getForegroundColor() {
        if(difference.isDiff()) {
            return Color.red;
        }
        return ColorLib.GREEN_STRONG;
    }
    @Override
    public String toString() {
        return key;
    }
}
