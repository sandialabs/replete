package finio.ui.actions.window;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class FullScreenAction extends DefaultFinioUiAction {

    @Override
    public void register(final AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new FullScreenWorker(ac, ac.getSelectedWorld(), "Full Screen");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setViewRequired(false)
        ;

        map.createAction("full-screen-window", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("window")
                    .setText("&Full Screen")
                    .setIcon(FinioImageModel.WINDOW_NEW));
//                    .setAccKey(KeyEvent.VK_F5))
//            .addDescriptor(
//                new PopupMenuActionDescriptor()
//                    .setPath("window")
//                    .setText("&New Window")
//                    .setIcon(CommonConcepts.REFRESH))
////                    .setAccKey(KeyEvent.VK_F5))
//            .addDescriptor(
//                new ToolBarActionDescriptor()
//                    .setGroup("other")
//                    .setToolTipText("Refresh")
//                    .setIcon(CommonConcepts.REFRESH));

    }

}
