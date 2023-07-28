package replete.ui.validation;

import java.io.Serializable;

import javax.swing.ImageIcon;

import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;

public class MessageType implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    // Standard Ones

    public static final MessageType INFO  = new MessageType(MessageLevel.INFO, "Information", ImageLib.get(CommonConcepts.INFO));
    public static final MessageType WARN  = new MessageType(MessageLevel.WARN, "Warning",     ImageLib.get(CommonConcepts.WARNING));
    public static final MessageType ERROR = new MessageType(MessageLevel.ERROR,"Error",       ImageLib.get(CommonConcepts.ERROR));

    // Core

    private MessageLevel level;
    private String label;
    private ImageIcon icon;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public MessageType(MessageLevel level, String label, ImageIcon icon) {
        this.level = level;
        this.label = label;
        this.icon = icon;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public MessageLevel getLevel() {
        return level;
    }
    public String getLabel() {
        return label;
    }
    public ImageIcon getIcon() {
        return icon;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return label;
    }
}
