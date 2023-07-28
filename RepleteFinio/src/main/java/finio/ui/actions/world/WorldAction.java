package finio.ui.actions.world;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.app.AppContext;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIActionMap;

public class WorldAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        map.createAction("world")
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setText("&World"));

    }

}
