package replete.text.stp;

public interface TransitionListener {
    void stateChanged(State previousState, State newState, String line, String[] captures);
}
