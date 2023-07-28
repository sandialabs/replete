package finio.ui.actions.misc;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.app.AppContext;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIActionMap;

public class MiscAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        map.createAction("misc")
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setText("Miscellaneous"));

    }

}
