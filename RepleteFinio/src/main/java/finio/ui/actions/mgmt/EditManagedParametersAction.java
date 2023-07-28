package finio.ui.actions.mgmt;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class EditManagedParametersAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        // Edit Managed Non Terminal Parameters
        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new EditManagedParametersDialogWorker(ac, ac.getSelectedWorld(),
                    "Edit Managed Non Terminal Parameters");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setWorldAllowed(false)
            .setNonTerminalAllowed(false)
            .setTerminalAllowed(false)
            .setManagedNonTerminalAllowed(true)
        ;

        map.createAction("mgmt-params", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("mgmt")
                    .setText("&Edit Parameters...")
                    .setIcon(CommonConcepts.OPTIONS))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("mgmt")
                    .setText("&Edit Parameters...")
                    .setIcon(CommonConcepts.OPTIONS));

    }

}
