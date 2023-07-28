package finio.ui.actions.change;

import finio.core.FConst;
import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.actions.validation.SpecificKeyedValueActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class PromoteValueAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new PromoteValueWorker(ac, ac.getSelectedWorld(), "Promote Value");
            }
        };

        AActionValidator validator = new SpecificKeyedValueActionValidator(ac, FConst.SYS_VALUE_KEY)
            .setWorldAllowed(false)
            .setSelectionReverseDepth(3)
        ;

        map.createAction("promote-value", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("change")
                    .setText("&Promote")
                    .setIcon(FinioImageModel.PROMOTE))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("change")
                    .setText("&Promote")
                    .setIcon(FinioImageModel.PROMOTE));

    }

}
