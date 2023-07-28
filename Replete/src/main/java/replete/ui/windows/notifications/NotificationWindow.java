package replete.ui.windows.notifications;

import javax.swing.event.ChangeListener;

import replete.ui.windows.common.RWindow;
import replete.ui.windows.notifications.msg.NotificationInfo;
import replete.ui.windows.notifications.msg.NotificationTask;
import replete.ui.worker.RWorker;

public interface NotificationWindow extends RWindow {
    public NotificationStatusBar getStatusBar();
    public NotificationModel getNotificationModel();
    public boolean isShowStatusBar();
    public void setShowStatusBar(boolean show);
    public boolean isShowNotificationArea();
    public void setShowNotificationArea(boolean show);
    public NotificationTabbedPane getTabbedPane();
    public void addAreaShownChangeListener(ChangeListener listener);


    /////////////
    // DEFAULT //
    /////////////

    public default void addInfo(String title) {                                     // Convenience method for common usage
        NotificationInfo info = new NotificationInfo()
            .setTitle(title)
        ;
        getNotificationModel().getInfos().add(info);
    }

    public default void addTaskAndExecuteFg(String title, RWorker<?, ?> worker) {     // Convenience method for common usage
        addTaskAndExecute(title, worker, true, false);
    }

    public default void addTaskAndExecuteBg(String title, RWorker<?, ?> worker) {     // Convenience method for common usage
        addTaskAndExecute(title, worker, false, true);
    }

    public default void addTaskAndExecute(String title, RWorker<?, ?> worker,
                                          boolean waitCursor, boolean autoRemove) {     // Convenience method for common usage
        NotificationTask task = new NotificationTask()
            .setAction(worker)
            .setTitle(title)
            .setUseWaitCursor(waitCursor)
            .setAutoRemove(autoRemove)
        ;
        getNotificationModel().getTasks().add(task);
        worker.execute();
    }
}
