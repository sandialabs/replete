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

public class ImportFileAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new ImportFileWorker(ac, ac.getSelectedWorld(), "Import File Path");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setTerminalAllowed(false)
            .setSingleSelect()
        ;

        map.createAction("import-file-path", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("import")
                    .setText("From File Path...")
                    .setIcon(CommonConcepts.FILE))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("import")
                    .setText("From File Path...")
                    .setIcon(CommonConcepts.FILE))
            .addDescriptor(
                new ToolBarActionDescriptor()
                    .setGroup("import1")
                    .setToolTipText("Import From File Path...")
                    .setIcon(CommonConcepts.FILE));

    }

}
