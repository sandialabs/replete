package replete.plugins;

public class LoadPluginException extends RuntimeException {
    public LoadPluginException() {
        super();
    }
    public LoadPluginException(String message, Throwable cause) {
        super(message, cause);
    }
    public LoadPluginException(String message) {
        super(message);
    }
    public LoadPluginException(Throwable cause) {
        super(cause);
    }
}
