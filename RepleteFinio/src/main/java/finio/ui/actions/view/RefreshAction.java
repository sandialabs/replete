package finio.ui.actions.view;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.ToolBarActionDescriptor;
import replete.ui.uiaction.UIAction;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class RefreshAction extends DefaultFinioUiAction {

    @Override
    public void register(final AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new UIActionListener() {
            public void actionPerformed(ActionEvent e, UIAction action) {
                ac.refresh();
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setViewRequired(false)
        ;

        map.createAction("refresh", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("view")
                    .setText("&Refresh")
                    .setIcon(CommonConcepts.REFRESH)
                    .setAccKey(KeyEvent.VK_F5))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("view")
                    .setText("&Refresh")
                    .setIcon(CommonConcepts.REFRESH)
                    .setAccKey(KeyEvent.VK_F5))
            .addDescriptor(
                new ToolBarActionDescriptor()
                    .setGroup("other")
                    .setToolTipText("Refresh")
                    .setIcon(CommonConcepts.REFRESH));

    }

}
