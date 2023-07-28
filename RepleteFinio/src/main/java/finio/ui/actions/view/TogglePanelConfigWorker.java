package finio.ui.actions.view;

import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.ViewPanel;
import finio.ui.world.NoViewsViewPanel;
import finio.ui.world.WorldPanel;
import finio.ui.worlds.WorldContext;

public class TogglePanelConfigWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public TogglePanelConfigWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getActionVerb() {
        return "toggling the configuration panel";
    }

    @Override
    protected void completeInner(Void result) {
        WorldPanel pnlWorld = ac.getSelectedWorld().getWorldPanel();
        ViewPanel pnlView = pnlWorld.getSelectedView();
        if(pnlView == null) {
            NoViewsViewPanel pnlNoViews = pnlWorld.getNoViewsPanel();
            pnlNoViews.setShowConfigDrawer(!pnlNoViews.isShowingConfigDrawer(), false);
        } else {
            pnlView.setShowConfigDrawer(!pnlView.isShowingConfigDrawer(), false);
        }
    }
}
