package replete.conf.node.rule;

import replete.conf.ConfigurationIssue;
import replete.conf.node.ConfigurationNode;

public abstract class ConfigurationRule {

    ////////////
    // FIELDS //
    ////////////

    protected final ConfigurationIssue.Level level;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ConfigurationRule(ConfigurationIssue.Level level) {
        this.level = level;
    }

    //////////////
    // ABSTRACT //
    //////////////

    public abstract boolean validate(ConfigurationNode e);

    public abstract ConfigurationIssue getIssue();
}
