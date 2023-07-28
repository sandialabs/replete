package replete.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import replete.text.StringUtil;

public class DateUtil {


    ////////////
    // FIELDS //
    ////////////

    private static String[][] timeUnitLabels = new String[][] {
        {"y",     "d",    "h",     "m",       "s",       "ms"},            // labelVerbosity = 0
        {"yr",    "dy",   "hr",    "min",     "sec",     "msec"},          // labelVerbosity = 1
        {"years", "days", "hours", "minutes", "seconds", "milliseconds"}   // labelVerbosity = 2
    };

    static String[][] timeUnitAliases = new String[][] {
        {"years",   "yrs",  "yr",  "y"},
        {"months",  "mon",  "M"},
        {"weeks",   "wks",  "wk",  "w"},
        {"days",    "day",  "dy",  "d"},
        {"hours",   "hrs",  "hr",  "h"},
        {"minutes", "mins", "min", "mn", "m"},
        {"seconds", "secs", "sec", "s"},
    };

    // Since these are shared objects, access to their
    // methods must be synchronized, as SimpleDateFormat
    // is not thread-safe {artf195129}.
    private static SimpleDateFormat shortFormat       = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat longFormat        = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
    private static SimpleDateFormat longCompactFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private static SimpleDateFormat longCompact2Format = new SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss");
    private static SimpleDateFormat logFormat         = new SimpleDateFormat("yyyyMMdd:HHmmss.SSS");
    // New Java 8 DateTimeFormatter
    private static final DateTimeFormatter longFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");


    /////////////////////
    // DATE FORMATTING //
    /////////////////////

    public static String toShortString(long millis) {
        return toShortString(new Date(millis));
    }
    public static String toShortString(Date d) {
        return format(shortFormat, d);
    }

    public static String toLongString(long millis) {
        return toLongString(new Date(millis));
    }
    public static String toLongString(Date d) {
        return format(longFormat, d);
    }

    public static String toLongCompactString(long millis) {
        return toLongCompactString(new Date(millis));
    }
    public static String toLongCompactString(Date d) {
        return format(longCompactFormat, d);
    }

    public static String toLongCompact2String(long millis) {
        return toLongCompact2String(new Date(millis));
    }
    public static String toLongCompact2String(Date d) {
        return format(longCompact2Format, d);
    }

    public static String toLogString(long millis) {
        return toLogString(new Date(millis));
    }
    public static String toLogString(Date d) {
        return format(logFormat, d);
    }

    // Only to be called by one of the above methods
    // that pass in a static object.
    private static String format(SimpleDateFormat format, Date d) {
        synchronized(format) {           // SimpleDateFormat is not thread-safe. It apparently
            return format.format(d);     // maintains a cache within that can become corrupted
        }                                // and cause ArrayIndexOutOfBoundsException's.
    }


    ///////////////////////////
    // LocalDateTime Methods //
    ///////////////////////////

    public static LocalDateTime dateFromStr(String str) throws DateTimeParseException {
        return LocalDateTime.parse(str, longFormatter);
    }

    public static String stringFromDate(LocalDateTime date) {
        return date == null ? "" : date.format(longFormatter);
    }

    public static boolean isValid(String str) {
        try {
            dateFromStr(str);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    public static String getStrForStartOfToday() {
        return stringFromDate(LocalDateTime.now().withHour(0).withMinute(1).withSecond(0));
    }

    public static String validOrBlank(String str) {
        if (isValid(str)) {
            return str;
        } else {
            return "";
        }
    }

    public static LocalDateTime validOrNull(String str) {
        try {
            return dateFromStr(str);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }


    /////////////
    // ELAPSED //
    /////////////

    public static String toElapsedString(long origMillis) {
        return toElapsedString(origMillis, ElapsedVerbosity.SHORT, false, false, false);
    }
    public static String toElapsedString(long origMillis, ElapsedVerbosity labelVerbosity) {
        return toElapsedString(origMillis, labelVerbosity, false, false, false);
    }
    public static String toElapsedString(long origMillis, ElapsedVerbosity labelVerbosity, boolean spacesAfterNums) {
        return toElapsedString(origMillis, labelVerbosity, spacesAfterNums, false, false);
    }
    public static String toElapsedString(long origMillis, ElapsedVerbosity labelVerbosity, boolean spacesAfterNums, boolean expandMS) {
        return toElapsedString(origMillis, labelVerbosity, spacesAfterNums, expandMS, false);
    }
    public static String toElapsedString(long origMillis, ElapsedVerbosity labelVerbosity, boolean spacesAfterNums, boolean expandMS, boolean ignorePastDays) {
        boolean neg = origMillis < 0;
        origMillis = Math.abs(origMillis);

        long millis = origMillis;
        long msPerSec = 1000;
        long msPerMin = msPerSec * 60;
        long msPerHour = msPerMin * 60;
        long msPerDay = msPerHour * 24;
        long msPerYear = msPerDay * 365;  // Technically approximation

        long years = millis / msPerYear;  millis %= msPerYear;
        long days = millis / msPerDay;    millis %= msPerDay;
        long hours = millis / msPerHour;  millis %= msPerHour;
        long mins = millis / msPerMin;    millis %= msPerMin;
        long secs = millis / msPerSec;    millis %= msPerSec;

        int level = labelVerbosity.ordinal();

        String sp = (!spacesAfterNums && level != 2) ? "" : " ";

        StringBuilder buffer = new StringBuilder();
        if(years != 0) {
            String lbl = timeUnitLabels[level][0];
            if(years == 1 && lbl.endsWith("s")) {
                lbl = lbl.substring(0, lbl.length() - 1);
            }
            buffer.append(years);
            buffer.append(sp);
            buffer.append(lbl);
            buffer.append(" ");
        }
        if(days != 0) {
            String lbl = timeUnitLabels[level][1];
            if(days == 1 && lbl.endsWith("s")) {
                lbl = lbl.substring(0, lbl.length() - 1);
            }
            buffer.append(days);
            buffer.append(sp);
            buffer.append(lbl);
            buffer.append(" ");
        }
        if(!ignorePastDays) {
            if(hours != 0) {
                String lbl = timeUnitLabels[level][2];
                if(hours == 1 && lbl.endsWith("s")) {
                    lbl = lbl.substring(0, lbl.length() - 1);
                }
                buffer.append(hours);
                buffer.append(sp);
                buffer.append(lbl);
                buffer.append(" ");
            }
            if(mins != 0) {
                String lbl = timeUnitLabels[level][3];
                if(mins == 1 && lbl.endsWith("s")) {
                    lbl = lbl.substring(0, lbl.length() - 1);
                }
                buffer.append(mins);
                buffer.append(sp);
                buffer.append(lbl);
                buffer.append(" ");
            }
            if(secs != 0 || origMillis == 0) {
                String lbl = timeUnitLabels[level][4];
                if(secs == 1 && lbl.length() > 1 && lbl.endsWith("s")) {
                    lbl = lbl.substring(0, lbl.length() - 1);
                }
                buffer.append(secs);
                buffer.append(sp);
                buffer.append(lbl);
                buffer.append(" ");
            }

            // If we want to show the millis like every other time unit...
            if(expandMS && millis != 0) {
                String lbl = timeUnitLabels[level][5];
                if(millis == 1 && level == 2 && lbl.length() > 1 && lbl.endsWith("s")) {
                    lbl = lbl.substring(0, lbl.length() - 1);
                }
                buffer.append(millis);
                buffer.append(sp);
                buffer.append(lbl);

            // else if we don't want to expand the millis, and the original
            // duration was less than 1 second...
            } else if(millis != 0 && origMillis < 1000) {
                String lbl = timeUnitLabels[level][4];
                if(lbl.length() > 1 && lbl.endsWith("s")) {
                    lbl = lbl.substring(0, lbl.length() - 1);
                }
                buffer.append("<1");
                buffer.append(sp);
                buffer.append(lbl);
            }
        }

        String ret = buffer.toString().trim();

        if(neg) {
            if(ret.startsWith("<1")) {
                ret = ">-1" + ret.substring(2);   // Not using another StringBuilder here
            } else {
                ret = "-" + ret;
            }
        }

        return ret;
    }

    public static Long convertTextToMsDur(String text) {
        text = text.trim();
        String[] values = parseDuration(text);
        if(values == null) {
            return null;
        }
        double totalDuration = 0;
        long[] millisPerUnit = {
            365 * 24 * 60 * 60 * 1000,    // simplistic
             30 * 24 * 60 * 60 * 1000,    // simplistic
              7 * 24 * 60 * 60 * 1000,
                  24 * 60 * 60 * 1000,
                       60 * 60 * 1000,
                            60 * 1000,
                                 1000
        };
        int u = 0;
        for(String value : values) {
            double unitMultiplier;
            if(value == null) {
                unitMultiplier = 0.0;
            } else {
                unitMultiplier = Double.parseDouble(value);
            }
            totalDuration += unitMultiplier * millisPerUnit[u++];
        }
        return (long) totalDuration;
    }

    public static String[] parseDuration(String target) {
        String prefix = "(?:\\s*([0-9]+(?:\\.[0-9]+)?)\\s*(?:";
        String suffix = "))?";
        String wholePattern = "";
        for(String[] timeUnits : timeUnitAliases) {
            String all = StringUtil.join(timeUnits, "|");
            String timeUnitPattern = prefix + all + suffix;
            wholePattern += timeUnitPattern;
        }
        if(!target.matches(wholePattern)) {
            return null;
        }
        String[] captures = StringUtil.extractCaptures(target, wholePattern);
        // ^ extractCaptures currently not returning null's back consistently.
        return captures;
    }


    public static String ago(Date sinceBaseDate) {
        return ago(sinceBaseDate == null ? null : sinceBaseDate.getTime());
    }
    public static String ago(Long sinceBaseTime) {
        long laterTimeNow = System.currentTimeMillis();
        return ago(laterTimeNow, sinceBaseTime);
    }
    public static String ago(long laterTime, Date sinceBaseDate) {
        return ago(laterTime, sinceBaseDate == null ? null : sinceBaseDate.getTime());
    }
    public static String ago(long laterTime, Long sinceBaseTime) {
        String ago = "";
        if(sinceBaseTime != null && laterTime != sinceBaseTime) {
            long elapsedSince = laterTime - sinceBaseTime;
            if(elapsedSince > 0) {
                ago = " (" + toElapsedString(elapsedSince,
                    ElapsedVerbosity.MED, true) + " ago)";
            } else {
                ago = " (" + toElapsedString(-elapsedSince,
                    ElapsedVerbosity.MED, true) + " from now)";
            }
        }
        return ago;
    }

    public static String dateDiff(long laterTime, Long sinceBaseTime) {
        String ago = "";
        if(sinceBaseTime != null && laterTime != sinceBaseTime) {
            long elapsedSince = laterTime - sinceBaseTime;
            if(elapsedSince > 0) {
                String tes = toElapsedString(elapsedSince, ElapsedVerbosity.MED, true, false, true);
                ago = tes.equals("") ? "(same)" : "+" + tes;
            } else {
                String tes = toElapsedString(-elapsedSince, ElapsedVerbosity.MED, true, false, true);
                ago = tes.equals("") ? "(same)" : "-" + tes;
            }
        }
        return ago;
    }

    public static String toLongStringAndAgo(long sinceBaseTime) {
        long laterTimeNow = System.currentTimeMillis();
        return toLongStringAndAgo(laterTimeNow, sinceBaseTime);
    }
    public static String toLongStringAndAgo(long laterTime, long sinceBaseTime) {
        return toLongString(sinceBaseTime) + ago(laterTime, sinceBaseTime);
    }
    public static String toLongStringAndAgoHtml(long laterTime, long sinceBaseTime) {
        return "<b>" + toLongString(sinceBaseTime) + "</b><i>" + ago(laterTime, sinceBaseTime) + "</i>";
    }

    public static Date parse(String source, String... formats) {
        for(String format : formats) {
            SimpleDateFormat fmt = new SimpleDateFormat(format);
            ParsePosition pos = new ParsePosition(0);
            Date date = fmt.parse(source, pos);
            if(date == null || pos.getIndex() != source.length()) {
                continue;
            }
            return date;
        }
        return null;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        System.out.println(DateUtil.toLongCompact2String(System.currentTimeMillis()));
        if(true) {
            return;
        }

        System.out.println(convertTextToMsDur("dog"));

        String[] formats = {
            "yyyy-MM-dd", "yyyyMMdd", "yyyy/MM/dd",
            "yyyy-MM-dd HH:mm",    "yyyy/MM/dd HH:mm",
            "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss",
            "yyyyMMdd:HHmmss",     "yyyyMMdd:HHmmss.SSS"
        };
        Date d = parse("1970-1-1 9:22:22", formats);
        System.out.println(d.getTime());
        System.out.println(d);
        if(true) {
            return;
        }
        long[] times = {-3500, -500, 0, 1, 10, 1000, 100000, 1458796, 10000000};
        for(boolean expMs : new boolean[] {true, false}) {
            System.out.println("ExpandMs = " + expMs);
            for(boolean spaces : new boolean[] {true, false}) {
                System.out.println("    Spaces = " + spaces);
                for(ElapsedVerbosity level : ElapsedVerbosity.values()) {
                    System.out.println("        Level = " + level);
                    for(long time : times) {
                        p(time, level, spaces, expMs);
                    }
                }
            }
        }
    }
    private static void p(long time, ElapsedVerbosity level, boolean spaces, boolean expandMs) {
        System.out.println("            " + time + " = [" + toElapsedString(time, level, spaces, expandMs) + "]");
    }
}
