package replete.ui.windows.notifications.msg;

import javax.swing.ImageIcon;

import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.windows.notifications.NotificationClickAction;
import replete.ui.windows.notifications.ProgressBarStyle;
import replete.ui.worker.RWorker;

public class NotificationTask extends NotificationCommon {


    ///////////
    // FIELD //
    ///////////

    private String step;         // Used when action is null
    private RWorker action;
    private boolean autoRemove;
    private boolean useWaitCursor;
    private boolean addError;
    private ProgressBarStyle progressBarStyle;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getStep() {         // Only for when action == null
        return step;
    }
    public RWorker getAction() {
        return action;
    }
    public boolean isAutoRemove() {
        return autoRemove;
    }
    public boolean isUseWaitCursor() {
        return useWaitCursor;
    }
    public boolean isAddError() {
        return addError;
    }
    public ProgressBarStyle getProgressBarStyle() {
        return progressBarStyle;
    }
    @Override
    public String getTitle() {
        String baseTitle = super.getTitle();
        if(baseTitle == null) {
            if(action != null) {
                return action.getTitle();
            }
        }
        return baseTitle;
    }

    // Mutators (Builder)

    public NotificationTask setStep(String step) {
        this.step = step;
        return this;
    }
    public NotificationTask setAction(RWorker action) {
        this.action = action;
        return this;
    }
    public NotificationTask setAutoRemove(boolean autoRemove) {
        this.autoRemove = autoRemove;
        return this;
    }
    public NotificationTask setUseWaitCursor(boolean useWaitCursor) {
        this.useWaitCursor = useWaitCursor;
        return this;
    }
    public NotificationTask setAddError(boolean addError) {
        this.addError = addError;
        return this;
    }
    public NotificationTask setProgressBarStyle(ProgressBarStyle progressBarStyle) {
        this.progressBarStyle = progressBarStyle;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // (To Retain Builder Pattern)

    @Override
    public NotificationTask setTitle(String title) {
        super.setTitle(title);
        return this;
    }
    @Override
    public NotificationTask setIcon(ImageIcon icon) {
        super.setIcon(icon);
        return this;
    }
    @Override
    public NotificationTask setClickAction(NotificationClickAction clickAction) {
        super.setClickAction(clickAction);
        return this;
    }
    @Override
    public NotificationTask setIcon(ImageModelConcept concept) {
        return (NotificationTask) super.setIcon(concept);
    }
}
