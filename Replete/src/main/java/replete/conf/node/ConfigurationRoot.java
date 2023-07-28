package replete.conf.node;

import java.util.ArrayList;
import java.util.Collection;

import replete.conf.ConfigurationDomain;
import replete.conf.exception.ConfigurationException;
import replete.errors.UnicornException;

public class ConfigurationRoot extends ConfigurationNode<Collection<ConfigurationNode>> {

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ConfigurationRoot() {
        this.key = ConfigurationDomain.KEY_ROOT;
        try {
            setValue(new ArrayList<>());
        } catch(ConfigurationException e) {
            // We should never reach this point.
            throw new UnicornException(e);
        }
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public ConfigurationNode setKey(ConfigurationKey key) {
        // Don't allow users to change the root's key.
        return this;
    }
}
