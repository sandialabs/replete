package finio.ui.actions.navigate;

import java.awt.event.KeyEvent;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.KeyPressedActionDescriptor;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class SelectRootAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new SelectRootWorker(ac, ac.getSelectedWorld(), "Select Root");
            }
        };

        AActionValidator selRootValidator = new AActionValidator(ac)
            .setSelect(0, Integer.MAX_VALUE)
        ;

        map.createAction("select-root", listener, selRootValidator)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("navigate")
                    .setText("Select &Root")
                    .setIcon(FinioImageModel.SELROOT))
            .addDescriptor(
                new KeyPressedActionDescriptor()
                    .setKeyCode(KeyEvent.VK_0))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("navigate")
                    .setText("Select &Root")
                    .setIcon(FinioImageModel.SELROOT));

    }

}
