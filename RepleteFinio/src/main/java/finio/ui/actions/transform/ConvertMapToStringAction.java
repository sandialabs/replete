package finio.ui.actions.transform;

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

public class ConvertMapToStringAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new ConvertMapToStringWorker(ac, ac.getSelectedWorld(), "Convert Map To String");
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setWorldAllowed(false)
            .setTerminalAllowed(false)
            .setSelectionReverseDepth(2)
        ;

        map.createAction("combine-map-to-string", listener, validator)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("transform")
                    .setText("Convert Map To String...")
                    .setIcon(FinioImageModel.CVT_MAP_STR)
                    .setAccKey(KeyEvent.VK_C)
                    .setAccCtrl(true)
                    .setAccShift(true))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("transform")
                    .setText("Convert Map To String...")
                    .setIcon(FinioImageModel.CVT_MAP_STR)
                    .setAccKey(KeyEvent.VK_C)
                    .setAccCtrl(true)
                    .setAccShift(true));

    }

}
