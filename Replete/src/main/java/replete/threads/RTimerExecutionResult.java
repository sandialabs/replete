package replete.threads;

import java.io.Serializable;

import replete.errors.ThrowableSnapshot;

public class RTimerExecutionResult implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    private int id;
    private long start;
    private long end;
    private ThrowableSnapshot throwableSnapshot;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public int getId() {
        return id;
    }
    public long getStart() {
        return start;
    }
    public long getEnd() {
        return end;
    }
    public ThrowableSnapshot getThrowableSnapshot() {
        return throwableSnapshot;
    }

    // Mutators

    public RTimerExecutionResult setId(int id) {
        this.id = id;
        return this;
    }
    public RTimerExecutionResult setStart(long start) {
        this.start = start;
        return this;
    }
    public RTimerExecutionResult setEnd(long end) {
        this.end = end;
        return this;
    }
    public RTimerExecutionResult setThrowableSnapshot(ThrowableSnapshot throwableSnapshot) {
        this.throwableSnapshot = throwableSnapshot;
        return this;
    }
}
