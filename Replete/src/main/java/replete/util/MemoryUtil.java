package replete.util;

/**
 * Contains convenience methods for dealing memory issues.
 *
 * @author Derek Trumbo
 */

public class MemoryUtil {
    public static void attemptToReclaim() {
        Runnable r = new Runnable() {
            public void run() {
                // Although a funny thing to see in Java,
                // did work on my machine, want to see how
                // it does on other machines.
                for(int x = 0; x < 5; x++) {
                    try{Thread.sleep(1000);}catch(Exception e){}
                    System.gc();
                }
            }
        };
        Thread t = new Thread(r, "attemptToReclaim");
        t.setDaemon(true);
        t.start();
    }


    private static Runtime runtime = Runtime.getRuntime();

    private static boolean freeEachCall = true;
    public static boolean isFreeEachCall() {
        return freeEachCall;
    }
    public static void setFreeEachCall(boolean f) {
        freeEachCall = f;
    }

    public static long getMaxMemory() {
        return runtime.maxMemory();
    }

    public static long getFreeMemory() {
        if(freeEachCall) {
            free();
        }
        return runtime.freeMemory();
    }

    public static long getTotalMemory() {
        if(freeEachCall) {
            free();
        }
        return runtime.totalMemory();
    }

    public static long getUsedMemory() {
        if(freeEachCall) {
            free();
        }
        return runtime.totalMemory() - runtime.freeMemory();
    }

    public static long getAllFreeMemory() {
        if(freeEachCall) {
            free();
        }
        return runtime.freeMemory() + runtime.maxMemory() - runtime.totalMemory();
    }

    public static double getPercentUsed() {
        if(freeEachCall) {
            free();
        }
        long used = runtime.totalMemory() - runtime.freeMemory();
        long max = runtime.maxMemory();
        return (double) used / max;
    }

    public static void printCurrent() {

        if(freeEachCall) {
            free();
        }

        long max = runtime.maxMemory();
        long alloc = runtime.totalMemory();
        long free = runtime.freeMemory();
        long used = alloc - free;
        long realFree = free + max - alloc;

        int scale = 1000;
        String units = "K";

        StringBuilder s = new StringBuilder();
        s.append("Mem=> ");
        s.append("U:");
        s.append(used / scale);
        s.append(units + "/F:");
        s.append(free / scale);
        s.append(units + "/T:");
        s.append(alloc / scale);
        s.append(units + "/M:");
        s.append(max /scale);
        s.append(units + "/RF:");
        s.append(realFree / scale);
        s.append(units + "/");
        s.append((int) ((double) used / max * 100));
        s.append("%");

        System.out.println(s);
    }

    public static void free() {
        try {
            for(int x = 0; x < 3; x++) {
                System.gc();                 Thread.sleep(100);
                System.runFinalization();    Thread.sleep(100);
            }
        } catch(InterruptedException ex) {
        }
    }

}
