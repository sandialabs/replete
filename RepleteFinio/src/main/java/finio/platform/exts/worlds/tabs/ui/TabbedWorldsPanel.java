package finio.platform.exts.worlds.tabs.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import finio.appstate.AppStateChangeEvent;
import finio.appstate.AppStateChangeListener;
import finio.ui.actions.FActionMap;
import finio.ui.app.AppContext;
import finio.ui.world.RenameWorldEvent;
import finio.ui.world.RenameWorldListener;
import finio.ui.world.WorldPanel;
import finio.ui.worlds.WorldContext;
import finio.ui.worlds.WorldsPanel;
import replete.ui.GuiUtil;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.menu.RMenuItem;
import replete.ui.tabbed.HeaderContextMenuEvent;
import replete.ui.tabbed.HeaderContextMenuListener;
import replete.ui.tabbed.RTabbedPane;
import replete.ui.tabbed.TabAboutToCloseEvent;
import replete.ui.tabbed.TabAboutToCloseListener;

public class TabbedWorldsPanel extends WorldsPanel {


    ////////////
    // FIELDS //
    ////////////

    private FActionMap actionMap;
    private RTabbedPane tabs;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public TabbedWorldsPanel(final AppContext ac, final FActionMap actionMap) {
        super(ac);
        this.actionMap = actionMap;

        Lay.BLtg(this,
            "C", tabs = Lay.TBL("dc"),
            WorldsPanel.STYLE
        );

        // Add all of the worlds that exist right now.
        int i = 0;
        for(WorldContext w : ac.getWorlds()) {
            addWorld(w, i++);
        }

        tabs.setTabPlacement(ac.getConfig().getWorldsTabPlacement());

        // Add listeners
        ac.getConfig().addPropertyChangeListener(appStateChangeListener);
        tabs.addChangeListener(tabChangeListener);
        tabs.addTabAboutToCloseListener(tabAboutToCloseListener);
        tabs.addHeaderContextMenuListener(headerContextMenuListener);
        ac.addRenameWorldListener(renameWorldListener);
        ac.addWorldAddedListener(worldAddedListener);
    }

    private AppStateChangeListener appStateChangeListener = new AppStateChangeListener() {
        public void stateChanged(AppStateChangeEvent e) {
            if(e.getName().equals("worldsTabPlacement")) {
                tabs.setTabPlacement((Integer) e.getCurr());
            }
        }
    };
    private ChangeListener tabChangeListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            fireWorldSelectedNotifier(tabs.getSelectedIndex());
        }
    };
    private HeaderContextMenuListener headerContextMenuListener = new HeaderContextMenuListener() {
        public void stateChanged(HeaderContextMenuEvent e) {
            JPopupMenu mnuPopup = new JPopupMenu();
            JMenuItem mnuRename= new RMenuItem("&Rename...", CommonConcepts.RENAME);
            mnuRename.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    actionMap.getAction("rename-world").execute();
                }
            });
            JMenuItem mnuClose = new RMenuItem("&Close", CommonConcepts.CLOSE);
            mnuClose.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    actionMap.getAction("close-world").execute();
                }
            });
            JMenuItem mnuCloseAll = new RMenuItem("&Close All", CommonConcepts.CLOSE_ALL);
            mnuCloseAll.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    actionMap.getAction("close-all-worlds").execute();
                }
            });
            mnuPopup.add(mnuRename);
            mnuPopup.add(mnuClose);
            mnuPopup.add(mnuCloseAll);
            mnuPopup.show(e.getComponent(), e.getX(), e.getY());
        }
    };
    private TabAboutToCloseListener tabAboutToCloseListener = new TabAboutToCloseListener() {
        public void stateChanged(TabAboutToCloseEvent e) {
            e.cancel();

            GuiUtil.safe(new Runnable() {
                public void run() {
                    actionMap.getAction("close-world").execute();
                }
            });
        }
    };
    private RenameWorldListener renameWorldListener = new RenameWorldListener() {
        public void stateChanged(RenameWorldEvent e) {
            tabs.setTitleAt(e.getWorldIndex(), e.getWorldContext().createTabTitle());
        }
    };

    @Override
    public void unsubscribe() {
        ac.getConfig().removePropertyChangeListener(appStateChangeListener);
        tabs.addChangeListener(tabChangeListener);
        tabs.removeHeaderContextMenuListener(headerContextMenuListener);
        tabs.removeTabAboutToCloseListener(tabAboutToCloseListener);
        ac.removeRenameWorldListener(renameWorldListener);
        ac.removeWorldAddedListener(worldAddedListener);
    }

    @Override
    public void addWorld(WorldContext wc, int i) {
        WorldPanel pnlWorld = wc.getWorldPanel();
        for(int c = 0; c < tabs.getTabCount(); c++) {
            if(tabs.getComponentAt(c) == wc.getWorldPanel()) {
                return;
            }
        }
        tabs.addTab(
            wc.createTabTitle(),
            CommonConcepts.WORLD, pnlWorld,
            wc.getSource() + "", pnlWorld.hashCode() + "");
        int index = tabs.getTabCount() - 1;
        if(wc.isDirty()) {
            tabs.setDirtyAt(index, true);
        }
        tabs.setMetadata(index, wc);
        tabs.setToolTipTextAt(index, wc.getSource() == null ? "<NEW>" : wc.getSource().getAbsolutePath());
        pnlWorld.init();
    }

    @Override
    public void removeWorld(WorldContext wc) {
        // TODO
    }

    @Override
    public void setSelectedIndex(int i) {
        tabs.setSelectedIndex(i);
    }
}
