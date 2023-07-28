package finio.platform.exts.view.treeview.ui.actions;

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

public class ToggleSimplifiedAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new ToggleSimplifiedWorker(ac, ac.getSelectedWorld(), "Simplify Non Terminal");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setTerminalAllowed(false)
            .setManagedNonTerminalAllowed(true)
        ;

        map.createAction("toggle-simplified", listener, validator)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("tree")
                    .setText("Simplify/Desimplify")    // Will be edited before shown
                    .setIcon(FinioImageModel.SIMPLIFY))
            .addDescriptor(
                new ToolBarActionDescriptor()
                    .setGroup("tree")
                    .setToolTipText("Simplify/Desimplify")
                    .setIcon(FinioImageModel.SIMPLIFY))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("tree")
                    .setText("Simplify/Desimplify")
                    .setIcon(FinioImageModel.SIMPLIFY));

    }

}
