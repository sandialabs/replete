package finio.ui.actions.window;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.app.AppContext;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIActionMap;

public class WindowAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        map.createAction("window")
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setText("&Window"))
            /*.addDescriptor(
                new PopupMenuActionDescriptor()
                    .setText("&Window")
                    .setLabelMenu(false)
                    .setIcon(FinioImageModel.VIEW))*/;

    }

}
