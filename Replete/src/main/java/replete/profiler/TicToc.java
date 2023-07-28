package replete.profiler;

/**
 * @author Derek Trumbo
 */

public class TicToc {
    private static long start = -1;
    public static long tic() {
        return tic(null);
    }
    public static long tic(String label) {
        if(label != null) {
            System.out.println(label);
        }
        start = System.currentTimeMillis();
        return start;
    }
    public static long toc() {
        return toc(null);
    }
    public static long toc(String label) {
        long now = System.currentTimeMillis();
        if(start == -1) {
            start = now;
        }
        long stop = now - start;
        if(label == null) {
            System.out.println(stop);
        } else {
            System.out.println(label + ": " + stop);
        }
        start = now;
        return stop;
    }
    public static long tocn() {
        long now = System.currentTimeMillis();
        if(start == -1) {
            start = now;
        }
        long stop = now - start;
        start = now;
        return stop;
    }
}
