package finio.ui.actions.view;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.ToolBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class TogglePanelConfigAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new TogglePanelConfigWorker(ac, ac.getSelectedWorld(), "Toggle Config");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setViewRequired(false)
        ;

        map.createAction("toggle-config", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("view")
                    .setText("&Toggle Config")
                    .setSepGroup("view-view")
                    .setIcon(FinioImageModel.VIEW_CONFIG))
           .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("view")
                    .setText("Toggle Config")
                    .setIcon(FinioImageModel.VIEW_CONFIG))
            .addDescriptor(
                new ToolBarActionDescriptor()
                    .setGroup("view")
                    .setToolTipText("Toggle Config")
                    .setIcon(FinioImageModel.VIEW_CONFIG));

    }

}
