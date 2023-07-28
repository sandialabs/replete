package replete.ui.worker;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.event.ExtChangeNotifier;
import replete.event.ProgressEvent;
import replete.event.ProgressListener;
import replete.extensions.ui.SwingWorkerChangedModifiers1_7_0_45;
import replete.progress.ProgressMessage;
import replete.ttc.DefaultTransparentTaskContext;
import replete.ttc.TransparentTaskContext;
import replete.ttc.TransparentTaskStopException;
import replete.ui.GuiUtil;
import replete.ui.worker.events.RWorkerStatusEvent;
import replete.ui.worker.events.RWorkerStatusListener;

// TODO:
//  - Consider whether ProgressMessage for the progress type is sufficient.
//    How do you provide a payload object along side the progress message?
//  - Comment more
//  - Review CommonThread and UIAction stuff and remove both if appropriate.
//  - Consider how SwingWorker's default canceled/interrupted method works
//    with this class (incl. Interrupted exception).  How is this related
//    to the TTC's stopping mechanism?  Could the TTC include a check to
//    the thread's interrupted state?  See PerfDec ImageLibraryLoader to see
//    how that all works again.
//  - Consider having a use mouse spinner boolean per RWorker
//  - Consider how a UI effectively manages the disabling/reenabling
//    of certain controls when a given task is active.
//  - Consider an RWorkerManager/Factory to help manage all the
//    RWorkers in a UI.
//  - also remember that an rworker might not have an associated prog bar
//  - consider a dependency graph like structure that allows some actions
//    to block other actions
//  - consider how locking works when various multiple rworkers are
//    operating on different parts of the same model.  are there any
//    facilities that RWorker framework could provide?
//  - allow NotificationTabbedPane to be "docked" into the status bar
//  - consider case when there is no "background" value to "get" but you
//    still want to be able to take action upon error.  Right now you
//    have to do this:
//        @Override
//        protected void complete() {
//            try {
//                getResult();
//
//            } catch(ExecutionException e) {
//                Dialogs.showDetails(uiController.getParentFrame(),
//                    "An error has occurred.", name, e.getCause());
//
//            } catch(Exception e) {
//                Dialogs.showDetails(uiController.getParentFrame(),
//                    "An error has occurred.", name, e);
//            }
//        }

public abstract class RWorker<Tgath, Tres>
                extends SwingWorkerChangedModifiers1_7_0_45<Tres, ProgressMessage>
                implements TransparentTaskContext {


    ////////////
    // FIELDS //
    ////////////

    protected RWorkerStatus status = null;
    protected Tgath gathered = null;
    protected boolean canProceed = false;
    protected Exception error;
    protected TransparentTaskContext ttContext;
    // Doing so will not only allow pause methods in this
    // class to work, but can indicate to UI components
    // managing UI actions to respond accordingly to the
    // action's pause-ability.
    // Doing so will not only allow the stop methods in this
    // class to work, but can indicate to UI components managing
    // UI actions to respond accordingly to the action's
    // stop-ability.
    protected long backgroundStarted;
    protected long backgroundEnded;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RWorker() {
        this(false, false);
    }
    public RWorker(boolean canPause, boolean canStop) {   // Can provide some initial values for a default TTC.
        this(new DefaultTransparentTaskContext(canPause, canStop, true));
    }
    public RWorker(TransparentTaskContext ttc) {
        setTransparentTaskContext(ttc);
        changeStatus(RWorkerStatus.INITIALIZED);
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public RWorkerStatus getStatus() {
        return status;
    }
    public Tgath getGathered() {
        return gathered;
    }
    public boolean canProceed() {
        return canProceed;
    }
    public Exception getError() {
        return error;
    }
    public long getBackgroundStarted() {
        return backgroundStarted;
    }
    public long getBackgroundEnded() {
        return backgroundEnded;
    }
    public String getTitle() {              // Meant to be overridden
        return null;
    }

    // Accessors (Computed)

    public long getBackgroundDuration() {
        if(backgroundStarted == 0) {
            return 0;
        }
        if(backgroundEnded == 0) {
            return System.currentTimeMillis() - backgroundStarted;
        }
        return backgroundEnded - backgroundStarted;
    }


    /////////////////
    // NEW METHODS //
    /////////////////

    // Subclasses should override this method if they want
    // to ask the user for information on the UI thread.
    // Dialog boxes are prime examples of what this method
    // will use to collect this information.  By default
    // this method returns null.
    protected Tgath gather() {
        return null;
    }

    // Subclasses should override this method to determine
    // whether or not execute should be called given the
    // input provided by the user in the gather step.
    // By default this method returns true.
    protected boolean proceed(Tgath gathered) {
        return true;
    }

    // If the user will not be proceeding based on the
    // input gathered from them, subclasses can override this
    // method to perform additional actions at that point.
    protected void declined() {}

    // This method replaces the doInBackground method
    // with regard to performing the background task.
    // Subclasses should override this method instead
    // of doInBackground to accomplish their long-running
    // background task.  This method still throws Exception
    // and 'this' (RWorkerContext) may be passed to methods
    // to have access to pause/stop/progress functionality.
    // This method will be called not on the UI thread but
    // on a worker thread.  This method is optional like
    // all of the rest.
    protected Tres background(Tgath gathered) throws Exception {
        return null;
    }

    // This method replaces the 'done' method.  Subclasses
    // should override this method instead of done to
    // perform additional operations on the UI thread after
    // the background task has completed.  Call the getResult
    // method to access the value produced by the background
    // task.
    protected void complete() {}

    // This method replaces the get method merely to capture
    // all exceptions from the background task at the right
    // layer in the API.  Calling get would simply not
    // record the error within the RWorker framework.
    // Ironically final just to remove confusion.  This method
    // also makes the stop exception a little more usable.
    public final Tres getResult() throws InterruptedException, ExecutionException, TransparentTaskStopException {
        try {
            return super.get();

        } catch(InterruptedException e) {  // How can this happen again?
            error = e;
            throw e;

        } catch(ExecutionException e) {

            // Convenience transfer
            if(e.getCause() instanceof TransparentTaskStopException) {
                error = (TransparentTaskStopException) e.getCause();
                throw (TransparentTaskStopException) e.getCause();
            }

            error = e;
            throw e;
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // Override standard implementation of execute so we
    // can extend its functionality.
    @Override
    public final void execute() {    // Ironically marking as final here to prevent confusion!

        // Ensure the following code is executed on the UI
        // thread.  All methods should be executed on the UI
        // thread except for doInBackground.
        GuiUtil.safe(new Runnable() {
            public void run() {

                // Change status to gather and perform the
                // gather operation.  Default implementation
                // returns null.
                changeStatus(RWorkerStatus.GATHER);
                gathered = gather();

                // Just in case stop was requested during the
                // gather step, stop the worker.  There may
                // be a gathered value, canProceed will be false,
                // stopRequested will be true, stopped will be
                // true, pause will be false, pauseRequested
                // could be either true or false, and error
                // will be null.  Status will go from
                //    INITIALIZED -> GATHER -> FINISHED
                // in this scenario
                try {
                    ttContext.checkStop();
                } catch(TransparentTaskStopException e) {
                    // Change status to finished and quit this method.
                    changeStatus(RWorkerStatus.PRE_FINISHED);
                    changeStatus(RWorkerStatus.FINISHED);
                    return;
                }

                // Change status to proceed and check the input
                // gathered from the user.
                changeStatus(RWorkerStatus.PROCEED);
                if(proceed(gathered)) {

                    // Mark that gathered input is valid and we will
                    // proceed with execution.
                    canProceed = true;

                    // Change status to execute and invoke the base
                    // class's version of the method to properly
                    // pass execution onto the background thread.
                    changeStatus(RWorkerStatus.EXECUTE);
                    RWorker.super.execute();

                    // Other mechanisms will handle status changes
                    // from here on.

                // If the proceed method did not pass, change the
                // status to declined and call the declined method.
                // This is still all happening on the UI thread.
                } else {
                    changeStatus(RWorkerStatus.DECLINED);
                    try {
                        declined();
                    } catch(Exception e) {
                        error = e;
                        throw e;
                    } finally {
                        changeStatus(RWorkerStatus.PRE_FINISHED);
                        changeStatus(RWorkerStatus.FINISHED);
                    }
                }
            }
        });
    }

    // Comment this
    @Override
    protected final Tres doInBackground() throws Exception {
        Tres result;
        try {
            backgroundStarted = System.currentTimeMillis();
            changeStatus(RWorkerStatus.BACKGROUND);
            result = background(gathered);
        } finally {
            backgroundEnded = System.currentTimeMillis();
        }
        return result;
    }

    // Comment this
    @Override
    protected final void process(List<ProgressMessage> messages) {
        fireProgressNotifier(messages);
        processProgress(messages);
    }

    // New method to override if you want to handle progress in this class
    // on the UI thread.
    protected void processProgress(List<ProgressMessage> messages) {

    }

    // This method is overridden to provide for status changes given
    // that the execute method has been executed.
    @Override
    protected final void done() {

        // Change the status to done and invoke the complete
        // method which subclasses should have overridden.
        changeStatus(RWorkerStatus.DONE);
        try {
            complete();

        } catch(Exception e) {
            error = e;
            throw e;

        } finally {
            changeStatus(RWorkerStatus.PRE_FINISHED);
            changeStatus(RWorkerStatus.FINISHED);
        }
    }


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

    public void setTransparentTaskContext(TransparentTaskContext newTtc) {

        // Unsubscribe
        if(ttContext != null) {
            ttContext.removePauseRequestedListener(pauseRequestedListener);
            ttContext.removeStopRequestedListener(stopRequestedListener);
            ttContext.removePauseListener(pauseListener);
            ttContext.removeStopListener(stopListener);
            ttContext.removeProgressListener(progressListener);
        }

        ttContext = newTtc;

        // Subscribe
        ttContext.addPauseRequestedListener(pauseRequestedListener);
        ttContext.addStopRequestedListener(stopRequestedListener);
        ttContext.addPauseListener(pauseListener);
        ttContext.addStopListener(stopListener);
        ttContext.addProgressListener(progressListener);
    }

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
    private ProgressListener progressListener = new ProgressListener() {
        public void stateChanged(ProgressEvent e) {
            publish(e.getMessage());
        }
    };


    //////////
    // MISC //
    //////////

    private void changeStatus(final RWorkerStatus status) {
        final RWorkerStatus previous = this.status;
        this.status = status;

        GuiUtil.safeSync(new Runnable() {
            public void run() {
                fireStatusNotifier(previous, status);
            }
        });
    }

    // Print the internal state and configuration of
    // this RWorker.
    public void printState() {
        System.out.println("status            = " + status);
        System.out.println("gathered          = " + gathered);
        System.out.println("canProceed        = " + canProceed);
        System.out.println("paused            = " + ttContext.isPaused());
        System.out.println("stopped           = " + ttContext.isStopped());
        System.out.println("pauseRequested    = " + ttContext.isPauseRequested());
        System.out.println("stopRequested     = " + ttContext.isStopRequested());
        System.out.println("error             = " + error);
        System.out.println("canPause          = " + canPause());
        System.out.println("canStop           = " + canStop());
        System.out.println("backgroundStarted = " + backgroundStarted);
        System.out.println("backgroundEnded   = " + backgroundEnded);
        System.out.println();
    }


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

    private ExtChangeNotifier<RWorkerStatusListener> statusNotifier = new ExtChangeNotifier<>();
    public void addStatusListener(RWorkerStatusListener listener) {
        statusNotifier.addListener(listener);
    }
    public void removeStatusListener(RWorkerStatusListener listener) {
        statusNotifier.removeListener(listener);
    }
    private void fireStatusNotifier(RWorkerStatus previous, RWorkerStatus current) {
        statusNotifier.fireStateChanged(new RWorkerStatusEvent(this, previous, current));
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
}
