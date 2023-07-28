package replete.text.stp;

public class SingleStateCriteria implements StateCriteria {
    private String stateName;
    public SingleStateCriteria(String stateName) {
        this.stateName = stateName;
    }
    @Override
    public boolean test(State state) {
        return state != null && state.getName().equals(stateName);
    }
}
