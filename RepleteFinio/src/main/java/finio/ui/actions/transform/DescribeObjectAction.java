package finio.ui.actions.transform;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class DescribeObjectAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new DescribeObjectWorker(ac, ac.getSelectedWorld(), "Describe Object");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setWorldAllowed(false)
            .setSelectionReverseDepth(2)
        ;

        map.createAction("describe-obj", listener, validator)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("transform")
                    .setText("Describe Object")
                    .setIcon(FinioImageModel.DESCRIBE_OBJ))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("transform")
                    .setText("Describe Object")
                    .setIcon(FinioImageModel.DESCRIBE_OBJ));

    }

}
