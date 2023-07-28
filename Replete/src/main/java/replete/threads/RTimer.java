package replete.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import replete.errors.ThrowableSnapshot;
import replete.errors.UnicornException;

// The class java.util.Timer class is from the earliest days
// of Java and has a number of disadvantages.  In particular
// tasks cannot be individually canceled.  The more modern
// ScheduledExecutorService fixes this and other issues.
// This makes the Timer object as a whole more suitable for
// single-task use.
//
// Additionally, neither the Timer nor ScheduledExecutorService
// class have a clean way to perform a proper "pause".  A
// proper pause would push off the execution of the tasks within
// by the amount of time that the timer/executor is paused.
//
// This class wraps a Timer object to add a layer of convenience
// to it.  One RTimer object is meant to be used with a single
// future or repeated task.  This class attempts to add some
// common "state transition" methods for clarity (start, pause,
// update, and dispose).
//
// As mentioned, the trivial, imperfect way to pause a Timer object
// is simply to cancel the timer, make a new timer and resubmit the
// task.  This does reset the next scheduled time to the original
// configured delay.  Future iterations of this class might attempt
// to properly implement this pause mechanic.  This strategy is also
// why we use composition instead of inheritance to wrap/extend
// the Timer functionality.
//
// When using these basic timers, if a TimerTask fails then the whole
// Timer will become dead.  Restarting this RTimer will have RTimer
// try again with a new Timer instance though the error might just
// happen again.

public class RTimer {


    ///////////
    // ENUMS //
    ///////////

    public enum RepeatType {
        FIXED_DELAY,
        FIXED_RATE
    }
    public enum UpdateType {  // Alternate name: AfterUpdateLeaveTimerInWhatStateType
        TURN_ON,
        TURN_OFF,
        MAINTAIN,
        CURRENT_AND_FLAG
    }


    ////////////
    // FIELDS //
    ////////////

    public static int MAX_EXE_RESULTS = 30;

    // Copied from Timer
    private final static AtomicInteger nextSerialNumber = new AtomicInteger(0);
    private static int serialNumber() {
        return nextSerialNumber.getAndIncrement();
    }

    // RTimer is designed to wrap Timer functionality and not
    // ScheduledExecutorService just due to Timer's simple
    // nature.  The goal was to make using Timer for single
    // tasks very easy and readable.  Another class can be
    // created to add a convenience layer to ScheduledExecutorService
    // if such is desired.
    private Timer timer;

    // Runnable used since no void/void function was added with the
    // new lambda support in java.util.function (probably because
    // that wouldn't qualify as a function!).  But instead of the
    // traditional runnable that gets used and discarded when given
    // to a Thread, this runnable must be repeatable like when given
    // to a ScheduledExecutorService.  The explicit name just rein-
    // forces that to the developer (obviously you can argue this is
    // overkill).
    private Runnable   runnable;
    private String     name;              // If null, a default name created a la Timer class functionality
    private boolean    daemon;
    private long       delay;
    private long       period;              // If zero, task will only be executed once
    private RepeatType repeatType = RepeatType.FIXED_RATE;

    private AtomicInteger exeCount = new AtomicInteger();
    private List<RTimerExecutionResult> executionResults = new ArrayList<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RTimer() {}
    public RTimer(boolean daemon) {
        this.daemon = daemon;
    }
    public RTimer(String name, boolean daemon) {
        this.name = name;
        this.daemon = daemon;
    }
    public RTimer(String name) {
        this.name = name;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getName() {
        return name;
    }
    public boolean isDaemon() {
        return daemon;
    }
    public long getDelay() {
        return delay;
    }
    public long getPeriod() {
        return period;
    }
    public RepeatType getRepeatType() {
        return repeatType;
    }
    public Runnable getRunnable() {
        return runnable;
    }

    // Accessors (Computed)

    public List<RTimerExecutionResult> getExecutionResultsCopy() {
        synchronized(executionResults) {
            return new ArrayList<>(executionResults);
        }
    }

    // Mutators

    public RTimer setRunnable(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }
    public RTimer setName(String name) {
        this.name = name;
        return this;
    }
    public RTimer setDaemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }
    public RTimer setDelay(long delay) {
        this.delay = delay;
        return this;
    }
    public RTimer setPeriod(long period) {
        this.period = period;
        return this;
    }
    public RTimer setRepeatType(RepeatType repeatType) {
        this.repeatType = repeatType;
        return this;
    }


    //////////
    // MISC //
    //////////

    public synchronized boolean isPaused() {   // Paused or not started
        return timer == null;
    }
    public synchronized void ready() {
        // No default implementation
    }
    public synchronized void start() {
        if(!isPaused()) {  // This could also throw an exception, but
            pause();       // more in depth analysis of state transitions
        }                  // will have to wait.

        TimerTask task = new RTimerTask(runnable);
        String tName = name == null ? "RTimer-" + serialNumber() : name;
        timer = new Timer(tName, daemon);

        if(repeatType == RepeatType.FIXED_RATE) {
            if(period == 0) {                      // These extra if's are just to normalize
                timer.schedule(task, delay);       // usage of the asymmetric Timer API
            } else {
                timer.scheduleAtFixedRate(task, delay, period);
            }
        } else {
            if(period == 0) {
                timer.schedule(task, delay);
            } else {
                timer.schedule(task, delay, period);
            }
        }
    }

    public synchronized void pause() {    // One day it would be nice if this was smarter
        if(timer != null) {
            timer.cancel();
            timer.purge();      // Needed?
            timer = null;
        }
    }

    // Params are taken in here unlike PersistentController.update
    // so that the "don't do anything if the params really haven't
    // changed" logic an be centralized here so all code using
    // update doesn't have to perform the checks itself.  Not sure
    // if it's a great design but it's convenient for now.  We
    // also integrate the "conditional restart" logic within the
    // method so client code doesn't have to 1) deal with it
    // themselves in uncoordinated manners but also to 2) remain
    // within the synchronized scope for safety.
    public synchronized void update(long delay, long period, UpdateType updateType) {
        update(delay, period, updateType, false);
    }
    public synchronized void update(long delay, long period, UpdateType updateType, boolean gatedOnFlag) {
        boolean changed = delay != this.delay || period != this.period;
        this.delay = delay;
        this.period = period;

        boolean paused = isPaused();
        if(updateType == UpdateType.TURN_OFF) {
            if(!paused) {
                pause();
            }
        } else if(updateType == UpdateType.TURN_ON) {
            if(paused) {
                start();
            } else {
                if(changed) {
                    pause();
                    start();
                }
            }
        } else if(updateType == UpdateType.MAINTAIN) {
            if(!paused) {
                if(changed) {
                    pause();
                    start();
                }
            }
        } else if(updateType == UpdateType.CURRENT_AND_FLAG) {
            if(!paused) {
                if(gatedOnFlag) {
                    if(changed) {
                        pause();
                        start();
                    }
                } else {
                    pause();
                }
            }
        } else {
            throw new UnicornException("Impossible UpdateType value");
        }
    }
    public synchronized void dispose() {
        pause();
    }
    public synchronized RTimerSummaryState createSummaryState() {
        return new RTimerSummaryState(this);
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    public class RTimerTask extends TimerTask {    // Public for ThreadInfo to directly reference
        private Runnable runnable;
        public RTimerTask(Runnable runnable) {
            this.runnable = runnable;
        }
        @Override
        public void run() {
            RTimerExecutionResult result = new RTimerExecutionResult();
            result.setId(exeCount.getAndIncrement());
            synchronized(executionResults) {
                executionResults.add(result);
                if(executionResults.size() > MAX_EXE_RESULTS) {
                    executionResults.remove(0);
                }
            }
            result.setStart(System.currentTimeMillis());
            try {
                runnable.run();
            } catch(Exception e) {
                result.setThrowableSnapshot(new ThrowableSnapshot(e));
                throw e;
            } finally {
                result.setEnd(System.currentTimeMillis());
            }
        }
    }
}
