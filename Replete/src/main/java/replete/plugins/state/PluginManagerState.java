package replete.plugins.state;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import replete.plugins.PluginInitializationResults;

public class PluginManagerState implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    // Global / Map(ID -> Object)
    private Map<String, PluginState> globalPlugins = new LinkedHashMap<>();
    private Map<String, ExtensionPointState> globalExtPoints = new LinkedHashMap<>();
    private Map<String, ExtensionState> globalExts = new LinkedHashMap<>();

    // Ownership / Map(ID -> List<Object>)
    private Map<String, List<ExtensionPointState>> ownedPluginExtPoints = new LinkedHashMap<>();
    private Map<String, List<ExtensionState>> ownedPluginExts = new LinkedHashMap<>();
    private Map<String, List<ExtensionState>> ownedExtPointExts = new LinkedHashMap<>();

    // Initialization Errors
    private PluginInitializationResults initializationResults = new PluginInitializationResults();


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public Map<String, PluginState> getGlobalPlugins() {
        return globalPlugins;
    }
    public Map<String, ExtensionPointState> getGlobalExtPoints() {
        return globalExtPoints;
    }
    public Map<String, ExtensionState> getGlobalExts() {
        return globalExts;
    }
    public Map<String, List<ExtensionPointState>> getOwnedPluginExtPoints() {
        return ownedPluginExtPoints;
    }
    public Map<String, List<ExtensionState>> getOwnedPluginExts() {
        return ownedPluginExts;
    }
    public Map<String, List<ExtensionState>> getOwnedExtPointExts() {
        return ownedExtPointExts;
    }
    public PluginInitializationResults getInitializationResults() {
        return initializationResults;
    }

    // Mutators

    public PluginManagerState setInitializationResults(PluginInitializationResults initializationResults) {
        this.initializationResults = initializationResults;
        return this;
    }
}
