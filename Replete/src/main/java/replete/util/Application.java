package replete.util;

/**
 * @author Derek Trumbo
 */

public class Application {
    private static final String APP_TITLE_KEY = "program.title";
    private static final String APP_NAME_KEY = "program.name";
    private static final String APP_VERS_KEY = "program.version";

    public static String getTitle() {
        return System.getProperty(APP_TITLE_KEY);
    }
    public static String getName() {
        return System.getProperty(APP_NAME_KEY);
    }
    public static String getVersion() {
        return System.getProperty(APP_VERS_KEY);
    }

    public static void setTitle(String name) {
        System.setProperty(APP_TITLE_KEY, name);
    }
    public static void setName(String name) {
        System.setProperty(APP_NAME_KEY, name);
    }
    public static void setVersion(String version) {
        System.setProperty(APP_VERS_KEY, version);
    }
}
