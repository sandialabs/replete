package finio.ui.actions.world;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.app.AppContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class ExitAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new ExitWorker(ac, ac.getSelectedWorld(), "Exit");
            }
        };

        map.createAction("exit", listener)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("world")
                    .setText("E&xit")
                    .setIcon(CommonConcepts.EXIT)
                    .setSepGroup("exit"));

    }

}
