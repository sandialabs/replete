package finio.ui.actions.change;

import java.awt.event.KeyEvent;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import replete.ui.uiaction.KeyPressedActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class EditKeyValueShiftAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new EditKeyValueWorker(ac, ac.getSelectedWorld(), "Edit Value", true);
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setWorldAllowed(false)
        ;

        map.createAction("edit-kv-with-shift", listener, validator)
            .addDescriptor(
                new KeyPressedActionDescriptor()
                    .setKeyCode(KeyEvent.VK_ENTER)
                    .setShift(true));
//            .addDescriptor(
//                new KeyPressedActionDescriptor()
//                    .setKeyCode(KeyEvent.VK_E)         Need to get this figured out...
//                    .setShift(true));

    }

}
