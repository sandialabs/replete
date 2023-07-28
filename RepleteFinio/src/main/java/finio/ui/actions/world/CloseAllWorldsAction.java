package finio.ui.actions.world;

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

public class CloseAllWorldsAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new CloseAllWorldsWorker(ac, ac.getSelectedWorld(), "Close All Worlds");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setViewRequired(false)
        ;

        map.createAction("close-all-worlds", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("world")
                    .setText("Close All Worlds")
                    .setIcon(CommonConcepts.CLOSE_ALL))
        .addDescriptor(
            new ToolBarActionDescriptor()
                .setGroup("world")
                .setToolTipText("Close All Worlds")
                .setIcon(CommonConcepts.CLOSE_ALL));

    }

}
