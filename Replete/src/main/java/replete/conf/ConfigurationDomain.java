package replete.conf;

import java.util.ArrayList;
import java.util.List;

import replete.conf.node.ConfigurationKey;
import replete.conf.node.ConfigurationRoot;

public abstract class ConfigurationDomain {

    ////////////
    // FIELDS //
    ////////////

    public static final ConfigurationKey KEY_ROOT = new ConfigurationKey("ROOT", "The root of the configuration.", new ArrayList<>());

    protected String shortName;
    protected String longName;
    protected List<ConfigurationKey> availableDomainKeys;
    protected ConfigurationRoot defaultTree;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    protected ConfigurationDomain() {
        availableDomainKeys = new ArrayList<>();
    }

    /////////////
    // GETTERS //
    /////////////

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public List<ConfigurationKey> getAvailableDomainKeys() {
        return availableDomainKeys;
    }

    public ConfigurationRoot getDefaultTree() {
        return defaultTree;
    }
}
