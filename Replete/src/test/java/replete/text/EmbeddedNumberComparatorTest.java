package replete.text;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EmbeddedNumberComparatorTest {

    @Test
    public void testComparator() {
        check("", "", 0);
        check("", "apple", -1);
        check("apple", "", 1);
        check("apple", "apple", 0);
        check("apple", "appleV", -1);
        check("appleV", "apple", 1);
        check("apple1", "apple1", 0);
        check("apple1", "apple1a", -1);
        check("apple1a", "apple1", 1);
        check("apple1a", "apple1a", 0);
        check("apple12a", "apple1a", 1);
        check("apple12a", "apple13a", -1);
        check("apple12a", "apple12a", 0);
        check("apple12a7", "apple12a7", 0);
        check("apple12a7", "apple12a9", -1);
        check("apple12a7", "apple12a5", 1);
        check("apple12a11", "apple12a9", 1);
        check("apple12a11", "apple12a9x", 1);
        check("111 52 63", "111 52 64", -1);
        check("111 52 63", "111 52 62", 1);
        check("a", "A", 1);
        check("A", "a", -1);
    }

    private static void check(String s1, String s2, int expected) {
        int actual = new EmbeddedNumberComparator(false).compare(s1, s2);
        assertEquals(expected, actual);
    }
}
