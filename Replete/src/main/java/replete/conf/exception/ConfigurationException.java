package replete.conf.exception;

import java.util.List;

import replete.conf.ConfigurationIssue;

public class ConfigurationException extends Exception {

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(List<ConfigurationIssue> issues) {
        super(formatIssues(issues));
    }

    /////////////
    // UTILITY //
    /////////////

    private static String formatIssues(List<ConfigurationIssue> issues) {
        StringBuilder sb = new StringBuilder();
        sb.append("The following configuration issues occurred:\n");
        for(ConfigurationIssue issue : issues) {
            sb.append("\n").append(issue.toString());
        }
        return sb.toString();
    }
}
