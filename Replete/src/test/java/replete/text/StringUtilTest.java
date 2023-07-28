package replete.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class StringUtilTest {

    @Test
    public void leftTrim() {
        assertEquals("", StringUtil.leftTrim(""));
        assertEquals("", StringUtil.leftTrim("  "));
        assertEquals("aaa ", StringUtil.leftTrim("aaa "));
        assertEquals("aaa  ", StringUtil.leftTrim("aaa  "));
        assertEquals("aaa", StringUtil.leftTrim(" aaa"));
        assertEquals("aaa", StringUtil.leftTrim("  aaa"));
        assertEquals("aaa ", StringUtil.leftTrim(" aaa "));
        assertEquals("aaa  ", StringUtil.leftTrim("  aaa  "));
        assertEquals("left", StringUtil.leftTrim("000left", '0'));

        try {
            StringUtil.leftTrim(null);
            fail();
        } catch(IllegalArgumentException e) {

        }
    }

    @Test
    public void rightTrim() {
        assertEquals("", StringUtil.rightTrim(""));
        assertEquals("", StringUtil.rightTrim("  "));
        assertEquals("aaa", StringUtil.rightTrim("aaa "));
        assertEquals("aaa", StringUtil.rightTrim("aaa  "));
        assertEquals(" aaa", StringUtil.rightTrim(" aaa"));
        assertEquals("  aaa", StringUtil.rightTrim("  aaa"));
        assertEquals(" aaa", StringUtil.rightTrim(" aaa "));
        assertEquals("  aaa", StringUtil.rightTrim("  aaa  "));
        assertEquals("right", StringUtil.rightTrim("right000", '0'));

        try {
            StringUtil.rightTrim(null);
            fail();
        } catch(IllegalArgumentException e) {

        }
    }

    @Test
    public void testRemoveStart() {
        assertEquals("Hello World", StringUtil.removeStart("Hello World", ""));
        assertEquals("Hello World", StringUtil.removeStart("Hello World", "ABC"));
        assertEquals("Hello World", StringUtil.removeStart("Hello World", "ello"));
        assertEquals(" World", StringUtil.removeStart("Hello World", "Hello"));
        assertEquals("", StringUtil.removeStart("Hello World", "Hello World"));
        assertEquals("Hello World", StringUtil.removeStart("Hello World", "Hello Worlds"));

        try {
            StringUtil.rightTrim(null);
            fail();
        } catch(IllegalArgumentException e) {

        }
        try {
            StringUtil.removeStart("", null);
            fail();
        } catch(IllegalArgumentException e) {

        }
    }

    @Test
    public void testRemoveEnd() {
        assertEquals("Hello World", StringUtil.removeEnd("Hello World", ""));
        assertEquals("Hello World", StringUtil.removeEnd("Hello World", "ABC"));
        assertEquals("Hello World", StringUtil.removeEnd("Hello World", "Worl"));
        assertEquals("Hello ", StringUtil.removeEnd("Hello World", "World"));
        assertEquals("Hello World", StringUtil.removeEnd("Hello WorldWorld", "World"));
        assertEquals("", StringUtil.removeEnd("Hello World", "Hello World"));
        assertEquals("Hello World", StringUtil.removeEnd("Hello World", "Hello Worlds"));

        try {
            StringUtil.removeEnd(null, "");
            fail();
        } catch(IllegalArgumentException e) {

        }

        try {
            StringUtil.removeEnd("", null);
            fail();
        } catch(IllegalArgumentException e) {

        }
    }

    @Test
    public void testReplicateChar() {
        assertEquals("aaa", StringUtil.replicateChar('a', 3));
        assertEquals("", StringUtil.replicateChar('a', 0));
        assertEquals("", StringUtil.replicateChar('a', -1));
    }

    @Test
    public void testSpaces() {
        assertEquals("   ", StringUtil.replicateChar(' ', 3));
        assertEquals("", StringUtil.replicateChar(' ', 0));
        assertEquals("", StringUtil.replicateChar(' ', -1));
    }

    @Test
    public void testPadLeft() {
        assertEquals("---toast", StringUtil.padLeft("toast", '-', 8));
        assertEquals("toast", StringUtil.padLeft("toast", '-', 4));
    }

    @Test
    public void testPadRight() {
        assertEquals("toast---", StringUtil.padRight("toast", '-', 8));
        assertEquals("toast", StringUtil.padRight("toast", '-', 4));
    }

    @Test
    public void testNewlines() {
        String s0 = null;
        assertEquals(null, StringUtil.convertNewlines(s0, NewlineType.CR));

        String s1 = "\nsonic\nboom\n";
        assertEquals("\rsonic\rboom\r",
            StringUtil.convertNewlines(s1, NewlineType.CR));
        assertEquals("\nsonic\nboom\n",
            StringUtil.convertNewlines(s1, NewlineType.LF));
        assertEquals("\r\nsonic\r\nboom\r\n",
            StringUtil.convertNewlines(s1, NewlineType.CRLF));
        assertEquals(" sonic boom ",
            StringUtil.convertNewlines(s1, NewlineType.NONE));
        assertEquals(s1, StringUtil.convertNewlines(s1, NewlineType.AUTO));
        assertEquals(s1, StringUtil.convertNewlines(s1, NewlineType.MIXED));

        String s2 = "\rgreen\remerald\r";
        assertEquals("\rgreen\remerald\r",
            StringUtil.convertNewlines(s2, NewlineType.CR));
        assertEquals("\ngreen\nemerald\n",
            StringUtil.convertNewlines(s2, NewlineType.LF));
        assertEquals("\r\ngreen\r\nemerald\r\n",
            StringUtil.convertNewlines(s2, NewlineType.CRLF));
        assertEquals(" green emerald ",
            StringUtil.convertNewlines(s2, NewlineType.NONE));
        assertEquals(s2, StringUtil.convertNewlines(s2, NewlineType.AUTO));
        assertEquals(s2, StringUtil.convertNewlines(s2, NewlineType.MIXED));

        String s3 = "\r\nrock\r\ngarden\r\n";
        assertEquals("\rrock\rgarden\r",
            StringUtil.convertNewlines(s3, NewlineType.CR));
        assertEquals("\nrock\ngarden\n",
            StringUtil.convertNewlines(s3, NewlineType.LF));
        assertEquals("\r\nrock\r\ngarden\r\n",
            StringUtil.convertNewlines(s3, NewlineType.CRLF));
        assertEquals(" rock garden ",
            StringUtil.convertNewlines(s3, NewlineType.NONE));
        assertEquals(s3, StringUtil.convertNewlines(s3, NewlineType.AUTO));
        assertEquals(s3, StringUtil.convertNewlines(s3, NewlineType.MIXED));

        String s4 = "\r\r\n\r\n\n\rlast\r\n\r\n\nsummer\r\r\r\r\n\n\n\n\r";

        assertEquals("\r\r\r\r\rlast\r\r\rsummer\r\r\r\r\r\r\r\r",
            StringUtil.convertNewlines(s4, NewlineType.CR));
        assertEquals("\n\n\n\n\nlast\n\n\nsummer\n\n\n\n\n\n\n\n",
            StringUtil.convertNewlines(s4, NewlineType.LF));
        assertEquals("\r\n\r\n\r\n\r\n\r\nlast\r\n\r\n\r\nsummer\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n",
            StringUtil.convertNewlines(s4, NewlineType.CRLF));
        assertEquals("     last   summer        ",
            StringUtil.convertNewlines(s4, NewlineType.NONE));
        assertEquals(s4, StringUtil.convertNewlines(s4, NewlineType.AUTO));
        assertEquals(s4, StringUtil.convertNewlines(s4, NewlineType.MIXED));

        assertEquals(NewlineType.LF, StringUtil.getFirstNewline(s1));
        assertEquals(NewlineType.CR, StringUtil.getFirstNewline(s2));
        assertEquals(NewlineType.CRLF, StringUtil.getFirstNewline(s3));
        assertEquals(NewlineType.CR, StringUtil.getFirstNewline(s4));
        assertEquals(NewlineType.LF, StringUtil.getFirstNewline("\nstar\r\n"));
        assertEquals(NewlineType.LF, StringUtil.getFirstNewline("\nstar\r\n"));
        assertEquals(NewlineType.NONE, StringUtil.getFirstNewline("star"));
        assertEquals(NewlineType.NONE, StringUtil.getFirstNewline(""));

        assertEquals(NewlineType.LF, StringUtil.getNewlineType(s1));
        assertEquals(NewlineType.CR, StringUtil.getNewlineType(s2));
        assertEquals(NewlineType.CRLF, StringUtil.getNewlineType(s3));
        assertEquals(NewlineType.MIXED, StringUtil.getNewlineType(s4));
        assertEquals(NewlineType.MIXED, StringUtil.getNewlineType("\n\rstar"));
        assertEquals(NewlineType.MIXED, StringUtil.getNewlineType("\r\nstar\r"));
        assertEquals(NewlineType.NONE, StringUtil.getNewlineType("star"));
    }

    @Test
    public void testCleanNullCharacters() {
        String testString = "T\0h\0i\0s\0 \0i\0s\0 \0a\0 \0s\0t\0r\0i\0n\0g\0 \0w\0i\0t\0h\0 " +
                            "\0i\0n\0t\0e\0r\0l\0e\0a\0v\0e\0d\0 \0n\0u\0l\0l\0 "              +
                            "\0c\0h\0a\0r\0a\0c\0t\0e\0r\0s\0.\0";
        assertEquals("This is a string with interleaved null characters.", StringUtil.cleanUnusualChars(testString));
    }

    @Test
    public void testCleanUnusualChars() {
        String testString = "This is a string\1\2\3\t\r\n with weird characters.©®";
        assertEquals("This is a string with weird characters.", StringUtil.cleanUnusualChars(testString));
    }

    @Test
    public void testCleanUnprintableControlChars() {
        String testString = "This is a string\1\2\3\t\r\n with weird characters.©®";
        assertEquals("This is a string\t\r\n with weird characters.©®", StringUtil.cleanUnprintableControlChars(testString));
    }

    @Test
    public void testCleanAllControlChars() {
        String testString = "This is a string\1\2\3\t\r\n with weird characters.©®";
        assertEquals("This is a string with weird characters.©®", StringUtil.cleanAllControlChars(testString));
    }

    @Test
    public void testCleanInternationalChars() {
        String testString = "This is a string\1\2\3\t\r\n with weird characters.©®";
        assertEquals("This is a string\1\2\3\t\r\n with weird characters.", StringUtil.cleanInternationalChars(testString));
    }
}
