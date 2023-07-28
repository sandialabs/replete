package replete.threads;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import replete.collections.Pair;
import replete.errors.ExceptionUtil;
import replete.errors.RuntimeConvertedException;
import replete.io.FileUtil;
import replete.text.NewlineType;
import replete.text.PatternPool;
import replete.text.StringUtil;
import replete.util.DateUtil;
import replete.util.ReflectionUtil;

public class ThreadUtil {


    ////////////
    // FIELDS //
    ////////////

    private static String[] platformThreadNameRegexs = new String[] {
        "Attach Listener",
        "Finalizer",
        "Reference Handler",
        "Signal Dispatcher",
        "DestroyJavaVM",
        "Java2D Disposer",
        "AWT-EventQueue-0",
        "AWT-Shutdown",
        "AWT-Windows",
        "Image Fetcher [0-9]+",
        "SwingWorker-pool-[0-9]+-thread-[0-9]+"
        // Some more obscure threads are not listed yet (e.g. RMI related threads)
    };

    // This pattern pool usage only minimally highlights the
    // potential for PatternPool class to make things easier
    // when using patterns.  But at least I don't have to make
    // my own data structure to ensure the regexs are only
    // parsed once - one already exists that I can use.
    private static PatternPool patternPool = new PatternPool();


    //////////////////////
    // PLATFORM THREADS //
    //////////////////////

    // It can be useful to distinguish between a thread your
    // software created vs. a thread created by the JVM or the
    // Java core libraries (called "platform threads" here).
    // Unfortunately we can't identify which are platform
    // threads by the group, as not all of such threads are in
    // the "system" group - many are in the "main" group.  So,
    // the strategy taken here is to just enumerate all thread
    // names known to correspond platform system threads.

    public static boolean isPlatformThread(Thread thread) {
        String threadName = thread.getName();
        String groupName = thread.getThreadGroup().getName();
        return isPlatformThread(threadName, groupName);
    }
    public static boolean isPlatformThread(String threadName, String groupName) {
        return groupName.equals("system") || isPlatformThreadName(threadName);
    }
    public static boolean isPlatformThreadName(String threadName) {
        for(String regex : platformThreadNameRegexs) {
            Pattern pattern = patternPool.getPattern(regex);
            Matcher m = pattern.matcher(threadName);
            if(m.matches()) {
                return true;
            }
        }
        return false;
    }


    //////////////
    // WRAPPERS //
    //////////////

    // Convert checked exceptions into non-checked exceptions.

    public static void checkInterrupted() {
        if(Thread.interrupted()) {
            throw new InterruptedRuntimeException("Thread " + Thread.currentThread().getId() + " was interrupted.");
        }
    }
    public static void join(Thread t) {
        try {
            t.join();
        } catch(InterruptedException e) {
            throw new RuntimeConvertedException(e);
        }
    }
    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch(Exception e) {
            throw new RuntimeConvertedException(e);
        }
    }
    public static void wait(Object target) {
        try {
            target.wait();
        } catch(InterruptedException e) {
            throw new RuntimeConvertedException(e);
        }
    }

    public static UncaughtExceptionHandler getSelfUncaughtExceptionHandler(Thread t) {
        return (UncaughtExceptionHandler) ReflectionUtil.get(t, "uncaughtExceptionHandler");
    }

    // Not perfect but does encapsulate a lot of possibilities.
    public static UehType getUehType(Thread t) {
        if(getSelfUncaughtExceptionHandler(t) != null) {
            return UehType.SELF_FIELD;
        }
        ThreadGroup group = t.getThreadGroup();   // Can be null if the thread has exited
        if(group != null) {
            if(t.getUncaughtExceptionHandler() == group) {
                if(group.getClass().equals(ThreadGroup.class)) {
                    if(group.getParent() != null) {
                        return UehType.GROUP_PARENT;   // Technically would have to repeat logic now
                    }
                    if(Thread.getDefaultUncaughtExceptionHandler() != null) {
                        return UehType.DEFAULT;
                    }
                    return UehType.NOTHING;
                }
                return UehType.CUSTOM_GROUP;
            }
            return UehType.SELF_CUSTOM;
        }
        return UehType.EMPTY;
    }


    //////////////////
    // DUMP THREADS //
    //////////////////

    public static void dumpThread() {
        dumpThreads(Thread.currentThread(), true, null, 300);
    }
    public static void dumpThread(int maxTraces) {
        dumpThreads(Thread.currentThread(), true, null, maxTraces);
    }
    public static void dumpThreads() {
        dumpThreads(null, false, null, 300);
    }
    public static void dumpThreads(boolean traces) {
        dumpThreads(null, traces, null, 300);
    }
    public static void dumpThreads(Thread target, boolean traces, Object monitor, int maxTraces) {
        System.out.print(createThreadInfo(target, traces, monitor, maxTraces));
    }

    public static String createThreadInfo(Thread target, boolean traces, Object monitor, int maxTraces) {
        StringBuilder buffer = new StringBuilder();

        // Create sorted map
        Map<Thread, StackTraceElement[]> sorted = new TreeMap<>(
            (o1, o2) -> {
                int d1 = o1.isDaemon() ? 1 : 0;
                int d2 = o2.isDaemon() ? 1 : 0;
                int res = d1 - d2;
                if(res == 0) {
                    return o1.getName().compareTo(o2.getName());
                }
                return res;
            }
        );

        // Sort the stack traces
        if(target != null) {
            sorted.put(target, target.getStackTrace());
        } else {
            sorted.putAll(Thread.getAllStackTraces());
        }

        String holdsLockText = " (HOLDS LOCK)";
        long ownerThreadId = monitor == null ? -1 : getMonitorOwnerId(monitor);
        int maxN = StringUtil.maxLength(sorted.keySet(), t -> {
            String lk = monitor == null || t.getId() != ownerThreadId ? "" : holdsLockText;

            String n = t.getName();
            if(n.startsWith("cluster-ClusterId")) {  // Hardcoded name shortening due to a very long Mongo thread name
                n = "cluster-ClusterId...";          // This could be made more general some day
            }

            return n + lk;
        });
        int maxG = StringUtil.maxLength(sorted.keySet(), t -> {
            return t.getThreadGroup() == null ? 0 : t.getThreadGroup().getName();
        });
        int maxS = StringUtil.maxLength(sorted.keySet(), "getState");
        int maxC = StringUtil.maxLength(sorted.keySet(), o -> {
            return o.getClass().getName();
        });

        // Find the ID of the thread that owns the monitor object

        String format = "%-" + maxN + "s  %3s  %-" + maxG + "s  %-6s  %-" + maxS + "s  %-3s  %-" + maxC + "s  %s\n";
        buffer.append(String.format(format, "Name", "JVM", "Group", "Type", "State", "UEH", "Class", "Head Stack Frame"));
        buffer.append(String.format(format, "====", "===", "=====", "====", "=====", "===", "=====", "================"));

        for(Thread t : sorted.keySet()) {
            StackTraceElement[] frames = sorted.get(t);
            String frameStr = frames.length == 0 ? "" : frames[0].getClassName() + "." + frames[0].getMethodName();

            // Print out basic thread info
            String gn = t.getThreadGroup() == null ? "(UNK)" : t.getThreadGroup().getName();
            String sys = isPlatformThread(t.getName(), gn) ? " Y ": "";
            String d = t.isDaemon() ? "DAEMON" : "NORMAL";
            String lk = monitor == null || t.getId() != ownerThreadId ? "" : holdsLockText;
            String n = t.getName();
            if(n.startsWith("cluster-ClusterId")) {
                n = "cluster-ClusterId...";
            }
            UehType ueh = getUehType(t);
            buffer.append(String.format(format, n + lk, sys, gn, d, t.getState(), ueh, t.getClass().getName(), frameStr));

            // Print out the stack traces if desired
            if(traces) {
                StackTraceElement[] elems = sorted.get(t);
                int howMany = Math.min(maxTraces, elems.length);
                for(int i = 0; i < howMany; i++) {
                    buffer.append("    " + elems[i] + "\n");
                }
                if(howMany < elems.length) {
                    buffer.append("    ...\n");
                }
            }
        }

        return buffer.toString();
    }


    //////////
    // MISC //
    //////////

    public static long getMonitorOwnerId(Object obj) {
        if(Thread.holdsLock(obj)) {
            return Thread.currentThread().getId();
        }
        for(ThreadInfo ti : ManagementFactory.getThreadMXBean().dumpAllThreads(true, false)) {
            for(MonitorInfo mi : ti.getLockedMonitors()) {
                if(mi.getIdentityHashCode() == System.identityHashCode(obj)) {
                    return ti.getThreadId();
                }
            }
        }
        return -1;
    }

    public static void initThreadDump(File dest, int rateMs) {
        Timer t = new Timer(false);
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                String info =
                    DateUtil.toLongString(System.currentTimeMillis()) + "\n" +
                    createThreadInfo(null, true, null, 300);
                FileUtil.writeTextContent(dest, info, NewlineType.NONE);
            }
        }, 0, rateMs);
    }

    public static Pair<Thread, StackTraceElement[]> getThreadTrace(long id) {
        Map<Thread, StackTraceElement[]> stackTraceMap = Thread.getAllStackTraces();
        return getThreadTrace(id, stackTraceMap);
    }
    public static Pair<Thread, StackTraceElement[]> getThreadTrace(
           long id, Map<Thread, StackTraceElement[]> stackTraceMap) {
        for(Thread thread : stackTraceMap.keySet()) {
            if(thread.getId() == id) {
                return new Pair<>(thread, stackTraceMap.get(thread));
            }
        }
        return null;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        XThread thread = new XThread();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
            }
        });
        thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("Current: " + Thread.currentThread());
                System.out.println("Subject: " + t);
            }
        });
        thread.start();
        ThreadUtil.sleep(1000);
        ThreadUtil.dumpThreads();
//        initThreadDump(User.getDesktop("text.txt"), 5000);
    }

    private static class XThread extends Thread {
        @Override
        public void run() {
            System.out.println("Running: " + Thread.currentThread());
            ThreadUtil.sleep(5000);
            ExceptionUtil.toss();
        }
    }

    public static void spawn(Runnable runnable) {
        new Thread() {
            @Override
            public void run() {
                runnable.run();
            }
        }.start();
    }
}
