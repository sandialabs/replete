package finio.ui.actions.edit;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.app.AppContext;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIActionMap;

public class EditAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        map.createAction("edit")
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setText("&Edit"));

    }

}
