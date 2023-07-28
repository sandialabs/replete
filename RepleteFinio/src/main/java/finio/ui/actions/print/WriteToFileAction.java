package finio.ui.actions.print;

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

public class WriteToFileAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new WriteToFileWorker(ac, ac.getSelectedWorld(), "Write To File");
            }
        };

        map.createAction("to-file", listener, new AActionValidator(ac))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("print")
                    .setText("To &File...")
                    .setIcon(CommonConcepts.FILE))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("print")
                    .setText("To &File...")
                    .setIcon(CommonConcepts.FILE));

    }

}
