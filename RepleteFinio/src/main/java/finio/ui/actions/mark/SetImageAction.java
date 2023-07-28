package finio.ui.actions.mark;

import java.awt.event.KeyEvent;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.KeyPressedActionDescriptor;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.ToolBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class SetImageAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        // NEEDS HIERARCHY CHECKING!
        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new SetImageWorker(ac, ac.getSelectedWorld(), "Set Image");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setSelectionReverseDepth(2)
        ;

        map.createAction("set-image", listener, validator)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("mark")
                    .setText("Set Image...")         // Changed before viewing?
                    .setIcon(FinioImageModel.SET_IMAGE))
            .addDescriptor(
                new ToolBarActionDescriptor()
                    .setGroup("mark")
                    .setToolTipText("Set Image...")
                    .setIcon(FinioImageModel.SET_IMAGE))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("mark")
                    .setText("Set Image...")
                    .setIcon(FinioImageModel.SET_IMAGE))
            .addDescriptor(
                new KeyPressedActionDescriptor()
                    .setKeyCode(KeyEvent.VK_I));

    }

}
