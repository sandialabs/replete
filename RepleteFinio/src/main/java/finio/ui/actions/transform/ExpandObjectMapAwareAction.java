package finio.ui.actions.transform;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.actions.validation.NonTerminalLikeValueActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class ExpandObjectMapAwareAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new ExpandObjectMapAwareWorker(ac,
                    ac.getSelectedWorld(), "Expand Object (Map-Aware)");
            }
        };

        AActionValidator validator = new NonTerminalLikeValueActionValidator(ac)
            .setWorldAllowed(false)
            .setNonTerminalAllowed(false)
            .setSelectionReverseDepth(2)
        ;

        map.createAction("expand-jntlike", listener, validator)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("transform")
                    .setText("Expand Object (Map-Aware)")
                    .setIcon(FinioImageModel.TERMINAL_EXPAND))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("transform")
                    .setText("Expand Object (Map-Aware)")
                    .setIcon(FinioImageModel.TERMINAL_EXPAND));

    }

}
