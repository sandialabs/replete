package replete.conf.node.rule;

import replete.conf.ConfigurationIssue;
import replete.conf.node.ConfigurationNode;

public class NumberRangeConfigurationRule extends ConfigurationRule {

    ////////////
    // FIELDS //
    ////////////

    private long start;
    private long end;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NumberRangeConfigurationRule(ConfigurationIssue.Level level, long start, long end) {
        super(level);
        this.start = start;
        this.end = end;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public boolean validate(ConfigurationNode e) {
        try {
            long number = Long.parseLong((String) e.getValue());
            return number >= start && number <= end;
        } catch(NumberFormatException nfe) {
            return false;
        }
    }

    @Override
    public ConfigurationIssue getIssue() {
        String message = "The provided number is outside the acceptable range of (" + start + ", " + end + ")";

        return new ConfigurationIssue(level, ConfigurationIssue.Type.VIOLATED_RULE, message);
    }
}
