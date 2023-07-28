package replete.logging;

import java.util.Set;
import java.util.TreeSet;

import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;

public class LogCodeManager {


    ////////////
    // FIELDS //
    ////////////

    private static Set<LogCode> logCodes = new TreeSet<>();


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public static Set<LogCode> getLogCodesCopy() {
        synchronized(logCodes) {
            return new TreeSet<>(logCodes);
        }
    }

    // Mutators

    public static LogCode create(String category, Class<?> clazz, String code, String description) {
        return create(category, clazz, code, description, false);
    }
    public static LogCode create(String category, Class<?> clazz, String code, String description, boolean important) {
        if(code.length() > 10) {
            throw new RuntimeException("code too long");
        }
        LogCode logCode = new LogCode(category, clazz, code, description, important);
        synchronized(logCodes) {
            logCodes.add(logCode);
        }
        fireCodeAddedNotifier();
        return logCode;
    }

    // Meant to be called at the beginning of an app to initialize
    // from a previous app instance's already known and discovered
    // codes.
    public static void setCodes(Set<LogCode> logCodes) {
        LogCodeManager.logCodes = logCodes;
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    private static transient ChangeNotifier codeAddedNotifier = new ChangeNotifier(LogCodeManager.class);
    public static void addCodeAddedListener(ChangeListener listener) {
        codeAddedNotifier.addListener(listener);
    }
    private static void fireCodeAddedNotifier() {
        codeAddedNotifier.fireStateChanged();
    }
}
