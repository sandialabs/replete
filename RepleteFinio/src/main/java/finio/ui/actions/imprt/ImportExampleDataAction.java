package finio.ui.actions.imprt;

import java.awt.event.KeyEvent;

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

public class ImportExampleDataAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new ImportExampleDataWorker(ac, ac.getSelectedWorld(), "Import Example Data");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setTerminalAllowed(false)
            .setSingleSelect()
        ;

        map.createAction("import-example", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("import")
                    .setText("Example Data")
                    .setIcon(FinioImageModel.EXAMPLE_DATA)
                    .setSepGroup("import-example")
                    .setAccKey(KeyEvent.VK_BACK_QUOTE))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("import")
                    .setText("Example Data")
                    .setSepGroup("import-example")
                    .setIcon(FinioImageModel.EXAMPLE_DATA))
            .addDescriptor(
                new ToolBarActionDescriptor()
                    .setGroup("import3")
                    .setToolTipText("Import Example Data")
                    .setIcon(FinioImageModel.EXAMPLE_DATA));

    }

}
