package finio.ui.actions.mark;

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

public class SetWorkingScopeAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new SetWorkingScopeWorker(ac, ac.getSelectedWorld(),
                    "Set As Working Scope");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setSingleSelect()
        ;

        map.createAction("working-scope", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("mark")
                    .setText("Set As Working Scope")
                    .setIcon(FinioImageModel.WORKING_SCOPE)
                    .setAccKey(KeyEvent.VK_R)
                    .setAccCtrl(true))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("mark")
                    .setText("Set As Working Scope")
                    .setIcon(FinioImageModel.WORKING_SCOPE)
                    .setAccKey(KeyEvent.VK_R)
                    .setAccCtrl(true));

    }

}
