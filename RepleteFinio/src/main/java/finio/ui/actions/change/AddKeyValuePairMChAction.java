package finio.ui.actions.change;

import static finio.core.impl.FMap.A;

import java.awt.event.KeyEvent;

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

public class AddKeyValuePairMChAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new AddKeyValuePairWorker(ac, ac.getSelectedWorld(),
                    "Add Map As Child", A(), false);
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setTerminalAllowed(false)
            .setSelectionReverseDepth(2)
        ;

        map.createAction("add-map-child", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("change/add")
                    .setText("&Map As Child")
                    .setIcon(FinioImageModel.NT_MAP)
                    .setAccKey(KeyEvent.VK_M)
                    .setAccCtrl(true)
                    .setAccShift(true))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("change/add")
                    .setText("&Map As Child")
                    .setIcon(FinioImageModel.NT_MAP)
                    .setAccKey(KeyEvent.VK_M)
                    .setAccCtrl(true)
                    .setAccShift(true));

    }

}
