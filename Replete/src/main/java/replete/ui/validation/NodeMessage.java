
package replete.ui.validation;

import javax.swing.Icon;

import replete.text.StringUtil;
import replete.ui.tree.NodeBase;

public class NodeMessage extends NodeBase {


    ////////////
    // FIELDS //
    ////////////

    private ValidationMessage message;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NodeMessage(ValidationMessage message) {
        this.message = message;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return
            "<html><i>(" + message.getType().getLabel() + ")</i> " +
            "<b>" + StringUtil.cleanXmlCdata(message.getReason()) + "</b>" +
            (message.getEvidence() != null ? " {" + StringUtil.cleanXmlCdata(message.getEvidence()) + "}" : "") +
            "</html>"
        ;
    }
    @Override
    public Icon getIcon(boolean expanded) {
        return message.getType().getIcon();
    }
}
