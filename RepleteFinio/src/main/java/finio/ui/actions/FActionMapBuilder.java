package finio.ui.actions;

import java.util.List;

import finio.appstate.AppStateChangeEvent;
import finio.appstate.AppStateChangeListener;
import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.plugins.extpoints.FinioUIAction;
import finio.ui.app.AppContext;
import replete.event.rnotif.RChangeEvent;
import replete.event.rnotif.RChangeListener;
import replete.plugins.ExtensionPoint;
import replete.plugins.PluginManager;

public class FActionMapBuilder {


    ////////////
    // FIELDS //
    ////////////

    private AppContext ac;
    private FActionMap actionMap;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FActionMap build(AppContext ac) {
        this.ac = ac;
        actionMap = new FActionMap(ac);
        ac.setActionMap(actionMap);

        ac.getConfig().addPropertyChangeListener(new AppStateChangeListener() {
            public void stateChanged(AppStateChangeEvent e) {
                if(e.getName().equals("worldsUseDesktopPane")) {
                    actionMap.validate();
                }
            }
        });
        ac.addWorldAddedListener(new RChangeListener() {
            public void handle(RChangeEvent e) {
                actionMap.validate();
            }
        });
        // removed too

        List<ExtensionPoint> exts = PluginManager.getExtensionsForPoint(FinioUIAction.class);
        if(exts.size() != 0) {
            for(ExtensionPoint ext : exts) {
                DefaultFinioUiAction action = (DefaultFinioUiAction) ext;
                action.register(ac);
            }
        }

        return actionMap;
    }
}
