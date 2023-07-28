package replete.plugins;

import org.apache.log4j.Logger;

import replete.logging.LogCode;
import replete.logging.LogCodeDynamic;
import replete.logging.LogCodeDynamicManager;
import replete.logging.LoggerDynamicManager;
import replete.util.ClassUtil;

public abstract class PersistentController<P, S> {


    ////////////
    // FIELDS //
    ////////////

    protected P params;
    protected boolean started = false;
    protected boolean disposed = false;
    protected long startTime;


    /////////////
    // LOGGING //
    /////////////

    // Advanced inheritance-compatible logging mechanism.  Allows this
    // class and all subclasses to use the same static log codes (now
    // "dynamic" log codes) defined here NOT because all these classes
    // will use the same logger and log code instances, but rather
    // class-specific loggers and log codes will be dynamically created
    // at runtime when individual classes require them for their
    // own logging.  Logging for static methods is still available as
    // well using the get[Logger|Code]Static() methods.  Dynamic log code
    // variable names should be "LCD_..." instead of "LC_..." so that
    // these codes do not conflict with any locally-defined log codes
    // in subclasses, which is still permitted using the basic pattern.

    protected static LoggerDynamicManager loggerDynamicManager = new LoggerDynamicManager();     // Used in methods below.
    protected static LogCodeDynamicManager logCodeDynamicManager = new LogCodeDynamicManager();  // Used in methods below.
    protected static LogCodeDynamic LCD_YB = LogCodeDynamicManager.create("Replete", "Y>", "Ready");
    protected static LogCodeDynamic LCD_YM = LogCodeDynamicManager.create("Replete", "Y-", "Ready Failed", true);
    protected static LogCodeDynamic LCD_SB = LogCodeDynamicManager.create("Replete", "S>", "Start");
    protected static LogCodeDynamic LCD_SM = LogCodeDynamicManager.create("Replete", "S-", "Start Failed", true);
    protected static LogCodeDynamic LCD_PB = LogCodeDynamicManager.create("Replete", "P>", "Pause");
    protected static LogCodeDynamic LCD_PM = LogCodeDynamicManager.create("Replete", "P-", "Pause Failed", true);
    protected static LogCodeDynamic LCD_UB = LogCodeDynamicManager.create("Replete", "U>", "Update");
    protected static LogCodeDynamic LCD_UM = LogCodeDynamicManager.create("Replete", "U-", "Update Failed", true);
    protected static LogCodeDynamic LCD_RB = LogCodeDynamicManager.create("Replete", "R>", "Reset");
    protected static LogCodeDynamic LCD_RM = LogCodeDynamicManager.create("Replete", "R-", "Reset Failed", true);
    protected static LogCodeDynamic LCD_DB = LogCodeDynamicManager.create("Replete", "D>", "Dispose");
    protected static LogCodeDynamic LCD_DM = LogCodeDynamicManager.create("Replete", "D-", "Dispose Failed", true);

    // Example non-static method usage: getLogger().debug(getCode(LCD_SB));
    // Example static method usage:     getLoggerStatic().debug(getCodeStatic(LCD_SB))

    // This pattern prevents you from having to have base-class methods
    // the use log statements just tied the base class itself and
    // then needing to provide overridable methods (e.g. getName()) to
    // provide distinguishing text within the log messages.

    protected static Logger getLoggerStatic() {                             // For static methods to get a logger.
        Class<?> clazz = ClassUtil.getCallingClass(1);
        return loggerDynamicManager.getLogger(clazz);
    }
    protected Logger getLogger() {                                          // For non-static methods to get a logger.
        return loggerDynamicManager.getLogger(getClass());
    }
    protected static LogCode getCodeStatic(LogCodeDynamic codeCreator) {    // For static methods to get a log code.
        Class<?> clazz = ClassUtil.getCallingClass(1);
        return logCodeDynamicManager.getCode(codeCreator, clazz);
    }
    protected LogCode getCode(LogCodeDynamic codeCreator) {                 // For non-static methods to get a log code.
        return logCodeDynamicManager.getCode(codeCreator, getClass());
    }


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PersistentController(P params) {
        this.params = params;
    }


    //////////
    // CORE //
    //////////

    public final synchronized boolean isPaused() {
        return isPausedInner();
    }

    public final synchronized void ready() {
        getLogger().debug(getCode(LCD_YB) + checkConvertParamsToString(params));
        try {
            if(disposed) {
                throw new IllegalStateException("PersistentController already disposed");
            }
            readyInner();
        } catch(Exception e) {
            getLogger().error(getCode(LCD_YM), e);
            throw e;
        }
    }

    public final synchronized void start() {
        getLogger().debug(getCode(LCD_SB));
        try {
            if(disposed) {
                throw new IllegalStateException("PersistentController already disposed");
            }
            startInner();
            started = true;
            startTime = System.currentTimeMillis();
        } catch(Exception e) {
            getLogger().error(getCode(LCD_SM), e);
            throw e;
        }
    }

    public final synchronized void pause() {
        getLogger().debug(getCode(LCD_PB));
        try {
            if(disposed) {
                throw new IllegalStateException("PersistentController already disposed");
            }
            if(!isPauseable()) {
                throw new IllegalStateException("PersistentController cannot be paused");
            }
            pauseInner();
        } catch(Exception e) {
            getLogger().error(getCode(LCD_PM), e);
        }
    }

    public final synchronized void update() {
        getLogger().debug(getCode(LCD_UB) + checkConvertParamsToString(params));
        try {
            if(disposed) {
                throw new IllegalStateException("PersistentController already disposed");
            }
            if(!isUpdatable()) {
                throw new IllegalStateException("PersistentController cannot be updated");
            }
            updateInner();
            // TODO: fire update events one day so other controllers can subscribe
        } catch(Exception e) {
            getLogger().error(getCode(LCD_UM), e);
        }
    }

    public final synchronized void reset() {
        getLogger().debug(getCode(LCD_RB));
        try {
            if(disposed) {
                throw new IllegalStateException("PersistentController already disposed");
            }
            if(!isResettable()) {
                throw new IllegalStateException("PersistentController cannot be reset");
            }
            resetInner();
        } catch(Exception e) {
            getLogger().error(getCode(LCD_RM), e);
        }
    }

    public final synchronized void dispose() {
        if(disposed) {
            return;
        }
        getLogger().debug(getCode(LCD_DB));
        try {
            disposeInner();
            disposed = true;
        } catch(Exception e) {
            getLogger().error(getCode(LCD_DM), e);
        }
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Mutators

    public boolean isStarted() {
        return started;
    }
    public long getStartTime() {
        return startTime;
    }
    public P getParams() {
        return params;
    }
    public boolean isDisposed() {
        return disposed;
    }

    // Mutator

    public void setParams(P params) {
        this.params = params;
    }


    //////////
    // MISC //
    //////////

    private String checkConvertParamsToString(P params) {
        String str = convertParamsToString(params);
        return str == null ? "" : " " + str;
    }
    protected String convertParamsToString(P params) {
        return null;   // Meant to be overridden by subclasses if they desire
    }


    //////////////
    // ABSTRACT //
    //////////////

    // All of these methods MUST be abstract. Do not remove 'abstract' for convenience.
    // Reasoning is to force the controller designer to consciously decide and KNOW
    // how their controller works.

    public abstract S createSummaryState();
    public abstract boolean isPauseable();
    public abstract boolean isUpdatable();
    public abstract boolean isResettable();

    protected /*abstract*/ void readyInner() {   // Really want to make this abstract like the
                                                 // others but don't have time to go to all the
    }                                            // classes and fix yet (also might make all non-abstract...)
    protected abstract boolean isPausedInner();
    protected abstract void startInner();
    protected abstract void pauseInner();
    protected abstract void updateInner();
    protected abstract void resetInner();
    protected abstract void disposeInner();
}
