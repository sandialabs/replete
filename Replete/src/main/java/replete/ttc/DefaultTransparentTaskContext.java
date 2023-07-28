package replete.ttc;

import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.event.ExtChangeNotifier;
import replete.event.ProgressEvent;
import replete.event.ProgressListener;
import replete.progress.ProgressMessage;

public class DefaultTransparentTaskContext implements TransparentTaskContext {


    ////////////
    // FIELDS //
    ////////////

    // Configuration
    private boolean canPause = false;       // Initially assumed task can't pause or stop
    private boolean canStop = false;
    private boolean canNotify = true;       // Initially progress notifications enabled

    // Request Flags (Thread Synchronization)
    protected boolean pauseRequested = false;
    protected boolean stopRequested = false;

    // State
    protected boolean paused = false;
    protected boolean stopped = false;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public DefaultTransparentTaskContext() {}       // Can use builder mutators for this one

    public DefaultTransparentTaskContext(boolean canPause, boolean canStop, boolean canNotify) {
        this.canPause = canPause;
        this.canStop = canStop;
        this.canNotify = canNotify;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean canPause() {
        return canPause;
    }
    public boolean canStop() {
        return canStop;
    }
    public boolean canNotify() {
        return canNotify;
    }
    public boolean isPaused() {
        return paused;
    }
    public boolean isStopped() {
        return stopped;
    }
    public boolean isPauseRequested() {
        return pauseRequested;
    }
    public boolean isStopRequested() {
        return stopRequested;
    }

    // Mutators

    public synchronized void setCanPause(boolean canPause) {
        this.canPause = canPause;
        if(!this.canPause && pauseRequested && !paused) {
            pauseRequested = false;
            firePauseRequestedNotifier();
        }
    }
    public synchronized void setCanStop(boolean canStop) {
        this.canStop = canStop;
        if(!this.canStop && stopRequested && !stopped) {
            stopRequested = false;
            fireStopRequestedNotifier();
        }
    }
    public void setCanNotify(boolean canNotify) {
        this.canNotify = canNotify;
    }
    public synchronized void clearPauseRequested() {
        if(!paused) {
            pauseRequested = false;     // Clear pause request before pause is executed.
            firePauseRequestedNotifier();
        }
    }
    public synchronized void clearStopRequested() {
        if(!stopped) {
            stopRequested = false;      // Clear stop request before stop is executed.
            fireStopRequestedNotifier();
        }
    }


    //////////////////
    // PAUSE & STOP //
    //////////////////

    public synchronized void pause() {
        if(canPause) {
            pauseRequested = true;
            firePauseRequestedNotifier();
        }
    }

    public synchronized void unpause() {
        pauseRequested = false;
        firePauseRequestedNotifier();
        notifyAll();
    }

    public synchronized void stopContext() {
        if(canStop) {
            stopRequested = true;
            fireStopRequestedNotifier();
            notifyAll();
        }
    }


    ////////////////////////////
    // CLIENT-PROCESS METHODS //
    ////////////////////////////

    // Checking

    public synchronized void checkPause() {
        if(canPause) {
            while(pauseRequested && !stopRequested) {

                // Paused.
                paused = true;
                firePauseNotifier();

                try {
                    wait();
                } catch(Exception e) {
                }
            }

            // No longer paused
            paused = false;
            pauseRequested = false;       // In case loop stops above due to stop.
            firePauseNotifier();
            firePauseRequestedNotifier();
        }
    }

    public synchronized void checkStop() throws TransparentTaskStopException {
        if(canStop) {
            if(stopRequested) {
                stopped = true;
                stopRequested = false;    // Reset request flag.
                fireStopNotifier();
                fireStopRequestedNotifier();
                throw new TransparentTaskStopException();
            }
        }
    }

    public synchronized void checkPauseAndStop() throws TransparentTaskStopException {
        checkPause();
        checkStop();
    }

    // Progress

    public void publishProgress(ProgressMessage pm) {
        if(canNotify) {
            fireProgressNotifier(pm);
        }
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

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

    private ExtChangeNotifier<ProgressListener> progressNotifier = new ExtChangeNotifier<>();
    public void addProgressListener(ProgressListener listener) {
        progressNotifier.addListener(listener);
    }
    public void removeProgressListener(ProgressListener listener) {
        progressNotifier.removeListener(listener);
    }
    private void fireProgressNotifier(ProgressMessage message) {
        progressNotifier.fireStateChanged(new ProgressEvent(message));
    }
}
