package finio.platform.exts.view.treeview.ui.actions;

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

public class ActionSelectedAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new ActionSelectedWorker(ac, ac.getSelectedWorld(), "Action Selected");
            }
        };

        AActionValidator validator = new AActionValidator(ac);

        map.createAction("action-selected", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("tree")
                    .setText("Action Selected")
                    .setIcon(FinioImageModel.SELECTION_ACTION)
                    .setAccKey(KeyEvent.VK_F)
                    .setAccCtrl(true));

    }

}
