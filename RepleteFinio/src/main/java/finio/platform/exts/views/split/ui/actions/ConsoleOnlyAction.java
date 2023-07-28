package finio.platform.exts.views.split.ui.actions;

import java.awt.event.KeyEvent;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class ConsoleOnlyAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new ConsoleOnlyWorker(ac, ac.getSelectedWorld(), "View Console Only");
            }
        };

        AActionValidator validator = new AActionValidator(ac);

        map.createAction("console-only", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("view")
                    .setText("&Console Only")
                    .setIcon(CommonConcepts.CONSOLE)
                    .setAccKey(KeyEvent.VK_4)
                    .setAccCtrl(true));

    }

}
