package replete.ui.windows.notifications;

import replete.collections.tl.TrackedList;
import replete.ui.windows.notifications.msg.NotificationError;
import replete.ui.windows.notifications.msg.NotificationInfo;
import replete.ui.windows.notifications.msg.NotificationTask;

public class NotificationModel {


    ////////////
    // FIELDS //
    ////////////

    private TrackedList<NotificationTask> tasks = new TrackedList<>();
    private TrackedList<NotificationError> errors = new TrackedList<>();
    private TrackedList<NotificationInfo> infos = new TrackedList<>();


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public TrackedList<NotificationTask> getTasks() {
        return tasks;
    }
    public TrackedList<NotificationError> getErrors() {
        return errors;
    }
    public TrackedList<NotificationInfo> getInfos() {
        return infos;
    }
}
