package finio.ui.actions.change;

import java.awt.event.KeyEvent;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class AddKeyValuePairTSibAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new AddKeyValuePairWorker(ac, ac.getSelectedWorld(),
                    "Add Terminal As Sibling", "value", true);
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setWorldAllowed(false)
            .setSelectionReverseDepth(2)
        ;

        map.createAction("add-terminal-sib", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("change/add")
                    .setText("&Terminal As Sibling")
                    .setIcon(FinioImageModel.TERMINAL)
                    .setAccKey(KeyEvent.VK_J)
                    .setAccCtrl(true))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("change/add")
                    .setText("&Terminal As Sibling")
                    .setIcon(FinioImageModel.TERMINAL)
                    .setAccKey(KeyEvent.VK_J)
                    .setAccCtrl(true));

    }

}
