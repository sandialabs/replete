package replete.pipeline;

import replete.numbers.AveragedLong;

public class DefaultExecuteSummary implements ExecuteSummary {


    ////////////
    // FIELDS //
    ////////////

    private AveragedLong duration;
    private Exception error;
    private int attemptedCount;
    private int successCount;
    private int failedCount;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public DefaultExecuteSummary(AveragedLong duration, Exception error,
                                 int attempted, int success, int failed) {
        this.duration = duration;
        this.error = error;
        attemptedCount = attempted;
        successCount = success;
        failedCount = failed;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    // Accessors

    @Override
    public AveragedLong getDuration() {
        return duration;
    }
    @Override
    public Exception getError() {
        return error;
    }
    @Override
    public int getExecuteAttemptedCount() {
        return attemptedCount;
    }
    @Override
    public int getExecuteSuccessCount() {
        return successCount;
    }
    @Override
    public int getExecuteFailedCount() {
        return failedCount;
    }

    // Accessors (Computed)

    @Override
    public boolean isError() {
        return error != null;
    }

    @Override
    public String toString() {
        return "DefaultExecuteSummary [duration=" + duration + ", error=" + error
                + ", attempted=" + attemptedCount + ", success=" + successCount
                + ", failed=" + failedCount + "]";
    }
}
