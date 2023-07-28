package replete.conf.node.rule;

import replete.conf.Conf;
import replete.conf.ConfigurationIssue;
import replete.conf.node.ConfigurationKey;
import replete.conf.node.ConfigurationNode;
import replete.conf.node.ConfigurationPath;

public class ValueExistsConfigurationRule extends ConfigurationRule {

    ////////////
    // FIELDS //
    ////////////

    private ConfigurationPath expectedPath;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ValueExistsConfigurationRule(ConfigurationIssue.Level level, ConfigurationPath expectedPath) {
        super(level);
        this.expectedPath = expectedPath;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public boolean validate(ConfigurationNode node) {
        ConfigurationNode currentNode = node;
        for(ConfigurationNode nextNode : expectedPath.getPath()) {
            ConfigurationKey nextKey = nextNode.getKey();
            boolean proceed = Conf.keyExists(currentNode, nextKey, false);
            if(!proceed) {
                return false;
            } else {
                currentNode = Conf.getNodeByKey(currentNode, nextKey, false);
                if(currentNode == null) {
                    return false;
                }
            }
        }
        Object finalValue = expectedPath.getPath().get(expectedPath.getPath().size()-1).getValue();
        return currentNode.getValue().equals(finalValue);
    }

    @Override
    public ConfigurationIssue getIssue() {
        String message = "The value is not present in the provided configuration tree.";
        return new ConfigurationIssue(level, ConfigurationIssue.Type.VIOLATED_RULE, message);
    }
}