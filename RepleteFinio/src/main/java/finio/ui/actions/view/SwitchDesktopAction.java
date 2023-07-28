package finio.ui.actions.view;

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

public class SwitchDesktopAction extends DefaultFinioUiAction {

    @Override
    public void register(final AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new SwitchDesktopWorker(ac, ac.getSelectedWorld(),
                    "Worlds With Desktop");
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

        map.createAction("world-switch-desktop", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("view")
                    .setText("Worlds With Desktop")
                    .setSepGroup("view-worlds")
                    .setIcon(FinioImageModel.DESKTOP_MODE));

    }

}
