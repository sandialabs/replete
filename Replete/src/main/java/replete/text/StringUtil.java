package replete.text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import replete.collections.Pair;
import replete.numbers.NumUtil;
import replete.text.patterns.PatternInterpretation;
import replete.text.patterns.PatternUtil;
import replete.util.ReflectionUtil;

/**
 * Convenience methods for string-related operations.
 *
 * @author Derek Trumbo
 */

public class StringUtil {


    ////////////
    // FIELDS //
    ////////////

    private static PatternPool patternPool = new PatternPool();


    ////////////
    // YES/NO //
    ////////////

    public static String yn(boolean val) {
        return val ? "Y" : "N";
    }
    public static String yesNo(boolean val) {
        return val ? "Yes" : "No";
    }
    public static String yn(Object val) {
        return (val != null) ? "Y" : "N";
    }
    public static String yesNo(Object val) {
        return (val != null) ? "Yes" : "No";
    }


    ////////////////
    // NULL/BLANK //
    ////////////////

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
    public static String replaceBlank(String str, String replaceWith) {
        return isBlank(str) ? replaceWith : str;
    }
    public static String ifNull(String primary, String ifNull) {
        return primary == null ? ifNull : primary;
    }
    public static String cleanNull(Object obj) {
        return obj == null ? "" : obj.toString();
    }
    public static String cleanNull(String str) {
        return str == null ? "" : str;
    }
    public static String forceBlankNull(String str) {
        return isBlank(str) ? null : str;
    }


    ////////////
    // COMMAS //
    ////////////

    public static String commas(long i) {
        return commas("" + i);
    }
    public static String commas(double d) {
        return commas("" + d);
    }
    public static String commas(String num) {
        int dp = num.indexOf('.');

        if(dp == -1) {
            String result = "";
            for(int x = 0; x < num.length(); x++) {
                if(x % 3 == 0 && x != 0) {
                    result = "," + result;
                }
                result = num.charAt(num.length() - x - 1) + result;
            }
            return result;
        }

        return commas(num.substring(0, dp)) + num.substring(dp);
    }


    ///////////////
    // REPLICATE //
    ///////////////

    // TODO: add boolean to add a \n character
    public static String replicateChar(char ch, int howMany) {
        StringBuilder ret = new StringBuilder();
        for(int c = 0; c < howMany; c++) {
            ret.append(ch);
        }
        return ret.toString();
    }
    public static String replicateStr(String str, int howMany) {
        StringBuilder ret = new StringBuilder();
        for(int c = 0; c < howMany; c++) {
            ret.append(str);
        }
        return ret.toString();
    }
    public static String spaces(int howMany) {
        return replicateChar(' ', howMany);
    }
    public static String spacesHtml(int howMany) {
        return replicateStr("&nbsp;", howMany);
    }


    //////////////////
    // MISSING TEXT //
    //////////////////

    public static String createMissingText() {
        return createMissingText(null);
    }
    public static String createMissingText(String label) {
        return "(" + (label == null ? "" : label + " ") + "Text Missing)";
    }
    public static boolean isMissingTextFormat(String text) {
        if(text == null) {
            return false;
        }
        return text.matches("^\\(.*Text Missing\\)$");
    }
    public static String markupMissingText(String text) {
        if(isMissingTextFormat(text)) {
            return "<font color='red'><i>" + cleanXmlCdata(text) + "</i></font>";
        }
        return text;
    }


    ////////////////
    // DOWNSIZING //
    ////////////////

    public static String extractPart(String line, String leftBoundary, String endBoundary) {
        int start = line.indexOf(leftBoundary);
        int end = line.indexOf(endBoundary, start + leftBoundary.length());
        return line.substring(start + leftBoundary.length(), end);
    }

    public static String between(String str, char ch1, char ch2) {
        int p1 = str.indexOf(ch1);
        int p2 = str.indexOf(ch2);
        if(p1 == -1 || p2 == -1) {
            throw new RuntimeException("between characters missing");
        }
        return str.substring(p1 + 1, p2);
    }

    public static String cutOut(String str, char ch1, char ch2, String repl) {
        int p1 = str.indexOf(ch1);
        int p2 = str.indexOf(ch2);
        if(p1 == -1 || p2 == -1) {
            throw new RuntimeException("cutOut characters missing");
        }
        String left = str.substring(0, p1);
        String right = str.substring(p2 + 1);
        return left + repl + right;
    }

    public static String removeStart(String str, String start) {
        if(str == null) {
            throw new IllegalArgumentException("str cannot be null.");
        }
        if(start == null) {
            throw new IllegalArgumentException("start cannot be null.");
        }

        int pos = str.indexOf(start);

        if(pos == -1 || pos != 0) {
            return str;
        }

        return str.substring(pos + start.length());
    }

    public static String first(String str, int howMany) {
        return first(str, howMany, null);
    }
    public static String first(String str, int howMany, String replaceWith) {
        if(str == null || str.isEmpty() || howMany >= str.length()) {
            return str;
        }
        return
            StringUtil.cleanNull(replaceWith) +
            str.substring(0, howMany);
    }
    public static String last(String str, int howMany) {
        return last(str, howMany, null);
    }
    public static String last(String str, int howMany, String replaceWith) {
        if(str == null || str.isEmpty() || howMany >= str.length()) {
            return str;
        }
        return
            StringUtil.cleanNull(replaceWith) +
            str.substring(str.length() - howMany);
    }

    public static String removeEnd(String str, String end) {
        if(str == null) {
            throw new IllegalArgumentException("str cannot be null.");
        }
        if(end == null) {
            throw new IllegalArgumentException("end cannot be null.");
        }

        int pos = str.lastIndexOf(end);

        if(pos == -1 || pos + end.length() != str.length()) {
            return str;
        }

        return str.substring(0, pos);
    }

    public static String leftTrim(String str) {
        return leftTrim(str, ' ');
    }

    public static String leftTrim(String str, char ch) {
        if(str == null) {
            throw new IllegalArgumentException("str cannot be null.");
        }
        int start = 0;
        while(start < str.length() && str.charAt(start) == ch) {
            start++;
        }
        return (start > 0) ? str.substring(start) : str;
    }

    public static String rightTrim(String str) {
        return rightTrim(str, ' ');
    }

    public static String rightTrim(String str, char ch) {
        if(str == null) {
            throw new IllegalArgumentException("str cannot be null.");
        }
        int len = str.length();
        while(len > 0 && str.charAt(len - 1) == ch) {
            len--;
        }
        return (len < str.length()) ? str.substring(0, len) : str;
    }

    public static String max(Object obj, int max) {
        return max(obj, max, true);
    }

    public static String max(Object obj, int max, boolean includeDots) {
        if(obj == null) {
            return null;
        }
        String str = obj.toString();
        if(str.length() > max) {
            return str.substring(0, max) + (includeDots ? "..." : "");
        }
        return str;
    }

    public static String maxFromRight(Object obj, int max) {
        return maxFromRight(obj, max, true);
    }

    public static String maxFromRight(Object obj, int max, boolean includeDots) {
        if(obj == null) {
            return null;
        }
        String str = obj.toString();
        if(str.length() > max) {
            return (includeDots ? "..." : "") + str.substring(str.length() - max);
        }
        return str;
    }

    public static String[] maxBoth(String str, int max) {
        if(str == null) {
            return null;
        }
        if(str.length() > max) {
            int left, right;
            if(max % 2 == 0) {
                left = right = max / 2;
            } else {
                left = max / 2 + 1;
                right = max / 2;
            }
            return new String[] {
                str.substring(0, left),
                str.substring(str.length() - right)
            };
        }
        return new String[] {str};
    }

    public static String squeeze(String str, int x) {
        return cut(snip(str, x), x);
    }

    public static String cut(String str, String cutme) {
        if(str == null || str.length() == 0) {
            return str;
        }
        if(!str.endsWith(cutme)) {
            return str;
        }
        return cut(str, cutme.length());
    }
    public static String cut(String str, int x) {
        if(str == null || str.length() == 0) {
            return str;
        }
        if(x > str.length()) {
            return "";
        }
        return str.substring(0, str.length() - x);
    }
    public static String cutAfter(String str, String find) {
        if(str == null || str.length() == 0) {
            return str;
        }
        int where = str.indexOf(find);
        if(where == -1) {
            return str;
        }
        return str.substring(0, where);
    }
    public static String snip(String str, String snipme) {
        if(str == null || str.length() == 0) {
            return str;
        }
        if(!str.startsWith(snipme)) {
            return str;
        }
        return snip(str, snipme.length());
    }
    public static String snip(String str, int x) {
        if(str == null || str.length() == 0) {
            return str;
        }
        if(x > str.length()) {
            return "";
        }
        return str.substring(x);
    }

    public static String firstSegment(String str, String delim) {
        int i = str.indexOf(delim);
        if(i == -1) {
            return str;
        }
        return str.substring(0, i);
    }

    public static String lastSegment(String str, String delim) {
        int i = str.lastIndexOf(delim);
        if(i == -1) {
            return str;
        }
        return str.substring(i + delim.length());
    }

    public static String removeMultipleBlankLines(String text) {
        text = text.replaceAll("\r\n(\\s|(\r\n))*\r\n", "\r\n\r\n");
        text = text.replaceAll("\n\\s*\n", "\n\n");
        text = text.replaceAll("\r\\s*\r", "\r\r");
        return text;
    }


    ///////////////
    // REPLACING //
    ///////////////

    // If str starts with start, remove end and prepend replaceWith.
    public static String replaceStart(String str, String start, String replaceWith) {
        if(str == null) {
            throw new IllegalArgumentException("str cannot be null.");
        }
        if(start == null) {
            throw new IllegalArgumentException("start cannot be null.");
        }

        int pos = str.indexOf(start);

        if(pos == -1) {
            return str;
        } else if(pos != 0) {
            return str;
        }

        return replaceWith + str.substring(pos + start.length());
    }

    // If str ends with end, remove end and append replaceWith.
    public static String replaceEnd(String str, String end, String replaceWith) {
        if(str == null) {
            throw new IllegalArgumentException("str cannot be null.");
        }
        if(end == null) {
            throw new IllegalArgumentException("end cannot be null.");
        }

        int pos = str.lastIndexOf(end);

        if(pos == -1) {
            return str;
        } else if(pos + end.length() != str.length()) {
            return str;
        }

        return str.substring(0, pos) + replaceWith;
    }


    /////////////
    // CONTROL //
    /////////////

    public static String toReadableChars(String str) {
        int len = str.length();
        StringBuilder buffer = new StringBuilder(len);
        for(int c = 0; c < len; c++) {
            char ch = str.charAt(c);
            if(ch >= 32) {
                buffer.append(ch);
            } else {
                if(ch == '\n') {
                    buffer.append("[NL]");
                } else if(ch == '\r') {
                    buffer.append("[CR]");
                } else if(ch == '\t') {
                    buffer.append("[TAB]");
                } else if(ch == '\b') {
                    buffer.append("[BS]");
                } else if(ch == 11) {
                    buffer.append("[VT]");
                } else {
                    buffer.append("[#" + (int) ch + "]");
                }
            }
        }
        return buffer.toString();
    }

    public static String cleanUnicodePunct(String str) {
        str = str.replaceAll("[\u2010\u2011\u2012\u2013\u2014\u2015]", "-");
        str = str.replaceAll("\u2016", "|");
        str = str.replaceAll("\u2017", "_");
        str = str.replaceAll("\u2018", "'");
        str = str.replaceAll("\u2019", "'");
        str = str.replaceAll("\u201A", ",");
        str = str.replaceAll("\u201B", "'");
        str = str.replaceAll("\u201C", "\"");
        str = str.replaceAll("\u201D", "\"");
        str = str.replaceAll("\u201F", "\"");
        str = str.replaceAll("\u8482", "(TM)");
        return str;
    }

    public static String cleanXmlCdata(String str) {
        return str.replaceAll("&", "&amp;").replaceAll("<", "&lt;");
    }

    public static String convertHtmlEntitiesToReadable(String str) {
        if(str == null) {
            return null;
        }
        return str
            .replaceAll("&lt;", "<")
            .replaceAll("&gt;", ">")
            .replaceAll("&amp;", "&")
            .replaceAll("&quot;", "\"")
            .replaceAll("&apos;", "'")
            .replaceAll("&#39;", "'")
        ;
    }

    public static String cleanXmlAttr(String str) {
        return str.replaceAll("&", "&amp;").replaceAll("\"", "&quot;");
    }

    public static String cleanUnusualChars(String str) {
        return str.replaceAll("[^\\u0020-\\u007e]", "");
    }

    public static String cleanUnprintableControlChars(String str) {
        return str.replaceAll("[\\u0000-\\u0008|\\u000b-\\u000c|\\u000e-\\u001f]", "");
    }

    public static String cleanAllControlChars(String str) {
        return str.replaceAll("[\\u0000-\\u001f]", "");
    }

    public static String cleanInternationalChars(String str) {
        return str.replaceAll("[\\u007e-\\uffff]", "");
    }

    public static String cleanControl(String str) {
        if(str == null) {
            return null;
        }
        return
            str.replaceAll("\r\n",   "[CRLF]")
               .replaceAll("\r",     "[CR]")
               .replaceAll("\n",     "[LF]")
               .replaceAll("\t",     "[TAB]")
               .replaceAll("\u000B", "[VT]");
    }


    /////////////
    // AUGMENT //
    /////////////

    public static String ensureStartsWith(String str, String prefix) {
        if(str != null && !str.startsWith(prefix)) {
            return prefix + str;
        }
        return str;
    }
    public static String ensureEndsWith(String str, String suffix) {
        if(str != null && !str.endsWith(suffix)) {
            return str + suffix;
        }
        return str;
    }
    public static String padLeft(String str, char ch, int fieldWidth) {
        if(str.length() >= fieldWidth) {
            return str;
        }

        return replicateChar(ch, fieldWidth - str.length()) + str;
    }

    public static String padRight(String str, char ch, int fieldWidth) {
        if(str.length() >= fieldWidth) {
            return str;
        }

        return str + replicateChar(ch, fieldWidth - str.length());
    }


    ////////////////
    // MAX LENGTH //
    ////////////////

    public static int maxLength(Collection<?> objs) {
        int maxLen = -1;
        for(Object obj : objs) {
            if(obj == null) {
                continue;
            }
            int curLen = obj.toString().length();
            if(curLen > maxLen) {
                maxLen = curLen;
            }
        }
        return maxLen;
    }
    public static int[] maxLengths(String[][] data) {
        int[] cols = new int[data[0].length];
        for(int c = 0; c < data[0].length; c++) {
            cols[c] = maxLength(data, c);
        }
        return cols;
    }
    public static int maxLength(String[][] data, int col) {
        int maxLen = -1;
        for(String[] row : data) {
            int curLen = row[col].length();
            if(curLen > maxLen) {
                maxLen = curLen;
            }
        }
        return maxLen;
    }
    public static <T> int maxLength(Collection<T> objs, StringUtilObjectTransformer<T> xform, int minimumLength) {
        return Math.max(maxLength(objs, xform), minimumLength);
    }
    public static <T> int maxLength(Collection<T> objs, StringUtilObjectTransformer<T> xform) {
        int maxLen = -1;
        for(Object obj : objs) {
            obj = xform.transform((T) obj);
            if(obj == null) {
                continue;
            }
            int curLen = obj.toString().length();
            if(curLen > maxLen) {
                maxLen = curLen;
            }
        }
        return maxLen;
    }
    public static int maxLength(Collection<?> objs, String fieldOrMethodName) {
        int maxLen = -1;
        for(Object obj : objs) {
            if(ReflectionUtil.hasField(obj, fieldOrMethodName)) {
                obj = ReflectionUtil.get(obj, fieldOrMethodName);
            } else if(ReflectionUtil.hasMethod(obj, fieldOrMethodName)) {
                obj = ReflectionUtil.invoke(obj, fieldOrMethodName);
            } else {
                throw new RuntimeException("field or method '" + fieldOrMethodName + "' not found on object");
            }
            if(obj == null) {
                continue;
            }
            int curLen = obj.toString().length();
            if(curLen > maxLen) {
                maxLen = curLen;
            }
        }
        return maxLen;
    }

    public static int maxLength(Collection<?> objs, String fieldOrMethodName, int minimumLength) {
        return Math.max(maxLength(objs, fieldOrMethodName), minimumLength);
    }


    //////////////
    // COUNTING //
    //////////////

    public static int countRightControlCharsAndSpaces(String str) {
        int n = 0;
        for(int i = str.length() - 1; i >= 0; i--) {
            char ch = str.charAt(i);
            if(ch <= 32) {
                n++;
            } else {
                break;
            }
        }
        return n;
    }
    public static int count(String line, char ch) {
        int count = 0;
        for(int c = 0; c < line.length(); c++) {
            if(line.charAt(c) == ch) {
                count++;
            }
        }
        return count;
    }


    /////////////////
    // PLURALIZING //
    /////////////////

    public static String n(String word) {
        if(word == null || word.isEmpty()) {
            return null;
        }
        String lower = word.toLowerCase();
        switch(lower.charAt(0)) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':   // Handles 90% of cases correctly maybe...
                return "n";
        }
        return "";
    }

    public static String s(long runs) {           // Pluralization Ending: "dog" => "dogS"
        return runs == 1 ? "" : "s";              // String result = "dog" + StringUtil.s(num);
    }
    public static String es(long runs) {          // Pluralization Ending: "match" => "matchES"
        return runs == 1 ? "" : "es";             // String result = "match" + StringUtil.es(num);
    }
    public static String ies(long runs) {         // Pluralization Ending: "citY" => "citIES"
        return runs == 1 ? "y" : "ies";           // String result = "cit" + StringUtil.ies(num);
    }

    public static String plu(long count, String singular, String plural) {
        return count == 1 ? singular : plural;    // Pluralization Alternates: "goose" => "geese"
    }                                             // String result = StringUtil.plu(num, "goose", "geese");

    public static String plu(long count, String stem, String singularSuffix, String pluralSuffix) {
        return stem + (count == 1 ? singularSuffix : pluralSuffix);
    }                                             // Pluralization Alternate Endings: "hippopotamUS" => "hippopotamI"
                                                  // String result = StringUtil.plu(num, "hippopotam", "us", "i");

    ////////////////////
    // CAPITALIZATION //
    ////////////////////

    // One word only.  Preserves case of all other characters in string.
    public static String capitalize(String str) {
        if(str == null || str.length() == 0) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    // One word only.
    public static String toTitleCase(String str) {
        if(str == null || str.length() == 0) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    public static String lcSafe(String str) {
        return str == null ? null : str.toLowerCase();
    }


    //////////
    // JOIN //
    //////////

    public static String join(String mid, Object... items) {
        return join(items, mid);
    }
    public static String join(String mid, String prefix, String suffix, Object... items) {
        return join(items, mid, prefix, suffix);
    }
    public static String join(Object[] items, String mid) {
        return join(items, mid, null, null);
    }
    public static String join(Object[] items, String mid, String prefix, String suffix) {
        return join(Arrays.asList(items), mid, prefix, suffix);  // possibly more efficient way to do this
    }
    public static String join(Iterable<?> items, String mid) {
        return join(items, mid, null, null);
    }
    public static String join(Iterable<?> items, String mid, String prefix, String suffix) {
        StringBuilder b = new StringBuilder();
        boolean first = true;
        for(Object o : items) {
            if(!first && mid != null) {
                b.append(mid);
            }
            if(prefix != null) {
                b.append(prefix);
            }
            if(o != null) {
                b.append(o.toString());
            }
            if(suffix != null) {
                b.append(suffix);
            }
            first = false;
        }
        return b.toString();
    }
    public static String join(Map<?, ?> map) {
        return join(map, "=", "\n", null, null);   // Specific use case
    }
    public static String join(Map<?, ?> map, String kvSep, String kvPairSep) {
        return join(map, kvSep, kvPairSep, null, null);
    }
    public static String join(Map<?, ?> map, String kvSep, String kvPairSep, String pre, String post) {
        StringBuilder b = new StringBuilder();
        if(pre != null) {
            b.append(pre);
        }
        for(Object k : map.keySet()) {
            Object v = map.get(k);
            b.append("" + k);
            if(kvSep != null) {
                b.append(kvSep);
            }
            b.append("" + v);
            if(kvPairSep != null) {
                b.append(kvPairSep);
            }
        }
        if(kvPairSep != null) {
            b.delete(b.length() - kvPairSep.length(), b.length());
        }
        if(post != null) {
            b.append(post);
        }
        return b.toString();
    }


    //////////////
    // NEWLINES //
    //////////////

    public static String convertNewlines(String str, NewlineType targetType) {

        if(str == null) {      // Not sure if this is good from an API sense, needs
            return null;       // some thought that could provide some standard
        }                      // guidance for other util methods as well.

        String result;

        if(targetType == NewlineType.LF) {
            result = str.replaceAll("\r\n", "\n");
            result = result.replaceAll("\r", "\n");

        } else if(targetType == NewlineType.CR) {
            result = str.replaceAll("\r\n", "\r");
            result = result.replaceAll("\n", "\r");

        } else if(targetType == NewlineType.CRLF) {
            String repl = "\\[~#@StringUtil convertNewlines@#~\\]";
            result = str.replaceAll("\r\n", repl);
            result = result.replaceAll("\n", "\r\n");
            result = result.replaceAll("\r\n", repl);
            result = result.replaceAll("\r", "\r\n");
            result = result.replaceAll(repl, "\r\n");

        // Replace all newlines with spaces.
        } else if(targetType == NewlineType.NONE) {
            result = str.replaceAll("\r\n", " ");
            result = result.replaceAll("\r", " ");
            result = result.replace("\n", " ");

        } else {
            result = str;       // AUTO, MIXED case (do nothing).
        }

        return result;
    }

    public static NewlineType getFirstNewline(String str) {
        int lf = str.indexOf("\n");
        int cr = str.indexOf("\r");

        // If there are no newlines in the string...
        if(lf == -1 && cr == -1) {
            return NewlineType.NONE;
        }

        // If there are no line feeds but there are
        // carriage returns...
        if(lf == -1 && cr != -1) {
            return NewlineType.CR;
        }

        // If there are no carriage returns but there are
        // line feeds...
        if(lf != -1 && cr == -1) {
            return NewlineType.LF;
        }

        // Both line feed and carriage return characters
        // exist...

        // If the first line feed is before the first
        // carriage return, then the string has mixed
        // newlines.  However, the first newline is
        // still the line feed.
        if(lf < cr) {
            return NewlineType.LF;
        }

        // The first carriage return comes before the
        // first line feed...
        if(str.charAt(cr + 1) == '\n') {
            return NewlineType.CRLF;
        }

        // Mixed, but first newline is the carriage return.
        return NewlineType.CR;
    }

    public static NewlineType getNewlineType(String str) {
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

        // There are no line feeds or carriage returns.
        if(lf == 0 && cr == 0) {
            return NewlineType.NONE;
        }

        // There are line feeds but no carriage returns.
        if(lf != 0 && cr == 0) {
            return NewlineType.LF;
        }

        // There are carriage returns but no line feeds.
        if(lf == 0 && cr != 0) {
            return NewlineType.CR;
        }

        // If there are an uneven number of line feeds
        // and carriage returns or we've previously
        // determined the string is mixed.
        if(lf != cr || mixed) {
            return NewlineType.MIXED;
        }

        // Else it must have consistent carriage return-
        // line feeds.
        return NewlineType.CRLF;
    }


    //////////
    // DIFF //
    //////////

    public static String diff(String expected, String actual) {
        return diff(expected, actual, 10);
    }
    public static String diff(String expected, String actual, int width) {
        int len = Math.min(expected.length(), actual.length());
        for(int x = 0; x < len; x++) {
            if(expected.charAt(x) != actual.charAt(x)) {
                return "Different at position " + x + "\n" +
                       "(expected = " + diffHelp(expected, x, width) + ")\n" +
                       "(actual   = " + diffHelp(actual, x, width) + ")\n";
            }
        }
        if(expected.length() > actual.length()) {
            int del = expected.length() - actual.length();
            return "Expected Has " + del + " Additional Characters: " +
                toReadableChars(expected.substring(actual.length()));
        } else if(actual.length() > expected.length()) {
            int del = actual.length() - expected.length();
            return "Actual Has " + del + " Additional Characters: " +
                toReadableChars(actual.substring(expected.length()));
        }
        return null;
    }

    private static String diffHelp(String a, int x, int width) {
        int maxDots = 3;
        int start = Math.max(0, x - width);
        int end = Math.min(a.length(), x + width + 1);
        String s = replicateChar('.', Math.min(start, maxDots)) + a.substring(start, end) + replicateChar('.', Math.min(a.length() - end, maxDots));
        return toReadableChars(s);
    }


    /////////////////////
    // NUMBERED STRING //
    /////////////////////

    // Interesting AMap concept: what if the actual numbered strings
    // are fields or method calls on the objects in the collection?
    public static String getNextNumberedString(Collection<?> items) {
        return getNextNumberedString(items, null, false);
    }
    public static String getNextNumberedString(Collection<?> items, boolean findMax) {
        return getNextNumberedString(items, null, findMax);
    }
    public static String getNextNumberedString(Collection<?> items, String prefix) {
        return getNextNumberedString(items, prefix, false);
    }
    public static String getNextNumberedString(Collection<?> items, String prefix, boolean findMax) {
        TreeSet<Integer> indices = new TreeSet<>();
        for(Object key : items) {
            if(key instanceof String && ((String) key).startsWith(prefix)) {
                String right = ((String) key).substring(prefix.length());
                if(NumUtil.isInt(right)) {
                    indices.add(Integer.parseInt(right));
                }
            }
        }
        int chosenIndex;
        if(findMax) {
            chosenIndex = indices.last() + 1;
        } else {
            chosenIndex = 0;
            for(Integer index : indices) {
                if(index == chosenIndex) {
                    chosenIndex++;
                }
            }
        }
        return prefix + chosenIndex;
    }


    ////////////////////
    // PADDING/INDENT //
    ////////////////////

    public static String indent(String str, int levels, int spacesPerLevel) {
        return indent(str, levels * spacesPerLevel);
    }
    public static String indent(String str, int spaceCount) {
        return indent(str, spaces(spaceCount));
    }

    public static String indent(String str, String prefix) {
        String qp = Matcher.quoteReplacement(prefix);
        str = convertNewlines(str, NewlineType.LF);  // Changes string so don't have to deal with mixed strings.
        NewlineType type = getNewlineType(str);
        switch(type) {
            case CRLF: str = str.replaceAll("\r\n", "\r\n" + qp); break;
            case CR: str = str.replaceAll("\r", "\r" + qp); break;
            case LF: str = str.replaceAll("\n", "\n" + qp); break;
            case MIXED:
            case AUTO:
                throw new RuntimeException("Cannot indent mixed-newline strings.");
        }
        return prefix + str;
    }

    public static String padding(Object[] items, Object cur) {
        return padding(Arrays.asList(items), cur);
    }
    public static String padding(Iterable<?> items, Object cur) {
        int max = -1;
        for(Object o : items) {
            if(o != null) {
                String s = o.toString();
                if(s.length() > max) {
                    max = s.length();
                }
            }
        }
        return spaces(max - cur.toString().length());
    }

    public static String padNewLines(String string, int howMany) {
        return prefixNewLines(string, spaces(howMany));
    }
    public static String padNewLines(String string, int howMany, boolean prefixFirst) {
        return prefixNewLines(string, spaces(howMany), prefixFirst);
    }

    public static String prefixNewLines(String string, String prefix) {
        return prefixNewLines(string, prefix, false);
    }
    public static String prefixNewLines(String string, String prefix, boolean prefixFirst) {
        StringBuilder buffer = new StringBuilder();
        if(prefixFirst) {
            buffer.append(prefix);
        }
        for(int i = 0; i < string.length(); i++) {
            char cur = string.charAt(i);
            if(cur == '\n') {                             // LF / Unix
                buffer.append('\n');
                buffer.append(prefix);

            } else if(cur == '\r') {                      // Mac / Win
                buffer.append('\r');
                if(i != string.length() - 1) {
                    if(string.charAt(i + 1) == '\n') {    // Win
                        buffer.append('\n');
                        i++;
                    }
                }
                buffer.append(prefix);
            } else {
                buffer.append(cur);
            }
        }
        return buffer.toString();
    }


    //////////////
    // TOSTRING //
    //////////////

    public static String toStringObject(Object o) {
        return toStringObject(o, false);
    }
    public static String toStringObject(Object o, boolean includeIdent) {
        if(o == null) {
            return "null";
        }
        StringBuilder b = new StringBuilder();
        b.append(o.getClass().getName());
        b.append('@');
        b.append(Integer.toHexString(o.hashCode()));
        if(includeIdent) {
            b.append(':');
            b.append(Integer.toHexString(System.identityHashCode(o)));
        }
        return b.toString();
    }
    public static String toString(Enumeration e) {
        if(e == null) {
            return "null";
        }
        StringBuilder buffer = new StringBuilder();
        buffer.append("[");
        boolean first = true;
        while(e.hasMoreElements()) {
            if(!first) {
                buffer.append(", ");
            }
            buffer.append(e.nextElement());
            first = false;
        }
        buffer.append("]");
        return buffer.toString();
    }


    //////////////
    // MATCHING //
    //////////////

    public static List<Match> find(String target, String patternWithTag, PatternInterpretation interp) {
        Pair<String, PatternInterpretation> result =
            PatternUtil.parsePatternInterpretationTag(patternWithTag, interp);
        String patternRegex =
            PatternUtil.convertToRegex(result.getValue1(), result.getValue2(), true);
        Pattern pattern = PatternUtil.createPattern(patternRegex, result.getValue2(), true);
        Matcher matcher = pattern.matcher(target);
        List<Match> matches = new ArrayList<>();
        while(matcher.find()) {
            int phraseStart = matcher.start(1);
            int phraseEndNonIncl = matcher.end(1);
            String text = target.substring(phraseStart, phraseEndNonIncl);
            Match match = new Match(phraseStart, phraseEndNonIncl, text);
            matches.add(match);
        }
        return matches;
    }

    public static boolean matchesAny(String[] targets, String patternRegex, PatternInterpretation interp) {
        return matchesAny(targets, patternRegex, interp.isCaseSensitive());
    }
    public static boolean matchesAny(String[] targets, String patternRegex, boolean caseSensitive) {
        for(String target : targets) {
            if(target != null && matches(target, patternRegex, caseSensitive)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matches(String target, String patternRegex, PatternInterpretation interp) {
        return matches(target, patternRegex, interp.isCaseSensitive());
    }
    public static boolean matches(String target, String patternRegex, boolean caseSensitive) {
        if(caseSensitive) {
            return matchesJavaLangString(target, patternRegex);
        }
        return matchesIgnoreCase(target, patternRegex);
    }

    private static boolean matchesJavaLangString(String str, String regex) {
        return matchesJavaUtilRegexPattern(regex, str);
    }
    private static boolean matchesJavaUtilRegexPattern(String regex, CharSequence input) {
        Pattern p = patternPool.getPattern(regex);
        Matcher m = p.matcher(input);
        return m.matches();
    }

    // Extends String.matches slightly by allowing case-insensitive string
    // comparisons a la String.equalsIgnoreCase and String.compareToIgnoreCase.
    public static boolean matchesIgnoreCase(String input, String patternRegex) {
        Pattern p = patternPool.getPattern(patternRegex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(input);
        return m.matches();
    }

    public static String replaceAll(String str, String pattern, String replacement, int flags) {
        return patternPool.getPattern(pattern, flags).matcher(str).replaceAll(replacement);
    }


    //////////
    // MISC //
    //////////

    public static String[] extractCaptures(String target, String pattern) {
        return extractCaptures(target, pattern, true);
    }
    public static String[] extractCaptures(String target, String pattern, boolean caseSensitive) {
        Pattern p = patternPool.getPattern(pattern, caseSensitive ? 0 : Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(target);
        String[] matches = null;
        while(m.find()) {
            matches = new String[m.groupCount()];
            for(int i = 0; i < m.groupCount(); i++) {
                matches[i] = m.group(i + 1);
            }
            break;
        }
        return matches;
    }
    public static String[] extractCaptures2(String target, String pattern) {
        return extractCaptures2(target, pattern, true);
    }
    public static String[] extractCaptures2(String target, String pattern, boolean caseSensitive) {
        Pattern p = patternPool.getPattern(pattern, caseSensitive ? 0 : Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(target);
        List<String> matches = new ArrayList<>();
        while(m.find()) {
            matches.add(m.group());
        }
        return matches.toArray(new String[0]);
    }


    public static String removeRightControlCharsAndSpaces(String str) {
        if(str == null) {
            return null;
        }
        int n = countRightControlCharsAndSpaces(str);
        return str.substring(0, str.length() - n);
    }

    public static String[] split(String str, int index) {
        return split(str, new int[] {index});
    }
    public static String[] split(String str, int[] indices) {
        if(indices == null || indices.length == 0) {
            return new String[] {str};
        }
        int sLen = str.length();
        int prev = 0;
        String[] parts = new String[indices.length + 1];
        for(int p = 0; p < indices.length + 1; p++) {
            int ind;
            if(p == indices.length) {
                ind = str.length();
            } else {
                int index = indices[p];
                if(index < 0) {
                    ind = sLen + index;
                } else {
                    ind = index;
                }
            }
            if(ind > sLen) {
                ind = sLen;
            }
            parts[p] = str.substring(prev, ind);
            prev = ind;
        }
        return parts;
    }
    public static String[] slice(String[] arr, int start) {
        return slice(arr, start, arr.length);
    }
    public static String[] slice(String[] arr, int start, int endNonIncl) {
        List<String> p = new ArrayList<>();
        for(int i = start; i < endNonIncl; i++) {
            p.add(arr[i]);
        }
        return p.toArray(new String[0]);
    }
    public static String highlightEnd(String idStr) {  // Could be parameterized further
        String[] parts = split(idStr, -3);
        return parts[0] + "<font color='#FF0C8D'><u><b>" + parts[1] + "</b></u></font>";
    }

    // Length/character checks just avoid having to use regular expressions
    public static boolean startsWithHier(String str, String prefix, char sep) {
        if(str.startsWith(prefix) &&
                (str.length() == prefix.length() || str.charAt(prefix.length()) == sep)) {
            return true;
        }
        return false;
    }

    public static String compressWhitespace(String str) {
        return str.replaceAll("\\s+", " ");
    }
    public static void printStringIdxs(String str) {
        System.out.println(str.replaceAll("[\\s&&[^ ]]", "|"));
        if(str.length() > 100) {
            for(int i = 0; i < str.length(); i++) {
                int h = i / 100;
                System.out.print(h);
            }
            System.out.println();
        }
        if(str.length() > 10) {
            for(int i = 0; i < str.length(); i++) {
                int h = i % 100;
                h = h / 10;
                System.out.print(h);
            }
            System.out.println();
        }
        for(int i = 0; i < str.length(); i++) {
            int h = i % 10;
            System.out.print(h);
        }
        System.out.println();
        System.out.println("String Length: " + str.length());
    }
    public static String prefixIf(String prefix) {
        return prefixIf(prefix, " ");
    }
    public static String prefixIf(String prefix, String delim) {
        if(isBlank(prefix)) {
            return "";
        }
        return prefix + delim;
    }
    public static String suffixIf(String suffix) {
        return suffixIf(suffix, " ");
    }
    public static String suffixIf(String suffix, String delim) {
        if(isBlank(suffix)) {
            return "";
        }
        return delim + suffix;
    }
    public static String joinIf(String prefix, String delim, String suffix) {
        if(!isBlank(prefix) && !isBlank(suffix)) {
            return prefix + delim + suffix;
        }
        if(!isBlank(prefix)) {
            return prefix;
        }
        if(!isBlank(suffix)) {
            return suffix;
        }
        return "";
    }
    public static String read(InputStream is) {
        try {
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append('\n');
            }
            return buffer.toString();
        } catch(Exception e) {
            throw new RuntimeException();
        }
    }

    public static String plus(int i) {
        return (i > 0 ? "+" : "") + i;
    }
    public static String plus(long l) {
        return (l > 0 ? "+" : "") + l;
    }

    public static String[] lines(String str) {
        return str.split("\\r?\\n|\\r");      // Some day replace with Java 11 String.lines()
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        testRegex1();
        testRegex2();
        if(true) {
            return;
        }

        System.out.println(compressWhitespace("  b   c   \t\t \nd \r  xxx  \n"));

//        System.out.println(startsWithHier("a.bc", "a.b", '.'));

//        System.out.println(replaceEnd("derek", "k", "Z"));
//        System.out.println("Derek".matches("e"));
//        System.out.println(prefixNewLines("\r\naa\r\nbb\r\ncc\rdd\ny\rz", "XX", true));
//        System.out.println("Derek".matches("^.*ERE.*$"));
//        System.out.println(matchesIgnoreCase("Derek", "^.*ERE.*$"));

        if(true) {
            return;
        }
//        String[][] strs = {
//            {"gov.sandia.avondale.cluster.plugin.PlatformPlugin",            "v2.3.0 (2017-01-19 21:44) [test]"},
//            {"gov.sandia.webcomms.plugin.WebCommsPlugin",                    "v1.0.0 (2017-01-19 21:44)"},
//            {"gov.sandia.avondale.webcrawler.plugin.WebCrawlerPlugin",       "v2.3.0 (2017-01-19 21:44)"},
//            {"gov.sandia.avondale.webcrawler.core.plugin.WebCrawler3Plugin", "v2.3.0 (2017-01-19 21:44)"},
//            {"finio.plugins.platform.PlatformPlugin",                        "v0.0.1"},
//            {"replete.scrutinize.PlatformPlugin",                            "v1.0.0 (2017-01-19 21:43)"},
//        };

//        out("   Installed Plugins:");
//        Map<String, Plugin> plugins = PluginManager.getAllPlugins();
//        if(plugins.size() == 0) {
//            out("      <none>");
//        } else {
//            for(String pluginName : plugins.keySet()) {
//                Plugin plugin = plugins.get(pluginName);
//                out("      " + pluginName + " v" + plugin.getVersion());


        Object[][] test = new Object[][] {
            {"DerekFlame", null},
            {"DerekFlame", new int[0]},
            {"DerekFlame", new int[] {0}},
            {"DerekFlame", new int[] {1}},
            {"DerekFlame", new int[] {5}},
            {"DerekFlame", new int[] {10}},
            {"DerekFlame", new int[] {1}},
            {"DerekFlame", new int[] {2, 8, 11}},
            {"DerekFlame", new int[] {1, -3, -2, 8}},
        };
        for(Object[] t : test) {
            String str = (String) t[0];
            int[] indices = (int[]) t[1];
            String[] arr = split(str, indices);
            System.out.println(str + "|" + Arrays.toString(indices) + " => " +
                Arrays.toString(arr));
        }
//        System.out.println(commas(123124));
//        System.out.println(commas(123124.665));
//        System.out.println(commas("66544245"));
//        System.out.println(commas("66544245.77881"));
//        System.out.println(new Long(0));
//        System.out.println(new Long(0).hashCode());
//        System.out.println(toStringObject(new Long(0)));
//        System.out.println(System.identityHashCode(new Long(0)));
//        System.out.println(toStringObject(new Long(0), true));
//        System.out.println(Arrays.toString(match("mb=[1be,derek]", "mb=\\[([^,\\]]+)(?:,([^\\]]+))?\\]")));
//        String input = "abcdefgh";
//        System.out.println(Arrays.toString(maxBoth(input, 7)));
//        System.out.println(squeeze("Derek", 1));
//        Map<String, String> map = new HashMap<String, String>();
//        map.put(null, "Green");
//        map.put("Mars", null);
//        map.put("Pluto", "Sad");
//        System.out.println(StringUtil.join(map));
//        System.out.println(StringUtil.join(map, null, null));
//        System.out.println(StringUtil.join(map, "=", ", ", "{", "}"));
//        System.out.println(StringUtil.join(map, ">>", " | ", "^", "$"));
//        System.out.println(map);
//        List<String> planets = new ArrayList<String>();
//        planets.add("earth");
//        planets.add("mars");
//        planets.add("saturn");
//        System.out.println("[" + join(planets.toArray(), null, "  @", null) + "]");
//        System.out.println(join(" ;;; ", new JButton("ad"), new Object(), System.class));
//        System.out.println(join(";", "@", "#", 4, 5, 6));
//        String str = "derek\nmorgan\ntrumbo";
//        System.out.println(indent(str, " t \\"));
//        System.out.println(commas(9994_122.3443424));
    }

    // These show limitations of Java's Regex API
    private static void testRegex1() {
        String target = "___a1ca2c.&.xyz---a3ca4c,&,xyz";
        String pattern = "(a.?c)+.*?(xyz)";
        Pattern p = patternPool.getPattern(pattern);
        Matcher m = p.matcher(target);
        int s = 0;
        System.out.println(target + " (" + target.length() + ")");
        for(int i = 0; i < target.length(); i++) {
            System.out.print(i % 10);
        }
        System.out.println();
        while(m.find()) {
            System.out.println("Found Sequence: " + s);
            System.out.println("    MR:     " + m.toMatchResult());
            //System.out.println("    RStart: " + m.regionStart());
            //System.out.println("    REnd:   " + m.regionEnd());
            System.out.println("    Group:  " + m.group());
            System.out.println("    GroupC: " + m.groupCount());
            System.out.println("    Start:  " + m.start());
            System.out.println("    End:    " + m.end());
            for(int i = 0; i <= m.groupCount(); i++) {
                System.out.println(
                    "    " + i + ": [" + m.start(i) + "-" + m.end(i) + ") \"" + m.group(i) + "\""
                );
            }
            s++;
        }
    }
    private static void testRegex2() {
        String target = "___a1ca2c.&.xyz---a3ca4c,&,xyz";
        String pattern = "a[13]c(a[24]c)+.*?(xyz)";
        Pattern p = patternPool.getPattern(pattern);
        Matcher m = p.matcher(target);
        int s = 0;
        System.out.println(target + " (" + target.length() + ")");
        for(int i = 0; i < target.length(); i++) {
            System.out.print(i % 10);
        }
        System.out.println();
        while(m.find()) {
            System.out.println("Found Sequence: " + s);
            System.out.println("    MR:     " + m.toMatchResult());
            //System.out.println("    RStart: " + m.regionStart());
            //System.out.println("    REnd:   " + m.regionEnd());
            System.out.println("    Group:  " + m.group());
            System.out.println("    GroupC: " + m.groupCount());
            System.out.println("    Start:  " + m.start());
            System.out.println("    End:    " + m.end());
            for(int i = 0; i <= m.groupCount(); i++) {
                System.out.println(
                    "    " + i + ": [" + m.start(i) + "-" + m.end(i) + ") \"" + m.group(i) + "\""
                );
            }
            s++;
        }
    }
}
