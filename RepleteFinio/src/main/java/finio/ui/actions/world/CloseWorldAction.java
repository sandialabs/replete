package finio.ui.actions.world;

import java.awt.event.KeyEvent;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.ToolBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class CloseWorldAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new CloseWorldWorker(ac, ac.getSelectedWorld(), "Close World");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setViewRequired(false)
        ;

        map.createAction("close-world", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("world")
                    .setText("&Close World")
                    .setIcon(CommonConcepts.CLOSE)
                    .setAccKey(KeyEvent.VK_W)
                    .setAccCtrl(true))
            .addDescriptor(
                new ToolBarActionDescriptor()
                    .setGroup("world")
                    .setToolTipText("Close World")
                    .setIcon(CommonConcepts.CLOSE));

    }

}
