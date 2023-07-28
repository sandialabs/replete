package finio.ui.actions.world;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.ToolBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class SaveWorldAsAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new SaveWorldWorker(ac, ac.getSelectedWorld(), "Save World As", null);
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setViewRequired(false)
        ;

        map.createAction("save-world-as", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("world")
                    .setText("Save World &As...")
                    .setIcon(CommonConcepts.SAVE_AS)
                    .setSepGroup("world-save"))
            .addDescriptor(
                new ToolBarActionDescriptor()
                    .setGroup("world")
                    .setToolTipText("Save World As...")
                    .setIcon(CommonConcepts.SAVE_AS));

    }

}
