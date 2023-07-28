package finio.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import finio.FinioAppMain;
import finio.appstate.AppStateChangeEvent;
import finio.appstate.AppStateChangeListener;
import finio.platform.exts.worlds.desktop.ui.DesktopWorldsPanel;
import finio.platform.exts.worlds.single.ui.SingleWorldsPanel;
import finio.platform.exts.worlds.tabs.ui.TabbedWorldsPanel;
import finio.ui.actions.FActionMap;
import finio.ui.app.AppContext;
import finio.ui.app.LoadInitialWorldPanel;
import finio.ui.images.FinioImageModel;
import finio.ui.world.RenameWorldEvent;
import finio.ui.world.RenameWorldListener;
import finio.ui.worlds.WorldContext;
import finio.ui.worlds.WorldSelectedEvent;
import finio.ui.worlds.WorldSelectedListener;
import finio.ui.worlds.WorldsLayoutType;
import finio.ui.worlds.WorldsPanel;
import replete.event.rnotif.RChangeEvent;
import replete.event.rnotif.RChangeListener;
import replete.ui.lay.Lay;
import replete.ui.menu.RMenu;
import replete.ui.menu.RMenuItem;
import replete.ui.text.RLabel;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIAction;
import replete.ui.uiaction.UIActionMenuBar;
import replete.ui.uiaction.UIActionToolBar;
import replete.ui.windows.notifications.NotificationFrame;

public class FFrame extends NotificationFrame {


    ///////////
    // FIELD //
    ///////////

    private AppContext ac;            // Application-level UI controller
    private FActionMap actionMap;          // ActionMap for the entire application

    private JComponent cmpCenter;
    private WorldsPanel worldsPane;         // Current worlds layout
    private WorldsLayoutType layoutType;

    private boolean showInit = true;
    private JMenuBar menuBar;
    private JToolBar toolBar;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FFrame(final AppContext ac) {
        super(FinioAppMain.TITLE);

        this.ac = ac;
        actionMap = ac.getActionMap();
        ac.setWindow(this);   // Symbiotic Reference

        setIcon(FinioImageModel.FINIO_LOGO);
        setShowStatusBar(true);

        ac.addWorldAddedListener(new RChangeListener() {
            public void handle(RChangeEvent e) {
                updateWorldsLayout();

                boolean newShowInit = ac.getWorldCount() == 0;
                if(showInit != newShowInit) {
                    if(newShowInit) {
                        setJMenuBar(null);
                        remove(toolBar);
                        setShowStatusBar(false);
                    } else {
                        setJMenuBar(menuBar);
                        add(toolBar, BorderLayout.NORTH);
                        setShowStatusBar(true);
                    }
                    showInit = newShowInit;
                }

//                int lastIndex = ac.getWorldCount() - 1;
//                WorldContext w = ac.getWorld(lastIndex);
//                worldsPane.addWorld(w, lastIndex);
            }
        });

        menuBar = new UIActionMenuBar(actionMap);
        toolBar = new UIActionToolBar(actionMap);
        actionMap.init();                            // Change bound controls as needed

        setShowStatusBar(false);

        Lay.hn(getContentPane(), "bg=180");
        Lay.BLtg(this,
            "C", cmpCenter = new LoadInitialWorldPanel(ac),
            "size=[1000,750],center"
        );

        getTabbedPane().addTab("Selected",     FinioImageModel.SELECTED,   new SelectedPanel(ac));
        getTabbedPane().addTab("Edit Context", FinioImageModel.EDIT_ACTIVE, new EditContextPanel(ac));

//        updateWorldsLayout();

        ac.addExitListener(e -> close());

        ac.getConfig().addPropertyChangeListener(new AppStateChangeListener() {
            public void stateChanged(AppStateChangeEvent e) {
                if(e.getName().equals("worldsUseDesktopPane") || e.getName().equals("worldsExpandSingleWorld")) {
                    updateWorldsLayout();
                }
            }
        });

        WorldSelectedListener titleListener = new WorldSelectedListener() {
            public void stateChanged(WorldSelectedEvent e) {
                worldsPane.setSelectedIndex(ac.getSelectedWorldIndex());
                updateTitle();
            }
        };
        ac.addSelectedWorldListener(titleListener);
//        ac.addFileChangedListener(titleListener);
//        ac.addDirtyChangedListener(titleListener);
        ac.addRenameWorldListener(new RenameWorldListener() {
            public void stateChanged(RenameWorldEvent e) {
                updateTitle();
            }
        });
        updateTitle();

        ac.addRecentChangedListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateRecentMenuItems();
            }
        });
        updateRecentMenuItems();

        // Upon application open, focus the console input box,
        // show the initial nodes in the tree, and set
        // the split pane divider location appropriately.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                for(WorldContext w : ac.getWorlds()) {
                    w.getWorldPanel().init();
                }
            }
        });

        actionMap.getMenuBarComponent("expand-single-world").setSelected(ac.getConfig().isWorldsExpandSingleWorld());

        int placement = ac.getConfig().getWorldsTabPlacement();
        actionMap.getMenuBarComponent("world-tabs-top").setSelected(placement == JTabbedPane.TOP);
        actionMap.getMenuBarComponent("world-tabs-left").setSelected(placement == JTabbedPane.LEFT);
        actionMap.getMenuBarComponent("world-tabs-right").setSelected(placement == JTabbedPane.RIGHT);
        actionMap.getMenuBarComponent("world-tabs-bottom").setSelected(placement == JTabbedPane.BOTTOM);
    }

    private void updateWorldsLayout() {
        WorldsLayoutType nextType;

        // Choose what the next layout type would be
        if(ac.getWorldCount() == 0) {
            nextType = null;
        } else if(ac.getWorldCount() == 1 && ac.getConfig().isWorldsExpandSingleWorld()) {
            nextType = WorldsLayoutType.SINGLE;
        } else if(ac.getConfig().isWorldsUseDesktopPane()) {
            nextType = WorldsLayoutType.DESKTOP;
        } else {
            nextType = WorldsLayoutType.TABBED;
        }

        // If the layout would change from its current state...
        if(layoutType == null || nextType != layoutType) {

            //
            if(worldsPane != null) {
                worldsPane.unsubscribe();
                worldsPane.removeWorldSelectedListener(worldChangeListener);
                // save parameters
            }

            if(cmpCenter != null) {
                getContentPane().remove(cmpCenter);
            }

            if(nextType != null) {
                switch(nextType) {
                    case SINGLE:  worldsPane = new SingleWorldsPanel(ac, actionMap);  break;
                    case TABBED:  worldsPane = new TabbedWorldsPanel(ac, actionMap);  break;
                    case DESKTOP: worldsPane = new DesktopWorldsPanel(ac, actionMap); break;
                }
                worldsPane.addWorldSelectedListener(worldChangeListener);
                if(cmpCenter instanceof LoadInitialWorldPanel) {
                    ((LoadInitialWorldPanel) cmpCenter).stop();
                }
                cmpCenter = worldsPane;
            } else {
                worldsPane = null;
                if(cmpCenter instanceof LoadInitialWorldPanel) {
                    ((LoadInitialWorldPanel) cmpCenter).stop();
                }
                cmpCenter = new LoadInitialWorldPanel(ac);
            }
            // Choose the next worlds pane

            layoutType = nextType;

            // restore layout state (only applies to desktop)
            getContentPane().add(cmpCenter, BorderLayout.CENTER);
            ((JPanel)getContentPane()).updateUI();
        }
    }

    WorldSelectedListener worldChangeListener = new WorldSelectedListener() {
        public void stateChanged(WorldSelectedEvent e) {
            ac.setSelectedWorldIndex(e.getIndex());
        }
    };


    //////////
    // MISC //
    //////////

    private void updateTitle() {
        WorldContext wc = ac.getSelectedWorld();
        String title = FinioAppMain.TITLE;
        if(!showInit) {
            if(wc != null) {
                title += " - ";
                title += wc.getSource() == null ? "<NEW>" : wc.getSource().getAbsolutePath();
                if(wc.isDirty()) {
                    title += "*";
                }
                title += " (" + wc.getName() + ")";
            }
        }
        setTitle(title);
    }

    private void updateRecentMenuItems() {
        UIAction file = ac.getActionMap().getAction("world");
        MenuBarActionDescriptor desc =
            (MenuBarActionDescriptor) file.getDescriptor(MenuBarActionDescriptor.class);
        RMenu mnu = (RMenu) desc.getComponent();
        List<String> recentFiles = ac.getRecentFiles();
        int size = mnu.getMenuComponentCount();
        JMenuItem mnuExit = (JMenuItem) mnu.getMenuComponent(size - 1);
        int recentLabel = -1;
        for(int m = size - 1; m >= 0; m--) {
            Component c = mnu.getMenuComponent(m);
            if(c instanceof JLabel) {
                recentLabel = m;
                break;
            }
        }
        if(recentLabel == -1) {
            recentLabel = size - 1;
        }
        for(int r = size - 1; r >= recentLabel; r--) {
            mnu.remove(r);
        }
        if(!recentFiles.isEmpty()) {
            mnu.add(Lay.<RLabel>lb("Recent Worlds:", "eb=5l"));
            int i = 1;
            for(final String rec : recentFiles) {
                String amp = i < 10 ? "&" : "";
                JMenuItem mnuRecent = new RMenuItem(amp + i + ": " + rec);
                mnuRecent.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ac.getActionMap().openWorld(new File(rec));
                    }
                });
                mnu.add(mnuRecent);
                i++;
            }
            mnu.addSeparator();
        }
        mnu.add(mnuExit);
    }
}
