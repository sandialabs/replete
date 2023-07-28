package finio.ui.actions.change;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.app.AppContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionMap;

public class ChangeAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        map.createAction("change")
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setText("&Change"))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setText("&Change")
                    .setLabelMenu(false)
                    .setIcon(CommonConcepts.CHANGE));

    }

}
