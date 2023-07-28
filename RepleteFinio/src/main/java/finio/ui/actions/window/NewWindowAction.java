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

public class NewWindowAction extends DefaultFinioUiAction {

    @Override
    public void register(final AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new NewWindowWorker(ac, ac.getSelectedWorld(), "Create a New Finio Window");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setViewRequired(false)
        ;

        map.createAction("new-window", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("window")
                    .setText("&New Window")
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
