package finio.ui.app;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import finio.appstate.AppConfig;
import finio.platform.exts.view.consoleview.ui.ConsoleOutputEvent;
import finio.platform.exts.view.consoleview.ui.ConsoleOutputListener;
import finio.ui.FFrame;
import finio.ui.actions.FActionMap;
import finio.ui.view.SelectionContext;
import finio.ui.view.ViewContainerPanel;
import finio.ui.view.ViewPanel;
import finio.ui.world.RenameWorldEvent;
import finio.ui.world.RenameWorldListener;
import finio.ui.world.WorldPanel;
import finio.ui.worlds.WorldContext;
import finio.ui.worlds.WorldSelectedEvent;
import finio.ui.worlds.WorldSelectedListener;
import replete.event.ChangeNotifier;
import replete.event.ExtChangeNotifier;
import replete.event.rnotif.RChangeListener;
import replete.event.rnotif.RChangeNotifier;
import replete.ui.windows.Dialogs;


public class AppContext {           // Used to be "AppUiController"


    ////////////
    // FIELDS //
    ////////////

    private AppConfig config = new AppConfig();
    private FFrame parent;
    private FActionMap actionMap;
    private List<WorldContext> worlds = new ArrayList<>();
    private int selectedWorldIndex = -1;
    private List<String> recentFiles = new ArrayList<>();

    public AppContext() {
        addSelectedWorldListener(new WorldSelectedListener() {
            public void stateChanged(WorldSelectedEvent e) {

                if(e.getPreviousIndex() != -1) {
                    WorldContext wc = worlds.get(e.getPreviousIndex());
                    wc.getWorldPanel().removeSelectedViewListener(
                        worldPanelSelectedViewListener);
                }

                if(e.getIndex() != -1) {
                    WorldContext wc = worlds.get(e.getIndex());
                    wc.getWorldPanel().addSelectedViewListener(
                        worldPanelSelectedViewListener);
                }

                fireSelectedViewNotifier();
            }
        });
    }

    private ChangeListener worldPanelSelectedViewListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            fireSelectedViewNotifier();
        }
    };
    private ChangeListener worldPanelViewSelectedValuesListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            fireSelectedValuesNotifier();
        }
    };
    private ChangeListener worldPanelViewEditContextChangedListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            fireEditContextChangedNotifier();
        }
    };


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Acccessors

    public FFrame getWindow() {
        return parent;
    }
    public FActionMap getActionMap() {
        return actionMap;
    }
    public List<String> getRecentFiles() {
        return recentFiles;
    }

    // Mutators

    public void setWindow(FFrame parent) {
        this.parent = parent;
    }
    public void setActionMap(FActionMap actionMap) {
        this.actionMap = actionMap;
    }
    public AppContext setRecent(List<String> recentFiles) {
        this.recentFiles = recentFiles;
        fireRecentChangedNotifier();
        return this;
    }
    public AppContext addRecent(String recent) {
        recentFiles.remove(recent);
        recentFiles.add(0, recent);
        fireRecentChangedNotifier();
        return this;
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

//    private ChangeNotifier dirtyChangedNotifier = new ChangeNotifier(this);
//    public void addDirtyChangedListener(ChangeListener listener) {
//        dirtyChangedNotifier.addListener(listener);
//    }
//    private void fireDirtyChangedNotifier() {
//        dirtyChangedNotifier.fireStateChanged();
//    }

//    private ChangeNotifier worldAddedNotifier = new ChangeNotifier(this);
//    public void addWorldAddedListener(ChangeListener listener) {
//        worldAddedNotifier.addListener(listener);
//    }
//    public void removeWorldAddedListener(ChangeListener listener) {
//        worldAddedNotifier.removeListener(listener);
//    }
//    private void fireWorldAddedNotifier() {
//        worldAddedNotifier.fireStateChanged();
//    }
    private RChangeNotifier worldAddedNotifier = new RChangeNotifier(this);
    public void addWorldAddedListener(RChangeListener listener) {
        worldAddedNotifier.addListener(listener);
    }
    public void removeWorldAddedListener(RChangeListener listener) {
        worldAddedNotifier.removeListener(listener);
    }
    private void fireWorldAddedNotifier() {
        worldAddedNotifier.fire();
    }

    private ChangeNotifier recentChangedNotifier = new ChangeNotifier(this);
    public void addRecentChangedListener(ChangeListener listener) {
        recentChangedNotifier.addListener(listener);
    }
    private void fireRecentChangedNotifier() {
        recentChangedNotifier.fireStateChanged();
    }

    private List<ConsoleOutputListener> consoleOutputListeners = new ArrayList<>();
    public void addConsoleOutputListener(ConsoleOutputListener listener) {
        consoleOutputListeners.add(listener);
    }
    private void fireConsoleOutputNotifier(String output) {
        ConsoleOutputEvent event = new ConsoleOutputEvent(output);
        for(ConsoleOutputListener listener : consoleOutputListeners) {
            listener.output(event);
        }
    }
    public void sendToConsole(String s) {
        fireConsoleOutputNotifier(s);
    }

    private ChangeNotifier exitNotifier = new ChangeNotifier(this);
    public void addExitListener(ChangeListener listener) {
        exitNotifier.addListener(listener);
    }
    private void fireExitNotifier() {
        exitNotifier.fireStateChanged();
    }

    private ExtChangeNotifier<WorldSelectedListener> selectedWorldNotifier = new ExtChangeNotifier<>();
    public void addSelectedWorldListener(WorldSelectedListener listener) {
        selectedWorldNotifier.addListener(listener);
    }
    private void fireSelectedWorldNotifier(int prev, int cur) {
        selectedWorldNotifier.fireStateChanged(new WorldSelectedEvent(prev, cur));
    }

    private ChangeNotifier selectedViewNotifier = new ChangeNotifier(this);
    public void addSelectedViewListener(ChangeListener listener) {
        selectedViewNotifier.addListener(listener);
    }
    private void fireSelectedViewNotifier() {
        selectedViewNotifier.fireStateChanged();
    }

    private ChangeNotifier selectedValuesNotifier = new ChangeNotifier(this);
    public void addSelectedValuesListener(ChangeListener listener) {
        selectedValuesNotifier.addListener(listener);
    }
    private void fireSelectedValuesNotifier() {
        selectedValuesNotifier.fireStateChanged();
    }

    private ChangeNotifier editContextChangedNotifier = new ChangeNotifier(this);
    public void addEditContextChangedListener(ChangeListener listener) {
        editContextChangedNotifier.addListener(listener);
    }
    private void fireEditContextChangedNotifier() {
        editContextChangedNotifier.fireStateChanged();
    }

    private ExtChangeNotifier<RenameWorldListener> renameWorldNotifier = new ExtChangeNotifier<>();
    public void addRenameWorldListener(RenameWorldListener listener) {
        renameWorldNotifier.addListener(listener);
    }
    public void removeRenameWorldListener(RenameWorldListener listener) {
        renameWorldNotifier.removeListener(listener);
    }
    private void fireRenameWorldNotifier(WorldContext wc, int index, String previous, String current) {
        RenameWorldEvent event = new RenameWorldEvent(wc, index, previous, current);
        renameWorldNotifier.fireStateChanged(event);
    }


    ////////////
    // WORLDS //
    ////////////

    public int getWorldIndex(WorldContext wc) {
        for(int i = 0; i < getWorldCount(); i++) {
            if(getWorld(i) == wc) {
                return i;
            }
        }
        return -1;
    }
    public int getWorldCount() {
        return worlds.size();
    }
    public WorldContext getWorld(int w) {
        return worlds.get(w);
    }
    public WorldContext getSelectedWorld() {
        if(selectedWorldIndex == -1 || selectedWorldIndex >= worlds.size()) {
            return null;
        }
        return worlds.get(selectedWorldIndex);
    }
    public AppContext addWorld(WorldContext wc) {
        if(worlds.contains(wc)) {
            throw new IllegalArgumentException("World already exists.");
        }
        worlds.add(wc);
        wc.getWorldPanel().addSelectedViewListener(worldPanelSelectedViewListener);
        wc.getWorldPanel().addSelectedValuesListener(worldPanelViewSelectedValuesListener);
        wc.getWorldPanel().addEditContextChangedListener(worldPanelViewEditContextChangedListener);
        fireWorldAddedNotifier();
        setSelectedWorldIndex(worlds.size() - 1);
        return this;
    }
    public List<WorldContext> getWorlds() {
        return worlds;
    }
    public void setSelectedWorldIndex(int newIndex) {
        if(newIndex != selectedWorldIndex) {
            int prev = selectedWorldIndex;
            selectedWorldIndex = newIndex;
            fireSelectedWorldNotifier(prev, selectedWorldIndex);
        }
    }
    public int getSelectedWorldIndex() {
        return selectedWorldIndex;
    }
    public void renameWorld(int selectedIndex, String input) {
        WorldContext wc = getWorld(selectedIndex);
        String previous = wc.getName();
        wc.setName(input);
        fireRenameWorldNotifier(wc, selectedIndex, previous, input);
    }
    public void closeWorld(int selectedIndex) {
        if(selectedIndex < 0 || selectedIndex >= worlds.size()) {
            throw new IllegalArgumentException("Invalid world index.");
        }
//        fireCloseWorldNotifier(selectedIndex);
    }
    public ViewPanel getSelectedViewPanel() {
        WorldContext wc = getSelectedWorld();
        if(wc == null) {
            return null;
        }
        WorldPanel pnlWorld = wc.getWorldPanel();
        return pnlWorld.getSelectedView();
    }
    public ViewContainerPanel getSelectedViewContainerPanel() {
        WorldContext wc = getSelectedWorld();
        if(wc == null) {
            return null;
        }
        WorldPanel pnlWorld = wc.getWorldPanel();
        return pnlWorld.getSelectedViewContainerPanel();
    }
    public SelectionContext[] getSelectedValues() {
        ViewPanel pnlView = getSelectedViewPanel();
        if(pnlView == null) {
            return null;
        }
        return pnlView.getSelectionValues();
    }


    //////////
    // MISC //
    //////////

    public void refresh() {
        for(WorldContext wc : worlds) {
            wc.refresh();
        }
    }
    public void notImpl(String feature) {
        Dialogs.showWarning(parent,
            "The feature [" + feature + "] is not yet implemented.", "Sorry!");
    }
    public void requestExit() {
        fireExitNotifier();
    }
    public AppConfig getConfig() {
        return config;
    }
    public AppContext setConfig(AppConfig config) {
        this.config = config;
        return this;
    }
}
