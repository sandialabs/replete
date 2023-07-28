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

public class CloseViewAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new CloseViewWorker(ac, ac.getSelectedWorld(), "Close View");
            }
        };

        AActionValidator validator = new AActionValidator(ac);

        map.createAction("close-view", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("view")
                    .setText("&Close View")
                    .setSepGroup("view-view")
                    .setIcon(CommonConcepts.CLOSE))
            .addDescriptor(
                new ToolBarActionDescriptor()
                    .setGroup("view")
                    .setToolTipText("Close View")
                    .setIcon(CommonConcepts.CLOSE));

    }

}
