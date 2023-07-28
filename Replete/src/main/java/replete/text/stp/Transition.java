package replete.text.stp;

public class Transition {
    public StateCriteria previousStateCriteria;
    public StateCriteria nextStateCriteria;
    private TransitionListener listener;

    public Transition(StateCriteria previousStateCriteria, StateCriteria nextStateCriteria,
                      TransitionListener listener) {
        this.previousStateCriteria = previousStateCriteria;
        this.nextStateCriteria = nextStateCriteria;
        this.listener = listener;
    }
    public StateCriteria getPreviousStateCriteria() {
        return previousStateCriteria;
    }
    public StateCriteria getNextStateCriteria() {
        return nextStateCriteria;
    }
    public TransitionListener getListener() {
        return listener;
    }
}
