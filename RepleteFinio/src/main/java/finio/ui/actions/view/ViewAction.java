package finio.ui.actions.view;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionMap;

public class ViewAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        map.createAction("view")
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setText("&View"))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setText("&View")
                    .setLabelMenu(false)
                    .setIcon(FinioImageModel.VIEW));

    }

}
