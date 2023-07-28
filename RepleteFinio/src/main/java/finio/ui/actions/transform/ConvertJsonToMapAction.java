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

public class ConvertJsonToMapAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new ConvertJsonToMapWorker(ac, ac.getSelectedWorld(), "Convert JSON To Map");
            }
        };

        AActionValidator validator = new SpecificTypeValueActionValidator(ac, String.class)
            .setWorldAllowed(false)
            .setNonTerminalAllowed(false)
            .setSelectionReverseDepth(2)
        ;

        map.createAction("json-from-string", listener, validator)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("transform")
                    .setText("Convert JSON To Map")
                    .setIcon(FinioImageModel.JSON))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("transform")
                    .setText("Convert JSON To Map")
                    .setIcon(FinioImageModel.JSON));

    }

}
