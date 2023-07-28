package replete.threads;

public class ContinuousThreadInfo extends RThreadInfo {


    ////////////
    // FIELDS //
    ////////////

    protected int iterationsStarted;
    protected int iterationsCompleted;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ContinuousThreadInfo(Thread thread, StackTraceElement[] trace, int maxStackTraceElements) {
        super(thread, trace, maxStackTraceElements);
    }

    @Override
    public void updateFrom(Thread thread, StackTraceElement[] trace, int maxStackTraceElements) {
        super.updateFrom(thread, trace, maxStackTraceElements);
        iterationsStarted = ((ContinuousThread) thread).iterationsStarted;
        iterationsCompleted = ((ContinuousThread) thread).iterationsCompleted;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public int getIterationsStarted() {
        return iterationsStarted;
    }
    public int getIterationsCompleted() {
        return iterationsCompleted;
    }
}
