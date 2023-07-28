package replete.ui.windows.notifications.msg;

import javax.swing.ImageIcon;

import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.windows.notifications.NotificationClickAction;

public class NotificationError extends NotificationCommon {


    ///////////
    // FIELD //
    ///////////

    private Throwable error;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public Throwable getError() {
        return error;
    }
    public NotificationError setError(Throwable error) {
        this.error = error;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // (To Retain Builder Pattern)

    @Override
    public NotificationError setTitle(String title) {
        super.setTitle(title);
        return this;
    }
    @Override
    public NotificationError setIcon(ImageIcon icon) {
        super.setIcon(icon);
        return this;
    }
    @Override
    public NotificationError setClickAction(NotificationClickAction clickAction) {
        super.setClickAction(clickAction);
        return this;
    }
    @Override
    public NotificationError setIcon(ImageModelConcept concept) {
        return (NotificationError) super.setIcon(concept);
    }
}
