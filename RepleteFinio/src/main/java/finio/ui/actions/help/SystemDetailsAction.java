package finio.ui.actions.help;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;
import replete.scrutinize.archive.app1.HostSystemInfoA;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class SystemDetailsAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new LocalWorker(ac, ac.getSelectedWorld(), "My System Info");
            }
        };

        map.createAction("my-sys-info", listener)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("help")
                    .setText("&My System Info...")
                    .setIcon(CommonConcepts.SEARCH));

    }


    /////////////////
    // INNER CLASS //
    /////////////////

    private  class LocalWorker extends FWorker<Void, HostSystemInfoA> {


        /////////////////
        // CONSTRUCTOR //
        /////////////////

        public LocalWorker(AppContext ac, WorldContext wc, String name) {
            super(ac, wc, name);
        }


        ////////////////
        // OVERRIDDEN //
        ////////////////

        @Override
        protected HostSystemInfoA background(Void gathered) throws Exception {
            return new HostSystemInfoA();    // Takes a while to construct this.
        }

        @Override
        public String getActionVerb() {
            return "creating the system information window";
        }

        @Override
        protected void completeInner(HostSystemInfoA appFrame) {
            appFrame.setVisible(true);
        }
    }
}
