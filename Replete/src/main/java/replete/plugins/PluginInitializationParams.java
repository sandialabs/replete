package replete.plugins;

import java.util.ArrayList;
import java.util.List;

public class PluginInitializationParams {


    ////////////
    // FIELDS //
    ////////////

    List<PluginInitializationParamsGroup> groups = new ArrayList<>();


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public List<PluginInitializationParamsGroup> getGroups() {
        return groups;
    }

    // Mutators

    public PluginInitializationParams add(PluginInitializationParamsGroup paramsGroup) {
        this.groups.add(paramsGroup);
        return this;
    }
}
