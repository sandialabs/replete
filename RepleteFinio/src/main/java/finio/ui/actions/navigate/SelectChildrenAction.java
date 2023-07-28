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

public class SelectChildrenAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new SelectChildrenWorker(ac, ac.getSelectedWorld(), "Select Children");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setTerminalAllowed(false)
            .setAllMustBeValid(false)
        ;

        map.createAction("select-children", listener, validator)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("navigate")
                    .setText("Select &Children")
                    .setIcon(FinioImageModel.SELCHILD))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("navigate")
                    .setText("Select &Children")
                    .setIcon(FinioImageModel.SELCHILD))
            .addDescriptor(
                new KeyPressedActionDescriptor()
                    .setKeyCode(KeyEvent.VK_C));

    }

}
