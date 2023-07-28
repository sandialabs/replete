package finio.ui.actions.view;

import java.util.List;

import finio.core.NonTerminal;
import finio.plugins.extpoints.View;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.ViewPanel;
import finio.ui.world.WorldPanel;
import finio.ui.worlds.WorldContext;

public class OpenNewViewWorker extends FWorker<Void, List<NonTerminal>> {


    ///////////
    // FIELD //
    ///////////

    private View view;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public OpenNewViewWorker(AppContext ac, WorldContext wc, String name, View view) {
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
        ViewPanel pnlView = view.createPanel(ac, wc, null, wc.getW());
        pnlWorld.addViewPanel(pnlView);
    }
}
