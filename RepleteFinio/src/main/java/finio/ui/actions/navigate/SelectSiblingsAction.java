package finio.ui.actions.navigate;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class SelectSiblingsAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new SelectSiblingsWorker(ac, ac.getSelectedWorld(), "Select Siblings");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setAllMustBeValid(false)
            .setSelectionReverseDepth(2)
        ;

        map.createAction("select-siblings", listener, validator)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("navigate")
                    .setText("Select &Siblings")
                    .setIcon(FinioImageModel.SELSIB))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("navigate")
                    .setText("Select &Siblings")
                    .setIcon(FinioImageModel.SELSIB))
            /*.addDescriptor(
                new KeyPressedActionDescriptor()
                    .setKeyCode(KeyEvent.VK_C))*/;

    }

}
