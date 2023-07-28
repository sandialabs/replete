package finio.ui.actions.change;

import java.awt.event.KeyEvent;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.KeyPressedActionDescriptor;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class EditKeyValueNoShiftAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new EditKeyValueWorker(ac, ac.getSelectedWorld(), "Edit Key", false);
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setWorldAllowed(false)
        ;

        map.createAction("edit-kv", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("change")
                    .setText("&Edit")
                    .setIcon(CommonConcepts.EDIT))
                    //.setAccKey(KeyEvent.VK_E))         Need to get this figured out...
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("change")
                    .setText("&Edit")
                    .setIcon(CommonConcepts.EDIT))
                    //.setAccKey(KeyEvent.VK_E))         Need to get this figured out...
            .addDescriptor(
                new KeyPressedActionDescriptor()
                    .setKeyCode(KeyEvent.VK_ENTER));
//            .addDescriptor(
//                new KeyPressedActionDescriptor()       Need to get this figured out...
//                    .setKeyCode(KeyEvent.VK_E));
    }

}
