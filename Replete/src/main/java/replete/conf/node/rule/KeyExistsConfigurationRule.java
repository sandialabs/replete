package replete.conf.node.rule;

import replete.conf.Conf;
import replete.conf.ConfigurationIssue;
import replete.conf.node.ConfigurationKey;
import replete.conf.node.ConfigurationNode;

public class KeyExistsConfigurationRule extends ConfigurationRule {

    ////////////
    // FIELDS //
    ////////////

    private ConfigurationKey key;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public KeyExistsConfigurationRule(ConfigurationIssue.Level level, ConfigurationKey key) {
        super(level);
        this.key = key;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public boolean validate(ConfigurationNode element) {
        return Conf.keyExists(element, key, true);
    }

    @Override
    public ConfigurationIssue getIssue() {
        String message = "The key is not present in the provided configuration subtree.";
        return new ConfigurationIssue(level, ConfigurationIssue.Type.VIOLATED_RULE, message);
    }
}