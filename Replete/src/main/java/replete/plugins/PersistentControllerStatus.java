package replete.plugins;

import java.io.Serializable;

public class PersistentControllerStatus implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    private String className;
    private boolean started;
    private boolean paused;
    private boolean disposed;             // Shouldn't really ever be true in normal use.
    private boolean pauseable;
    private boolean resettable;
    private boolean updatable;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PersistentControllerStatus() {
        // So mutators in subclasses can be used manually
    }
    public PersistentControllerStatus(PersistentController controller) {
        className  = controller.getClass().getName();
        started    = controller.isStarted();
        paused     = controller.isPaused();
        disposed   = controller.isDisposed();
        pauseable  = controller.isPauseable();
        resettable = controller.isResettable();
        updatable  = controller.isUpdatable();
    }
    public PersistentControllerStatus(PersistentControllerStatus status) {
        className  = status.className;
        started    = status.started;
        paused     = status.paused;
        disposed   = status.disposed;
        pauseable  = status.pauseable;
        resettable = status.resettable;
        updatable  = status.updatable;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getClassName() {
        return className;
    }
    public boolean isStarted() {
        return started;
    }
    public boolean isPaused() {
        return paused;
    }
    public boolean isDisposed() {
        return disposed;
    }
    public boolean isPauseable() {
        return pauseable;
    }
    public boolean isResettable() {
        return resettable;
    }
    public boolean isUpdatable() {
        return updatable;
    }
}
