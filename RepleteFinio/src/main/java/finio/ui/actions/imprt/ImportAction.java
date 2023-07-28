package finio.ui.actions.imprt;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.app.AppContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionMap;

public class ImportAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        map.createAction("import")
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setText("&Import"))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setText("&Import")
                    .setIcon(CommonConcepts.IMPORT)
                    .setLabelMenu(false));

    }

}
