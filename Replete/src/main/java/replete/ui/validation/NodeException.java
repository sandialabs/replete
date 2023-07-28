
package replete.ui.validation;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import replete.text.StringUtil;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.tree.NodeBase;

public class NodeException extends NodeBase {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    protected static ImageIcon ICON  = ImageLib.get(CommonConcepts.EXCEPTION);

    // Core

    private ValidationMessage message;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NodeException(ValidationMessage message) {
        this.message = message;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        Throwable ex = message.getException();
        return
            "<html><i>(Exception)</i> " +
            "<b>" + ex.getClass().getName() + "</b>: " +
            StringUtil.cleanXmlCdata(ex.getMessage()) +
            "</html>"
        ;
    }
    @Override
    public Icon getIcon(boolean expanded) {
        return ICON;
    }
}
