package replete.threads.deadlock;

public class DeadlockDetectorErrorEvent {
    private boolean stop = false;
    private Exception error;

    public DeadlockDetectorErrorEvent(Exception error) {
        this.error = error;
    }

    public Exception getError() {
        return error;
    }
    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }
}
