package replete.conf.node.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import replete.conf.ConfigurationIssue;
import replete.conf.node.ConfigurationNode;

public class CheckValuesListConfigurationRule extends ConfigurationRule {

    ////////////
    // FIELDS //
    ////////////

    private List<Object> values;
    private boolean disallowed;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public CheckValuesListConfigurationRule(ConfigurationIssue.Level level, boolean disallowed, Object ... valuesToAdd) {
        this(level, disallowed, Arrays.asList(valuesToAdd));
    }

    public CheckValuesListConfigurationRule(ConfigurationIssue.Level level, boolean disallowed, List<Object> values) {
        super(level);
        this.values = new ArrayList<>();
        this.values.addAll(values);

        this.disallowed = disallowed;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public boolean validate(ConfigurationNode element) {
        return disallowed ^ values.contains(element.getValue());
    }

    @Override
    public ConfigurationIssue getIssue() {
        String type = disallowed ? "disallowed" : "allowed";
        String message = "The provided string violated the following " + type + " values list: " + values.toString();
        return new ConfigurationIssue(level, ConfigurationIssue.Type.VIOLATED_RULE, message);
    }
}
