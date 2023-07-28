
package replete.ui.windows.notifications;

import replete.collections.tl.TLChangeListener;
import replete.collections.tl.events.TLChangeEvent;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.tabbed.RTabbedPane;
import replete.ui.windows.notifications.msg.NotificationError;
import replete.ui.windows.notifications.msg.NotificationInfo;
import replete.ui.windows.notifications.msg.NotificationTask;

public class NotificationTabbedPane extends RTabbedPane {


    ////////////
    // FIELDS //
    ////////////

    // Const

    public static final int INIT_WIDTH = 250;
    public static final int INIT_HEIGHT = 300;

    private NotificationModel notificationModel;

    private NotificationTabPanel<NotificationTask> pnlProg;
    private NotificationTabPanel<NotificationError> pnlErr;
    private NotificationTabPanel<NotificationInfo> pnlInfo;

    private boolean switchToTasksOnChange = false;
    private boolean switchToErrorsOnChange = false;
    private boolean switchToInfosOnChange = false;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NotificationTabbedPane(NotificationModel mdl) {
        notificationModel = mdl;
        Lay.TBLtg(this,
            "Progress",
                Lay.sp(pnlProg = new NotificationTabPanel<>(mdl, "Tasks", mdl.getTasks())),
                ImageLib.get(CommonConcepts.PROGRESS),
            "Errors",
                Lay.sp(pnlErr = new NotificationTabPanel<>(mdl, "Errors", mdl.getErrors())),
                ImageLib.get(CommonConcepts.ERROR),
            "Notifications",
                Lay.sp(pnlInfo = new NotificationTabPanel<>(mdl, "Notifications", mdl.getInfos())),
                ImageLib.get(CommonConcepts.INFO),
            "borders,eb=2b"
        );
        updateFromModel();

        getNotificationModel().getTasks().addChangeListener(new TLChangeListener<NotificationTask>() {
            public void stateChanged(TLChangeEvent<NotificationTask> e) {
                updateFromModel();
                if(switchToTasksOnChange) {
                    setSelectedIndex(0);
                }
            }
        });
        getNotificationModel().getErrors().addChangeListener(new TLChangeListener<NotificationError>() {
            public void stateChanged(TLChangeEvent<NotificationError> e) {
                updateFromModel();
                if(switchToErrorsOnChange) {
                    setSelectedIndex(1);
                }
            }
        });
        getNotificationModel().getInfos().addChangeListener(new TLChangeListener<NotificationInfo>() {
            public void stateChanged(TLChangeEvent<NotificationInfo> e) {
                updateFromModel();
                if(switchToInfosOnChange) {
                    setSelectedIndex(2);
                }
            }
        });
    }


    //////////
    // MISC //
    //////////

    protected void updateFromModel() {
        String ex = notificationModel.getTasks().size() == 0 ? "" : " (" + notificationModel.getTasks().size() + ")";
        setTitleAt("Progress", "Progress" + ex);
        ex = notificationModel.getErrors().size() == 0 ? "" : " (" + notificationModel.getErrors().size() + ")";
        setTitleAt("Errors", "Errors" + ex);
        ex = notificationModel.getInfos().size() == 0 ? "" : " (" + notificationModel.getInfos().size() + ")";
        setTitleAt("Notifications", "Notifications" + ex);
    }

    public void updateTimedInfo() {
        pnlProg.updateTimedInfo();
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public NotificationModel getNotificationModel() {
        return notificationModel;
    }

    // Mutators (Builder)

    public NotificationTabbedPane setSwitchToTasksOnChange(boolean switchToTasksOnChange) {
        this.switchToTasksOnChange = switchToTasksOnChange;
        return this;
    }
    public NotificationTabbedPane setSwitchToErrorsOnChange(boolean switchToErrorsOnChange) {
        this.switchToErrorsOnChange = switchToErrorsOnChange;
        return this;
    }
    public NotificationTabbedPane setSwitchToInfosOnChange(boolean switchToInfosOnChange) {
        this.switchToInfosOnChange = switchToInfosOnChange;
        return this;
    }
}
