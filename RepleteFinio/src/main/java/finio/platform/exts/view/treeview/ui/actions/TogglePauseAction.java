package finio.platform.exts.view.treeview.ui.actions;

import finio.platform.exts.view.treeview.ui.FTreePanel;
import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.actions.validation.SpecificViewActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.ToolBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class TogglePauseAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new TogglePauseWorker(ac, ac.getSelectedWorld(), "Pause");
            }
        };

        AActionValidator validator = new SpecificViewActionValidator(ac, FTreePanel.class) {
//            @Override
//            protected boolean accept(AppContext ac, ValueSelectionContext C) {
//                if(super.accept(ac, C)) {
//                    return !((ANode) C.getPanelData()[0]).isPaused();
//                }
//                return false;
//            }
        }.setTerminalAllowed(false)
         .setManagedNonTerminalAllowed(true);

        map.createAction("toggle-pause", listener, validator)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("tree")
                    .setText("Pause")    // Will be edited before shown
                    .setIcon(FinioImageModel.NT_MAP_PAUSED))
            .addDescriptor(
                new ToolBarActionDescriptor()
                    .setGroup("tree")
                    .setToolTipText("Pause")
                    .setIcon(FinioImageModel.NT_MAP_PAUSED))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("tree")
                    .setText("Pause")
                    .setIcon(FinioImageModel.NT_MAP_PAUSED));

    }

}
