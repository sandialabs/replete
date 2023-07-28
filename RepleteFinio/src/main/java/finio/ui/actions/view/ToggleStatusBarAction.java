package finio.ui.actions.view;

import java.awt.event.ActionEvent;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIAction;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class ToggleStatusBarAction extends DefaultFinioUiAction {

    @Override
    public void register(final AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new UIActionListener() {
            public void actionPerformed(ActionEvent e, UIAction action) {
                ac.getWindow().setShowStatusBar(
                    !ac.getWindow().isShowStatusBar());
            }
        };

        map.createAction("statusbar", listener)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("view")
                    .setText("&Toggle Status Bar")
                    .setSepGroup("view-app")
                    .setIcon(FinioImageModel.STATUS_BAR));

    }

}
