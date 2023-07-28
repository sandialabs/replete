package replete.threads;

public abstract class ConsumerThread<T> extends ContinuousThread {


    ////////////
    // FIELDS //
    ////////////

    // For targetWork, null always means a lack of work.  As written,
    // this class cannot handle null values being the actual target of
    // the work performed.  Additionally, if the producer returns a null
    // value, that value is construed to indicate that this consuming
    // thread should check its run iteration conditions again. It is most
    // likely the sign of a pause/stop/disable having been signaled on
    // both the thread and the producer's delegates.

    protected T targetWork;
    protected TargetWorkProducer<T> producer;
    private ConsumingStrategy<T> consumingStrategy;
    protected boolean producerPauseRequested = false;
    protected boolean producerStopRequested = false;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ConsumerThread(String name) {
        this(name, null);
    }
    public ConsumerThread(String name, TargetWorkProducer<T> producer) {
        super(name);
        consumingStrategy = new TransparentWaitPauseStopConsumingStrategy<T>(this);
        setProducer(producer);
    }


    /////////
    // RUN //
    /////////

    @Override
    protected void runIteration() {
        performWork(targetWork);
    }

    protected abstract void performWork(T workOn);

    @Override
    protected void checkConditions() {
        targetWork = null;
        while(true) {                            // Continue while paused or producer not producing anything
            P.block("checkPauseAndStop");
            try {
                checkPauseAndStop();
            } finally {
                P.end();
            }
            if(checkProducerForWork()) {
                break;
            }
        }
    }

    protected boolean checkProducerForWork() {
        targetWork = consumingStrategy.produceWork();
        if(targetWork != null) {
            return true;
        }
        return false;       // No work on which to work, pause or stop activated
    }


    /////////
    // TTC //
    /////////

    @Override
    public synchronized void pause() {
        ttContext.pause();
        producerPauseRequested = true;
        synchronized(producer) {
            producer.notifyAll();
        }
    }
    @Override
    public synchronized void unpause() {
        ttContext.unpause();
        producerPauseRequested = false;
        synchronized(producer) {
            producer.notifyAll();
        }
    }
    @Override
    public synchronized void stopContext() {
        ttContext.stopContext();
        producerStopRequested = true;
        synchronized(producer) {
            producer.notifyAll();
        }
    }
    @Override
    public synchronized void clearPauseRequested() {
        ttContext.clearPauseRequested();
        producerPauseRequested = false;
        synchronized(producer) {
            producer.notifyAll();
        }
    }
    @Override
    public synchronized void clearStopRequested() {
        ttContext.clearStopRequested();
        producerStopRequested = false;
        synchronized(producer) {
            producer.notifyAll();
        }
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public T getTargetWork() {
        return targetWork;
    }
    public TargetWorkProducer<T> getProducer() {
        return producer;
    }
    public ConsumingStrategy<T> getConsumingStrategy() {
        return consumingStrategy;
    }

    // Accessors (Computed)

    @Override
    public ConsumerThreadInfo getInfo(StackTraceElement[] trace) {
        return new ConsumerThreadInfo(this, trace, ThreadStats.ELEMENT_MAX);
    }

    // Mutator

    public void setProducer(TargetWorkProducer<T> producer) {
        this.producer = producer;
        consumingStrategy.setProducer(producer);
    }
    public ConsumerThread setConsumingStrategy(ConsumingStrategy<T> consumingStrategy) {
        this.consumingStrategy = consumingStrategy;
        consumingStrategy.setProducer(producer);
        return this;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        TestThread t = new TestThread(1);
        t.start();
        TestThread t2 = new TestThread(2);
        t2.start();
        ThreadUtil.join(t);
//        ThreadUtil.join(t2);

        System.out.println("Alive:        " + t.isAlive());
        System.out.println("Start Run:    " + t.getStartRun());
        System.out.println("End Run:      " + t.getEndRun());
        System.out.println("It Started:   " + t.getIterationsStarted());
        System.out.println("It Completed: " + t.getIterationsCompleted());
        System.out.println("Target Work:  " + t.getTargetWork());
        System.out.println(t.getProfilerState());

        System.out.println("Alive:        " + t2.isAlive());
        System.out.println("Start Run:    " + t2.getStartRun());
        System.out.println("End Run:      " + t2.getEndRun());
        System.out.println("It Started:   " + t2.getIterationsStarted());
        System.out.println("It Completed: " + t2.getIterationsCompleted());
        System.out.println("Target Work:  " + t2.getTargetWork());
        System.out.println(t2.getProfilerState());
    }

    private static class TestThread extends ConsumerThread<String> {
        private TestThread(int i) {
            super("TT:" + i);
        }
        @Override
        protected void performWork(String work) {
            ThreadUtil.sleep(2000);
            System.out.println(getName() + "/" + getIterationsStarted());
            if(getIterationsStarted() == 6 && getName().equals("TT:1")) {
                stopContext();
            }
            if(getIterationsStarted() == 4 && getName().equals("TT:2")) {
                pause();
            }
        }
    }
}
