package finio.appstate;

import java.util.ArrayList;
import java.util.List;

import replete.event.ExtChangeNotifier;


public class AppState {


    ////////////
    // FIELDS //
    ////////////

    private List<String> recentFiles = new ArrayList<>();
    private List<WorldBundle> worlds = new ArrayList<>();
    private AppConfig config = new AppConfig();


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public List<String> getRecentFiles() {
        return recentFiles;
    }
    public List<WorldBundle> getWorlds() {
        return worlds;
    }
    public AppConfig getConfig() {
        return config;
    }

    // Mutators

    public void setRecentFiles(List<String> recentFiles) {
        this.recentFiles = recentFiles;
        // Fire change property?  Worlds?
    }


    ////////////
    // STATIC //
    ////////////

    protected static AppState state;
    public static void setState(AppState appState) {
        state = appState;
    }
    public static AppState getState() {
        return state;
    }


    //////////
    // MISC //
    //////////

    protected Object readResolve() {
        propertyChangeNotifier = new ExtChangeNotifier<>();
        return this;
    }


    //////////////
    // NOTIFIER //
    //////////////

    private transient ExtChangeNotifier<AppStateChangeListener> propertyChangeNotifier =
        new ExtChangeNotifier<>();
    public void addPropertyChangeListener(AppStateChangeListener listener) {
        propertyChangeNotifier.addListener(listener);
    }
    public void removePropertyChangeListener(AppStateChangeListener listener) {
        propertyChangeNotifier.removeListener(listener);
    }
    private void firePropertyChangeNotifier(String name, Object prev, Object curr) {
        propertyChangeNotifier.fireStateChanged(new AppStateChangeEvent(name, prev, curr));
    }
}
