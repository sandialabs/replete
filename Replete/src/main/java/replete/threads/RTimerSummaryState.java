package replete.threads;

import java.io.Serializable;
import java.util.List;

import replete.threads.RTimer.RepeatType;

public class RTimerSummaryState implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    public static final long EXECUTION_WARNING_THRESHOLD = 1 * 1000;  // 1s

    private String     key;
    private String     name;
    private String     context;   // Kind of acts as the "ThreadGroup" - a way to organize identically named RTimers
    private boolean    paused;
    private boolean    daemon;
    private long       delay;
    private long       period;
    private RepeatType repeatType = RepeatType.FIXED_RATE;
    private String     type;
    private String     runnableType;

    private List<RTimerExecutionResult> executionResults;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RTimerSummaryState(RTimer timer) {
        key          = timer.getClass().getName() + "/" + timer.hashCode();
        name         = timer.getName();
//        context      = ???;   // Filled in by RTimer subclasses
        paused       = timer.isPaused();
        daemon       = timer.isDaemon();
        delay        = timer.getDelay();
        period       = timer.getPeriod();
        repeatType   = timer.getRepeatType();
        type         = timer.getClass().getName();
        runnableType = timer.getRunnable().getClass().getName();

        executionResults = timer.getExecutionResultsCopy();
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getKey() {
        return key;
    }
    public String getName() {
        return name;
    }
    public String getContext() {
        return context;
    }
    public boolean isPaused() {
        return paused;
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
    public String getType() {
        return type;
    }
    public String getRunnableType() {
        return runnableType;
    }
    public List<RTimerExecutionResult> getExecutionResults() {
        return executionResults;
    }

    // Mutators

    public RTimerSummaryState setContext(String context) {
        this.context = context;
        return this;
    }
}
