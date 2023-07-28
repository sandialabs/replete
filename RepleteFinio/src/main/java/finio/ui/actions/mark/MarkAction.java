package finio.ui.actions.mark;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.app.AppContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionMap;

public class MarkAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        map.createAction("mark")
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setText("&Mark"))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setText("&Mark")
                    .setLabelMenu(false)
                    .setIcon(CommonConcepts.EDIT));

    }

}
