package replete.ui.windows.notifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import replete.collections.tl.TLAddListener;
import replete.collections.tl.TLRemoveListener;
import replete.collections.tl.TrackedList;
import replete.collections.tl.events.TLAddEvent;
import replete.collections.tl.events.TLRemoveEvent;
import replete.ui.lay.Lay;
import replete.ui.windows.notifications.msg.NotificationCommon;
import replete.ui.windows.notifications.msg.NotificationError;
import replete.ui.windows.notifications.msg.NotificationInfo;
import replete.ui.windows.notifications.msg.NotificationTask;
import replete.ui.windows.notifications.pnl.NotificationCommonPanel;
import replete.ui.windows.notifications.pnl.NotificationErrorPanel;
import replete.ui.windows.notifications.pnl.NotificationInfoPanel;
import replete.ui.windows.notifications.pnl.NotificationTaskPanel;

public class NotificationTabPanel<T extends NotificationCommon> extends JPanel {


    ////////////
    // FIELDS //
    ////////////

    private String type;
    private Map<NotificationCommon, JPanel> panelMap = new HashMap<>();
    private List<NotificationCommonPanel> panelList = new ArrayList<>();
    private TrackedList<? extends NotificationCommon> notifList;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NotificationTabPanel(final NotificationModel model, String type, final TrackedList<T> notifList) {
        this.type = type;
        this.notifList = notifList;

        notifList.addAddListener(new TLAddListener<T>() {
            public void stateChanged(TLAddEvent<T> e) {
                for(Object index : e.getAdded().keySet()) {
                    final NotificationCommon notif = e.getAdded().get(index);
                    NotificationCommonPanel pnl;
                    if(notif instanceof NotificationTask) {
                        pnl = new NotificationTaskPanel(model, (NotificationTask) notif);
                        pnl.addRemoveListener(new ChangeListener() {
                            public void stateChanged(ChangeEvent e) {
                                notifList.remove(notif);
                            }
                        });
                    } else if(notif instanceof NotificationError) {
                        pnl = new NotificationErrorPanel((NotificationError) notif);
                        pnl.addRemoveListener(new ChangeListener() {
                            public void stateChanged(ChangeEvent e) {
                                notifList.remove(notif);
                            }
                        });
                    } else {
                        pnl = new NotificationInfoPanel((NotificationInfo) notif);
                        pnl.addRemoveListener(new ChangeListener() {
                            public void stateChanged(ChangeEvent e) {
                                notifList.remove(notif);
                            }
                        });
                    }
                    addPanel(notif, pnl);
                }
            }
        });
        notifList.addRemoveListener(new TLRemoveListener<T>() {
            public void stateChanged(TLRemoveEvent<T> e) {
                for(Object index : e.getRemoved().keySet()) {
                    final NotificationCommon notif = e.getRemoved().get(index);
                    if(notif instanceof NotificationTask) {
                        removePanel(notif);
                    } else if(notif instanceof NotificationError) {
                        removePanel(notif);
                    } else {
                        removePanel(notif);
                    }
                }
            }
        });
        updatePanels();
    }


    //////////////
    // MUTATORS //
    //////////////

    public synchronized void addPanel(NotificationCommon notif, NotificationCommonPanel pnl) {
        panelList.remove(pnl);
        panelList.add(pnl);
        panelMap.put(notif, pnl);
        updatePanels();
    }
    public synchronized void clearAll() {
        panelList.clear();
        panelMap.clear();
        updatePanels();
    }
    public synchronized void removePanel(NotificationCommon notif) {
        panelList.remove(panelMap.get(notif));
        panelMap.remove(notif);
        updatePanels();
    }
    public synchronized void updatePanels() {
        removeAll();
        if(panelList.size() == 0) {
            Lay.GBLtg(this, Lay.lb("<html><i>No " + type + "</i></html>"));
        } else {
            setBounds(0, 0, 100, panelList.size() * 100);
            Lay.BxLtg(this);
            boolean even = true;
            for(int p = panelList.size() - 1; p >= 0; p--) {
                JPanel pnl = panelList.get(p);
                Lay.hn(pnl, "dimh=60,eb=5lr,chtransp");
                add(pnl);
                if(even) {
                    Lay.hn(pnl, "bg=white");
                } else {
                    Lay.hn(pnl, "bg=EFFAFF");
                }
                even = !even;
            }
            add(Box.createVerticalGlue());
        }
        updateUI();
    }


    //////////
    // MISC //
    //////////

    public synchronized void updateTimedInfo() {
        for(NotificationCommonPanel pnl : panelList) {
            pnl.updateTimedInfo();
        }
    }
}
