package replete.pipeline;

import java.io.Serializable;

import replete.numbers.AveragedLong;

public interface ExecuteSummary extends Serializable {
    public static final long UNSET = -1;

    public AveragedLong getDuration();
    public boolean isError();
    public Exception getError();
    public int getExecuteAttemptedCount();
    public int getExecuteFailedCount();
    public int getExecuteSuccessCount();
}
