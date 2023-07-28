package finio.ui.actions.change;

import finio.core.impl.FList;
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

public class AddKeyValuePairLChAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                Object V = new FList();
                return new AddKeyValuePairWorker(ac, ac.getSelectedWorld(),
                    "Add List As Child", V, false);
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setTerminalAllowed(false)
            .setSelectionReverseDepth(2)
        ;

        map.createAction("add-list-child", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("change/add")
                    .setText("&List As Child")
                    .setIcon(FinioImageModel.NT_LIST))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("change/add")
                    .setText("&List As Child")
                    .setIcon(FinioImageModel.NT_LIST));

    }

}
