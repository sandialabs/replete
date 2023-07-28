package replete.parallel;

public class ExecuteParameters {


    ////////////
    // FIELDS //
    ////////////

    public static final int DEFAULT_THREADS = 20;

    private int threads = DEFAULT_THREADS;
    private int maxSimulSubmitted = Integer.MAX_VALUE;
    private DoneListener<?> doneListener;   // Needs identifier like P or run #...
    private int pollDelay = 1000;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ExecuteParameters() {}

    // Alternate instantiation
    public static ExecuteParameters create() {
        return new ExecuteParameters();
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public int getThreads() {
        return threads;
    }
    public int getMaxSimulSubmitted() {
        return maxSimulSubmitted;
    }
    public DoneListener<?> getDoneListener() {
        return doneListener;
    }
    public int getPollDelay() {
        return pollDelay;
    }

    // Mutators

    public ExecuteParameters setThreads(int t) {
        threads = t;
        return this;
    }
    public ExecuteParameters setMaxSimulSubmitted(int m) {
        maxSimulSubmitted = m;
        return this;
    }
    public ExecuteParameters setDoneListener(DoneListener<?> doneListener) {
        this.doneListener = doneListener;
        return this;
    }
    public ExecuteParameters setPollDelay(int pollDelay) {
        this.pollDelay = pollDelay;
        return this;
    }
}
