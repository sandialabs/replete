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

public class CollapseAllAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new CollapseAllWorker(ac, ac.getSelectedWorld(), "Collapse All");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setTerminalAllowed(false)
            .setAllMustBeValid(false)
            .setManagedNonTerminalAllowed(true)
        ;

        map.createAction("collapse-all", listener, validator)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("tree")
                    .setText("Colla&pse All")
                    .setIcon(CommonConcepts.COLLAPSE_ALL)
                    .setAccKey(KeyEvent.VK_MINUS)
                    .setAccShift(true))
            .addDescriptor(
                new KeyPressedActionDescriptor()
                    .setKeyCode(KeyEvent.VK_MINUS)
                    .setShift(true))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("tree")
                    .setText("Colla&pse All")
                    .setIcon(CommonConcepts.COLLAPSE_ALL)
                    .setAccKey(KeyEvent.VK_MINUS)
                    .setAccShift(true));

    }

}
