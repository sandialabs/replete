package finio.ui.actions.transform;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.actions.validation.SpecificTypeValueActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class ConvertBytesToStringAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new ConvertBytesToStringWorker(ac, ac.getSelectedWorld(), "Convert Bytes To String");
            }
        };

        AActionValidator validator = new SpecificTypeValueActionValidator(ac, byte[].class)
            .setWorldAllowed(false)
            .setNonTerminalAllowed(false)
            .setSelectionReverseDepth(2)
        ;

        map.createAction("convert-bytes-to-string", listener, validator)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("transform")
                    .setText("Convert Bytes To String")
                    .setIcon(FinioImageModel.TERMINAL_EXPAND))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("transform")
                    .setText("Convert Bytes To String")
                    .setIcon(FinioImageModel.TERMINAL_EXPAND));

    }

}
