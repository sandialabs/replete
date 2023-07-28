package finio.ui.actions.imprt;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.ToolBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class ImportScrutinizationAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new ImportScrutinizationWorker(ac,
                    ac.getSelectedWorld(), "Import Local Scrutinization");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setTerminalAllowed(false)
            .setSingleSelect()
        ;

        map.createAction("import-scrutinization", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("import")
                    .setText("Local Scrutinization")
                    .setSepGroup("import-ls")
                    .setIcon(FinioImageModel.IMPORT_SCRUTZ))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("import")
                    .setText("Local Scrutinization")
                    .setSepGroup("import-ls")
                    .setIcon(FinioImageModel.IMPORT_SCRUTZ))
            .addDescriptor(
                new ToolBarActionDescriptor()
                    .setGroup("import2")
                    .setToolTipText("Import Local Scrutinization")
                    .setIcon(FinioImageModel.IMPORT_SCRUTZ));

    }

}
