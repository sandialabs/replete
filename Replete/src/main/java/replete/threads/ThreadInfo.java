package replete.threads;

import java.io.Serializable;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import replete.errors.ThrowableSnapshot;
import replete.text.StringLib;
import replete.threads.RTimer.RTimerTask;
import replete.threads.deadlock.DeadlockDetector;
import replete.util.ReflectionUtil;

public class ThreadInfo implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    protected String key;
    protected long id;
    protected String name;
    protected boolean alive;
    protected boolean intp;
    protected boolean daemon;
    protected State state;
    protected String type;
    protected String runnableType;
    protected String executorTimerTaskType;
    protected String group;
    protected boolean deadlocked;
    protected boolean removed;
    protected UehType uehType;
    protected ThrowableSnapshot uncaughtExceptionSnapshot;
    protected List<String> topStackTrace = new ArrayList<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ThreadInfo(Thread thread, StackTraceElement[] trace, int maxStackTraceElements) {
        updateFrom(thread, trace, maxStackTraceElements);
    }

    public void updateFrom(Thread thread, StackTraceElement[] trace, int maxStackTraceElements) {
        String newKey = thread.getClass().getName() + "/" + thread.getId() + "/" + thread.hashCode();
        if(key == null) {
            key = newKey;
        } else if(!key.equals(newKey)) {
            throw new RuntimeException("Incorrect thread for this ThreadInfo");
        }

        populateInternalThreadRunnables(thread);

        id         = thread.getId();
        name       = thread.getName();
        alive      = thread.isAlive();
        intp       = thread.isInterrupted();
        daemon     = thread.isDaemon();
        state      = thread.getState();
        type       = thread.getClass().getName();
        group      = (thread.getThreadGroup() == null ? StringLib.NONE : thread.getThreadGroup().getName());
        deadlocked = DeadlockDetector.get().isDeadlocked(id);
        removed    = false;
        uehType    = ThreadUtil.getUehType(thread);

        topStackTrace.clear();

        // xxxx
        if(trace != null) {
            for(int i = 0; i < maxStackTraceElements && i < trace.length; i++) {
                StackTraceElement tr = trace[i];
                topStackTrace.add(
                    tr.getClassName() + "." +
                    tr.getMethodName() + "(" +
                    tr.getFileName() + ":" +
                    (tr.getLineNumber() < 0 ? "unknown" : "" + tr.getLineNumber()) + ")");
            }
        }
    }

    private void populateInternalThreadRunnables(Thread thread) {
        try {
            Runnable runnable = ReflectionUtil.get(thread, "target");
            runnableType = runnable == null ? null : runnable.getClass().getName();
            if(runnable != null) {
                String workerClassName = ThreadPoolExecutor.class.getName() + "$Worker";
                if(runnable.getClass().getName().equals(workerClassName)) {
                    Runnable firstTask = ReflectionUtil.get(runnable, "firstTask");
                    if(firstTask != null) {      // This condition is RARELY true
                        executorTimerTaskType = firstTask.getClass().getName();
                    } else {
                        ThreadPoolExecutor outer = ReflectionUtil.getOuterInstance(runnable);
                        BlockingQueue<Runnable> queue = outer.getQueue();

                        // The SynchronousQueue type of queue is not peekable, so it is
                        // ignored but the DelayedWorkQueue class is a little more
                        // tractable to deal with.
                        if(queue.getClass().getSimpleName().equals("DelayedWorkQueue")) {
                            Object peeked = ReflectionUtil.invoke(queue, "peek");
                            if(peeked != null) {
                                Object callable = ReflectionUtil.get(peeked, "callable");
                                if(callable != null) {
                                    Object task = ReflectionUtil.get(callable, "task");
                                    executorTimerTaskType = task == null ? null : task.getClass().getName();
                                }
                            }
                        }
                    }
                }

            // Inspect special threads used by java.util.Timer or replete.threads.RTimer's
            // usage of a Timer object to learn about out the TimerTask running and
            // possibly the RTimer's repeatableRunnable field.
            } else if(thread.getClass().getName().equals("java.util.TimerThread")) {
                Object queue = ReflectionUtil.get(thread, "queue");

                // Synchronize on the queue object as well just like the Java code does for safety.
                // The getMin method will return null if the timer thread has died due
                // to an uncaught exception.
                TimerTask task;
                synchronized(queue) {
                    task = ReflectionUtil.invoke(queue, "getMin");
                }
                if(task != null) {
                    executorTimerTaskType = task.getClass().getName();
                    if(executorTimerTaskType.equals(RTimerTask.class.getName())) {
                        runnable = ReflectionUtil.get(task, "runnable");       // Can't be null

                        // We'll reuse the "Runnable Type" field which above would
                        // normally hold only a Thread's direct runnable field for
                        // the RTimer's repeatableRunnable field since the runnable
                        // is always null for the RTimer's Timer's TimerThead's
                        // runnable.
                        runnableType = runnable.getClass().getName();
                    }
                }
            }
        } catch(Exception e) {
            // Best effort only - some pretty naughty tricks used above that could easily fail
        }
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getKey() {
        return key;
    }
    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public boolean isAlive() {
        return alive;
    }
    public boolean isIntp() {
        return intp;
    }
    public boolean isDaemon() {
        return daemon;
    }
    public State getState() {
        return state;
    }
    public String getType() {
        return type;
    }
    public String getRunnableType() {
        return runnableType;
    }
    public String getExecutorTimerTaskType() {
        return executorTimerTaskType;
    }
    public String getGroup() {
        return group;
    }
    public boolean isDeadlocked() {
        return deadlocked;
    }
    public boolean isRemoved() {
        return removed;
    }
    public UehType getUehType() {
        return uehType;
    }
    public ThrowableSnapshot getUncaughtExceptionSnapshot() {
        return uncaughtExceptionSnapshot;
    }
    public List<String> getTopStackTrace() {
        return topStackTrace;
    }

    // Mutator

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }
    public ThreadInfo setUncaughtExceptionSnapshot(ThrowableSnapshot uncaughtExceptionSnapshot) {
        this.uncaughtExceptionSnapshot = uncaughtExceptionSnapshot;
        return this;
    }
}
