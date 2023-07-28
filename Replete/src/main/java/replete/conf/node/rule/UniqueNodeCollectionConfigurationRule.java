package replete.conf.node.rule;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import replete.conf.Conf;
import replete.conf.ConfigurationIssue;
import replete.conf.ConfigurationIssue.Level;
import replete.conf.node.ConfigurationNode;

public class UniqueNodeCollectionConfigurationRule extends ConfigurationRule {

    ////////////
    // FIELDS //
    ////////////

    private boolean recursiveCheck;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public UniqueNodeCollectionConfigurationRule(Level level, boolean recursiveCheck) {
        super(level);
        this.recursiveCheck = recursiveCheck;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public boolean validate(ConfigurationNode node) {
        boolean valid = false;
        if(Conf.isCollectionOfConfNodes(node)) {
            Collection<ConfigurationNode> collection = (Collection) node.getValue();
            Set<ConfigurationNode> set = new HashSet<>(collection);

            valid = set.size() == collection.size();
            if(recursiveCheck) {
                for(ConfigurationNode thisNode : collection) {
                    valid = validate(thisNode);
                }
            }
        }
        return valid;
    }

    @Override
    public ConfigurationIssue getIssue() {
        String message = "Duplicates were detected in the provided tree.";
        return new ConfigurationIssue(level, ConfigurationIssue.Type.VIOLATED_RULE, message);
    }

}
