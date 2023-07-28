package finio.ui.actions.world;

import java.awt.event.KeyEvent;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.app.AppContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.ToolBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class NewWorldAction extends DefaultFinioUiAction {

    @Override
    public void register(final AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new NewWorldWorker(ac, ac.getSelectedWorld(), "New World");
            }
        };

        map.createAction("new-world", listener)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("world")
                    .setText("&New World")
                    .setIcon(CommonConcepts.NEW)
                    .setSepGroup("world-new-open")
                    .setAccKey(KeyEvent.VK_N)
                    .setAccCtrl(true))
            .addDescriptor(
                new ToolBarActionDescriptor()
                    .setGroup("world")
                    .setToolTipText("New World")
                    .setIcon(CommonConcepts.NEW));

    }

}
