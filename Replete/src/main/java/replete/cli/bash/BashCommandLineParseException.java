package replete.cli.bash;

public class BashCommandLineParseException extends RuntimeException {
    public BashCommandLineParseException() {
        super();
    }
    public BashCommandLineParseException(String message, Throwable cause) {
        super(message, cause);
    }
    public BashCommandLineParseException(String message) {
        super(message);
    }
    public BashCommandLineParseException(Throwable cause) {
        super(cause);
    }
}
