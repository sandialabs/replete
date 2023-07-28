package replete.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * A class that can parse a string that is a list of
 * fields into those individual fields.  The required
 * field separator can be any character and quote
 * characters can be supplied that denote the beginning
 * and end of a field.  The quote character can also
 * be any character.  There can be a mix of fields
 * surrounded by quotes and fields not surrounded
 * by quotes.  The quotes that surround a field serve
 * two purposes: to preserve whitespace on either
 * side of a field's content, and to keep a separator
 * character from being interpreted as an actual
 * separator character.
 *
 *   "one, two, three"                  (one)(two)(three) [fieldsep=,]
 *   " one  ,   two   , three "         (one)(two)(three) [fieldsep=,]
 *   "'one ',  two  ,  'three four'"    (one )(two)(three four) [fieldsep=, quote=']
 *   "one two : three : 'four : five'"  (one two)(three)(four : five) [fieldsep=: quote=']
 *   "{a b } ?c d  ? {e ? f}"          (a b )(c d)(e ? f) [fieldsep=? quote_leading={ quote_trailing=}]
 *
 * It's possible this class could be replicated with
 * an involved regular expression using Java's built-
 * in regex API.
 *
 * Returns null to indicate parse errors.
 *
 * Future: Be able to escape a quote character so it
 * remains part of the field.
 *
 * @author Derek Trumbo
 */

public class FieldListParser {

    //////////////////////
    // PUBLIC INTERFACE //
    //////////////////////

    public static List<String> parseLine(String line, char fieldSeparator) {
        return parseLine(line, fieldSeparator, (char) 0);
    }
    public static List<String> parseLine(String line, char fieldSeparator, char quoteDelim) {
        return parseLine(line, fieldSeparator, quoteDelim, quoteDelim);
    }
    public static List<String> parseLine(String line, char fieldSeparator, char quoteDelimLeading, char quoteDelimTrailing) {
        return new FieldListParser(line, fieldSeparator, quoteDelimLeading, quoteDelimTrailing).parse();
    }

    // Not completely done.
    public static List<List<String>> parseLines(Reader reader, char fieldSeparator) {
        List<List<String>> allLines = new ArrayList<List<String>>();
        BufferedReader bufReader = new BufferedReader(reader);
        String inLine;
        String outLine = "";
        try {
            while((inLine = bufReader.readLine()) != null) {
                if(inLine.startsWith("\"" + fieldSeparator)) {
                    outLine += inLine;
                } else if(!inLine.startsWith("\"")) {
                    outLine += inLine;
                } else {
                    if(!outLine.equals("")) {
                        allLines.add(parseLine(outLine, fieldSeparator, '"'));
                    }
                    outLine = inLine;
                }
            }
            if(!outLine.equals("")) {
                allLines.add(parseLine(outLine, fieldSeparator, '"'));
            }
        } catch (IOException e) {
            return null;
        }
        return allLines;
    }

    ////////////
    // PARSER //
    ////////////

    // Identifies the current state of the parser,
    // or more specifically, the type of the previous
    // character.
    private enum State {
        INIT,
        QUOTE_LEADING,   // Quote on opening side of field
        QUOTE_TRAILING,  // Quote on closing side of field
        FIELD_SEP,       // Commas or tabs between fields
        INTERNAL_CHAR    // All other characters (those making up a field's content)
    }

    // INPUT PARAMS //

    private String input;
    private char fieldSeparator;
    private char quoteDelimLeading;
    private char quoteDelimTrailing;

    // PARSING STATE //

    private State state = State.INIT;
    private String curField = "";
    private List<String> allFields = new ArrayList<String>();
    boolean curFieldHasQuotes = false;

    public FieldListParser(String i, char f, char q1, char q2) {
        input = i;             // This parameter should never be trimmed in case field sep or quote characters are whitespace characters.
        fieldSeparator = f;
        quoteDelimLeading = q1;
        quoteDelimTrailing = q2;
    }

    private List<String> parse() {

        if(input == null) {
            return null;
        }

        // All the characters in the string are turned into a queue that can
        // be more easily manipulatable by called methods.
        Queue<Character> charQueue = stringToQueue(input);

        // Look at each character in the string.
        while(charQueue.peek() != null) {
            char curChar = charQueue.poll();

            // The first character (will not be whitespace)...
            if(state == State.INIT) {

                if(!startField(curChar)) {
                    return null;
                }

            // The character after the leading quote...
            } else if(state == State.QUOTE_LEADING) {

                // If it's a trailing quote...
                if(isQuote(curChar, quoteDelimTrailing)) {
                    state = State.QUOTE_TRAILING;

                // All other characters (more leading quotes, whitespace,
                // field separators, all others).
                } else {
                    curField += curChar;
                    state = State.INTERNAL_CHAR;
                }

            // The character(s) after the trailing quote...
            } else if(state == State.QUOTE_TRAILING) {

                // If there's another quote before the field
                // separator, then the string is malformed.
                if(isQuote(curChar, quoteDelimLeading) || isQuote(curChar, quoteDelimTrailing)) {
                    // Error condition.
                    return null;

                // If it's a field separator, then register
                // the previous field and start a new one.
                } else if(isFieldSeparator(curChar, fieldSeparator)) {

                    endField();

                } else if(isSpace(curChar)) {
                    // Do nothing, ignore whitespace outside of fields.
                    // Keep state the same.

                // All other characters.
                } else {
                    // Error condition.
                    return null;
                }

            // The character(s) after the field separator...
            } else if(state == State.FIELD_SEP) {

                if(!startField(curChar)) {
                    return null;
                }

            // Characters within a field.
            } else {  // State.ICHAR

                if(isQuote(curChar, quoteDelimTrailing)) {
                    if(curFieldHasQuotes) {
                        state = State.QUOTE_TRAILING;
                    } else {
                        // Consider the quote internal to the field.
                        curField += curChar;
                        // Keep state the same.
                    }
                } else if(isFieldSeparator(curChar, fieldSeparator)) {
                    if(curFieldHasQuotes) {
                        // Consider the separator internal to the field.
                        curField += curChar;
                        // Keep state the same.
                    } else {
                        endField();
                    }
                } else {
                    curField += curChar;
                }
            }
        }
        endField();
        return allFields;
    }
    private Queue<Character> stringToQueue(String str) {
        Queue<Character> charQueue = new LinkedList<Character>();
        for(int c = 0; c < str.length(); c++) {
            charQueue.add(str.charAt(c));
        }
        return charQueue;
    }
    public boolean isQuote(char ch, char quoteDelim) {
        return (ch == quoteDelim);
    }
    public boolean isFieldSeparator(char ch, char fieldSeparator) {
        return (ch == fieldSeparator);
    }
    public boolean isSpace(char ch) {
        return Character.isWhitespace(ch);
    }
    private boolean startField(char curChar) {

        // If it's a leading quote...
        if(isQuote(curChar, quoteDelimLeading)) {
            curFieldHasQuotes = true;
            state = State.QUOTE_LEADING;

        // If it's a trailing quote...
        } else if(isQuote(curChar, quoteDelimTrailing)) {
            // Error condition.
            return false;

        // If it's another field separator, then add
        // a blank value.
        } else if(isFieldSeparator(curChar, fieldSeparator)) {
            endField();

        } else if(isSpace(curChar)) {
            // Do nothing, ignore whitespace outside of fields.

        } else {
            curField += curChar;
            curFieldHasQuotes = false;
            state = State.INTERNAL_CHAR;
        }

        return true;
    }

    private void endField() {
        if(!curFieldHasQuotes) {
            curField = curField.trim();
        }
        allFields.add(curField);
        curField = "";
        state = State.FIELD_SEP;
    }

    //////////
    // TEST //
    //////////

    private static class TestParseParams {
        public String line;
        public char fieldSeparator;
        public char quoteDelim1;
        public char quoteDelim2;
        public String expected;
        public TestParseParams(String l, char f, String e) {
            line = l;
            fieldSeparator = f;
            quoteDelim1 = quoteDelim2 = 0;
            expected = e;
        }
        public TestParseParams(String l, char f, char q, String e) {
            line = l;
            fieldSeparator = f;
            quoteDelim1 = q;
            quoteDelim2 = q;
            expected = e;
        }
        public TestParseParams(String l, char f, char q1, char q2, String e) {
            line = l;
            fieldSeparator = f;
            quoteDelim1 = q1;
            quoteDelim2 = q2;
            expected = e;
        }
    }

    public static String fieldsToString(List<String> allFields) {
        String result = "";
        for(String field : allFields) {
            result += "(" + field + ")";
        }
        return result;
    }

    public static void main(String[] args) {
        TestParseParams[] tests = new TestParseParams[] {
               new TestParseParams("  \"  abc ,\"   ,     'def'  ", ',', '\'', "(\"  abc)(\")(def)"),
               new TestParseParams("abc  , def", ',', '\"', "(abc)(def)"),
               new TestParseParams("[dude what] :   where are you :when[do you] want to go", ':', '[', ']', "(dude what)(where are you)(when[do you] want to go)"),
               new TestParseParams("one, two, three", ',', "(one)(two)(three)"),
               new TestParseParams(" one  ,   two   , three ", ',', "(one)(two)(three)"),
               new TestParseParams("'one ',  two  ,  'three four'", ',', '\'', "(one )(two)(three four)"),
               new TestParseParams("one two : three : 'four : five'", ':', '\'', "(one two)(three)(four : five)"),
               new TestParseParams("{a b } ?c d  ? {e ? f}", '?', '{', '}', "(a b )(c d)(e ? f)"),
               new TestParseParams(" x x x x ", ' ', "()(x)(x)(x)(x)()"),
               new TestParseParams(",A,B,C,D,", ',', "()(A)(B)(C)(D)()"),
               new TestParseParams("\tx  john\tx\tsmith\t\t", '\t', 'x', "()(  john\t)(smith)()()"),
               new TestParseParams("   thh\n t  ", ',', 't', "(hh\n )"),
        };

        int curTest = -1;

        for(int t = 0; t < tests.length; t++) {
            if(curTest == -1 || t == curTest) {
                TestParseParams test = tests[t];
                List<String> allFields = parseLine(test.line, test.fieldSeparator, test.quoteDelim1, test.quoteDelim2);
                System.out.print("Test " + t + ": ");
                if(allFields == null) {
                    System.out.print("ERROR");
                } else {
                    String actual = fieldsToString(allFields);
                    System.out.print(actual + " == " + test.expected + " ? " + (actual.equals(test.expected)));
                }
                System.out.println();
            }
        }
    }
}
