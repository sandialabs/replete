package replete.threads;

import replete.errors.ThrowableSnapshot;

public class RThreadInfo extends ThreadInfo {


    ////////////
    // FIELDS //
    ////////////

    protected boolean paused = false;
    protected boolean stopped = false;
    protected long startRun = 0;
    protected long endRun = 0;

//    protected RProfiler P;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RThreadInfo(Thread thread, StackTraceElement[] trace, int maxStackTraceElements) {
        super(thread, trace, maxStackTraceElements);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isPaused() {
        return paused;
    }
    public boolean isStopped() {
        return stopped;
    }
    public long getStartRun() {
        return startRun;
    }
    public long getEndRun() {
        return endRun;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void updateFrom(Thread thread, StackTraceElement[] trace, int maxStackTraceElements) {
        super.updateFrom(thread, trace, maxStackTraceElements);
        RThread avThread = (RThread) thread;
        paused   = avThread.isPaused();
        stopped  = avThread.isStopped();
        startRun = avThread.startRun;
        endRun   = avThread.endRun;

        if(avThread.uncaughtException != null) {
            uncaughtExceptionSnapshot = new ThrowableSnapshot(avThread.uncaughtException);
        }
    }
}
