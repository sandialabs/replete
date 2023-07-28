package replete.ttc;

import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.event.ExtChangeNotifier;
import replete.event.ProgressEvent;
import replete.event.ProgressListener;
import replete.progress.ProgressMessage;

public class TransparentThreadContextThread extends Thread
        implements TransparentTaskContext {


    ///////////
    // FIELD //
    ///////////

    protected TransparentTaskContext ttContext = new DefaultTransparentTaskContext();


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

    public synchronized void setTransparentTaskContext(TransparentTaskContext newTtc) {

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
            fireProgressNotifier(e.getMessages());
        }
    };


    protected ExtChangeNotifier<ProgressListener> progressNotifier = new ExtChangeNotifier<>();
    public void addProgressListener(ProgressListener listener) {
        progressNotifier.addListener(listener);
    }
    public void removeProgressListener(ProgressListener listener) {
        progressNotifier.removeListener(listener);
    }
    protected void fireProgressNotifier(List<ProgressMessage> msgs) {
        progressNotifier.fireStateChanged(new ProgressEvent(msgs));
    }

    protected ChangeNotifier pauseRequestedNotifier = new ChangeNotifier(this);
    public void addPauseRequestedListener(ChangeListener listener) {
        pauseRequestedNotifier.addListener(listener);
    }
    public void removePauseRequestedListener(ChangeListener listener) {
        pauseRequestedNotifier.removeListener(listener);
    }
    protected void firePauseRequestedNotifier() {
        pauseRequestedNotifier.fireStateChanged();
    }

    protected ChangeNotifier stopRequestedNotifier = new ChangeNotifier(this);
    public void addStopRequestedListener(ChangeListener listener) {
        stopRequestedNotifier.addListener(listener);
    }
    public void removeStopRequestedListener(ChangeListener listener) {
        stopRequestedNotifier.removeListener(listener);
    }
    protected void fireStopRequestedNotifier() {
        stopRequestedNotifier.fireStateChanged();
    }

    protected ChangeNotifier pauseNotifier = new ChangeNotifier(this);
    public void addPauseListener(ChangeListener listener) {
        pauseNotifier.addListener(listener);
    }
    public void removePauseListener(ChangeListener listener) {
        pauseNotifier.removeListener(listener);
    }
    protected void firePauseNotifier() {
        pauseNotifier.fireStateChanged();
    }

    protected ChangeNotifier stopNotifier = new ChangeNotifier(this);
    public void addStopListener(ChangeListener listener) {
        stopNotifier.addListener(listener);
    }
    public void removeStopListener(ChangeListener listener) {
        stopNotifier.removeListener(listener);
    }
    protected void fireStopNotifier() {
        stopNotifier.fireStateChanged();
    }
}
