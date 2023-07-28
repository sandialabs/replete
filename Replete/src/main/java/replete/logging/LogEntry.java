package replete.logging;

import java.util.Date;

/**
 * @author Derek Trumbo
 */

public class LogEntry implements Comparable<LogEntry> {
    protected LogEntryType type;
    protected Date date;
    protected String text;

    @Override
    public String toString() {
        String extra = "";
        if(date != null) {
            extra = " (" + date + ")";
        }
        return type.getName() + extra;
    }

    // Implement a descending sort order between
    // any two LogEntry objects.
    public int compareTo(LogEntry entry) {

        // Handle null date references.
        if(date == null && entry.date != null) {
            return 1;
        } else if(date == null && entry.date == null) {
            return 0;
        } else if(date != null && entry.date == null) {
            return -1;
        }

        if(date.getTime() < entry.date.getTime()) {
            return 1;
        } else if(date.getTime() == entry.date.getTime()) {
            return 0;
        }

        return -1;
    }
}
