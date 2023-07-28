package replete.threads;

import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.event.ExtChangeNotifier;
import replete.event.ProgressEvent;
import replete.event.ProgressListener;
import replete.profiler.RProfiler;
import replete.progress.ProgressMessage;
import replete.ttc.DefaultTransparentTaskContext;
import replete.ttc.TransparentTaskContext;
import replete.ttc.TransparentTaskStopException;

public class RThread extends Thread implements TransparentTaskContext {


    ////////////
    // FIELDS //
    ////////////

    // Static

//    private static List<RThread> allThreads = new ArrayList<>();    // Might change to include groups; commented out due to memory concerns

    protected long startRun = 0;
    protected long endRun = 0;
    protected Exception uncaughtException;

    protected RProfiler P;
    protected TransparentTaskContext ttContext;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RThread(String name) {
        super(name);
        init();
    }

    private void init() {
//        synchronized(allThreads) {
//            allThreads.add(this);      // Might change to include groups
//        }
        ttContext = new DefaultTransparentTaskContext(true, true, true);
        ttContext.addPauseRequestedListener(pauseRequestedListener);
        ttContext.addStopRequestedListener(stopRequestedListener);
        ttContext.addPauseListener(pauseListener);
        ttContext.addStopListener(stopListener);
        ttContext.addProgressListener(progressListener);
    }

//    public static List<RThread> getAllThreads() {
//        synchronized(allThreads) {
//            return new ArrayList<>(allThreads);
//        }
//    }


    /////////
    // RUN //
    /////////

    @Override
    public final void run() {
        P = RProfiler.get(getName() + " Profiler");
        startRun = RProfiler.now();
        try {
            P.block("Run (Inner)");
            try {
                runInner();
            } finally {
                P.end();
            }
        } catch(Exception e) {
            uncaughtException = e;
            boolean throwUpwards = true;
            if(handlesUncaughtException()) {
                P.block("Handle Uncaught Exception");
                try {
                    throwUpwards = handleUncaughtException(e);
                } finally {
                    P.end();
                }
            }
            if(throwUpwards) {
                throw e;
            }
        } finally {
            endRun = RProfiler.now();
            P.close();
        }
    }

    protected void runInner() {
        // Override this method
    }

    protected boolean handlesUncaughtException() {
        return false;
    }
    protected boolean handleUncaughtException(Exception e) {
        return true;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public Exception getUncaughtException() {
        return uncaughtException;
    }
    public long getStartRun() {
        return startRun;
    }
    public long getEndRun() {
        return endRun;
    }

    // Accessors (Computed)

    public String getProfilerState() {
        return P == null ? null : P.getTreeString();
    }
    public RThreadInfo getInfo(StackTraceElement[] trace) {
        return new RThreadInfo(this, trace, ThreadStats.ELEMENT_MAX);
    }

    // Mutators

    //?


    //////////////////////////////
    // TRANSPARENT TASK CONTEXT //
    //////////////////////////////


    //////////////////
    // PAUSE & STOP //
    //////////////////

    public void pause() {
        ttContext.pause();
    }
    public void unpause() {
        ttContext.unpause();
    }
    public void stopContext() {
        ttContext.stopContext();
    }
    public boolean isPaused() {
        return ttContext.isPaused();
    }
    public boolean isStopped() {
        return ttContext.isStopped();
    }
    public boolean isPauseRequested() {
        return ttContext.isPauseRequested();
    }
    public boolean isStopRequested() {
        return ttContext.isStopRequested();
    }
    public boolean canPause() {
        return ttContext.canPause();
    }
    public boolean canStop() {
        return ttContext.canStop();
    }
    public boolean canNotify() {
        return ttContext.canNotify();
    }

    public void setCanPause(boolean canPause) {
        ttContext.setCanPause(canPause);
    }
    public void setCanStop(boolean canStop) {
        ttContext.setCanStop(canStop);
    }
    public void setCanNotify(boolean canNotify) {
        ttContext.setCanNotify(canNotify);
    }
    public void clearPauseRequested() {
        ttContext.clearPauseRequested();
    }
    public void clearStopRequested() {
        ttContext.clearStopRequested();
    }

    // Client-processes to call
    @Override
    public void checkPause() {
        ttContext.checkPause();
    }
    @Override
    public void checkStop() throws TransparentTaskStopException {
        ttContext.checkStop();
    }
    @Override
    public void checkPauseAndStop() throws TransparentTaskStopException {
        ttContext.checkPauseAndStop();
    }
    @Override
    public void publishProgress(ProgressMessage pm) {
        ttContext.publishProgress(pm);
    }

    private ProgressListener progressListener = new ProgressListener() {
        public void stateChanged(ProgressEvent e) {
            fireProgressNotifier(e.getMessages());
        }
    };
    private ChangeListener pauseRequestedListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            firePauseRequestedNotifier();
        }
    };
    private ChangeListener stopRequestedListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            fireStopRequestedNotifier();
        }
    };
    private ChangeListener pauseListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            firePauseNotifier();
        }
    };
    private ChangeListener stopListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            fireStopNotifier();
        }
    };


    ///////////////
    // NOTIFIERS //
    ///////////////

    private ExtChangeNotifier<ProgressListener> progressNotifier = new ExtChangeNotifier<>();
    public void addProgressListener(ProgressListener listener) {
        progressNotifier.addListener(listener);
    }
    public void removeProgressListener(ProgressListener listener) {
        progressNotifier.removeListener(listener);
    }
    private void fireProgressNotifier(List<ProgressMessage> msgs) {
        progressNotifier.fireStateChanged(new ProgressEvent(msgs));
    }

    private ChangeNotifier pauseRequestedNotifier = new ChangeNotifier(this);
    public void addPauseRequestedListener(ChangeListener listener) {
        pauseRequestedNotifier.addListener(listener);
    }
    public void removePauseRequestedListener(ChangeListener listener) {
        pauseRequestedNotifier.removeListener(listener);
    }
    private void firePauseRequestedNotifier() {
        pauseRequestedNotifier.fireStateChanged();
    }

    private ChangeNotifier stopRequestedNotifier = new ChangeNotifier(this);
    public void addStopRequestedListener(ChangeListener listener) {
        stopRequestedNotifier.addListener(listener);
    }
    public void removeStopRequestedListener(ChangeListener listener) {
        stopRequestedNotifier.removeListener(listener);
    }
    private void fireStopRequestedNotifier() {
        stopRequestedNotifier.fireStateChanged();
    }

    private ChangeNotifier pauseNotifier = new ChangeNotifier(this);
    public void addPauseListener(ChangeListener listener) {
        pauseNotifier.addListener(listener);
    }
    public void removePauseListener(ChangeListener listener) {
        pauseNotifier.removeListener(listener);
    }
    private void firePauseNotifier() {
        pauseNotifier.fireStateChanged();
    }

    private ChangeNotifier stopNotifier = new ChangeNotifier(this);
    public void addStopListener(ChangeListener listener) {
        stopNotifier.addListener(listener);
    }
    public void removeStopListener(ChangeListener listener) {
        stopNotifier.removeListener(listener);
    }
    private void fireStopNotifier() {
        stopNotifier.fireStateChanged();
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
        ThreadUtil.join(t2);

        System.out.println("Alive:     " + t.isAlive());
        System.out.println("Start Run: " + t.getStartRun());
        System.out.println("End Run:   " + t.getEndRun());
        System.out.println(t.getProfilerState());

        System.out.println("Alive:     " + t2.isAlive());
        System.out.println("Start Run: " + t2.getStartRun());
        System.out.println("End Run:   " + t2.getEndRun());
        System.out.println(t2.getProfilerState());

//        for(RThread th : RThread.getAllThreads()) {
//            System.out.println(th + " " + th.hashCode());
//        }
    }

    private static class TestThread extends RThread {
        private TestThread(int id) {
            super("TT:" + id);
        }
        @Override
        protected void runInner() {
            P.block("A");
            ThreadUtil.sleep(1000);
            P.end();

            P.block("B");
            ThreadUtil.sleep(2000);
            P.end();

            throw new RuntimeException("test");
        }
        @Override
        protected boolean handlesUncaughtException() {
            return true;
        }
        @Override
        protected boolean handleUncaughtException(Exception e) {
            System.err.println("WHAT?");
            e.printStackTrace();
            return false;
        }
    }
}
