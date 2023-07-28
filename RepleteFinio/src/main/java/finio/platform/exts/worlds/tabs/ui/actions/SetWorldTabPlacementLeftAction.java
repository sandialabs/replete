package finio.platform.exts.worlds.tabs.ui.actions;

import javax.swing.JTabbedPane;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.ActionValidator;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class SetWorldTabPlacementLeftAction extends DefaultFinioUiAction {

    @Override
    public void register(final AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new SetWorldTabPlacementWorker(ac, ac.getSelectedWorld(),
                    "World Tabs Left", JTabbedPane.LEFT);
            }
        };

        ActionValidator validator = new ActionValidator() {
            public boolean isValid(String actionId) {
                return new AActionValidator(ac)
                .setViewRequired(false)
                .isValid(actionId) &&
                !ac.getConfig().isWorldsUseDesktopPane();
            }
        };

        map.createAction("world-tabs-left", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("view")
                    .setOptMenu(true, "world-tabs")
                    .setText("World Tabs Left")
                    .setSepGroup("view-worlds-tabs")
                    .setIcon(FinioImageModel.TABS_LEFT));

    }

}
