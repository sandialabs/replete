package finio.platform.exts.views.split.ui.actions;

import java.awt.event.KeyEvent;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class TreeConsoleHorizAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new TreeConsoleWorker(ac, ac.getSelectedWorld(), "View Tree & Console Horizontal", false);
            }
        };

        map.createAction("tree-console-horiz", listener, new AActionValidator(ac))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("view")
                    .setText("Tree/Console &Horizontal")
                    .setIcon(FinioImageModel.TC_HORIZ)
                    .setAccKey(KeyEvent.VK_3)
                    .setAccCtrl(true));

    }

}
