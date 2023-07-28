package finio.ui.actions.help;

import finio.FinioAppMain;
import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.NotImplWorker;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class AboutAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new LocalWorker(ac, ac.getSelectedWorld(), "About");
            }
        };

        map.createAction("about", listener)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("help")
                    .setText("&About " + FinioAppMain.TITLE)
                    .setIcon(CommonConcepts.ABOUT));

    }


    /////////////////
    // INNER CLASS //
    /////////////////

    private class LocalWorker extends NotImplWorker {


        /////////////////
        // CONSTRUCTOR //
        /////////////////

        public LocalWorker(AppContext ac, WorldContext wc, String name) {
            super(ac, wc, name);
        }
    }
}
