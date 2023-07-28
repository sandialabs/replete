package finio.ui.actions.misc;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class ImportUiHierarchyAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new ImportUiHierarchyWorker(ac, ac.getSelectedWorld(),
                    "Show UI Component Hierarchy");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setTerminalAllowed(false)
            .setSingleSelect()
        ;

        map.createAction("show-ui-cmp", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("misc")
                    .setText("Import UI Component Hierarchy")
                    .setIcon(FinioImageModel.IMPORT_COMP_HIER));

    }

}
