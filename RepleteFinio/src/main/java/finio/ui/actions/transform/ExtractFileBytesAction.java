package finio.ui.actions.transform;

import java.io.File;

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

public class ExtractFileBytesAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new ExtractFileBytesWorker(ac, ac.getSelectedWorld(), "Extract Bytes");
            }
        };

        AActionValidator validator = new SpecificTypeValueActionValidator(ac, File.class)
            .setWorldAllowed(false)
            .setNonTerminalAllowed(false)
            .setSelectionReverseDepth(2)
        ;

        map.createAction("extract-bytes", listener, validator)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("transform")
                    .setText("Extract Bytes")
                    .setIcon(FinioImageModel.TERMINAL_BINARY))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("transform")
                    .setText("Extract Bytes")
                    .setIcon(FinioImageModel.TERMINAL_BINARY));

    }

}
