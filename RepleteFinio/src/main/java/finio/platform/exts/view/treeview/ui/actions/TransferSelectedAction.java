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

public class TransferSelectedAction extends DefaultFinioUiAction {

    // TODO: Some day also allow a relative path to be added to the console.
    // This only currently does absolute path, relative to the "My World"
    // node.
    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new TransferSelectedWorker(ac, ac.getSelectedWorld(), "Transfer Selected");
            }
        };

        AActionValidator validator = new AActionValidator(ac);

        map.createAction("transfer-selected", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("tree")
                    .setText("Transfer Selected")
                    .setIcon(FinioImageModel.SELECTION_TRANSFER)
                    .setAccKey(KeyEvent.VK_G)
                    .setAccCtrl(true));

    }

}
