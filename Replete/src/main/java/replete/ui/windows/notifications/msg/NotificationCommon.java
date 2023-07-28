package replete.ui.windows.notifications.msg;

import javax.swing.ImageIcon;

import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.windows.notifications.NotificationClickAction;

public class NotificationCommon {


    ////////////
    // FIELDS //
    ////////////

    private String title;
    private ImageIcon icon;
    private NotificationClickAction clickAction;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getTitle() {
        return title;
    }
    public ImageIcon getIcon() {
        return icon;
    }
    public NotificationClickAction getClickAction() {
        return clickAction;
    }

    // Mutators

    public NotificationCommon setTitle(String title) {
        this.title = title;
        return this;
    }
    public NotificationCommon setIcon(ImageIcon icon) {
        this.icon = icon;
        return this;
    }
    public NotificationCommon setClickAction(NotificationClickAction clickAction) {
        this.clickAction = clickAction;
        return this;
    }

    // Mutators (Extra)

    public NotificationCommon setIcon(ImageModelConcept concept) {
        icon = ImageLib.get(concept);
        return this;
    }
}
