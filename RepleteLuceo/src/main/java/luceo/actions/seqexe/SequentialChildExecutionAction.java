package luceo.actions.seqexe;

import java.util.ArrayList;
import java.util.List;

import luceo.actions.Action;
import replete.text.RStringBuilder;
import replete.text.StringUtil;

public class SequentialChildExecutionAction extends Action {

    private List<Action> childActions = new ArrayList<>();

    // need notifiers for this data structure

    @Override
    protected void executeInner() {
        for(Action action : childActions) {
            //if(!action.isDeactivated)
            action.execute();  // No try-catch here for normal operations unless this
                               // action wanted to take some action on a per-action basis.
        }
    }

    public synchronized void addAction(Action action) {
        childActions.add(action);
    }
    public synchronized void addAction(int index, Action action) {
        childActions.add(index, action);
    }
    public synchronized void removeAction(int index) {
        childActions.remove(index);
    }
    public synchronized void moveAction(int index, int destIndex) {
        Action a = childActions.remove(index);
        childActions.add(destIndex, a);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void renderSummary(RStringBuilder buffer, int level) {
        super.renderSummary(buffer, level);

        String sp = StringUtil.spaces(level * 4);
        int a = 1;
        for(Action action : childActions) {
            buffer.appendln(sp + "    Child Action #" + a);
            action.renderSummary(buffer, level + 2);
            a++;
        }
    }
}