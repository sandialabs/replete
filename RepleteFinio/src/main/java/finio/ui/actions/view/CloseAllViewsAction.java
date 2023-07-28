package finio.ui.actions.view;

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

public class CloseAllViewsAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new CloseAllViewsWorker(ac, ac.getSelectedWorld(), "Close All Views");
            }
        };

        AActionValidator validator = new AActionValidator(ac);

        map.createAction("close-all-views", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("view")
                    .setText("Close All Views")
                    .setSepGroup("view-view")
                    .setIcon(CommonConcepts.CLOSE_ALL))
            .addDescriptor(
                new ToolBarActionDescriptor()
                    .setGroup("view")
                    .setToolTipText("Close All Views")
                    .setIcon(CommonConcepts.CLOSE_ALL));

    }

}
