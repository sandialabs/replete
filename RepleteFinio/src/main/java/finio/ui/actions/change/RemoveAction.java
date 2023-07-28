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

public class RemoveAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new RemoveWorker(ac, ac.getSelectedWorld(), "Remove");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setWorldAllowed(false)
            .setSelectionReverseDepth(2)
        ;

        map.createAction("remove", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("change")
                    .setText("&Remove")
                    .setIcon(CommonConcepts.REMOVE)
                    .setAccKey(KeyEvent.VK_DELETE))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("change")
                    .setText("&Remove")
                    .setIcon(CommonConcepts.REMOVE)
                    .setAccKey(KeyEvent.VK_DELETE))
//            .addDescriptor(
//                new KeyPressedActionDescriptor()
//                    .setKeyCode(KeyEvent.VK_DELETE))
            .addDescriptor(
                new KeyPressedActionDescriptor()
                    .setKeyCode(KeyEvent.VK_D));

    }

}
