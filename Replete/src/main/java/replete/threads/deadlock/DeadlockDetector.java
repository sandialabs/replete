package replete.threads.deadlock;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import replete.event.ExtChangeNotifier;

// Initial idea from: http://korhner.github.io/java/multithreading/detect-java-deadlocks-programmatically/

public class DeadlockDetector {


    ///////////////
    // SINGLETON //    Should only have one detector for entire JVM process
    ///////////////

    private static DeadlockDetector instance;
    public static DeadlockDetector get() {
        if(instance == null) {
            instance = new DeadlockDetector();
        }
        return instance;
    }


    ////////////
    // FIELDS //
    ////////////

    private static final long DEFAULT_PERIOD = 30;
    private static final TimeUnit DEFAULT_UNIT = TimeUnit.SECONDS;

    private long period = DEFAULT_PERIOD;
    private TimeUnit unit = DEFAULT_UNIT;
    private List<DeadlockHandler> deadlockHandlers = new ArrayList<>();

    private ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new ThreadFactory() {
        public Thread newThread(Runnable r) {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setName("DeadlockDetector-" + t.getName());   // Always naming your threads helps when performing diagnostics on your Java app
            t.setDaemon(true);
            return t;
        }
    });
    private Runnable deadlockCheck;
    private LongSummaryStatistics findThreadsDurationStats = new LongSummaryStatistics();
    private Map<Long, DeadlockedThreadDescriptor> deadlockedThreadDescriptors = new HashMap<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    private DeadlockDetector() {
        deadlockCheck = () -> {
            try {

                // Look for current deadlocked threads and time it
                // since we don't yet know this code well yet.
                long now = System.currentTimeMillis();
                long[] deadlockedThreadIds = threadMxBean.findDeadlockedThreads();
                long dur = System.currentTimeMillis() - now;
                synchronized(findThreadsDurationStats) {
                    findThreadsDurationStats.accept(dur);
                }

                // If any threads were just found to be deadlocked by
                // above code, OR any threads have EVER been found
                // to be deadlocked, perform bookkeeping and alert
                // the handlers.
                if(deadlockedThreadIds != null || !deadlockedThreadDescriptors.isEmpty()) {
                    Set<Long> found = new HashSet<>();

                    // For all threads found to be deadlocked, update
                    // internal bookkeeping of these threads.
                    updateInternalState(now, deadlockedThreadIds, found);

                    // Mark those threads that weren't found in the
                    // current deadlock set as "removed".  This should
                    // technically be impossible by all accounts.
                    // It's being implemented in the early days of
                    // adding deadlock detection to ensure proper
                    // understanding of the problem and our solution.
                    for(Long id : deadlockedThreadDescriptors.keySet()) {
                        if(!found.contains(id)) {
                            DeadlockedThreadDescriptor desc = deadlockedThreadDescriptors.get(id);
                            desc.setRemoved(true);
                        }
                    }

                    // Alert all handlers with all known deadlocked thread
                    // information.
                    for(DeadlockHandler deadlockHandler : deadlockHandlers) {
                        deadlockHandler.handleDeadlock(this, deadlockedThreadDescriptors);
                    }
                }
            } catch(Exception e) {
                fireErrorNotifier(e);      // Listeners can cause detector to stop.
            }
        };
    }

    private void updateInternalState(long now, long[] deadlockedThreadIds, Set<Long> found) {
        synchronized(deadlockedThreadDescriptors) {
            if(deadlockedThreadIds != null) {
                for(long id : deadlockedThreadIds) {
                    DeadlockedThreadDescriptor desc = deadlockedThreadDescriptors.get(id);
                    if(desc == null) {
                        desc = new DeadlockedThreadDescriptor(id);
                        desc.setNewlyEcountered(true);
                        desc.setFirstTimeReported(now);

                        deadlockedThreadDescriptors.put(id, desc);
                    } else {
                        desc.setNewlyEcountered(false);
                    }
                    desc.setLastTimeReported(now);
                    desc.setRemoved(false);

                    // Presumed to be a fast operation. Could
                    // technically be null under normal circumstances
                    // if thread has ended, but should be non-null here
                    // since it's deadlocked.
                    desc.setInfo(threadMxBean.getThreadInfo(id));

                    found.add(id);
                }
            }
        }
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors (Computed)

    public LongSummaryStatistics getFindThreadsDurationStats() {
        synchronized(findThreadsDurationStats) {
            LongSummaryStatistics copy = new LongSummaryStatistics();
            copy.combine(findThreadsDurationStats);
            return copy;
        }
    }

    // Mutators

    public void setPeriod(int period, TimeUnit unit) {
        this.period = period;
        this.unit = unit;
    }
    public void addHandler(DeadlockHandler deadlockHandler) {
        deadlockHandlers.add(deadlockHandler);
    }


    //////////
    // MISC //
    //////////

    public boolean isDeadlocked(long threadId) {
        synchronized(deadlockedThreadDescriptors) {
            return deadlockedThreadDescriptors.containsKey(threadId);
        }
    }
    public void start() {
        scheduler.scheduleAtFixedRate(deadlockCheck, period, period, unit);
    }
    public void stop() {
        scheduler.shutdown();
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    private ExtChangeNotifier<DeadlockDetectorErrorListener> errorNotifier = new ExtChangeNotifier<>();
    public void addErrorListener(DeadlockDetectorErrorListener listener) {
        errorNotifier.addListener(listener);
    }
    private void fireErrorNotifier(Exception e) {
        DeadlockDetectorErrorEvent event = new DeadlockDetectorErrorEvent(e);
        errorNotifier.fireStateChanged(event);
        if(event.isStop()) {
            stop();
        }
    }
}
