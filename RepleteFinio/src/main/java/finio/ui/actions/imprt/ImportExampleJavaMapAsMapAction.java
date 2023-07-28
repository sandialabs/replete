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

public class ImportExampleJavaMapAsMapAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new ImportExampleJavaMapAsMapWorker(ac,
                    ac.getSelectedWorld(), "Import Example Java Map As Map");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setTerminalAllowed(false)
            .setSingleSelect()
        ;

        map.createAction("import-pojm", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("import")
                    .setText("Example Java Map as Map")
                    .setSepGroup("import-example")
                    .setIcon(FinioImageModel.POJM))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("import")
                    .setText("Example Java Map as Map")
                    .setSepGroup("import-example")
                    .setIcon(FinioImageModel.POJM))
            .addDescriptor(
                new ToolBarActionDescriptor()
                    .setGroup("import3")
                    .setToolTipText("Import Example Java Map as Map")
                    .setIcon(FinioImageModel.POJM));

    }

}