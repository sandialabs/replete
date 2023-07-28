package replete.cli.errors;

/**
 * @author Derek Trumbo
 */

public class XArgsException extends CommandLineParseException {
    private String xargsEscapeToken;
    public XArgsException(String xargsEscapeToken) {
        super("Invalid XArgs escape token '" + xargsEscapeToken + "'");
        this.xargsEscapeToken = xargsEscapeToken;
    }
    public String getXargsEscapeToken() {
        return xargsEscapeToken;
    }
}
