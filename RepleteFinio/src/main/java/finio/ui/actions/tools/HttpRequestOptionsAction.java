package finio.ui.actions.tools;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.app.AppContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class HttpRequestOptionsAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new HttpRequestOptionsDialogWorker(ac, ac.getSelectedWorld(),
                    "HTTP Request Options");
            }
        };

        map.createAction("http-options", listener)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("tools")
                    .setText("HTTP Request &Options...")
                    .setIcon(CommonConcepts.OPTIONS));
    }

}
