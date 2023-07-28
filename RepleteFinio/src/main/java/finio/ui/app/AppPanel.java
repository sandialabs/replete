package finio.ui.app;

import javax.swing.JPanel;

public class AppPanel extends JPanel {

/*
    ////////////
    // FIELDS //
    ////////////

    private AppContext ac;           // Application context (global config, etc.)
    private List<WorldContainerPanel> worldPanels = new ArrayList<>();
        // List of world panels, contained within this app panel, each with a data
        // model rooted somewhere within the world data model tree.  Each are actually
        // added to the views panel.  However, the views panel could be switched with
        // a new one at any time using the "WorldContainerPanel" class, though this
        // should probably just become a JPanel of its own.
    private int selectedWorldIndex = -1;
    private WorldsPanel pnlWorlds;             // Contains the world panels.  Can be changed.
    private NoWorldsWorldPanel pnlNoWorlds;    // Allows the user to load a world.
    private boolean expandSingleWorld;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public AppPanel(AppContext ac) {
        this.ac = ac;

        Lay.BLtg(this,
            "C", pnlNoWorlds = new NoWorldsWorldPanel(ac)
        );

        // Can one day choose a default view (like Tree) given a default preference.
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public List<WorldContainerPanel> getWorldPanels() {
        return worldPanels;
    }
    public int getSelectedWorldIndex() {
        return selectedWorldIndex;
    }
    public WorldContainerPanel getSelectedWorldContainerPanel() {
        if(selectedWorldIndex == -1 || selectedWorldIndex >= worldPanels.size()) {
            return null;
        }
        return worldPanels.get(selectedWorldIndex);
    }
    public WorldPanel getSelectedWorld() {
        if(selectedWorldIndex == -1 || selectedWorldIndex >= worldPanels.size()) {
            return null;
        }
        return worldPanels.get(selectedWorldIndex).getWorldPanel();
    }
    public WorldsPanel getWorldViews() {
        return pnlWorlds;
    }
    public NoWorldsWorldPanel getNoWorldsPanel() {
        return pnlNoWorlds;
    }

    // Mutators

    public void setSelectedWorldIndex(int selectedWorldIndex) {
        this.selectedWorldIndex = selectedWorldIndex;
        fireSelectedViewNotifier();
    }
    public void setWorldsPanel(WorldsPanel pnlWorlds) {
        if(this.pnlWorlds != null) {
            this.pnlWorlds.removeWorldSelectionListener(worldSelListener);
        }
        this.pnlWorlds = pnlWorlds;
        pnlWorlds.addWorldSelectionListener(worldSelListener);
        // TODO: Add all views to the new views panel.
        // ALSO: The views panels themselves, though removed, might
        // still be listening to the WorldPanel or WorldContext,
        // potentially adding UI components to itself and thus
        // removing those UI components from the parents where
        // they should be located.  So we need a "disconnect" your
        // listeners for orphaned UI components.
    }

    public void addWorldPanel(final WorldPanel pnlWorld) {
        worldPanels.add(new WorldContainerPanel(pnlWorld, null));
        updateCenterPanel();

        pnlWorld.addSelectedValuesListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                fireSelectedValuesNotifier();
            }
        });
        pnlWorld.addEditContextChangedListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                fireEditContextChangedNotifier();
            }
        });

//        pnlView.addAnyActionListener(new ChangeListener() {
//            public void stateChanged(ChangeEvent e) {
//                updateWorkingScopeErrorIcon();
//            }
//        });
        pnlWorld.addSelectedListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int index = -1;
                for(int i = 0; i < worldPanels.size(); i++) {
                    if(worldPanels.get(i).getWorldPanel() == pnlWorld) {
                        index = i;
                        break;
                    }
                }
                setSelectedWorldIndex(index);
                ac.getActionMap().validate();
            }
        });

        pnlWorld.init();
        pnlWorld.focus();

        fireViewAddedNotifier();
        setSelectedWorldIndex(worldPanels.size() - 1);
    }


    //////////
    // MISC //
    //////////

    public void refresh() {
        for(WorldContainerPanel pnlWorldCont : worldPanels) {
            pnlWorldCont.getWorldPanel().refresh();
        }
    }

    public void increaseFont() {
        for(WorldContainerPanel pnlWorldCont : worldPanels) {
            pnlWorldCont.getWorldPanel().increaseFont();
        }
    }
    public void decreaseFont() {
        for(WorldContainerPanel pnlWorldCont : worldPanels) {
            pnlWorldCont.getWorldPanel().decreaseFont();
        }
    }

    public void closeWorld(int index) {
        WorldContainerPanel pnlWorldCont = worldPanels.remove(index);
        updateCenterPanel();
        fireCloseWorldNotifier(pnlWorldCont, index);
    }

    public void setExpandSingleWorld(boolean expandSingleWorld) {
        this.expandSingleWorld = expandSingleWorld;
        updateCenterPanel();
    }

    private void updateCenterPanel() {
        removeAll();

        if(worldPanels.size() == 0) {
            add(pnlNoWorlds, BorderLayout.CENTER);

        } else if(worldPanels.size() == 1 && expandSingleWorld) {
            add(worldPanels.get(0).getWorldPanel(), BorderLayout.CENTER);

        } else {
            add(pnlWorlds, BorderLayout.CENTER);
        }

        updateUI();
    }

    public void init() {
        pnlWorlds.init();
//        updateWorkingScopeErrorIcon();
    }

    private WorldSelectionListener worldSelListener = new WorldSelectionListener() {
        public void stateChanged(WorldSelectionEvent e) {
            setSelectedWorldIndex(e.getIndex());
        }
    };


    ///////////////
    // NOTIFIERS //
    ///////////////

    // this is probably bad design, and needs to change as we let various windows have various different working scopes.

    private ChangeNotifier anyActionNotifier = new ChangeNotifier(this);
    public void addAnyActionListener(ChangeListener listener) {
        anyActionNotifier.addListener(listener);
    }
    private void fireAnyActionNotifier() {
        anyActionNotifier.fireStateChanged();
    }
    public void renameSelectedView(String input) {
        WorldContainerPanel pnlWorldCont = getSelectedWorldContainerPanel();
        if(pnlWorldCont != null) {
            String previous = pnlWorldCont.getWorldName();
            pnlWorldCont.setWorldName(input.isEmpty() ? null : input);
            fireRenameViewNotifier(pnlWorldCont, selectedWorldIndex, previous, pnlWorldCont.getWorldName());
        }
    }

    private ChangeNotifier viewAddedNotifier = new ChangeNotifier(this);
    public void addViewAddedListener(ChangeListener listener) {
        viewAddedNotifier.addListener(listener);
    }
    private void fireViewAddedNotifier() {
        viewAddedNotifier.fireStateChanged();
    }

    private ChangeNotifier selectedViewNotifier = new ChangeNotifier(this);
    public void addSelectedViewListener(ChangeListener listener) {
        selectedViewNotifier.addListener(listener);
    }
    public void removeSelectedViewListener(ChangeListener listener) {
        selectedViewNotifier.removeListener(listener);
    }
    private void fireSelectedViewNotifier() {
        selectedViewNotifier.fireStateChanged();
    }

    private ChangeNotifier selectedValuesNotifier = new ChangeNotifier(this);
    public void addSelectedValuesListener(ChangeListener listener) {
        selectedValuesNotifier.addListener(listener);
    }
    public void removeSelectedValuesListener(ChangeListener listener) {
        selectedValuesNotifier.removeListener(listener);
    }
    private void fireSelectedValuesNotifier() {
        selectedValuesNotifier.fireStateChanged();
    }

    private ChangeNotifier editContextChangedNotifier = new ChangeNotifier(this);
    public void addEditContextChangedListener(ChangeListener listener) {
        editContextChangedNotifier.addListener(listener);
    }
    public void removeEditContextChangedListener(ChangeListener listener) {
        editContextChangedNotifier.removeListener(listener);
    }
    private void fireEditContextChangedNotifier() {
        editContextChangedNotifier.fireStateChanged();
    }

    private ExtChangeNotifier<RenameViewListener> renameViewNotifier = new ExtChangeNotifier<>();
    public void addRenameViewListener(RenameViewListener listener) {
        renameViewNotifier.addListener(listener);
    }
    public void removeRenameViewListener(RenameViewListener listener) {
        renameViewNotifier.removeListener(listener);
    }
    private void fireRenameViewNotifier(WorldContainerPanel pnlWorldCont, int index, String previous, String current) {
        RenameViewEvent event = new RenameWorldEvent(pnlWorldCont, index, previous, current);
        renameViewNotifier.fireStateChanged(event);
    }

    private ExtChangeNotifier<CloseWorldListener> closeWorldNotifier = new ExtChangeNotifier<>();
    public void addCloseWorldListener(CloseWorldListener listener) {
        closeWorldNotifier.addListener(listener);
    }
    public void removeCloseWorldListener(CloseWorldListener listener) {
        closeWorldNotifier.removeListener(listener);
    }
    private void fireCloseWorldNotifier(WorldContainerPanel pnlWorldCont, int index) {
        CloseWorldEvent event = new CloseWorldEvent(pnlWorldCont, index);
        closeWorldNotifier.fireStateChanged(event);
    }
*/
}
