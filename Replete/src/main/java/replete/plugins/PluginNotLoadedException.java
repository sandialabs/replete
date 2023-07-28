package replete.plugins;

public class PluginNotLoadedException extends RuntimeException {
    public PluginNotLoadedException() {
        super();
    }
    public PluginNotLoadedException(String message, Throwable cause) {
        super(message, cause);
    }
    public PluginNotLoadedException(String message) {
        super(message);
    }
    public PluginNotLoadedException(Throwable cause) {
        super(cause);
    }
}
