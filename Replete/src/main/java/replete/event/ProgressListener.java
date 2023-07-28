package replete.event;


public interface ProgressListener extends ExtChangeListener<ProgressEvent> {
    void stateChanged(ProgressEvent e);
}