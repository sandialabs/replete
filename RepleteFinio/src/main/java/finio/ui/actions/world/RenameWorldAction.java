package finio.ui.actions.world;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class RenameWorldAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new RenameWorldWorker(ac, ac.getSelectedWorld(), "Rename World");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setViewRequired(false)
        ;

        map.createAction("rename-world", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("world")
                    .setText("&Rename World...")
                    .setIcon(CommonConcepts.RENAME));

    }

}
