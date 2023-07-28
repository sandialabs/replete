package replete.threads.deadlock;

import java.lang.management.ThreadInfo;

import replete.util.DateUtil;

public class DeadlockedThreadDescriptor {


    ////////////
    // FIELDS //
    ////////////

    private long id;
    private boolean newlyEcountered;
    private boolean removed;
    private long firstTimeReported;
    private long lastTimeReported;
    private ThreadInfo info;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public DeadlockedThreadDescriptor(long id) {
        this.id = id;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public long getId() {
        return id;
    }
    public boolean isNewlyEcountered() {
        return newlyEcountered;
    }
    public boolean isRemoved() {
        return removed;
    }
    public long getFirstTimeReported() {
        return firstTimeReported;
    }
    public long getLastTimeReported() {
        return lastTimeReported;
    }
    public ThreadInfo getInfo() {
        return info;
    }

    // Mutators

    public DeadlockedThreadDescriptor setNewlyEcountered(boolean newlyEcountered) {
        this.newlyEcountered = newlyEcountered;
        return this;
    }
    public DeadlockedThreadDescriptor setRemoved(boolean removed) {
        this.removed = removed;
        return this;
    }
    public DeadlockedThreadDescriptor setFirstTimeReported(long firstTimeReported) {
        this.firstTimeReported = firstTimeReported;
        return this;
    }
    public DeadlockedThreadDescriptor setLastTimeReported(long lastTimeReported) {
        this.lastTimeReported = lastTimeReported;
        return this;
    }
    public DeadlockedThreadDescriptor setInfo(ThreadInfo info) {
        this.info = info;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return
            "Thread ID: " +
            id +
            (removed ? " !REMOVED!" : "") +                // Shouldn't ever happen
            (newlyEcountered ? " (NEWLY FOUND)" : "") +
            ", First Detected @ " + DateUtil.toLongString(firstTimeReported) +
            "\nThread Info: " +
            (info == null ? "[NO INFO!]" : info.toString().trim());
    }
}
