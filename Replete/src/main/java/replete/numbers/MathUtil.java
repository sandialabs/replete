package replete.numbers;

/**
 * @author Derek Trumbo
 */

public class MathUtil {
    public static int negMod(int left, int right) {
        if(left < 0) {
            return right - (-1* (left % right));
        }
        return left % right;
    }
}
