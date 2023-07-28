package finio.ui.actions.view;

import java.awt.event.ActionEvent;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.app.AppContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIAction;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class ToggleNotifAreaAction extends DefaultFinioUiAction {

    @Override
    public void register(final AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new UIActionListener() {
            public void actionPerformed(ActionEvent e, UIAction action) {
                ac.getWindow().setShowNotificationArea(
                    !ac.getWindow().isShowNotificationArea());
            }
        };

        map.createAction("notifarea", listener)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("view")
                    .setText("Toggle Notification Area")
                    .setSepGroup("view-app")
                    .setIcon(CommonConcepts.EXCEPTION));

    }

}
