package finio.ui.actions.mgmt;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionMap;

public class ManagementAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        map.createAction("mgmt")
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setText("&Management"))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setText("&Management")
                    .setIcon(FinioImageModel.MANAGEMENT));

    }

}
