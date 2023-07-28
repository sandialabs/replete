package replete.logging;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;

import replete.ui.windows.Dialogs;


/**
 * @author Derek Trumbo
 */

public class LogManager {
    private static SimpleDateFormat dateF = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

    public static File logFile;

    public static File getLogFile() {
        return logFile;
    }

    public static void setLogFile(File file) {
        logFile = file;
    }

    public static List<LogEntry> readLogEntries() {

        if(logFile == null || !logFile.exists()) {
            return new ArrayList<LogEntry>();
        }

        try {

            List<LogEntry> entries = new ArrayList<LogEntry>();
            StringBuffer text = null;
            Date date = null;
            LogEntryType type = LogEntryType.INFO;

            BufferedReader reader = new BufferedReader(new FileReader(logFile));

            String line;
            while((line = reader.readLine()) != null) {
                if(isNewEntry(line)) {

                    if(text != null) {
                        LogEntry entry = new LogEntry();
                        entry.text = text.toString().trim();
                        entry.date = date;
                        entry.type = type;
                        entries.add(entry);
                    }

                    text = new StringBuffer();
                    date = null;
                    type = LogEntryType.INFO;
                }

                if(text != null) {
                    text.append(line + "\n");

                    if(isDateLine(line)) {
                        date = extractDate(line);
                    }
                    if(isTypeLine(line)) {
                        type = extractType(line);
                    }
                }
            }

            if(text != null) {
                LogEntry entry = new LogEntry();
                entry.text = text.toString().trim();
                entry.date = date;
                entry.type = type;
                entries.add(entry);
            }

            reader.close();

            Collections.sort(entries);

            return entries;

        } catch(IOException e) {

            return new ArrayList<LogEntry>();
        }
    }

    public static boolean isNewEntry(String line) {
        String pattern = "^---- .* Log Entry ----$";
        return line.matches(pattern);
    }

    public static boolean isDateLine(String line) {
        return line.startsWith("Date:");
    }
    public static boolean isTypeLine(String line) {
        return line.startsWith("Severity:");
    }
    public static Date extractDate(String line) {
        try {
            return dateF.parse(line.substring(5).trim());
        } catch(ParseException e) {
            return null;
        }
    }
    public static LogEntryType extractType(String line) {
        try {
            String t = line.substring("Severity:".length()).trim();
            return LogEntryType.fromString(t);
        } catch(IllegalArgumentException e) {
            return LogEntryType.INFO;
        }
    }

    public static void log(JFrame parent, LogEntryType type, String msg) {
        log(parent, type, msg, null);
    }

    public static void log(JFrame parent, LogEntryType type, Throwable t) {
        log(parent, type, null, t);
    }

    public static void log(JFrame parent, LogEntryType type, Throwable t, boolean showDialogs) {
        log(parent, type, null, t, showDialogs);
    }

    public static void log(JFrame parent, LogEntryType type, String msg, Throwable t) {
        log(parent, type, msg, t, true);
    }

    public static void log(JFrame parent, LogEntryType type, String msg, Throwable t, boolean showDialogs) {

        String error = "---- " + System.getProperty("program.name") + " Log Entry ----\n" +
            "Severity: " + type.getName() + "\n" +
            "Date: " + dateF.format(new Date()) + "\n";

        if(msg != null) {
            error += msg + "\n";
        }

        if(t != null) {
            StringWriter writer = new StringWriter();
            t.printStackTrace(new PrintWriter(writer));
            error += writer.toString();

            // Write error to standard error.
            t.printStackTrace();
        }

        error += "\n\n";

        // Append error string to log file.
        if(logFile != null) {
            try {
                BufferedWriter out =
                    new BufferedWriter(new FileWriter(logFile, true));
                out.write(error);
                out.close();
            }
            catch(IOException ioe) {}
        }

        String dlgMsg = "";
        if(t != null && msg == null) {
            if(t instanceof OutOfMemoryError) {
                dlgMsg ="Out of memory.\n" +
                    "Try allocating more memory (Java VM argument -Xmx1024m, for example)";
            } else {
                dlgMsg = "Uncaught exception raised to top of thread,\n" +
                    "indicating a possible internal inconsistency.\n" +
                    "Your last selected action " +
                    "did not complete successfully.";
            }
        } else if(msg != null) {
            dlgMsg = msg;
        }

        if(showDialogs) {
            int res = Dialogs.showMulti(parent, dlgMsg, System.getProperty("program.name") + ": " + type.getName(),
                new String[] {"OK", "Log &Viewer..."}, type.dlgType);

            if(res == 1) {
                LogViewer logViewer = new LogViewer(parent);
                logViewer.setVisible(true);
            }
        }
    }
}
