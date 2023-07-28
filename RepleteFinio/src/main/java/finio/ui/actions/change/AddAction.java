package finio.ui.actions.change;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionMap;

public class AddAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        AActionValidator validator = new AActionValidator(ac);

        map.createAction("add", validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("change")
                    .setText("&Add")
                    .setIcon(CommonConcepts.ADD))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("change")
                    .setText("&Add")
                    .setIcon(CommonConcepts.ADD));

    }

}
