package finio.ui.actions.navigate;

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

public class ZoomInAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        // NEEDS HIERARCHY CHECKING ?
        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new ZoomInWorker(ac, ac.getSelectedWorld(), "Zoom In");
            }
        };

        map.createAction("zoom-in", listener, new AActionValidator(ac))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("navigate")
                    .setText("Zoom In")
                    .setIcon(CommonConcepts.ZOOM_IN))
            .addDescriptor(
                new ToolBarActionDescriptor()
                    .setGroup("navigate")
                    .setToolTipText("Zoom In")
                    .setIcon(CommonConcepts.ZOOM_IN))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("navigate")
                    .setText("Zoom &In")
                    .setIcon(CommonConcepts.ZOOM_IN));

    }

}
