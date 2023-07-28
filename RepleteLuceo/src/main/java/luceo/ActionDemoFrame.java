package luceo;

import java.awt.event.ActionListener;

import luceo.actions.Action;
import luceo.actions.seqexe.SequentialChildExecutionAction;
import luceo.actions.trivial.TrivialAction;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.list.RList;
import replete.ui.list.RListModel;
import replete.ui.windows.escape.EscapeFrame;

public class ActionDemoFrame extends EscapeFrame {
    private RList<Action> lst;
    private RListModel<Action> mdl;

    SequentialChildExecutionAction masterAction = new SequentialChildExecutionAction();

    public ActionDemoFrame() {
        super("Action Demo");

        Lay.BLtg(this,
            "N", Lay.FL("L",
                Lay.btn(CommonConcepts.ADD,    "Add", (ActionListener) e -> addAction()),
                Lay.btn(CommonConcepts.REMOVE, "Remove", (ActionListener) e -> removeAction()),
                Lay.btn(CommonConcepts.PLAY,   "Execute", (ActionListener) e -> execute()),
                Lay.btn(CommonConcepts.PRINT,  "Print", (ActionListener) e -> printSummaries())
            ),
            "C", Lay.sp(
                lst = Lay.lst(mdl = new RListModel<>())
            ),
            "size=800,center"
        );
    }

    private void printSummaries() {
        masterAction.printSummary();
    }

    private void removeAction() {
        int[] selIdxs = lst.getSelectedIndices();
        for(int i = selIdxs.length - 1; i >= 0; i--) {
            int idx = selIdxs[i];

            masterAction.removeAction(idx);
            mdl.remove(idx);
        }
    }

    private void addAction() {
        TrivialAction action = new TrivialAction();
        masterAction.addAction(action);
        mdl.addElement(action);
    }

    private void execute() {
        try {
            masterAction.execute();
        } catch(Exception e) {
            System.err.println("ERROR");
            e.printStackTrace();
        }
    }
}
