
package replete.ui.diff;

import java.awt.Color;

import javax.swing.Icon;

import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.tree.NodeBase;

public class NodeSimpleDiffComment extends NodeBase {


    ////////////
    // FIELDS //
    ////////////

    private String comment;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NodeSimpleDiffComment(String key) {
        comment = key;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getKey() {
        return comment;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Icon getIcon(boolean expanded) {
        return ImageLib.get(CommonConcepts.FILE);
    }
    @Override
    public Color getForegroundColor() {
        return Color.black;
    }
    @Override
    public String toString() {
        return comment;
    }
}
