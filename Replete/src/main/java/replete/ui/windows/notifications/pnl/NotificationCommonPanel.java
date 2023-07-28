package replete.ui.windows.notifications.pnl;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;

public class NotificationCommonPanel extends JPanel {


    public void updateTimedInfo() {}


    ///////////////
    // NOTIFIERS //
    ///////////////

    private ChangeNotifier removeNotifier = new ChangeNotifier(this);
    public void addRemoveListener(ChangeListener listener) {
        removeNotifier.addListener(listener);
    }
    protected void fireRemoveNotifier() {
        removeNotifier.fireStateChanged();
    }
}
