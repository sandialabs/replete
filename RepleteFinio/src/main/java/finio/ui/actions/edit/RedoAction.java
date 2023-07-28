package finio.ui.actions.edit;

import java.awt.event.KeyEvent;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.NotImplWorker;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class RedoAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new LocalWorker(ac, ac.getSelectedWorld(), "Redo");
            }
        };

        map.createAction("redo", listener, new AActionValidator(ac))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("edit")
                    .setText("&Redo")
                    .setIcon(CommonConcepts.REDO)
                    .setAccKey(KeyEvent.VK_Y)
                    .setAccCtrl(true));

    }


    /////////////////
    // INNER CLASS //
    /////////////////

    private class LocalWorker extends NotImplWorker {


        /////////////////
        // CONSTRUCTOR //
        /////////////////

        public LocalWorker(AppContext ac, WorldContext wc, String name) {
            super(ac, wc, name);
        }

    }
}
