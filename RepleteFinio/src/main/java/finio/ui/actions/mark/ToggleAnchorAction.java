package finio.ui.actions.mark;

import java.awt.event.KeyEvent;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.ToolBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class ToggleAnchorAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        // NEEDS HIERARCHY CHECKING!
        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new ToggleAnchorWorker(ac, ac.getSelectedWorld(), "Toggle Anchor");
            }
        };

        map.createAction("toggle-anchor", listener, new AActionValidator(ac))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("mark")
                    .setText("Anchor")         // Changed before viewing?
                    .setIcon(FinioImageModel.ANCHOR)
                    .setAccKey(KeyEvent.VK_E)
                    .setAccCtrl(true))
            .addDescriptor(
                new ToolBarActionDescriptor()
                    .setGroup("mark")
                    .setToolTipText("Toggle Anchor")
                    .setIcon(FinioImageModel.ANCHOR))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("mark")
                    .setText("Toggle Anchor")
                    .setIcon(FinioImageModel.ANCHOR));

    }

}
