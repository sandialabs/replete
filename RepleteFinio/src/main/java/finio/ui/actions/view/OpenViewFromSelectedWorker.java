package finio.ui.actions.view;

import java.util.List;

import finio.core.NonTerminal;
import finio.plugins.extpoints.View;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.SelectionContext;
import finio.ui.view.ViewPanel;
import finio.ui.world.WorldPanel;
import finio.ui.worlds.WorldContext;

public class OpenViewFromSelectedWorker extends FWorker<Void, List<NonTerminal>> {


    ///////////
    // FIELD //
    ///////////

    private View view;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public OpenViewFromSelectedWorker(AppContext ac, WorldContext wc, String name, View view) {
        super(ac, wc, name);
        this.view = view;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getActionVerb() {
        return "opening a new view";
    }
    
    @Override
    protected void completeInner(List<NonTerminal> Ms) {
        WorldPanel pnlWorld = ac.getSelectedWorld().getWorldPanel();
        for(SelectionContext C : getValidSelected()) {
            ViewPanel pnlView = view.createPanel(ac, wc, C.getK(), C.getV());
            pnlWorld.addViewPanel(pnlView);
        }
    }
}
