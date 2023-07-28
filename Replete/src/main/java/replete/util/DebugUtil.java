
package replete.util;

import java.awt.Component;
import java.awt.IllegalComponentStateException;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicLong;

import replete.text.RStringBuilder;
import replete.text.StringUtil;
import replete.ui.GuiUtil;

/**
 * Useful class when debugging applications.
 *
 * @author Derek Trumbo
 */

public class DebugUtil {
    public static int step = 0;

    public static void noOp() {

    }


    ////////////////////
    // BASIC PRINTING //
    ////////////////////

    // Used as short hand for 'System.out.println' and
    // in place of having to concatenate strings together
    // for debug messages.

    public static void p() {
        System.out.println();
    }
    public static void p(Object o) {
        p(null, o);
    }
    public static void p(String desc, Object o) {
        if(desc != null) {
            System.out.print(desc + ": ");
        }
        System.out.println(o);
    }
    public static void pa(Object ... args) {
        pa(null, args);
    }
    public static void pa(String desc, Object ... args) {
        if(desc != null) {
            System.out.print(desc + ": ");
        }
        for(Object arg : args) {
            System.out.print(arg + ", ");
        }
        System.out.println();
    }


    //////////////////////
    // OBJECT INSPECION //
    //////////////////////

    public static void printObjectDetails(Object o) {
        printObjectDetails(o, 0, "Object Details");
    }

    public static void printObjectDetails(Object o, String desc) {
        printObjectDetails(o, 0, desc);
    }

    public static void printObjectDetails(Object o, int indent) {
        printObjectDetails(System.out, o, indent, "Object Details");
    }

    public static void printObjectDetails(Object o, int indent, String desc) {
        printObjectDetails(System.out, o, indent, desc);
    }

    public static void printObjectDetails(PrintStream ps, Object o, int indent, String desc) {
        String header = StringUtil.spaces(indent);
        if(desc != null) {
            header += desc + " ";
        }
        if(o != null) {
            header += "(hashCode=" + o.hashCode() + " | sysHashCode=" + System.identityHashCode(o) + ")";
        }
        ps.println(header);
        ps.println(getObjectDetails(o, indent));
    }

    public static String getObjectDetails(Object o, int indent) {
        if(o == null) {
            return "null";
        }
        String spaces = StringUtil.spaces(indent);
        String spaces2 = StringUtil.spaces(indent + 3);
        String TS = o.toString();
        String[] ci = getClassInfo(o);
        String supers = ci[0];
        String infc = ci[1];
        String toStr = spaces + "[*TS=" + TS.substring(0, (TS.length() > 100) ? 100 : TS.length())
                        + (((TS.length() > 100)) ? "..." : "") + "]\n";
        supers = spaces2 + "[C=" + supers + "]\n";
        infc = spaces2 + "[I=" + infc + "]";
        return toStr + supers + infc;
    }

    private static String[] getClassInfo(Object o) {
        String supers = "";
        String infc = "";
        Class<?> c = o.getClass();
        while(c.getSuperclass() != null) {
            Class<?>[] i = c.getInterfaces();
            for(Class<?> cx : i) {
                infc += cx.getSimpleName() + ", ";
            }
            supers = c.getSimpleName() + " < " + supers;
            c = c.getSuperclass();
        }
        supers = StringUtil.cut(supers, 3);
        infc = StringUtil.cut(infc, 2);
        if(infc.equals("")) {
            infc = "<none>";
        }
        return new String[] {supers, infc};
    }

    public static String getTypeInfo(Object o) {
        if(o == null) {
            return "null";
        }
        String[] ci = getClassInfo(o);
        String supers = ci[0];
        String infc = ci[1];
        return "E:" + supers + "  I:" + infc;
    }

    public static String quick(Object o) {
        if(o == null) {
            return "null";
        }
        return
            o.getClass().getName() + "@" + o.hashCode() +
            "  S:" + System.identityHashCode(o) +
            "  " + getTypeInfo(o);
    }
    public static String clazz(Object o) {
        if(o == null) {
            return "(NULL)";
        }
        return
            o.getClass().getSimpleName();
    }


    ///////////////////////
    // SYSTEM PROPERTIES //
    ///////////////////////

    /**
     * This method prints out all the properties held by the System class. This method contains
     * slightly more advanced code than:
     *
     * <pre>
     * Properties props = System.getProperties();
     * props.list(System.out);
     * </pre>
     *
     * Since the list method only prints out a maximum of 40 characters per key/value pair (followed
     * by an ellipsis if necessary) and the output is not ordered.
     */
    public static void showSystemProperties() {
        System.out.println("-- System Properties --");
        Vector<String> v = new Vector<String>();
        for(Object o : System.getProperties().keySet()) {
            v.add(o + "=" + System.getProperty((String) o));
        }
        Collections.sort(v);
        Iterator<String> it = v.iterator();
        while(it.hasNext()) {
            System.out.println(it.next());
        }
    }


    //////////
    // MISC //
    //////////

    public static String getNewlineInfo(String str) {
        int lf = 0;
        int cr = 0;
        boolean mixed = false;

        for(int pos = 0; pos < str.length(); pos++) {
            char ch = str.charAt(pos);
            if(ch == '\n') {
                lf++;
            }
            if(ch == '\r') {
                cr++;
                if(pos == str.length() - 1 || str.charAt(pos + 1) != '\n') {
                    mixed = true;
                }
            }
        }

        String result = "Line Feeds:\n";
        result += "  NL: " + lf + "\n";
        result += "  CR: " + cr + "\n";
        result += "  Format: ";
        if(lf == 0 && cr == 0) {
            result += "Unknown";
        } else if(lf != 0 && cr == 0) {
            result += "Unix";
        } else if(lf == 0 && cr != 0) {
            result += "Mac";
        } else {
            result += "Windows";
            if(lf != cr || mixed) {
                result += " (mixed)";
            }
        }

        return result + "\n";
    }

    public static void printNearTrace() {
        printNearTrace(null, 5);
    }
    public static void printNearTrace(int howMany) {
        printNearTrace(null, howMany);
    }
    public static void printNearTrace(String label) {
        printNearTrace(label, 5);
    }
    public static void printNearTrace(String label, int howMany) {
        System.out.print(getNearTrace(label, howMany));
    }

    public static String getNearTrace() {
        return getNearTrace(null, 5);
    }
    public static String getNearTrace(int howMany) {
        return getNearTrace(null, howMany);
    }
    public static String getNearTrace(String label) {
        return getNearTrace(label, 5);
    }
    public static String getNearTrace(String label, int howMany) {
        RStringBuilder builder = new RStringBuilder();
        StackTraceElement[] elems = Thread.currentThread().getStackTrace();
        int count = 0;
        String extra = label != null ? " @ " + label : "";
        builder.appendln("Near Trace" + extra + ":");
        for(StackTraceElement e : elems) {
            if(e.getClassName().equals(DebugUtil.class.getName()) ||
                    e.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            builder.append("    ");
            builder.appendln(e);
            if(++count == howMany) {
                break;
            }
        }
        return builder.toString();
    }

    public static String getMethodClass(int which) {
        StackTraceElement[] elems = Thread.currentThread().getStackTrace();
        return elems[which + 2].getClassName();  // +2 ignores Thread.* & DebugUtil.getMethodClass
    }

    public static void trackFocus() {
        PropertyChangeListener l = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Component prev = (Component) evt.getOldValue();
                Component next = (Component) evt.getNewValue();
                System.out.println("-Focus Leaving: " + render(prev));
                System.out.println("+Focus Entering: " + render(next));
            }
            private String render(Component c) {
                if(c == null) {
                    return "(none)";
                }
                String ret = c.getClass().getName();
                Window w = GuiUtil.win(c);
                if(w != null) {
                    if(ReflectionUtil.hasMethod(w, "getTitle")) {
                        ret += " {in \"" + ReflectionUtil.invoke(w, "getTitle") + "\"}";
                    }
                }
                if(ReflectionUtil.hasMethod(c, "getText")) {
                    ret += " \"" + ReflectionUtil.invoke(c, "getText") + "\"";
                }
                try {
                    Point curLoc = c.getLocationOnScreen();
                    ret += " [" + curLoc.x + "x" + curLoc.y + "]";
                } catch(IllegalComponentStateException e) {
                    ret += " [(Not On Screen)]";
                }
                ret += " (" + c.toString() + ")";
                return ret;
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("permanentFocusOwner", l);
    }

    public synchronized static void step() {
        step(false);
    }
    public synchronized static void step(boolean restart) {
        if(restart) {
            step = 1;
        }
        System.err.println("Step " + step);
        step++;
    }

    // Some JVM options to try: -XX:MaxJavaStackTraceDepth=1000000 -Xss2M
    public static void countMaxStackFrames() {
        AtomicLong count = new AtomicLong();
        try {
            overflow(count);
        } catch(Throwable e) {
            int len = e.getStackTrace().length;
            System.out.println("Stack Trace Entries: " + StringUtil.commas(len));
            System.out.println("Stack Frame Depth:   " + StringUtil.commas(count.get()));
            if(len != 0) {
                StackTraceElement first = e.getStackTrace()[len - 1];
                System.out.println("First Stack Trace Entry: " + first);
            }
        }
    }
    private static void overflow(AtomicLong count) {
        count.incrementAndGet();
        overflow(count);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        countMaxStackFrames();
//        char[] str = new char[] {0, 11, '\n', 'D', 'e', 'r', '\r', 4, 22, 'X', '\t'};
//        String s = new String(str);
//        System.out.println(StringUtil.toReadableChars(s));
//        System.out.println(getNewlineInfo("once\r\nupon\r\na time\r"));
    }
}
