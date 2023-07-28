package finio.ui.actions.print;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.app.AppContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionMap;

public class PrintAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        map.createAction("print")
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setText("&Print"))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setText("&Print")
                    .setLabelMenu(false)
                    .setIcon(CommonConcepts.PRINT));

    }

}
