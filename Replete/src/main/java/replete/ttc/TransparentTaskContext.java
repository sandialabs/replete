package replete.ttc;

import javax.swing.event.ChangeListener;

import replete.event.ProgressListener;
import replete.progress.ProgressMessage;

public interface TransparentTaskContext {

    // Accessors
    boolean canPause();
    boolean canStop();
    boolean canNotify();
    boolean isPaused();
    boolean isStopped();
    boolean isPauseRequested();
    boolean isStopRequested();

    // Mutators
    void setCanPause(boolean canPause);
    void setCanStop(boolean canStop);
    void setCanNotify(boolean canNotify);
    void clearPauseRequested();
    void clearStopRequested();

    // Actions
    void pause();
    void unpause();
    void stopContext();    // Named this instead of stop due to conflict with Thread.stop() in TransparentThreadContextThread

    // Client
    void checkPause();
    void checkStop() throws TransparentTaskStopException;
    void checkPauseAndStop() throws TransparentTaskStopException;
    void publishProgress(ProgressMessage pm);

    // Notifications
    void addPauseRequestedListener(ChangeListener listener);
    void removePauseRequestedListener(ChangeListener listener);
    // --
    void addStopRequestedListener(ChangeListener listener);
    void removeStopRequestedListener(ChangeListener listener);
    // --
    void addPauseListener(ChangeListener listener);
    void removePauseListener(ChangeListener listener);
    // --
    void addStopListener(ChangeListener listener);
    void removeStopListener(ChangeListener listener);
    // --
    void addProgressListener(ProgressListener listener);     // Need generic, source-less PL to unify API's
    void removeProgressListener(ProgressListener listener);
}
