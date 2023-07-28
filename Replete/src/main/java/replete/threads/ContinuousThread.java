package replete.threads;

import replete.ttc.TransparentTaskStopException;

public abstract class ContinuousThread extends RThread {


    ////////////
    // FIELDS //
    ////////////

    protected int iterationsStarted;
    protected int iterationsCompleted;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ContinuousThread(String name) {
        super(name);
    }


    /////////
    // RUN //
    /////////

    @Override
    protected final void runInner() {
        try {
            while(true) {
                iterationsStarted++;
                P.block("Continuous Loop Iteration");
                try {
                    checkConditionsAndRunIteration();
                } finally {
                    P.end();
                }
                iterationsCompleted++;
            }
        } catch(TransparentTaskStopException e) {
            if(handlesStop()) {
                P.block("Handle Stop");
                try {
                    handleStop();
                } finally {
                    P.end();
                }
            }
        }
    }

    private void checkConditionsAndRunIteration() {
        P.block("checkConditions");
        try {
            checkConditions();
        } finally {
            P.end();
        }

        P.block("runIteration");
        try {
            runIteration();
        } finally {
            P.end();
        }
    }

    protected void checkConditions() {
        P.block("checkPauseAndStop");
        try {
            checkPauseAndStop();
        } finally {
            P.end();
        }
    }

    protected abstract void runIteration();

    protected boolean handlesStop() {
        return false;
    }
    protected void handleStop() {

    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getIterationsStarted() {
        return iterationsStarted;
    }
    public int getIterationsCompleted() {
        return iterationsCompleted;
    }
    @Override
    public ContinuousThreadInfo getInfo(StackTraceElement[] trace) {
        return new ContinuousThreadInfo(this, trace, ThreadStats.ELEMENT_MAX);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        TestThread t = new TestThread(1);
        t.start();
//        TestThread t2 = new TestThread(2);
//        t2.start();
        ThreadUtil.join(t);
//        ThreadUtil.join(t2);

        System.out.println("Alive:        " + t.isAlive());
        System.out.println("Start Run:    " + t.getStartRun());
        System.out.println("End Run:      " + t.getEndRun());
        System.out.println("It Started:   " + t.getIterationsStarted());
        System.out.println("It Completed: " + t.getIterationsCompleted());
        System.out.println(t.getProfilerState());

//        System.out.println("Alive:        " + t2.isAlive());
//        System.out.println("Start Run:    " + t2.getStartRun());
//        System.out.println("End Run:      " + t2.getEndRun());
//        System.out.println("It Started:   " + t2.getIterationsStarted());
//        System.out.println("It Completed: " + t2.getIterationsCompleted());
//        System.out.println(t2.getProfilerState());
    }

    private static class TestThread extends ContinuousThread {
        private TestThread(int i) {
            super("TT:" + i);
        }
        @Override
        protected void runIteration() {
            ThreadUtil.sleep(1000);
            System.out.println(getName() + "/" + getIterationsStarted());
            if(getIterationsStarted() == 3 && getName().equals("TT:1")) {
                stopContext();
            }
//            if(getIterationsStarted() == 4 && getName().equals("TT:2")) {
//                pause();
//            }
        }
        @Override
        protected boolean handlesStop() {
            return true;
        }
        @Override
        protected void handleStop() {
            System.out.println("Stopped: " + getIterationsStarted() + "/" + getIterationsCompleted());
        }
    }
}
