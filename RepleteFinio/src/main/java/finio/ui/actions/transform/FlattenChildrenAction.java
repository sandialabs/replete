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

public class FlattenChildrenAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new FlattenWorker(ac, ac.getSelectedWorld(), "Flatten Children");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setSelectionReverseDepth(2)
        ;

        map.createAction("flatten-children", listener, validator)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("transform")
                    .setText("Flatten Children")
                    .setIcon(FinioImageModel.FLATTEN))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("transform")
                    .setText("Flatten Children")
                    .setIcon(FinioImageModel.FLATTEN));

    }

}
