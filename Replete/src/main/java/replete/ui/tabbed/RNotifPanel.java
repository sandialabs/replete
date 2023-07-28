package replete.ui.tabbed;

import java.awt.LayoutManager;

import replete.ui.panels.RPanel;
import replete.ui.windows.notifications.NotificationWindow;
import replete.ui.worker.RWorker;

public class RNotifPanel extends RPanel {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RNotifPanel() {}
    public RNotifPanel(LayoutManager mgr) {
        super(mgr);
    }


    //////////
    // MISC //
    //////////

    public NotificationWindow getNWindow() {
        return (NotificationWindow) getWindow();
    }
    public void addInfo(String title) {
        NotificationWindow parent = (NotificationWindow) getWindow();
        parent.addInfo(title);
    }
    public void addTaskAndExecuteFg(String title, RWorker<?, ?> worker) {
        NotificationWindow parent = (NotificationWindow) getWindow();
        parent.addTaskAndExecuteFg(title, worker);
    }
    public void addTaskAndExecuteBg(String title, RWorker<?, ?> worker) {
        NotificationWindow parent = (NotificationWindow) getWindow();
        parent.addTaskAndExecuteBg(title, worker);
    }
    public void addTaskAndExecute(String title, RWorker<?, ?> worker,
                                  boolean waitCursor, boolean autoRemove) {     // Convenience method for common usage
        NotificationWindow parent = (NotificationWindow) getWindow();
        parent.addTaskAndExecute(title, worker, waitCursor, autoRemove);
    }
}
