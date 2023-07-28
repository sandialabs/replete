package replete.logging;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import replete.errors.ExceptionUtil;

// DailyRollingFileAppender causes "log4j:ERROR Failed to rename avondale.log to avondale.log.yyyy-MM-dd"
// https://tedorg.wordpress.com/2009/03/17/when-log4j-dailyrollingfileappender-fails-to-roll-over/

public class EventCountingAppender extends DailyRollingFileAppender {


    ///////////////
    // INSTANCES //
    ///////////////

    private static Map<String, EventCountingAppender> instances = new HashMap<>();
    public static void setInstance(String name, EventCountingAppender appender) {
        instances.put(name, appender);
    }
    public static EventCountingAppender getInstance(String name) {
        return instances.get(name);
    }


    ////////////
    // FIELDS //
    ////////////

    private int totalEventCount = 0;
    private Map<String, Integer> eventCounts = new TreeMap<>();
    private Map<String, Integer> exceptionCounts = new TreeMap<>();
    private Map<String, String> untrackedLoggers = new HashMap<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    // Default constructor seems to be called over the other one in our system.
    public EventCountingAppender() {}
    public EventCountingAppender(Layout layout, String filename, String datePattern) throws IOException {
        super(layout, filename, datePattern);
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getTotalEventCount() {
        return totalEventCount;
    }
    public Map<String, Integer> getEventCountsCopy() {
        synchronized(eventCounts) {
            return new TreeMap<>(eventCounts);  // Make copy
        }
    }
    public Map<String, Integer> getExceptionCountsCopy() {
        synchronized(exceptionCounts) {
            return new TreeMap<>(exceptionCounts);  // Make copy
        }
    }
    public Map<String, String> getUntrackedLoggers() {
        return untrackedLoggers;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected void subAppend(LoggingEvent e) {
        super.subAppend(e);
        totalEventCount++;
        recordEvent(e);
        recordErrorEvent(e);
    }


    //////////
    // MISC //
    //////////

    private void recordEvent(LoggingEvent e) {
        Object messageObj = e.getMessage();
        String message;
        if(messageObj instanceof String) {
            message = (String) messageObj;
        } else if(messageObj != null) {
            message = messageObj.toString();
        } else {
            message = "(null)";
        }

        // Often a fully qualified class name, but not always
        String loggerName = e.getLoggerName();
        String classCode = untrackedLoggers.get(loggerName);

        if(classCode == null) {
            try {
                if(message.charAt(0) != '{') {
                    throw new RuntimeException("Message does not begin with '{'");
                }
                int clbr = message.indexOf('}');
                if(clbr == -1) {
                    throw new RuntimeException("brace character missing");
                }
                String code = message.substring(0, clbr + 1).trim();  // Include { and }
                if(code.length() > 10) {
                    throw new RuntimeException("code too long");
                }
                classCode = loggerName + " " + code;

            } catch(Exception ex) {
                classCode = "<UNKNOWN>";

                System.err.println(EventCountingAppender.class.getSimpleName() +
                    ": <UNKNOWN> Event: (" + e.getLevel() + ") " + e.getMessage());
                System.err.println("  Logger: " + e.getLoggerName() +
                    " [parse error line num: " + ExceptionUtil.getTopLineNumber(ex) + "]");
            }
        }

        synchronized(eventCounts) {
            Integer count = eventCounts.get(classCode);
            if(count == null) {
                count = 0;
            }
            eventCounts.put(classCode, count + 1);
        }
    }

    private void recordErrorEvent(LoggingEvent e) {
        ThrowableInformation ti = e.getThrowableInformation();
        if(ti != null) {
            synchronized(exceptionCounts) {
                String errorClass = ti.getThrowable().getClass().getName();
                Integer count = exceptionCounts.get(errorClass);
                if(count == null) {
                    count = 0;
                }
                exceptionCounts.put(errorClass, count + 1);
            }
        }
    }
}
