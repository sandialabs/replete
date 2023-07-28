package finio.platform.exts.view.treeview.ui.actions;

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

public class ExpandAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new ExpandWorker(ac, ac.getSelectedWorld(), "Expand");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setWorldAllowed(false)
            .setTerminalAllowed(false)
            .setAllMustBeValid(false)
            .setManagedNonTerminalAllowed(true)
        ;

        map.createAction("expand", listener, validator)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("tree")
                    .setText("&Expand")
                    .setIcon(CommonConcepts.EXPAND)
                    .setAccKey(KeyEvent.VK_EQUALS))
            .addDescriptor(
                new KeyPressedActionDescriptor()
                    .setKeyCode(KeyEvent.VK_EQUALS))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("tree")
                    .setText("&Expand")
                    .setIcon(CommonConcepts.EXPAND)
                    .setAccKey(KeyEvent.VK_EQUALS));

    }

}
