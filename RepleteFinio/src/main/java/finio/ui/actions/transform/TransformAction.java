package finio.ui.actions.transform;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionMap;

public class TransformAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        map.createAction("transform")
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setText("Transform"))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setText("&Transform")
                    .setLabelMenu(false)
                    .setIcon(FinioImageModel.TRANSFORM));

    }

}
