package replete.profiler;

import replete.util.DateUtil;

public class NanosToWallClockTest {

    public static void main(String[] args) {
        int MAX = 10;
        long prevMs = -1;
        long prevNs = -1;
        for(int i = 0; i < MAX; i++) {
            long ms = nowMs();
            long ns = nowNs();
            System.out.println(ms + " (" + DateUtil.toLongString(ms) + ")");
            System.out.println(ns + " (" + DateUtil.toLongString(ns / 1000000) + ")");
//            ThreadUtil.sleep(20);
            prevMs = ms;
            prevNs = ns;
        }
    }

    private static long nowMs() {
        return System.currentTimeMillis();
    }
    private static long nowNs() {
        return System.nanoTime();
    }
}
