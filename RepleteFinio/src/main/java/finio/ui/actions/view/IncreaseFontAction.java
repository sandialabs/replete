package finio.ui.actions.view;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIAction;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class IncreaseFontAction extends DefaultFinioUiAction {

    @Override
    public void register(final AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new UIActionListener() {
            public void actionPerformed(ActionEvent e, UIAction action) {
                ac.getSelectedWorld().getWorldPanel().increaseFont();
            }
        };

        map.createAction("increase-font", listener, new AActionValidator(ac))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("view")
                    .setText("&Increase Font")
                    .setSepGroup("view-font")
                    .setIcon(FinioImageModel.FONT_INC)
                    .setAccKey(KeyEvent.VK_EQUALS)
                    .setAccCtrl(true));

    }

}
