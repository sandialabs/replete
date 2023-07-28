package finio.platform.exts.view.treeview.ui.actions;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionMap;

public class TreeAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        map.createAction("tree")
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setText("&Tree"))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setText("&Tree")
                    .setLabelMenu(false)
                    .setIcon(FinioImageModel.TREE_VIEW));

    }

}
