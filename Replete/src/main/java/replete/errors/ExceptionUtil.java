package replete.errors;


import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import javax.swing.SwingUtilities;

import replete.text.StringUtil;
import replete.ui.GuiUtil;
import replete.ui.windows.Dialogs;
import replete.ui.windows.ExceptionDetails;
import replete.util.ReflectionUtil;


/**
 * @author Derek Trumbo
 */

public class ExceptionUtil {

    // WHY O WHY HAS JAVA USED 'THIS' AND NOT NULL FOR LACK OF A CAUSE!!!!!!
    // This is only useful if you're going to be trying to serialize
    // Throwable objects directly outside of the software.  However,
    // using a ThrowableSnapshot is a much more advised approach.
    public static void nullifyLeafCause(Throwable ex) {
        Throwable causeOrTarget;
        while(ex != null) {
            if(ex instanceof InvocationTargetException) {
                causeOrTarget = ReflectionUtil.get(ex, "target");   // Can't just call getCause()
            } else {
                causeOrTarget = ReflectionUtil.get(ex, "cause");    // Can't just call getCause()
            }
            if(ex == causeOrTarget) {
                break;
            }
            ex = causeOrTarget;
        }
        if(ex != null) {
            ReflectionUtil.set(ex, "cause", null);        // Only "cause" relevant here.  ITE's would never have "target" refer to themselves.
        }
    }

    public static void fillStackTraces(Throwable ex) {
        while(ex != null) {
            ex.getStackTrace();   // Would rather call getOurStackTrace()...
            ex = ex.getCause();   // Should still work with InvocationTargetException's as well
        }
    }

    public static Integer getTopLineNumber(Exception e) {
        if(e.getStackTrace() != null && e.getStackTrace().length > 0) {
            return e.getStackTrace()[0].getLineNumber();
        }
        return null;
    }

    public static Integer getFirstLineNumber(Exception e, Class<?> clazz) {
        StackTraceElement[] elems = e.getStackTrace();
        if(elems != null) {
            for(int i = 0; i < elems.length; i++) {
               StackTraceElement ste = elems[i];
               if(ste.getClassName().equals(clazz.getName())) {
                   return ste.getLineNumber();
               }
            }
        }
        return null;
    }

    /**
     * For use when you need an exception or error's
     * entire stack trace as a single string.
     */
    public static String toCompleteString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * For use when you need an exception or error's
     * entire stack trace as a single string.
     * Converts tabs to spaces for you.
     */
    public static String toCompleteString(Throwable t, int tabsToSpaces) {
        return toCompleteString(t).replaceAll("\\t", StringUtil.spaces(tabsToSpaces));
    }

    /**
     * A very basic uncaught exception handler.
     */
    public static void setStandardUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {

                // Write error to standard error always.
                e.printStackTrace();
                // TODO: Log4J always

                Window win = null;
                if(SwingUtilities.isEventDispatchThread()) {
                    Component c = KeyboardFocusManager.
                        getCurrentKeyboardFocusManager().getFocusOwner(); // Null safe if no UI yet
                    win = GuiUtil.win(c);                                 // Null safe if no UI yet
//                } else if(t instanceof WindowAwareThread) {  // Interface that could be written...
//                    win = ((WindowAwareThread) t).getWindow();
                    // Although seemingly an OK idea, would indicate poor
                    // BG thread spawning pattern in your UI code.
                }

                // TODO: Could dump a lot more information
                // Like all thread information or an
                // entire Scrutinize dump that the user could
                // choose to send to a central server like
                // other software after looking at it.

                if(e instanceof OutOfMemoryError) {
                    Dialogs.showDetails(win, "Out of memory.\n" +
                        "Try allocating more memory (Java VM argument\n" +
                        "-Xmx1024m, for example)",
                        "Out of Memory", e);
                } else {
                    String title = "Fatal Error";
                    String msg =
                        "Uncaught exception raised to top of thread,\n" +
                        "indicating a possible internal inconsistency.\n" +
                        "Your last selected action did not complete successfully.\n" +
                        "Please save the diagnostic information below for\n" +
                        "troubleshooting.";
                    Dialogs.showDetails(win,
                        new ExceptionDetails()
                            .setMessage(msg)
                            .setTitle(title)
                            .setDetailsMessage("Thread: " + t)
                            .setError(e));
                }
            }
        });
    }

    public static void toss() {
        toss("Debug Exception");
    }
    public static void toss(String msg) {
        throw new IntentionalDebugException(msg);
    }
    public static void toss(double chance) {
        toss("Debug Exception", chance);
    }
    public static void toss(String msg, double chance) {
        double test = new Random().nextDouble();
        if(test < chance) {
            throw new IntentionalDebugException(msg + " (" + test + " < " + chance + ")");
        }
    }
}
