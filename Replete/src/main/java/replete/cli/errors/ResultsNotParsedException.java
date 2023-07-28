package replete.cli.errors;

/**
 * @author Derek Trumbo
 */

// Represents errors that occur when result methods are called before
// a successful parse of arguments was performed.
public class ResultsNotParsedException extends OptionAccessException {
    public ResultsNotParsedException(String var) {
        super("Attempted to access the " + var + " before a successful parse.");
    }
}
