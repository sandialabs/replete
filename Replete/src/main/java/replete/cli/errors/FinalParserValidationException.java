package replete.cli.errors;

/**
 * @author Derek Trumbo
 */

public class FinalParserValidationException extends CommandLineParseException {
    public FinalParserValidationException(String msg) {
        super(msg);
    }
}
