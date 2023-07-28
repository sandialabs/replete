package finio.ui.actions.change;

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

public class DescribeValueAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new DescribeValueWorker(ac, ac.getSelectedWorld(), "Describe Value");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setWorldAllowed(false)
            .setSelectionReverseDepth(3)
        ;

        map.createAction("describe-value", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("change")
                    .setText("&Describe")
                    .setIcon(FinioImageModel.NT_MAP_DESCRIBE))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("change")
                    .setText("&Describe")
                    .setIcon(FinioImageModel.NT_MAP_DESCRIBE));

    }

}
