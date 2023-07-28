package replete.mail;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a utility class that provides some useful methods
 * for the mail template subsystem.
 *
 * @author Derek Trumbo
 */

public class MailVariableResolver {

    /**
     * Given a string representing the body of an email
     * with embedded ${variable} expressions to represent
     * dynamic content, replace the variable expressions
     * with their corresponding values from the given
     * map.  Variable names are case-sensitive.
     */
    public static String resolve(String text, Map<String, String> vars) {

        if(text == null) {
            throw new IllegalArgumentException("'text' must not be null");
        }
        if(vars == null) {
            throw new IllegalArgumentException("'vars' must not be null");
        }

        for(String varName : vars.keySet()) {
            String value = vars.get(varName);
            // Two special characters allowed in replacement string
            // must be escaped.
            value = value.replaceAll("\\\\", "\\\\\\\\");
            value = value.replaceAll("\\$", "\\\\\\$");
            text = text.replaceAll("\\$\\{" + varName + "\\}", value);      // Matcher.quoteReplacement??
        }
        return text;
    }

    /**
     * Searches a given string for variable tokens like ${XYZ}
     * and returns all of the variable names as a set.
     */
    public static Set<String> findVariables(String text) {
        Set<String> vars = new HashSet<String>();

        if(text == null) {
            throw new IllegalArgumentException("'text' must not be null");
        }

        // Compile an expression that will find all of the variable
        // names.  The outer capture group finds the ${XYZ} and the
        // inner capture group isolates the XYZ inside.  The characters
        // $, {, and } are all special characters in Java's regex
        // scheme, so they all need to be escaped.  The *? is the
        // non-greedy version of *.
        Pattern p = Pattern.compile("(\\$\\{([a-zA-Z0-9]*?)\\})");
        Matcher m = p.matcher(text);
        while(m.find()) {
            vars.add(m.group(2));    // Add the inner capture group (variable name).
        }

        return vars;
    }
}
