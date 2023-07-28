package finio.ui.actions.help;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;
import replete.plugins.ui.PluginDialog;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class PluginsAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new LocalWorker(ac, ac.getSelectedWorld(), "Plug-ins");
            }
        };

        map.createAction("plugins", listener)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("help")
                    .setText("&Plug-ins...")
                    .setIcon(CommonConcepts.PLUGIN));

    }


    /////////////////
    // INNER CLASS //
    /////////////////

    private class LocalWorker extends FWorker<Void, Void> {


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
        protected Void gather() {
            PluginDialog dlg = new PluginDialog(ac.getWindow());
            dlg.setVisible(true);
            return null;
        }

//        public void showPluginDialog(PluginManagerState piState, String nodeName) {
//            PluginDialog dlg = new PluginDialog(parentRef, piState);
//            dlg.setTitle("Plug-ins on " + nodeName);
//            dlg.setVisible(true);
//        }
    }
}
