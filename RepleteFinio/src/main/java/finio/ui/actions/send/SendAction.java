package finio.ui.actions.send;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionMap;

public class SendAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        map.createAction("send")
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setText("&Send"))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setText("&Send")
                    .setLabelMenu(false)
                    .setIcon(FinioImageModel.SEND));

    }

}
