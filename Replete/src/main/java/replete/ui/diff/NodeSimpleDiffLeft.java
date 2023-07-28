
package replete.ui.diff;

import java.awt.Color;

import javax.swing.Icon;

import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.tree.NodeBase;

public class NodeSimpleDiffLeft extends NodeBase {


    ////////////
    // FIELDS //
    ////////////

    private String clue;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NodeSimpleDiffLeft(String clue) {
        this.clue = clue;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getClue() {
        return clue;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Icon getIcon(boolean expanded) {
        return ImageLib.get(CommonConcepts.PREV);
    }
    @Override
    public Color getForegroundColor() {
        return Color.black;
    }
    @Override
    public String toString() {
        return clue;
    }
}
