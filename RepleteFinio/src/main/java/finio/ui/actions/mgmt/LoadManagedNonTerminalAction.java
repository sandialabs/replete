package finio.ui.actions.mgmt;

import java.awt.event.KeyEvent;

import finio.core.managed.ManagedNonTerminal;
import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.actions.validation.SpecificTypeValueActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import finio.ui.view.SelectionContext;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class LoadManagedNonTerminalAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new LoadManagedNonTerminalWorker(ac,
                    ac.getSelectedWorld(), "Load Managed Non Terminal");
            }
        };

        AActionValidator validator = new SpecificTypeValueActionValidator(ac, ManagedNonTerminal.class) {
            @Override
            protected boolean accept(AppContext ac, SelectionContext C) {
                if(super.accept(ac, C)) {
                    ManagedNonTerminal G = (ManagedNonTerminal) C.getV();
                    return !G.isLoaded();
                }
                return false;
            }
        };

        map.createAction("mgmt-load", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("mgmt")
                    .setText("&Load")
                    .setIcon(FinioImageModel.LOAD)
                    .setAccKey(KeyEvent.VK_L)
                    .setAccCtrl(true))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("mgmt")
                    .setText("&Load")
                    .setIcon(FinioImageModel.LOAD));

    }

}
