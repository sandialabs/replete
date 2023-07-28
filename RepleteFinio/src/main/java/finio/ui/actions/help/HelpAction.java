package finio.ui.actions.help;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.app.AppContext;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIActionMap;

public class HelpAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        map.createAction("help")
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setText("&Help"));

    }

}
