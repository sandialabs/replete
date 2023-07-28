package replete.ui.windows.notifications.msg;

import javax.swing.ImageIcon;

import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.windows.notifications.NotificationClickAction;

public class NotificationInfo extends NotificationCommon {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // (To Retain Builder Pattern)

    @Override
    public NotificationInfo setTitle(String title) {
        super.setTitle(title);
        return this;
    }
    @Override
    public NotificationInfo setIcon(ImageIcon icon) {
        super.setIcon(icon);
        return this;
    }
    @Override
    public NotificationInfo setClickAction(NotificationClickAction clickAction) {
        super.setClickAction(clickAction);
        return this;
    }
    @Override
    public NotificationInfo setIcon(ImageModelConcept concept) {
        return (NotificationInfo) super.setIcon(concept);
    }
}
