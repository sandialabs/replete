package replete.text;

import java.util.Comparator;

/**
 * A comparator that can compare any two strings with integer values
 * embedded inside, considering each group of digits as a distinct
 * field that should be compared numerically.  For example, it can
 * be used to sort lists like:
 *
 *   "Event 1", "Event 9", "Event 10"
 *
 * So that "Event 10" does not sort immediately after "Event 1".
 * This comparator does not care where the numbers are.  Lists
 * like:
 *
 *   "abc9xyz99", "abc9xyz400", "abc12xyz35", "abc12xyz250"
 *
 * Can be effectively sorted considering each group of digits
 * numerically.
 *
 * @author Derek Trumbo
 */

public class EmbeddedNumberComparator implements Comparator<String> {

    protected boolean ignoreCase;

    public EmbeddedNumberComparator(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    @Override
    public int compare(String str1, String str2) {

        if(str1 == null || str2 == null) {
            throw new NullPointerException("Cannot compare null strings.");
        }

        // Both strings non-null here.

        // Length of each string.
        int len1 = str1.length();
        int len2 = str2.length();

        // Holds the current numbers being compared.
        String num1 = "";
        String num2 = "";

        // Pointer to the current character in each string.
        int pos1 = 0;
        int pos2 = 0;

        // Continues until one of the conditions inside
        // stops the loop.
        while(true) {

            // Handles case when one or both of the
            // strings have reached their ends.
            if(pos1 == len1) {
                if(pos2 == len2) {

                    // Either both strings were empty
                    // strings or all their characters
                    // were equivalent.
                    return 0;
                }

                // The first string has reached its
                // end before the second string
                // (and all previous characters were
                // equivalent).
                return -1;

            } else if(pos2 == len2) {

                // The second string has reached its
                // end before the first string
                // (and all previous characters were
                // equivalent).
                return 1;
            }

            // Grab the characters at the current
            // positions.
            char ch1 = str1.charAt(pos1);
            char ch2 = str2.charAt(pos2);

            // If both of the current characters in each
            // string are digits 0-9, extract the entire
            // integer value and perform a numeric
            // comparison between those values.
            if(Character.isDigit(ch1) && Character.isDigit(ch2)) {

                // Find all the characters that represent
                // the current integer in the first string.
                num1 = "" + ch1;
                pos1++;
                while(pos1 < str1.length() && Character.isDigit(ch1 = str1.charAt(pos1))) {
                    num1 += ch1;
                    pos1++;
                }

                // Find all the characters that represent
                // the current integer in the second string.
                num2 = "" + ch2;
                pos2++;
                while(pos2 < str2.length() && Character.isDigit(ch2 = str2.charAt(pos2))) {
                    num2 += ch2;
                    pos2++;
                }

                // Convert the characters to integers and compare them.
                // TODO: This is a bug for any numbers larger than Long.MAX_VALUE
                long long1 = Long.parseLong(num1);
                long long2 = Long.parseLong(num2);
                if(long1 < long2) {
                    return -1;
                } else if(long1 > long2) {
                    return 1;
                }

                // Reset the numerical strings.
                num1 = "";
                num2 = "";

                // The integer portions are identical, and
                // the character pointers are where they should
                // be, pointing to the characters after the
                // numbers, or equal to the length if at the
                // end of the string.

            // If either one of the current characters are
            // not digits 0-9, then just do a regular
            // lexicographic comparison.
            } else {

                // Make the characters same case if ignore case
                // turned on.
                if(ignoreCase) {
                    ch1 = Character.toUpperCase(ch1);
                    ch2 = Character.toUpperCase(ch2);
                }

                int diff = ch1 - ch2;
                if(diff < 0) {
                    return -1;
                } else if(diff > 0) {
                    return 1;
                }

                // They are the same character so advance the
                // positions in both strings.
                pos1++;
                pos2++;
            }
        }
    }
}
