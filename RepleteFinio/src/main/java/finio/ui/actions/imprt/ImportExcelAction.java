package finio.ui.actions.imprt;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.ToolBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class ImportExcelAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new ImportExcelWorker(ac, ac.getSelectedWorld(), "Import Excel Document");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setTerminalAllowed(false)
            .setSingleSelect()
        ;

        map.createAction("import-excel", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("import")
                    .setText("From Excel...")
                    .setSepGroup("import-ms")
                    .setIcon(CommonConcepts.MS_EXCEL))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("import")
                    .setText("From Excel...")
                    .setSepGroup("import-ms")
                    .setIcon(CommonConcepts.MS_EXCEL))
            .addDescriptor(
                new ToolBarActionDescriptor()
                    .setGroup("import2")
                    .setToolTipText("Import from Excel...")
                    .setIcon(CommonConcepts.MS_EXCEL));

    }

}
