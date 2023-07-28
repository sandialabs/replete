package finio.ui.actions.transform;

import java.util.List;

import finio.core.impl.DiffResult;
import finio.core.impl.NonTerminalDiffUtil;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;

public class DiffMapsWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public DiffMapsWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected Void background(Void gathered) throws Exception {
        List<SelectionContext> Cs = getValidSelected();
        SelectionContext C0 = Cs.get(0);
        SelectionContext C1 = Cs.get(1);

        DiffResult result = NonTerminalDiffUtil.diff(C0.getK(), C0.getV(), C1.getK(), C1.getV());

//        NonTerminal M = (NonTerminal) Oresult;
//        FMapRenderer R = new StandardAMapRenderer();
//        System.out.println(R.renderValue(M));
//        System.out.println(R.renderValue(M.getSysMeta()));
//        M.getSysMeta(M)

        wc.getW().put("DiffResult", result);

//        ac.getWorld(0).getW().put("DiffResult", Oresult);
//        for(SelectionContext C : Cs) {
//            select(
//                new SelectRequest()
//                    .setContext(C)
//                    .setAction(SelectAction.SELF)
//            );
//            expand(
//                new ExpandRequest()
//                    .setContext(C)
//                    .setAction(SelectAction.SELF)
//            );
//        }
        return null;
    }

    @Override
    public String getActionVerb() {
        return "diffing maps";
    }
}
