package finio.ui.actions.help;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;
import replete.pstate2.PersistentStateLoadException;
import replete.pstate2.PersistentStateManager;
import replete.pstate2.XmlFileManager;
import replete.scrutinize.archive.app2.HostSystemInfoB;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;
import replete.util.User;

public class SystemDetailsActionAlt extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new LocalWorker(ac, ac.getSelectedWorld(), "My System Info (Alt)");
            }
        };

        map.createAction("my-sys-info-2", listener)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("help")
                    .setText("&My System Info (Alt)...")
                    .setIcon(CommonConcepts.SEARCH));

    }


    /////////////////
    // INNER CLASS //
    /////////////////

    private class LocalWorker extends FWorker<Void, HostSystemInfoB> {


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
        protected HostSystemInfoB background(Void gathered) throws Exception {
            PersistentStateManager stateMgr = new XmlFileManager(User.getHome(".hsi"));
            replete.scrutinize.archive.app2.AppState state = null;
            try {
                state = (replete.scrutinize.archive.app2.AppState) stateMgr.load();
            } catch(PersistentStateLoadException e) {
                e.printStackTrace();
            }
            if(state == null) {
                state = new replete.scrutinize.archive.app2.AppState();
            }
            replete.scrutinize.archive.app2.AppState.setState(state);

            return new HostSystemInfoB();
        }

        @Override
        public String getActionVerb() {
            return "creating the system information window";
        }

        @Override
        protected void completeInner(HostSystemInfoB fra) {
            fra.setVisible(true);
            fra.reloadLocal();
        }
    }
}
