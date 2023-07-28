package luceo.actions;

import java.util.ArrayList;
import java.util.List;

public class ExecuteSummary {
    // it's possible that we only need "inner" set if we handle more and
    // more with platform
    long startOuter = -1;
    long startInner = -1;  // no inner if any set up steps fail
    long endOuter   = -1;
    long endInner   = -1;    // (e.g. start notifier and input validation)

    Exception error;
    List<ExecuteMessage> messages = new ArrayList<>();


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public long getStartOuter() {
        return startOuter;
    }
    public long getStartInner() {
        return startInner;
    }
    public long getEndOuter() {
        return endOuter;
    }
    public long getEndInner() {
        return endInner;
    }
    public Exception getError() {
        return error;
    }
    public List<ExecuteMessage> getMessages() {
        return messages;
    }

    // Accessors (Computed)

    public boolean isSuccess() {
        return endInner != -1 && error == null;  // endInner ref ensures executeInner has exited at least under whatever circumstances
    }

    // Mutators

}
