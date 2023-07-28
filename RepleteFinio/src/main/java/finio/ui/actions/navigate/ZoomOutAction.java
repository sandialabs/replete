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

public class ZoomOutAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        // NEEDS HIERARCHY CHECKING ?
        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new ZoomOutWorker(ac, ac.getSelectedWorld(), "Zoom Out");
            }
        };

        map.createAction("zoom-out", listener, new AActionValidator(ac))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("navigate")
                    .setText("Zoom Out")
                    .setIcon(CommonConcepts.ZOOM_OUT))
            .addDescriptor(
                new ToolBarActionDescriptor()
                    .setGroup("navigate")
                    .setToolTipText("Zoom Out")
                    .setIcon(CommonConcepts.ZOOM_OUT))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("navigate")
                    .setText("Zoom &Out")
                    .setIcon(CommonConcepts.ZOOM_OUT));

    }

}
