package finio.ui.actions.navigate;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.app.AppContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionMap;

public class NavigateAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        map.createAction("navigate")
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setText("&Navigate"))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setText("&Navigate")
                    .setLabelMenu(false)
                    .setIcon(CommonConcepts.FILE_NAVIGATOR));

    }

}
